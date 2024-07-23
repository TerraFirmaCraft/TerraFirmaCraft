/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.DelegateItemHandler;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.PartialFluidHandler;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.capabilities.SimpleFluidHandler;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.component.heat.IHeatConsumer;
import net.dries007.tfc.common.component.mold.IMold;
import net.dries007.tfc.common.container.CrucibleContainer;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.FluidAlloy;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IntArrayBuilder;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import net.dries007.tfc.util.data.FluidHeat;

public class CrucibleBlockEntity extends TickableInventoryBlockEntity<CrucibleBlockEntity.CrucibleInventory> implements ICalendarTickable
{
    public static final int SLOTS = 10;
    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_END = 8;
    public static final int SLOT_OUTPUT = 9;

    private static final Component NAME = Component.translatable("tfc.tile_entity.crucible");
    private static final int TARGET_TEMPERATURE_STABILITY_TICKS = 5;

    public static void serverTick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible)
    {
        crucible.checkForLastTickSync();
        crucible.checkForCalendarUpdate();

        if (crucible.needsRecipeUpdate)
        {
            crucible.needsRecipeUpdate = false;
            crucible.updateCaches();
        }

        if (crucible.temperature != crucible.targetTemperature)
        {
            crucible.temperature = HeatCapability.adjustTempTowards(crucible.temperature, crucible.targetTemperature);
        }
        if (crucible.targetTemperatureStabilityTicks > 0)
        {
            crucible.targetTemperatureStabilityTicks--;
        }
        if (crucible.targetTemperature > 0 && crucible.targetTemperatureStabilityTicks == 0)
        {
            // Crucible target temperature decays constantly, since it is set externally. As long as we don't consider ourselves 'stable' (received a external setTemperature() call within the last 5 ticks.
            crucible.targetTemperature = HeatCapability.adjustTempTowards(crucible.targetTemperature, 0);
        }

        // Input draining
        boolean canFill = crucible.lastFillTicks <= 0;
        for (int slot = SLOT_INPUT_START; slot <= SLOT_INPUT_END; slot++)
        {
            final ItemStack inputStack = crucible.inventory.getStackInSlot(slot);
            if (!inputStack.isEmpty())
            {
                final @Nullable IHeat inputHeat = HeatCapability.get(inputStack);
                if (inputHeat != null)
                {
                    // Always heat up the item regardless if it is melting or not
                    HeatCapability.addTemp(inputHeat, crucible.temperature, 2 + crucible.temperature * 0.0025f); // Breaks even at 400 C

                    final HeatingRecipe recipe = crucible.cachedRecipes[slot];
                    if (recipe != null && recipe.isValidTemperature(inputHeat.getTemperature()))
                    {
                        // Convert input
                        final ItemStack outputItem = recipe.assembleItem(inputStack);
                        final FluidStack outputFluid = recipe.assembleFluid(inputStack);

                        // Output transformations
                        FoodCapability.applyTrait(outputItem, FoodTraits.BURNT_TO_A_CRISP.value());
                        HeatCapability.setTemperature(outputItem, crucible.temperature);

                        // Add output to crucible
                        crucible.inventory.setStackInSlot(slot, outputItem);
                        crucible.inventory.fill(outputFluid, IFluidHandler.FluidAction.EXECUTE);
                        crucible.markForSync();
                    }
                }
            }

            if (canFill)
            {
                // Re-query the input stack, as it may have changed.
                // Try to handle draining from a mold-like item
                final ItemStack drainStack = crucible.inventory.getStackInSlot(slot);
                IMold mold = IMold.get(drainStack);
                if (mold != null && mold.isMolten())
                {
                    // Drain contents into the crucible
                    if (FluidHelpers.transferExact(mold, crucible.inventory, 1))
                    {
                        if (crucible.fastPourTicks >= 0 && crucible.fastPourSlot == slot)
                        {
                            crucible.fastPourTicks--;
                            crucible.lastFillTicks = TFCConfig.SERVER.crucibleFastPouringRate.get();
                        }
                        else
                        {
                            crucible.lastFillTicks = TFCConfig.SERVER.cruciblePouringRate.get();
                        }
                        crucible.markForSync();
                    }
                }
            }
        }

        if (crucible.lastFillTicks > 0)
        {
            crucible.lastFillTicks--;
        }

        // Fill output
        if (crucible.inventory.isMolten())
        {
            final FluidStack outputDrop = crucible.inventory.drain(1, IFluidHandler.FluidAction.SIMULATE);
            final FluidStack outputRemainder = Helpers.mergeOutputFluidIntoSlot(crucible.inventory, outputDrop, crucible.temperature, SLOT_OUTPUT);
            if (outputRemainder.isEmpty())
            {
                // Remainder was emptied, so do the extraction for real
                crucible.inventory.drain(1, IFluidHandler.FluidAction.EXECUTE);
            }
            crucible.markForSync();
        }
    }

    private final SidedHandler.Builder<IFluidHandler> sidedFluidInventory;
    private final SidedHandler.Noop<IHeatConsumer> sidedHeat;
    private final IntArrayBuilder syncableData;

    private final HeatingRecipe[] cachedRecipes;
    private float temperature;
    private float targetTemperature;
    private boolean needsRecipeUpdate;

    /**
     * Prevent the target temperature from "hovering" around a particular value.
     * Effectively means that setTemperature() sets for the next 5 ticks, before it starts to decay naturally.
     */
    private int targetTemperatureStabilityTicks;
    private int lastFillTicks;
    private long lastUpdateTick; // for ICalendarTickable
    private int fastPourSlot;
    private int fastPourTicks;

    public CrucibleBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.CRUCIBLE.get(), pos, state, CrucibleInventory::new, NAME);

        cachedRecipes = new HeatingRecipe[9];
        needsRecipeUpdate = true;
        temperature = targetTemperature = 0;
        lastFillTicks = fastPourTicks = fastPourSlot = 0;
        lastUpdateTick = Integer.MIN_VALUE;

        sidedFluidInventory = new SidedHandler.Builder<>(inventory);

        // Inputs in top, the output slot is accessed via the sides
        if (TFCConfig.SERVER.crucibleEnableAutomation.get())
        {
            sidedInventory
                .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3, 4, 5, 6, 7, 8), Direction.UP)
                .on(new PartialItemHandler(inventory).insert(SLOT_OUTPUT).extract(SLOT_OUTPUT), Direction.Plane.HORIZONTAL);

            // Fluids go in the top and out the sides
            sidedFluidInventory
                .on(new PartialFluidHandler(inventory).insert(), Direction.UP)
                .on(new PartialFluidHandler(inventory).extract(), Direction.Plane.HORIZONTAL);
        }

        // Heat can be accessed from all sides
        sidedHeat = new SidedHandler.Noop<>(inventory);

        syncableData = new IntArrayBuilder()
            .add(() -> (int) temperature, value -> temperature = value);
    }

    public float getTemperature()
    {
        return temperature;
    }

    public FluidAlloy getAlloy()
    {
        return inventory.alloy;
    }

    public FluidStack getAlloyResult()
    {
        assert level != null;
        return inventory.alloy.getResult(level);
    }

    public ContainerData getSyncableData()
    {
        return syncableData;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return HeatCapability.has(stack) && (slot != SLOT_OUTPUT || Helpers.mightHaveCapability(stack, Capabilities.FluidHandler.ITEM));
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;

        // Crucible has no fuel to consume, but it does drop the internal target and temperature over time.
        targetTemperature = HeatCapability.adjustTempTowards(targetTemperature, 0, ticks);
        temperature = HeatCapability.adjustTempTowards(temperature, targetTemperature, ticks);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    @Deprecated
    public long getLastCalendarUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    @Deprecated
    public void setLastCalendarUpdateTick(long tick)
    {
        lastUpdateTick = tick;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return CrucibleContainer.create(this, player.getInventory(), containerId);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        temperature = nbt.getFloat("temperature");
        targetTemperature = nbt.getFloat("targetTemperature");
        targetTemperatureStabilityTicks = nbt.getInt("targetTemperatureStabilityTicks");
        lastUpdateTick = nbt.getLong("lastUpdateTick");
        needsRecipeUpdate = true;
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putFloat("temperature", temperature);
        nbt.putFloat("targetTemperature", targetTemperature);
        nbt.putInt("targetTemperatureStabilityTicks", targetTemperatureStabilityTicks);
        nbt.putBoolean("empty", Helpers.isEmpty(inventory) && inventory.alloy.isEmpty()); // We save this in order for the block item to efficiently check if the crucible is empty later
        nbt.putLong("lastUpdateTick", lastUpdateTick);
        super.saveAdditional(nbt, provider);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        if (slot != SLOT_OUTPUT)
        {
            cachedRecipes[slot] = HeatingRecipe.getRecipe(inventory.getStackInSlot(slot));
        }
    }

    public void setFastPouring(int slot)
    {
        fastPourSlot = slot;
        fastPourTicks = 20;
    }

    private void updateCaches()
    {
        for (int slot = SLOT_INPUT_START; slot <= SLOT_INPUT_END; slot++)
        {
            cachedRecipes[slot] = HeatingRecipe.getRecipe(inventory.getStackInSlot(slot));
        }
    }

    static class CrucibleInventory implements DelegateItemHandler, SimpleFluidHandler, IHeatConsumer, INBTSerializable<CompoundTag>
    {
        private final CrucibleBlockEntity crucible;
        private final InventoryItemHandler inventory;
        private final FluidAlloy alloy;

        CrucibleInventory(InventoryBlockEntity<?> entity)
        {
            this.crucible = (CrucibleBlockEntity) entity;
            this.inventory = new InventoryItemHandler(entity, SLOTS);
            this.alloy = new FluidAlloy(TFCConfig.SERVER.crucibleCapacity.get());
        }

        public boolean isMolten()
        {
            assert crucible.level != null;
            final @Nullable FluidHeat metal = FluidHeat.get(alloy.getResult(crucible.level).getFluid());
            return metal == null || crucible.temperature > metal.meltTemperature();
        }

        @Override
        public IItemHandlerModifiable getItemHandler()
        {
            return inventory;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider)
        {
            final CompoundTag nbt = new CompoundTag();
            nbt.put("inventory", inventory.serializeNBT(provider));
            nbt.put("alloy", alloy.serializeNBT());
            return nbt;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt)
        {
            inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
            alloy.deserializeNBT(nbt.getCompound("alloy"));
        }

        @NotNull
        @Override
        public FluidStack getFluidInTank(int tank)
        {
            assert crucible.level != null;
            return alloy.getResult(crucible.level);
        }

        @Override
        public int getTankCapacity(int tank)
        {
            return alloy.getMaxAmount();
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack)
        {
            return FluidHeat.get(stack.getFluid()) != null;
        }

        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action)
        {
            return alloy.add(resource, action);
        }

        @NotNull
        @Override
        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action)
        {
            if (isMolten())
            {
                assert crucible.level != null;
                final FluidStack result = alloy.extract(crucible.level.getRecipeManager(), maxDrain, action);
                if (action.execute())
                {
                    crucible.markForSync();
                }
                return result;
            }
            return FluidStack.EMPTY;
        }

        @Override
        public float getTemperature()
        {
            return crucible.temperature;
        }

        @Override
        public void setTemperature(float temperature)
        {
            // Override to still cause an update to the stability ticks
            if (temperature >= crucible.temperature)
            {
                crucible.temperature = temperature;
                crucible.targetTemperatureStabilityTicks = TARGET_TEMPERATURE_STABILITY_TICKS;
                crucible.markForSync();
            }
        }
    }
}
