/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.mold;

import java.util.Objects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.fluid.FluidComponent;
import net.dries007.tfc.common.component.fluid.FluidContainer;
import net.dries007.tfc.common.component.fluid.FluidContainerInfo;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.HeatContainer;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.util.data.FluidHeat;

public class Mold implements IMold, FluidContainer, HeatContainer
{
    private final ItemStack stack;
    private final FluidContainerInfo containerInfo;
    private final IHeat heat;
    private FluidComponent fluid;

    public Mold(ItemStack stack, FluidContainerInfo containerInfo)
    {
        this.stack = stack;
        this.containerInfo = containerInfo;
        this.heat = HeatCapability.mutableView(stack);
        this.fluid = Objects.requireNonNull(stack.get(TFCComponents.FLUID));
    }

    public boolean isMolten()
    {
        return heat.getTemperature() >= FluidHeat.getOrUnknown(fluid.content()).meltTemperature();
    }

    @Override
    public FluidStack getFluidInTank(int tank)
    {
        return fluid.content();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        final var remainder = fluid.fill(resource, containerInfo);
        if (action.execute()) updateFluid(remainder.content());
        return remainder.filled();
    }

    @Override
    public FluidStack drainIgnoringTemperature(int maxDrain, FluidAction action)
    {
        final var remainder = fluid.drain(maxDrain);
        if (action.execute()) updateFluid(remainder.content());
        return remainder.drained();
    }

    @Override
    public ItemStack getContainer()
    {
        return stack;
    }

    @Override
    public FluidContainerInfo containerInfo()
    {
        return containerInfo;
    }

    @Override
    public IHeat heatContainer()
    {
        return heat;
    }

    private void updateFluid(FluidStack fluid)
    {
        // Update the heat capacity of the heat component at the same time
        final FluidHeat heat = FluidHeat.getOrUnknown(fluid);
        final float heatCapacity = HeatCapability.POTTERY_HEAT_CAPACITY + heat.heatCapacity(fluid.getAmount());

        this.stack.set(TFCComponents.FLUID, this.fluid = new FluidComponent(fluid));
        this.heat.setHeatCapacity(heatCapacity);
    }
}
