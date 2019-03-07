/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class LockableFluidHandler implements IFluidHandler
{
    private boolean locked;
    private IFluidHandler handler;

    public LockableFluidHandler(IFluidHandler handler)
    {
        this.handler = handler;
    }

    public void setLockStatus(boolean locked)
    {
        this.locked = locked;
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return handler.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (locked)
        {
            return 0;
        }

        return handler.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (locked)
        {
            return null;
        }

        return handler.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (locked)
        {
            return null;
        }

        return handler.drain(maxDrain, doDrain);
    }
}
