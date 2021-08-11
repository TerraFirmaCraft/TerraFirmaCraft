/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;

public interface IRecipeDelegate<C extends Container> extends Recipe<C>
{
    Recipe<C> getDelegate();

    @Override
    default boolean matches(C inv, Level worldIn)
    {
        return getDelegate().matches(inv, worldIn);
    }

    @Override
    default ItemStack assemble(C inv)
    {
        return getDelegate().assemble(inv);
    }

    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return getDelegate().canCraftInDimensions(width, height);
    }

    @Override
    default ItemStack getResultItem()
    {
        return getDelegate().getResultItem();
    }

    @Override
    default NonNullList<ItemStack> getRemainingItems(C inv)
    {
        return getDelegate().getRemainingItems(inv);
    }

    @Override
    default NonNullList<Ingredient> getIngredients()
    {
        return getDelegate().getIngredients();
    }

    @Override
    default boolean isSpecial()
    {
        return getDelegate().isSpecial();
    }

    @Override
    default String getGroup()
    {
        return getDelegate().getGroup();
    }

    @Override
    default ItemStack getToastSymbol()
    {
        return getDelegate().getToastSymbol();
    }

    interface Shaped<C extends Container> extends IRecipeDelegate<C>, IShapedRecipe<C>
    {
        @Override
        IShapedRecipe<C> getDelegate();

        @Override
        default int getRecipeWidth()
        {
            return getDelegate().getRecipeWidth();
        }

        @Override
        default int getRecipeHeight()
        {
            return getDelegate().getRecipeHeight();
        }
    }
}
