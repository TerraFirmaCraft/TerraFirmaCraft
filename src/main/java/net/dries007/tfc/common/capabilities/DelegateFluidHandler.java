/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * A delegate interface for {@link IFluidHandler}
 */
public interface DelegateFluidHandler extends IFluidHandler
{
    IFluidHandler getFluidHandler();

    @Override
    default int getTanks()
    {
        return getFluidHandler().getTanks();
    }

    @Nonnull
    @Override
    default FluidStack getFluidInTank(int tank)
    {
        return getFluidHandler().getFluidInTank(tank);
    }

    @Override
    default int getTankCapacity(int tank)
    {
        return getFluidHandler().getTankCapacity(tank);
    }

    @Override
    default boolean isFluidValid(int tank, @Nonnull FluidStack stack)
    {
        return getFluidHandler().isFluidValid(tank, stack);
    }

    @Override
    default int fill(FluidStack resource, FluidAction action)
    {
        return getFluidHandler().fill(resource, action);
    }

    @Nonnull
    @Override
    default FluidStack drain(FluidStack resource, FluidAction action)
    {
        return getFluidHandler().drain(resource, action);
    }

    @Nonnull
    @Override
    default FluidStack drain(int maxDrain, FluidAction action)
    {
        return getFluidHandler().drain(maxDrain, action);
    }
}
