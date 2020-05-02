/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids.capability;

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidHandlerSided implements IFluidHandler
{
    private final IFluidHandlerSidedCallback callback;
    private final IFluidHandler handler;
    private final EnumFacing side;

    public FluidHandlerSided(IFluidHandlerSidedCallback callback, IFluidHandler handler, EnumFacing side)
    {
        this.callback = callback;
        this.handler = handler;
        this.side = side;
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return handler.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (callback.canFill(resource, side))
        {
            return handler.fill(resource, doFill);
        }

        return 0;
    }

    @Override
    @Nullable
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (callback.canDrain(side))
        {
            return handler.drain(resource, doDrain);
        }

        return null;
    }

    @Override
    @Nullable
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (callback.canDrain(side))
        {
            return handler.drain(maxDrain, doDrain);
        }

        return null;
    }
}
