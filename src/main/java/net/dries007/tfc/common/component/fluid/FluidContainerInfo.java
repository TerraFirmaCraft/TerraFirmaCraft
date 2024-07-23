/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.fluid;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidContainerInfo
{
    default boolean canContainFluid(FluidStack input)
    {
        return canContainFluid(input.getFluid());
    }

    boolean canContainFluid(Fluid input);

    int fluidCapacity();
}
