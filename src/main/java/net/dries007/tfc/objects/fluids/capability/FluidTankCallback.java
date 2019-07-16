package net.dries007.tfc.objects.fluids.capability;

import net.minecraftforge.fluids.FluidTank;

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
