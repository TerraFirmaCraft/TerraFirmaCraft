/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

/**
 * A simple set of implementations for {@link IRecipe}, that skips some of the more unused methods for non-crafting uses.
 */
public interface ISimpleRecipe<C extends IInventory> extends IRecipe<C>
{
    @Override
    default ItemStack assemble(C inv)
    {
        return getResultItem().copy();
    }

    @Override
    default boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    /**
     * This is overridden by default for our recipes as vanilla only supports it's own recipe types in the recipe book anyway.
     * There have been forge PRs to try and add support to this, but frankly, nobody cares.
     * This then prevents "Unknown recipe category" log spam for every recipe in {@link net.minecraft.client.util.ClientRecipeBook#categorizeAndGroupRecipes(Iterable)}
     */
    @Override
    default boolean isSpecial()
    {
        return true;
    }
}