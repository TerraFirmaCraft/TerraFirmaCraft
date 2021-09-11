/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

/**
 * A simple set of implementations for {@link Recipe}, that skips some of the more unused methods for non-crafting uses.
 */
public interface ISimpleRecipe<C extends Container> extends Recipe<C>
{
    @Override
    default ItemStack assemble(C inventory)
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
     * This then prevents "Unknown recipe category" log spam for every recipe in {@link net.minecraft.client.ClientRecipeBook}
     */
    @Override
    default boolean isSpecial()
    {
        return true;
    }
}