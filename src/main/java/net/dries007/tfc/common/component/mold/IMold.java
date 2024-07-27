/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.mold;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.TFCCapabilities;
import net.dries007.tfc.common.component.heat.IHeat;

/**
 * A capability that is returned by items implementing a fluid and a heat handler.
 */
public interface IMold extends IFluidHandlerItem, IHeat
{
    @Nullable
    static IMold get(ItemStack stack)
    {
        return stack.getCapability(TFCCapabilities.MOLD);
    }

    /**
     * @return {@code true} if the content of the mold is liquid, i.e. not solidified and can be drained.
     */
    boolean isMolten();

    /**
     * Default implementation which does not allow draining if the internal fluid is not molten
     */
    @Override
    default FluidStack drain(int maxDrain, FluidAction action)
    {
        return isMolten() ? drainIgnoringTemperature(maxDrain, action) : FluidStack.EMPTY;
    }

    /**
     * Like {@link IFluidHandlerItem#drain(int, FluidAction)}, but ignores the effect of {@link #isMolten()}
     * This will unconditionally drain either solid or liquid metal, use with care.
     */
    FluidStack drainIgnoringTemperature(int maxDrain, FluidAction action);
}
