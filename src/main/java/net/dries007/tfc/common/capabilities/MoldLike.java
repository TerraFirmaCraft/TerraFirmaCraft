/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.recipes.input.NonEmptyInput;

/**
 * Interface for capabilities that are "like" molds. In that,
 * - They have both a internal fluid, and heat capability.
 * - They have an {@link #isMolten()}, which defines if the contents is able to be extracted (and should be checked before trying to extract!)
 *
 * Also extends {@link NonEmptyInput} in order to easily use it as a recipe query container.
 */
public interface MoldLike extends IFluidHandlerItem, NonEmptyInput
{
    @Nullable
    static MoldLike get(ItemStack stack)
    {
        return HeatCapability.get(stack) instanceof MoldLike v ? v : null;
    }

    /**
     * @return {@code true} if the fluid contents of the mold is molten.
     */
    boolean isMolten();

    /**
     * Like {@link IFluidHandlerItem#drain(int, FluidAction)}, but ignores the effect of {@link #isMolten()}
     * This will unconditionally drain either solid or liquid metal, use with care.
     */
    FluidStack drainIgnoringTemperature(int maxDrain, FluidAction action);
}
