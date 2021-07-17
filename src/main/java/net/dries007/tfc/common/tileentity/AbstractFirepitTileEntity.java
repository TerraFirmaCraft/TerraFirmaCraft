/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.types.Fuel;
import net.dries007.tfc.common.types.FuelManager;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IntArrayBuilder;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;

public abstract class AbstractFirepitTileEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundNBT>> extends TickableInventoryTileEntity<C> implements ICalendarTickable, INamedContainerProvider
{
    public static final int SLOT_FUEL_CONSUME = 0; // where fuel is taken by the firepit
    public static final int SLOT_FUEL_INPUT = 3; // where fuel is inserted into the firepit (0-3 are all fuel slots)

    public static final int DATA_SLOT_TEMPERATURE = 0;

    public static void convertTo(IWorld world, BlockPos pos, BlockState state, AbstractFirepitTileEntity<?> firepit, Block newBlock)
    {
        // Convert firepit to another device
        // Normally, as soon as we set the block, it would eject all contents thanks to DeviceBlock and InventoryTileEntity
        // So, we need to first copy the inventory into a temporary storage, clear it, then switch blocks, and copy back later
        firepit.ejectMainInventory();
        NonNullList<ItemStack> saved = Helpers.extractAllItems(firepit.inventory);

        world.setBlock(pos, newBlock.defaultBlockState().setValue(FirepitBlock.LIT, state.getValue(FirepitBlock.LIT)), 3);

        final AbstractFirepitTileEntity<?> newFirepit = Helpers.getTileEntity(world, pos, AbstractFirepitTileEntity.class);
        if (newFirepit != null)
        {
            Helpers.insertAllItems(newFirepit.inventory, saved);
            newFirepit.copyFrom(firepit);
        }
    }

    protected final IIntArray syncableData;
    protected boolean needsSlotUpdate = false; // sets when fuel needs to be cascaded
    protected int burnTicks; // ticks remaining for the burning of the fuel item
    protected int airTicks; // ticks remaining for bellows provided air
    protected float burnTemperature; // burn temperature of the current fuel item
    protected float temperature; // current actual temperature
    private long lastPlayerTick;

    public AbstractFirepitTileEntity(TileEntityType<?> type, InventoryFactory<C> inventoryFactory, ITextComponent defaultName)
    {
        super(type, inventoryFactory, defaultName);

        burnTicks = 0;
        temperature = 0;
        burnTemperature = 0;
        lastPlayerTick = Calendars.SERVER.getTicks();

        syncableData = new IntArrayBuilder().add(() -> (int) temperature, value -> temperature = value);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        temperature = nbt.getFloat("temperature");
        burnTicks = nbt.getInt("burnTicks");
        airTicks = nbt.getInt("airTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        lastPlayerTick = nbt.getLong("lastPlayerTick");

        updateCachedRecipe();

        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putFloat("temperature", temperature);
        nbt.putInt("burnTicks", burnTicks);
        nbt.putInt("airTicks", airTicks);
        nbt.putFloat("burnTemperature", burnTemperature);
        nbt.putLong("lastPlayerTick", lastPlayerTick);
        return super.save(nbt);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsSlotUpdate = true;
        updateCachedRecipe();
    }

    @Override
    public void tick()
    {
        super.tick();
        checkForCalendarUpdate();

        assert level != null;
        if (!level.isClientSide)
        {
            boolean isRaining = level.isRainingAt(worldPosition);
            BlockState state = level.getBlockState(worldPosition);
            if (state.getValue(FirepitBlock.LIT))
            {
                // Update fuel
                if (burnTicks > 0)
                {
                    burnTicks -= airTicks > 0 || isRaining ? 2 : 1; // Fuel burns twice as fast using bellows, or in the rain
                }
                if (burnTicks <= 0)
                {
                    if (!consumeFuel(state))
                    {
                        extinguish(state);
                    }
                }
            }
            if (airTicks > 0)
            {
                airTicks--;
            }
            if (temperature > 0 || burnTemperature > 0)
            {
                temperature = HeatCapability.adjustDeviceTemp(temperature, burnTemperature, airTicks, isRaining);
            }
            handleCooking();
            if (needsSlotUpdate)
            {
                cascadeFuelSlots();
            }
        }
    }

    @Override
    public void onCalendarUpdate(long deltaPlayerTicks)
    {
        assert level != null;
        if (!level.getBlockState(worldPosition).getValue(FirepitBlock.LIT)) return;

        // Consume fuel as dictated by the delta player ticks (don't simulate any input changes), and then extinguish
        if (burnTicks > deltaPlayerTicks)
        {
            burnTicks -= deltaPlayerTicks;
            return;
        }
        else
        {
            deltaPlayerTicks -= burnTicks;
            burnTicks = 0;
        }
        needsSlotUpdate = true; // Need to consume fuel
        for (int i = SLOT_FUEL_CONSUME; i <= SLOT_FUEL_INPUT; i++)
        {
            ItemStack fuelStack = inventory.getStackInSlot(i);
            Fuel fuel = FuelManager.get(fuelStack);
            if (fuel != null)
            {
                inventory.setStackInSlot(i, ItemStack.EMPTY);
                if (fuel.getDuration() > deltaPlayerTicks)
                {
                    burnTicks = (int) (fuel.getDuration() - deltaPlayerTicks);
                    burnTemperature = fuel.getTemperature();
                    return;
                }
                else
                {
                    deltaPlayerTicks -= fuel.getDuration();
                    burnTicks = 0;
                }
            }
        }
        if (deltaPlayerTicks > 0) // Consumed all fuel, so extinguish and cool instantly
        {
            extinguish(level.getBlockState(worldPosition));
            coolInstantly();
        }
    }

    @Override
    public long getLastUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    public void setLastUpdateTick(long tick)
    {
        lastPlayerTick = tick;
    }

    public void extinguish(BlockState state)
    {
        assert level != null;
        if (state.getValue(FirepitBlock.LIT))
        {
            Helpers.playSound(level, worldPosition, SoundEvents.FIRE_EXTINGUISH);
        }
        level.setBlockAndUpdate(worldPosition, state.setValue(FirepitBlock.LIT, false));
        burnTicks = 0;
        airTicks = 0;
        burnTemperature = 0;
    }

    /**
     * Attempts to light the firepit. Use over just setting the block state to LIT = true, as if there is no fuel, that will light the firepit for one tick which looks strange
     *
     * @param state The current firepit block state
     */
    public void light(BlockState state)
    {
        assert level != null;
        if (consumeFuel(state))
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(FirepitBlock.LIT, true));
        }
    }

    public void ejectMainInventory()
    {
        assert level != null;
        for (int i = FirepitTileEntity.SLOT_ITEM_INPUT; i < inventory.getSlots(); i++)
        {
            Helpers.spawnItem(level, worldPosition, inventory.getStackInSlot(i), 0.7D);
        }
    }

    public void copyFrom(AbstractFirepitTileEntity<?> other)
    {
        burnTicks = other.burnTicks;
        airTicks = other.airTicks;
        burnTemperature = other.burnTemperature;
        temperature = other.temperature;

        needsSlotUpdate = true;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        // Output slots can carry full stacks, the rest is individual
        return (slot == FirepitTileEntity.SLOT_OUTPUT_1 || slot == FirepitTileEntity.SLOT_OUTPUT_2) ? 64 : 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        switch (slot)
        {
            case SLOT_FUEL_INPUT:
                return FuelManager.get(stack) != null && stack.getItem().is(TFCTags.Items.FIREPIT_FUEL);
            case FirepitTileEntity.SLOT_ITEM_INPUT:
                return stack.getCapability(HeatCapability.CAPABILITY).isPresent();
            case FirepitTileEntity.SLOT_OUTPUT_1:
            case FirepitTileEntity.SLOT_OUTPUT_2:
                return true;
            default:
                return false;
        }
    }

    public IIntArray getSyncableData()
    {
        return syncableData;
    }

    /**
     * Attempts to consume one piece of fuel. Returns if the fire pit consumed any fuel (and so, ended up lit)
     *
     * @param state The current firepit block state
     */
    protected boolean consumeFuel(BlockState state)
    {
        final ItemStack fuelStack = inventory.getStackInSlot(SLOT_FUEL_CONSUME);
        if (!fuelStack.isEmpty())
        {
            // Try and consume a piece of fuel
            inventory.setStackInSlot(SLOT_FUEL_CONSUME, ItemStack.EMPTY);
            needsSlotUpdate = true;
            Fuel fuel = FuelManager.get(fuelStack);
            if (fuel != null)
            {
                burnTicks += fuel.getDuration();
                burnTemperature = fuel.getTemperature();
            }
        }
        return burnTicks > 0;
    }

    /**
     * Handles firepit-specific cooking operations
     */
    protected abstract void handleCooking();

    /**
     * Cools all contents of the firepit instantly.
     */
    protected abstract void coolInstantly();

    /**
     * Updates cached recipes due to an inventory or other change.
     */
    protected abstract void updateCachedRecipe();

    protected void cascadeFuelSlots()
    {
        int lowestOpenSlot = SLOT_FUEL_CONSUME;
        for (int i = SLOT_FUEL_CONSUME; i <= SLOT_FUEL_INPUT; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                if (i > lowestOpenSlot)
                {
                    inventory.setStackInSlot(lowestOpenSlot, stack.copy());
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
                lowestOpenSlot++;
            }
        }
        needsSlotUpdate = false;
    }
}
