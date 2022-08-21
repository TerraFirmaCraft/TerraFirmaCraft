/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;

/**
 * An un-writable, unmodifiable view of an alloy
 */
public interface AlloyView
{
    default Metal getResult()
    {
        return getResult(Helpers.getUnsafeRecipeManager());
    }

    default Metal getResult(Level level)
    {
        return getResult(level.getRecipeManager());
    }

    /**
     * Gets the result of mixing the alloy right now
     *
     * @return the result metal. Unknown if it doesn't match any recipe
     */
    Metal getResult(RecipeManager recipes);

    /**
     * Gets the total amount of alloy created
     *
     * @return The amount, rounded to the closest integer
     */
    int getAmount();

    /**
     * Gets the maximum amount this storage can hold
     *
     * @return The maximum amount
     */
    int getMaxUnits();

    /**
     * Returns a read-only copy of the metals in an alloy
     * The alloy may also contain values with a % content less than epsilon, which are not visible in this view
     *
     * @return a map of metals -> unit values
     */
    Object2DoubleMap<Metal> getMetals();

    default FluidStack getResultAsFluidStack()
    {
        if (!isEmpty())
        {
            return new FluidStack(getResult().getFluid(), getAmount());
        }
        return FluidStack.EMPTY;
    }

    default boolean isEmpty()
    {
        return getAmount() == 0;
    }
}
