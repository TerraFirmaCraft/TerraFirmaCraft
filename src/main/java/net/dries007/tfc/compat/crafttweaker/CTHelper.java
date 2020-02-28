/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.crafttweaker;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IngredientStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public final class CTHelper
{
    @Nonnull
    public static IIngredient getInternalIngredient(@Nonnull crafttweaker.api.item.IIngredient ingredient)
    {
        int amount = ingredient.getAmount();
        if (ingredient instanceof IngredientStack)
        {
            IngredientStack is = (IngredientStack) ingredient;
            ingredient = (crafttweaker.api.item.IIngredient) is.getInternal();
        }
        if (ingredient instanceof IOreDictEntry)
        {
            return IIngredient.of(((IOreDictEntry) ingredient).getName(), amount);
        }
        else if (ingredient instanceof IItemStack)
        {
            ItemStack st = (ItemStack) ingredient.getInternal();
            st.setCount(amount);
            return IIngredient.of(st);
        }
        else if (ingredient instanceof ILiquidStack)
        {
            return IIngredient.of((FluidStack) ingredient.getInternal());
        }
        throw new IllegalArgumentException("Ingredient is not IOreDictEntry, IItemStack or ILiquidStack");
    }
}
