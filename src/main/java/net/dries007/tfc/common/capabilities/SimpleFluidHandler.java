/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A simplification of {@link IFluidHandler} for fluid containers which don't have internal 'tanks'.
 */
public interface SimpleFluidHandler extends IFluidHandler
{
    @Override
    default int getTanks()
    {
        return 1;
    }

    @NotNull
    @Override
    default FluidStack drain(FluidStack resource, FluidAction action)
    {
        // Can only drain what is contained.
        if (resource.getFluid() == getFluidInTank(0).getFluid())
        {
            return drain(resource.getAmount(), action);
        }
        return FluidStack.EMPTY;
    }

    default FluidStack getFluidInTank()
    {
        return getFluidInTank(0);
    }
}
