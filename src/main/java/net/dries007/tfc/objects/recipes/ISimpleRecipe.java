/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

/**
 * A simple set of implementations for {@link IRecipe}, that skips some of the more unused methods for non-crafting uses.
 */
public interface ISimpleRecipe<C extends IInventory> extends IRecipe<C>
{
    @Override
    default ItemStack getCraftingResult(C inv)
    {
        return getRecipeOutput().copy();
    }

    @Override
    default boolean canFit(int width, int height)
    {
        return true;
    }
}
