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
 * A partially exposed fluid handler, implementing the actual handler part of {@link SidedHandler} for {@link IFluidHandler}s
 * This allows either inserting or extracting to be allowed. Any other operations are denied.
 */
public class PartialFluidHandler implements DelegateFluidHandler
{
    private final IFluidHandler internal;
    private boolean insert, extract;

    public PartialFluidHandler(IFluidHandler internal)
    {
        this.internal = internal;
    }

    public PartialFluidHandler extract()
    {
        this.extract = true;
        return this;
    }

    public PartialFluidHandler insert()
    {
        this.insert = true;
        return this;
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
        return extract ? internal.drain(resource, action) : FluidStack.EMPTY;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        return extract ? internal.drain(maxDrain, action) : FluidStack.EMPTY;
    }
}
