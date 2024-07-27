/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.mold;

import java.util.List;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.CachedMut;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.fluid.FluidContainer;
import net.dries007.tfc.common.component.fluid.FluidContainerInfo;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.HeatContainer;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.component.item.ItemContainer;
import net.dries007.tfc.common.component.item.ItemContainerInfo;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.util.FluidAlloy;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.FluidHeat;

public class Vessel implements IMold, ItemContainer, FluidContainer, HeatContainer
{
    /**
     * Access a vessel specifically from a given stack. This internally queries that the container exposes itself as a mold. Prefer,
     * if possible, using {@link IMold#get(ItemStack)} instead as this only works for this specific implementation.
     */
    @Nullable
    public static Vessel get(ItemStack stack)
    {
        final @Nullable IMold mold = IMold.get(stack);
        return mold instanceof Vessel vessel ? vessel : null;
    }

    private final ItemStack stack;
    private final ContainerInfo containerInfo;
    private final IHeat heat;

    private VesselComponent vessel;

    public Vessel(ItemStack stack, ContainerInfo containerInfo)
    {
        this.stack = stack;
        this.containerInfo = containerInfo;
        this.vessel = Objects.requireNonNull(stack.get(TFCComponents.VESSEL));
        this.heat = HeatCapability.mutableView(stack);
    }

    @Override
    public List<ItemStack> contents()
    {
        return vessel.itemContent();
    }

    @Override
    public ContainerInfo containerInfo()
    {
        return containerInfo;
    }

    @Override
    public IHeat heatContainer()
    {
        return heat;
    }

    @Override
    public ItemStack getContainer()
    {
        return stack;
    }

    @Override
    public boolean isMolten()
    {
        final FluidStack result = vessel.fluidContent().getResult();
        final @Nullable FluidHeat fluidHeat = FluidHeat.get(result.getFluid());
        return !result.isEmpty() && fluidHeat != null && getTemperature() > fluidHeat.meltTemperature();
    }

    /**
     * @return {@code true} if we are in inventory manipulation mode, i.e. zero temperature, and no fluid content present.
     */
    public boolean isInventory()
    {
        return getTemperature() == 0f && vessel.fluidContent().isEmpty();
    }

    public boolean isEmpty()
    {
        return vessel.fluidContent().isEmpty() && Helpers.isEmpty(vessel.itemContent());
    }

    @Override
    public void setTemperature(float temperature)
    {
        heat.setTemperature(temperature);
        updateInventoryOnMelt();
    }

    @Override
    public FluidStack getFluidInTank(int tank)
    {
        return vessel.fluidContent().getResult();
    }

    @Override
    public int fill(FluidStack input, FluidAction action)
    {
        // We must allow filling when in any mode - otherwise the default 'empty' mode, which is inventory, would not
        // accept fluid, and we would have to rely on only interior melting to change the state (or heating).
        // As is, this will create states of 'fluid, with unmelted contents', which is perfectly acceptable
        if (action.simulate())
        {
            // Like with draining, simulation can occur without a copy
            return vessel.fluidContent().fill(input, action, containerInfo);
        }
        else
        {
            // Otherwise, we need to do a copy-execute-return pattern, like with draining
            final FluidAlloy newContent = vessel.fluidContent().copy();
            final int result = newContent.fill(input, action, containerInfo);
            stack.set(TFCComponents.VESSEL, vessel = vessel.with(newContent));
            return result;
        }
    }

    @Override
    public FluidStack drainIgnoringTemperature(int maxDrain, FluidAction action)
    {
        // Ignoring temperature, all we need to check is if we are not in inventory mode - then we can drain
        // If we are in inventory mode, we have an empty fluid, so there's no point (unlike filling) in executing there
        if (!isInventory())
        {
            if (action.simulate())
            {
                // If simulating, we can just simulate the drain on the underlying object
                return vessel.fluidContent().drain(maxDrain, action);
            }
            else
            {
                // Otherwise, we need to first copy the underlying fluid alloy (to not start performing mutable operations),
                // then execute a drain, and update the vessel component with the new alloy
                final FluidAlloy newContent = vessel.fluidContent().copy();
                final FluidStack result = newContent.drain(maxDrain, action);
                updateWith(vessel.with(newContent));
                return result;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack)
    {
        if (isInventory())
        {
            updateWith(vessel.with(slot, stack.copy()));
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        // Only allow insertion if the temperature is currently zero - this prevents issues where we may have to
        // update the heat and melting status after immediately adding items
        if (isInventory())
        {
            // First, copy the input and apply the preserved trait before inserting
            final ItemStack input = FoodCapability.applyTrait(stack.copy(), FoodTraits.PRESERVED.value());

            // Insert and compute the result plus the remainder. If not simulating, at this point make a copy
            // of the vessel, updating the item in the slot along with clearing the cache
            final var result = ItemComponent.insert(vessel.itemContent().get(slot), input, containerInfo);
            if (!simulate)
            {
                updateWith(vessel.with(slot, result.content()));
            }

            // Before returning the remainder stack, remove the preserved trait, to prevent it from leaking
            return FoodCapability.removeTrait(result.remainder(), FoodTraits.PRESERVED.value());
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (isInventory())
        {
            // Just extract if we are in inventory mode
            final var result = ItemComponent.extract(vessel.itemContent().get(slot), amount);
            if (!simulate)
            {
                updateWith(vessel.with(slot, result.content()));
            }

            // Before returning the extracted stack, remove the preserved trait, to prevent it from leaking
            return FoodCapability.removeTrait(result.extract(), FoodTraits.PRESERVED.value());
        }
        return ItemStack.EMPTY;
    }

    private void updateInventoryOnMelt()
    {
        @Nullable VesselComponent updated = null;
        for (int slot = 0; slot < VesselComponent.SLOTS; slot++)
        {
            final ItemStack stack = vessel.itemContent().get(slot);
            final @Nullable HeatingRecipe recipe = getCachedRecipe(slot);
            if (recipe != null && recipe.isValidTemperature(heat.getTemperature()))
            {
                // Melt item, add the contents to the alloy. Excess solids are placed into the inventory, more than can fit is voided.
                // At this point we know that the vessel is going to be modified, so we make a mutable copy for the period of this function
                if (updated == null) updated = vessel.copyMut();

                final ItemStack outputStack = recipe.assembleStacked(stack, containerInfo.slotCapacity());
                final FluidStack outputFluid = recipe.assembleFluid(stack);

                if (!outputFluid.isEmpty())
                {
                    outputFluid.setAmount(outputFluid.getAmount() * stack.getCount());
                }

                updated.itemContent().set(slot, outputStack); // Update the stack
                updated.cachedRecipes().get(slot).unload(); // Invalidate the cache
                updated.fluidContent().fill(outputFluid, FluidAction.EXECUTE, containerInfo); // And add the fluid to the alloy
            }
        }
        if (updated != null)
        {
            updateWith(updated);
        }
    }

    private void updateWith(VesselComponent vessel)
    {
        stack.set(TFCComponents.VESSEL, this.vessel = vessel);
        updateHeatCapacity();
    }

    /**
     * Updates the heat capacity of the (independent) heat component. Must be called on any modification to the vessel's content,
     * after modifications have been reflected in the current value of {@code vessel}
     */
    private void updateHeatCapacity()
    {
        float value = HeatCapability.POTTERY_HEAT_CAPACITY;
        float valueFromItems = 0;

        // Include any inventory items
        int count = 0;
        for (ItemStack stack : vessel.itemContent())
        {
            final @Nullable IHeat heat = HeatCapability.get(stack);
            if (heat != null)
            {
                count += stack.getCount();
                valueFromItems += heat.getHeatCapacity() * stack.getCount(); // heat capacity is always assumed to be stack size = 1, so we have to multiply here
            }
        }
        if (count > 0)
        {
            // Vessel has (item) contents
            // Instead of an ideal mixture, we weight slightly so that heating items in a vessel is more efficient than heating individually.
            value += valueFromItems * 0.7f + (valueFromItems / count) * 0.3f;
        }

        final FluidAlloy fluid = vessel.fluidContent();
        if (!fluid.isEmpty())
        {
            // Bias so that larger quantities of liquid cool faster (relative to a perfect mixture)
            value += FluidHeat.getOrUnknown(fluid.getResult()).heatCapacity(fluid.getAmount() * 0.7f);
        }

        heat.setHeatCapacity(value);
    }

    private @Nullable HeatingRecipe getCachedRecipe(int slot)
    {
        final CachedMut<HeatingRecipe> cache = vessel.cachedRecipes().get(slot);
        if (!cache.isLoaded()) cache.load(HeatingRecipe.getRecipe(vessel.itemContent().get(slot)));
        return cache.value();
    }

    public interface ContainerInfo extends ItemContainerInfo, FluidContainerInfo {}
}
