/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public interface IRecipeDelegate<C extends IInventory> extends IRecipe<C>
{
    IRecipe<C> getInternal();

    @Override
    default boolean matches(C inv, World worldIn)
    {
        return getInternal().matches(inv, worldIn);
    }

    @Override
    default ItemStack assemble(C inv)
    {
        return getInternal().assemble(inv);
    }

    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return getInternal().canCraftInDimensions(width, height);
    }

    @Override
    default ItemStack getResultItem()
    {
        return getInternal().getResultItem();
    }

    @Override
    default NonNullList<ItemStack> getRemainingItems(C inv)
    {
        return getInternal().getRemainingItems(inv);
    }

    @Override
    default NonNullList<Ingredient> getIngredients()
    {
        return getInternal().getIngredients();
    }

    @Override
    default boolean isSpecial()
    {
        return getInternal().isSpecial();
    }

    @Override
    default String getGroup()
    {
        return getInternal().getGroup();
    }

    @Override
    default ItemStack getToastSymbol()
    {
        return getInternal().getToastSymbol();
    }
}
