/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * A permanently non-empty implementation of {@link RecipeInput} for recipes that don't use item stacks as inputs.
 */
public interface NonEmptyInput extends RecipeInput
{
    @Override
    default ItemStack getItem(int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    default int size()
    {
        return 0;
    }

    /**
     * This is used in two locations in vanilla to avoid recipe queries if the inventory is empty. In practice, we don't
     * really need to use this, so it is safe to assume the input is never empty (even if it may be).
     * @return {@code true} if the input is empty, and cannot match any possible recipe
     */
    @Override
    default boolean isEmpty()
    {
        return false;
    }
}