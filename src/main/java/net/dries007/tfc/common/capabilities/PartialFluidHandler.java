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
 * A partial implementation of an {@link IFluidHandler}, which either exposes insert or extract capabilities
 *
 * @see SidedHandler
 */
public final class PartialFluidHandler implements DelegateFluidHandler
{
    public static IFluidHandler extractOnly(IFluidHandler handler)
    {
        return new PartialFluidHandler(handler, false);
    }

    public static IFluidHandler insertOnly(IFluidHandler handler)
    {
        return new PartialFluidHandler(handler, true);
    }

    private final IFluidHandler internal;
    private final boolean insert;

    PartialFluidHandler(IFluidHandler internal, boolean insert)
    {
        this.internal = internal;
        this.insert = insert;
    }

    @Override
    public IFluidHandler getFluidHandler()
    {
        return internal;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return insert ? internal.fill(resource, action) : 0;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        return !insert ? internal.drain(resource, action) : FluidStack.EMPTY;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        return !insert ? internal.drain(maxDrain, action) : FluidStack.EMPTY;
    }
}
