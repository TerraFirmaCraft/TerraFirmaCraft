/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import java.util.function.Predicate;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class InventoryFluidTank extends FluidTank
{
    private final FluidTankCallback callback;

    public InventoryFluidTank(int capacity, FluidTankCallback callback)
    {
        super(capacity);
        this.callback = callback;
    }

    public InventoryFluidTank(int capacity, Predicate<FluidStack> validator, FluidTankCallback callback)
    {
        super(capacity, validator);
        this.callback = callback;
    }

    @Override
    protected void onContentsChanged()
    {
        callback.fluidTankChanged();
    }
}
