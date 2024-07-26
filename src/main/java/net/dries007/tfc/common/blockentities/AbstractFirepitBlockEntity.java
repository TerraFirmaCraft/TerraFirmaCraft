/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IntArrayBuilder;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import net.dries007.tfc.util.data.Fuel;

public abstract class AbstractFirepitBlockEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends TickableInventoryBlockEntity<C> implements ICalendarTickable, MenuProvider
{
    public static final int SLOT_FUEL_CONSUME = 0; // where fuel is taken by the firepit
    public static final int SLOT_FUEL_2 = 1;
    public static final int SLOT_FUEL_3 = 2;
    public static final int SLOT_FUEL_INPUT = 3; // where fuel is inserted into the firepit (0-3 are all fuel slots)

    public static void convertTo(LevelAccessor level, BlockPos pos, BlockState state, AbstractFirepitBlockEntity<?> firepit, Block newBlock)
    {
        // Convert firepit to another device
        // Normally, as soon as we set the block, it would eject all contents thanks to DeviceBlock and InventoryBlockEntity
        // So, we need to first copy the inventory into a temporary storage, clear it, then switch blocks, and copy back later
        firepit.ejectMainInventory();
        NonNullList<ItemStack> saved = Helpers.extractAllItems(firepit.inventory);

        final BlockState newState = Helpers.copyProperties(newBlock.defaultBlockState(), state);
        level.setBlock(pos, newState, 3);
        Helpers.playPlaceSound(level, pos, newState);

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
            final AABB bounds = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 0.5, pos.getZ() + 1);
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
                if (firepit.burnTicks <= 0)
                    firepit.markForSync();
            }
            if (firepit.burnTicks <= 0 && !firepit.consumeFuel())
            {
                firepit.extinguish(state);
            }
        }
        if (firepit.airTicks > 0)
        {
            firepit.airTicks--;
            if (firepit.airTicks <= 0)
                firepit.markForSync();
        }
        if (firepit.temperature > 0 || firepit.burnTemperature > 0)
        {
            firepit.temperature = HeatCapability.adjustDeviceTemp(firepit.temperature, firepit.burnTemperature, firepit.airTicks, isRaining);
        }
        HeatCapability.provideHeatTo(level, pos.above(), Direction.DOWN, firepit.temperature);
        firepit.handleCooking();
        if (firepit.needsSlotUpdate)
        {
            firepit.cascadeFuelSlots();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, AbstractFirepitBlockEntity<?> firepit)
    {
        if (state.getValue(FirepitBlock.LIT))
        {
            if (firepit.burnTicks > 0)
            {
                firepit.burnTicks -= firepit.airTicks > 0 || level.isRainingAt(pos) ? 2 : 1;
            }
        }
        if (firepit.airTicks > 0)
        {
            firepit.airTicks--;
        }

        // Modified from CampfireBlockEntity.particleTick
        final RandomSource random = level.getRandom();
        final int smoke = state.getValue(FirepitBlock.SMOKE_LEVEL); // 0 -> 4

        if (state.getBlock() instanceof FirepitBlock block &&
            state.getValue(FirepitBlock.LIT) &&
            random.nextInt(9 - smoke) == 0)
        {
            final double x = pos.getX() + 0.5;
            final double y = pos.getY() + block.getParticleHeightOffset();
            final double z = pos.getZ() + 0.5;
            for (int i = 0; i < 1 + random.nextInt(3); i++)
            {
                level.addAlwaysVisibleParticle(TFCParticles.SMOKES.get(smoke).get(), x + Helpers.triangle(random) * 0.5f, y + random.nextDouble(), z + Helpers.triangle(random) * 0.5f, 0, 0.07D, 0);
            }
        }
    }

    protected final IntArrayBuilder syncableData;

    protected boolean needsSlotUpdate = false; // set when fuel needs to be cascaded
    protected boolean needsRecipeUpdate = false; // set when the recipe needs to be re-cached on tick
    protected int burnTicks; // ticks remaining for the burning of the fuel item
    protected int airTicks; // ticks remaining for bellows provided air
    protected float burnTemperature; // burn temperature of the current fuel item
    protected float temperature; // current actual temperature
    private long lastPlayerTick = Integer.MIN_VALUE;
    private float dirtiness = 0f; // represents the dirtiness of the fire as handled by the fuel added
    private int lastMaxBurnTicks = Integer.MAX_VALUE;
    private int ash = 0;

    public AbstractFirepitBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventoryFactory, Component defaultName)
    {
        super(type, pos, state, inventoryFactory, defaultName);

        burnTicks = 0;
        temperature = 0;
        burnTemperature = 0;

        syncableData = new IntArrayBuilder().add(() -> (int) temperature, value -> temperature = value);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        temperature = nbt.getFloat("temperature");
        burnTicks = nbt.getInt("burnTicks");
        airTicks = nbt.getInt("airTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        lastPlayerTick = nbt.getLong("lastPlayerTick");
        dirtiness = nbt.getFloat("dirtiness");
        lastMaxBurnTicks = nbt.getInt("lastMaxBurnTicks");
        ash = nbt.getInt("ash");

        needsRecipeUpdate = true;

        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putFloat("temperature", temperature);
        nbt.putInt("burnTicks", burnTicks);
        nbt.putInt("airTicks", airTicks);
        nbt.putFloat("burnTemperature", burnTemperature);
        nbt.putLong("lastPlayerTick", lastPlayerTick);
        nbt.putFloat("dirtiness", dirtiness);
        nbt.putInt("lastMaxBurnTicks", lastMaxBurnTicks);
        nbt.putInt("ash", ash);
        super.saveAdditional(nbt, provider);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsSlotUpdate = true;
        updateCachedRecipe();
        markForSync();
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
    public long getLastCalendarUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    @Deprecated
    public void setLastCalendarUpdateTick(long tick)
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
        if (burnTicks > 0)
        {
            return true; // Already lit
        }
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
                case FirepitBlockEntity.SLOT_ITEM_INPUT -> HeatCapability.has(stack);
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
        assert level != null;
        final ItemStack fuelStack = inventory.getStackInSlot(SLOT_FUEL_CONSUME);
        if (!fuelStack.isEmpty())
        {
            // Try and consume a piece of fuel
            inventory.setStackInSlot(SLOT_FUEL_CONSUME, ItemStack.EMPTY);
            needsSlotUpdate = true;
            Fuel fuel = Fuel.get(fuelStack);
            if (fuel != null)
            {
                burnTicks += fuel.duration();
                lastMaxBurnTicks = fuel.duration();
                burnTemperature = fuel.temperature();
                dirtiness += 1f - fuel.purity();
            }
            if (level.random.nextFloat() < 0.5f)
            {
                addAsh(1);
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
        markForSync();
        needsSlotUpdate = false;
    }

    public BurnStage getBurnStage(int slot)
    {
        if (burnTemperature < 1f || temperature < 1f)
            return slot < SLOT_FUEL_3 ? BurnStage.COLD : BurnStage.FRESH;
        final float pctFromFirepitTemp = Math.min(1f, temperature / burnTemperature);
        if (pctFromFirepitTemp < 0.1f)
            return slot < SLOT_FUEL_3 ? BurnStage.COLD : BurnStage.FRESH;
        float lastMaxBurnTicks = this.lastMaxBurnTicks < 1f ? 1500f : this.lastMaxBurnTicks;
        final float pctFromBurnTicks = Math.min(1f, (float) burnTicks / lastMaxBurnTicks);
        if (slot == SLOT_FUEL_CONSUME)
        {
            return BurnStage.HEAT_STAGES[Math.round(pctFromFirepitTemp * 3)];
        }
        if (slot == SLOT_FUEL_2)
        {
            return BurnStage.HEAT_STAGES[Math.round(2 * pctFromFirepitTemp + pctFromBurnTicks)];
        }
        if (slot == SLOT_FUEL_3)
        {
            return BurnStage.HEAT_STAGES[Math.round(pctFromFirepitTemp + 2 * pctFromBurnTicks)];
        }
        if (slot == SLOT_FUEL_INPUT)
        {
            return BurnStage.HEAT_STAGES[Math.round(3 * pctFromBurnTicks)];
        }
        return BurnStage.FRESH;
    }

    public int getAsh()
    {
        return ash;
    }

    public void setAsh(int ash)
    {
        this.ash = ash;
    }

    public void addAsh(int ash)
    {
        this.ash = Math.min(8, ash);
    }

    @Override
    public void ejectInventory()
    {
        assert level != null;
        super.ejectInventory();
        if (ash > 0)
        {
            Helpers.spawnItem(level, worldPosition, new ItemStack(TFCItems.POWDERS.get(Powder.WOOD_ASH).get(), ash));
        }
    }

    public enum BurnStage
    {
        FRESH,
        DRIED,
        RED,
        WHITE,
        COLD;

        private static final BurnStage[] HEAT_STAGES = {FRESH, DRIED, RED, WHITE};
    }
}
