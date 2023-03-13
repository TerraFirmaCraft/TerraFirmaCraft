/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IntArrayBuilder;
import net.dries007.tfc.util.calendar.ICalendarTickable;

public abstract class AbstractFirepitBlockEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends TickableInventoryBlockEntity<C> implements ICalendarTickable, MenuProvider
{
    public static final int SLOT_FUEL_CONSUME = 0; // where fuel is taken by the firepit
    public static final int SLOT_FUEL_INPUT = 3; // where fuel is inserted into the firepit (0-3 are all fuel slots)

    public static void convertTo(LevelAccessor level, BlockPos pos, BlockState state, AbstractFirepitBlockEntity<?> firepit, Block newBlock)
    {
        // Convert firepit to another device
        // Normally, as soon as we set the block, it would eject all contents thanks to DeviceBlock and InventoryBlockEntity
        // So, we need to first copy the inventory into a temporary storage, clear it, then switch blocks, and copy back later
        firepit.ejectMainInventory();
        NonNullList<ItemStack> saved = Helpers.extractAllItems(firepit.inventory);

        level.setBlock(pos, newBlock.defaultBlockState().setValue(FirepitBlock.LIT, state.getValue(FirepitBlock.LIT)), 3);

        final BlockEntity newEntity = level.getBlockEntity(pos);
        if (newEntity instanceof AbstractFirepitBlockEntity<?> newFirepit)
        {
            Helpers.insertAllItems(newFirepit.inventory, saved);
            newFirepit.copyFrom(firepit);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractFirepitBlockEntity<?> firepit)
    {
        firepit.checkForLastTickSync();
        firepit.checkForCalendarUpdate();

        if (firepit.needsRecipeUpdate)
        {
            firepit.needsRecipeUpdate = false;
            firepit.updateCachedRecipe();
        }
        if (level.getGameTime() % 20 == 0)
        {
            final AABB bounds = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 0.3, pos.getZ() + 1);
            Helpers.gatherAndConsumeItems(level, bounds, firepit.inventory, SLOT_FUEL_CONSUME, SLOT_FUEL_INPUT);
            firepit.updateSmokeLevel(state);
        }

        boolean isRaining = level.isRainingAt(pos);
        if (state.getValue(FirepitBlock.LIT))
        {
            // Update fuel
            if (firepit.burnTicks > 0)
            {
                firepit.burnTicks -= firepit.airTicks > 0 || isRaining ? 2 : 1; // Fuel burns twice as fast using bellows, or in the rain
            }
            if (firepit.burnTicks <= 0 && !firepit.consumeFuel())
            {
                firepit.extinguish(state);
            }
        }
        if (firepit.airTicks > 0)
        {
            firepit.airTicks--;
        }
        if (firepit.temperature > 0 || firepit.burnTemperature > 0)
        {
            firepit.temperature = HeatCapability.adjustDeviceTemp(firepit.temperature, firepit.burnTemperature, firepit.airTicks, isRaining);
        }
        HeatCapability.provideHeatTo(level, pos.above(), firepit.temperature);
        firepit.handleCooking();
        if (firepit.needsSlotUpdate)
        {
            firepit.cascadeFuelSlots();
        }
    }

    protected final ContainerData syncableData;

    protected boolean needsSlotUpdate = false; // set when fuel needs to be cascaded
    protected boolean needsRecipeUpdate = false; // set when the recipe needs to be re-cached on tick
    protected int burnTicks; // ticks remaining for the burning of the fuel item
    protected int airTicks; // ticks remaining for bellows provided air
    protected float burnTemperature; // burn temperature of the current fuel item
    protected float temperature; // current actual temperature
    private long lastPlayerTick = Integer.MIN_VALUE;
    private float dirtiness = 0f; // represents the dirtiness of the fire as handled by the fuel added

    public AbstractFirepitBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventoryFactory, Component defaultName)
    {
        super(type, pos, state, inventoryFactory, defaultName);

        burnTicks = 0;
        temperature = 0;
        burnTemperature = 0;

        syncableData = new IntArrayBuilder().add(() -> (int) temperature, value -> temperature = value);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        temperature = nbt.getFloat("temperature");
        burnTicks = nbt.getInt("burnTicks");
        airTicks = nbt.getInt("airTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        lastPlayerTick = nbt.getLong("lastPlayerTick");
        dirtiness = nbt.getFloat("dirtiness");

        needsRecipeUpdate = true;

        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putFloat("temperature", temperature);
        nbt.putInt("burnTicks", burnTicks);
        nbt.putInt("airTicks", airTicks);
        nbt.putFloat("burnTemperature", burnTemperature);
        nbt.putLong("lastPlayerTick", lastPlayerTick);
        nbt.putFloat("dirtiness", dirtiness);
        super.saveAdditional(nbt);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsSlotUpdate = true;
        updateCachedRecipe();
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        if (level.getBlockState(worldPosition).getValue(FirepitBlock.LIT))
        {
            final HeatCapability.Remainder remainder = HeatCapability.consumeFuelForTicks(ticks, inventory, burnTicks, burnTemperature, SLOT_FUEL_CONSUME, SLOT_FUEL_INPUT);

            burnTicks = remainder.burnTicks();
            burnTemperature = remainder.burnTemperature();
            needsSlotUpdate = true;
            if (remainder.ticks() > 0) // Consumed all fuel, so extinguish and cool instantly
            {
                extinguish(level.getBlockState(worldPosition));
                coolInstantly();
            }
        }
    }

    @Override
    @Deprecated
    public long getLastUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    @Deprecated
    public void setLastUpdateTick(long tick)
    {
        lastPlayerTick = tick;
    }

    public void updateSmokeLevel(BlockState state)
    {
        assert level != null;
        dirtiness = Mth.clamp(0.99f * dirtiness, 0f, 1f);
        final int wantedSmokeLevel = Mth.ceil(Mth.map(dirtiness, 0f, 1f, 0f, 4f));
        if (state.hasProperty(FirepitBlock.SMOKE_LEVEL) && state.getValue(FirepitBlock.SMOKE_LEVEL) != wantedSmokeLevel)
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(FirepitBlock.SMOKE_LEVEL, wantedSmokeLevel));
        }
    }

    public void intakeAir(int amount)
    {
        airTicks += amount;
        if (airTicks > BellowsBlockEntity.MAX_DEVICE_AIR_TICKS)
        {
            airTicks = BellowsBlockEntity.MAX_DEVICE_AIR_TICKS;
        }
    }

    public void extinguish(BlockState state)
    {
        assert level != null;
        if (state.getValue(FirepitBlock.LIT))
        {
            Helpers.playSound(level, worldPosition, SoundEvents.FIRE_EXTINGUISH);
            level.setBlockAndUpdate(worldPosition, state.setValue(FirepitBlock.LIT, false));
            burnTicks = 0;
            airTicks = 0;
            burnTemperature = 0;
            dirtiness = 0;
        }
    }

    /**
     * Attempts to light the firepit. Use over just setting the block state to LIT = true, as if there is no fuel, that will light the firepit for one tick which looks strange
     *
     * @param state The current firepit block state
     * @return {@code true} if the firepit was lit.
     */
    public boolean light(BlockState state)
    {
        assert level != null;
        if (consumeFuel())
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(FirepitBlock.LIT, true));
            return true;
        }
        return false;
    }

    public void ejectMainInventory()
    {
        assert level != null;
        for (int i = FirepitBlockEntity.SLOT_ITEM_INPUT; i < inventory.getSlots(); i++)
        {
            final ItemStack stack = Helpers.removeStack(inventory, i);
            if (!stack.isEmpty())
            {
                Helpers.spawnItem(level, worldPosition, stack, 0.7D);
            }
        }
    }

    public void copyFrom(AbstractFirepitBlockEntity<?> other)
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
        return (slot == FirepitBlockEntity.SLOT_OUTPUT_1 || slot == FirepitBlockEntity.SLOT_OUTPUT_2) ? 64 : 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return switch (slot)
            {
                case SLOT_FUEL_INPUT -> Fuel.get(stack) != null && Helpers.isItem(stack.getItem(), TFCTags.Items.FIREPIT_FUEL);
                case FirepitBlockEntity.SLOT_ITEM_INPUT -> Helpers.mightHaveCapability(stack, HeatCapability.CAPABILITY);
                case FirepitBlockEntity.SLOT_OUTPUT_1, FirepitBlockEntity.SLOT_OUTPUT_2 -> true;
                default -> false;
            };
    }

    public float getTemperature()
    {
        return temperature;
    }

    public ContainerData getSyncableData()
    {
        return syncableData;
    }

    /**
     * Attempts to consume one piece of fuel. Returns if the fire pit consumed any fuel (and so, ended up lit)
     */
    protected boolean consumeFuel()
    {
        final ItemStack fuelStack = inventory.getStackInSlot(SLOT_FUEL_CONSUME);
        if (!fuelStack.isEmpty())
        {
            // Try and consume a piece of fuel
            inventory.setStackInSlot(SLOT_FUEL_CONSUME, ItemStack.EMPTY);
            needsSlotUpdate = true;
            Fuel fuel = Fuel.get(fuelStack);
            if (fuel != null)
            {
                burnTicks += fuel.getDuration();
                burnTemperature = fuel.getTemperature();
                dirtiness += 1f - fuel.getPurity();
            }
            markForSync();
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
