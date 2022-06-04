/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import java.util.function.Predicate;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class SnitchingFluidTank extends FluidTank
{
    private final FluidTankCallback callback;

    public SnitchingFluidTank(int capacity, FluidTankCallback callback)
    {
        super(capacity);
        this.callback = callback;
    }

    public SnitchingFluidTank(int capacity, Predicate<FluidStack> validator, FluidTankCallback callback)
    {
        super(capacity, validator);
        this.callback = callback;
    }

    @Override
    protected void onContentsChanged()
    {
        callback.fluidTankChanged(this);
    }
}
