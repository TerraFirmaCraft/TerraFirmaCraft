/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.jei;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

/**
 * Wraps recipe ingredients and outputs for JEI
 */
public interface IJEIRecipeWrapper
{
    /**
     * Returns a list of Item Ingredients for JEI
     *
     * @return NonNullList with ItemStack IIngredients
     */
    default NonNullList<IIngredient<ItemStack>> getItemIngredients()
    {
        return NonNullList.create();
    }

    /**
     * Returns a list of Fluid Ingredients for JEI
     *
     * @return NonNullList with FluidStack IIngredients
     */
    default NonNullList<IIngredient<FluidStack>> getFluidIngredients()
    {
        return NonNullList.create();
    }

    /**
     * Returns a list of Item Outputs
     *
     * @return NonNullList with ItemStacks
     */
    default NonNullList<ItemStack> getItemOutputs()
    {
        return NonNullList.create();
    }

    /**
     * Returns a list of Fluid Outputs
     *
     * @return NonNullList with FluidStacks
     */
    default NonNullList<FluidStack> getFluidOutputs()
    {
        return NonNullList.create();
    }
}
