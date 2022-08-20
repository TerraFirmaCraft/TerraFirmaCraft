/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.common.capabilities.*;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeatBlock;
import net.dries007.tfc.common.container.CrucibleContainer;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.*;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrucibleBlockEntity extends TickableInventoryBlockEntity<CrucibleBlockEntity.CrucibleInventory> implements ICalendarTickable
{
    public static final int SLOTS = 10;
    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_END = 8;
    public static final int SLOT_OUTPUT = 9;

    private static final Component NAME = Helpers.translatable("tfc.tile_entity.crucible");
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
        for (int i = SLOT_INPUT_START; i <= SLOT_INPUT_END; i++)
        {
            final int slot = i;
            final ItemStack inputStack = crucible.inventory.getStackInSlot(i);
            if (!inputStack.isEmpty())
            {
                inputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> {

                    // Always heat up the item regardless if it is melting or not
                    if (cap.getTemperature() < crucible.temperature)
                    {
                        HeatCapability.addTemp(cap, crucible.temperature, 2 + crucible.temperature * 0.0025f); // Breaks even at 400 C
                    }

                    final HeatingRecipe recipe = crucible.cachedRecipes[slot];
                    if (recipe != null && recipe.isValidTemperature(cap.getTemperature()))
                    {
                        // Convert input
                        final ItemStackInventory inventory = new ItemStackInventory(inputStack);
                        final ItemStack outputItem = recipe.assemble(inventory);
                        final FluidStack outputFluid = recipe.getOutputFluid();

                        // Output transformations
                        FoodCapability.applyTrait(outputItem, FoodTraits.BURNT_TO_A_CRISP);
                        outputItem.getCapability(HeatCapability.CAPABILITY).ifPresent(outputCap -> outputCap.setTemperature(crucible.temperature));

                        // Add output to crucible
                        crucible.inventory.setStackInSlot(slot, outputItem);
                        crucible.inventory.fill(outputFluid, IFluidHandler.FluidAction.EXECUTE);
                        crucible.markForSync();
                    }
                });
            }

            if (canFill)
            {
                // Re-query the input stack, as it may have changed.
                // Try to handle draining from a mold-like item
                final ItemStack drainStack = crucible.inventory.getStackInSlot(i);
                MoldLike mold = MoldLike.get(drainStack);
                if (mold != null && mold.isMolten())
                {
                    // Drain contents into the crucible
                    if (FluidHelpers.transferExact(mold, crucible.inventory, 1))
                    {
                        crucible.lastFillTicks = TFCConfig.SERVER.cruciblePouringRate.get();
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
            final ItemStack outputStack = crucible.inventory.getStackInSlot(SLOT_OUTPUT);
            final MoldLike mold = MoldLike.get(outputStack);
            if (mold != null)
            {
                FluidHelpers.transferExact(crucible.inventory, mold, 1);
                mold.setTemperatureIfWarmer(crucible.temperature);
                crucible.markForSync();
            }
        }
    }

    private final SidedHandler.Builder<IFluidHandler> sidedFluidInventory;
    private final SidedHandler.Noop<IHeatBlock> sidedHeat;
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

    public CrucibleBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.CRUCIBLE.get(), pos, state, CrucibleInventory::new, NAME);

        cachedRecipes = new HeatingRecipe[9];
        needsRecipeUpdate = true;
        temperature = targetTemperature = 0;
        lastFillTicks = 0;
        lastUpdateTick = 0;

        // Inputs in top, the output slot is accessed via the sides
        sidedInventory
            .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3, 4, 5, 6, 7, 8), Direction.UP)
            .on(new PartialItemHandler(inventory).insert(SLOT_OUTPUT).extract(SLOT_OUTPUT), Direction.Plane.HORIZONTAL);

        // Fluids go in the top and out the sides
        sidedFluidInventory = new SidedHandler.Builder<IFluidHandler>(inventory)
            .on(new PartialFluidHandler(inventory).insert(), Direction.UP)
            .on(new PartialFluidHandler(inventory).extract(), Direction.Plane.HORIZONTAL);

        // Heat can be accessed from all sides
        sidedHeat = new SidedHandler.Noop<>(inventory);

        syncableData = new IntArrayBuilder()
            .add(() -> (int) temperature, value -> temperature = value);
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
     * Gets a READ ONLY view of the alloy. Used for display / informational purposes only
     */
    public AlloyView getAlloy()
    {
        return inventory.alloy;
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
    public long getLastUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    public void setLastUpdateTick(long tick)
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
    public void loadAdditional(CompoundTag nbt)
    {
        temperature = nbt.getFloat("temperature");
        targetTemperature = nbt.getFloat("targetTemperature");
        targetTemperatureStabilityTicks = nbt.getInt("targetTemperatureStabilityTicks");
        needsRecipeUpdate = true;
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putFloat("temperature", temperature);
        nbt.putFloat("targetTemperature", targetTemperature);
        nbt.putInt("targetTemperatureStabilityTicks", targetTemperatureStabilityTicks);
        nbt.putBoolean("empty", Helpers.isEmpty(inventory) && inventory.alloy.isEmpty()); // We save this in order for the block item to efficiently check if the crucible is empty later
        super.saveAdditional(nbt);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == Capabilities.FLUID)
        {
            return sidedFluidInventory.getSidedHandler(side).cast();
        }
        if (cap == HeatCapability.BLOCK_CAPABILITY)
        {
            return sidedHeat.getSidedHandler(side).cast();
        }
        return super.getCapability(cap, side);
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

    private void updateCaches()
    {
        for (int slot = SLOT_INPUT_START; slot <= SLOT_INPUT_END; slot++)
        {
            cachedRecipes[slot] = HeatingRecipe.getRecipe(inventory.getStackInSlot(slot));
        }
    }

    static class CrucibleInventory implements DelegateItemHandler, SimpleFluidHandler, INBTSerializable<CompoundTag>, IHeatBlock
    {
        private final CrucibleBlockEntity crucible;

        private final InventoryItemHandler inventory;
        private final Alloy alloy;

        CrucibleInventory(InventoryBlockEntity<?> entity)
        {
            this.crucible = (CrucibleBlockEntity) entity;
            this.inventory = new InventoryItemHandler(entity, SLOTS);
            this.alloy = new Alloy(TFCConfig.SERVER.crucibleCapacity.get());
        }

        public boolean isMolten()
        {
            return crucible.temperature > alloy.getResult().getMeltTemperature();
        }

        @Override
        public IItemHandlerModifiable getItemHandler()
        {
            return inventory;
        }

        @Override
        public CompoundTag serializeNBT()
        {
            final CompoundTag nbt = new CompoundTag();
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("alloy", alloy.serializeNBT());
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            alloy.deserializeNBT(nbt.getCompound("alloy"));
        }

        @NotNull
        @Override
        public FluidStack getFluidInTank(int tank)
        {
            return alloy.getResultAsFluidStack();
        }

        @Override
        public int getTankCapacity(int tank)
        {
            return alloy.getMaxUnits();
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack)
        {
            return Metal.get(stack.getFluid()) != null;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            final Metal metal = Metal.get(resource.getFluid());
            if (metal != null)
            {
                return alloy.add(metal, resource.getAmount(), action.simulate());
            }
            return 0;
        }

        @NotNull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action)
        {
            if (isMolten())
            {
                final Metal result = alloy.getResult();
                final int amount = alloy.removeAlloy(maxDrain, action.simulate());
                if (action.execute())
                {
                    crucible.markForSync();
                }
                return new FluidStack(result.getFluid(), amount);
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
            crucible.targetTemperature = temperature;
            crucible.targetTemperatureStabilityTicks = TARGET_TEMPERATURE_STABILITY_TICKS;
            crucible.markForSync();
        }

        @Override
        public void setTemperatureIfWarmer(float temperature)
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
