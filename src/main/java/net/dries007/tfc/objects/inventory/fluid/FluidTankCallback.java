package net.dries007.tfc.objects.inventory.fluid;

import net.minecraftforge.fluids.FluidTank;

import net.dries007.tfc.objects.inventory.capability.IFluidTankCallback;

public class FluidTankCallback extends FluidTank
{
    private final IFluidTankCallback callback;

    public FluidTankCallback(IFluidTankCallback callback, int capacity)
    {
        super(capacity);
        this.callback = callback;
    }

    @Override
    protected void onContentsChanged()
    {
        callback.setAndUpdateFluidTank();
    }
}
