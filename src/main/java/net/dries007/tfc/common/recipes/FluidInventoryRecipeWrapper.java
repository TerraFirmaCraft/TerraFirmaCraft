/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class FluidInventoryRecipeWrapper extends RecipeWrapper
{
    protected FluidStack inputFluid;

    public FluidInventoryRecipeWrapper(ItemStackHandler inventory, FluidStack inputFluid)
    {
        super(inventory);
        this.inputFluid = inputFluid;
    }

    public FluidStack getInputFluid()
    {
        return inputFluid;
    }
}
