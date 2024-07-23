/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

/**
 * This interface simplifies {@link IFluidHandlerItem} for basic fluid containers, with a single internal tank, and delegates implementation
 * details to a {@link FluidContainerInfo}, which provides a validity and capacity check for the container.
 */
public interface FluidContainer extends IFluidHandlerItem
{
    FluidContainerInfo fluidContainerInfo();

    @Override
    default int getTanks()
    {
        return 1;
    }

    @Override
    default int getTankCapacity(int tank)
    {
        return fluidContainerInfo().fluidCapacity();
    }

    @Override
    default boolean isFluidValid(int tank, FluidStack stack)
    {
        return fluidContainerInfo().canContainFluid(stack);
    }

    @Override
    default FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, getFluidInTank(0))) return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }
}
