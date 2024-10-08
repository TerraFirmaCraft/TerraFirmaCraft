/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

/**
 * This is a type of {@link ICustomIngredient} which represents a precise condition that has non-simple behavior, and wants to modify displayed stacks
 * to convey additional information about the ingredient.
 *
 * @see AndIngredient
 */
public interface PreciseIngredient extends ICustomIngredient
{
    @Override
    default Stream<ItemStack> getItems()
    {
        // By default, we accept every item, once modified through this ingredient.
        // This should only be used rarely, when we have a top-level precise ingredient which actually accepts all items
        return BuiltInRegistries.ITEM.stream()
            .map(Item::getDefaultInstance)
            .filter(e -> !e.isEmpty())
            .map(this::modifyStackForDisplay);
    }

    @Override
    default boolean isSimple()
    {
        return false;
    }

    /**
     * Modifies the given stack for display purposes. This consists of item stacks returned by the parent {@link AndIngredient}'s {@link #getItems()},
     * and is used over invoking each sub-ingredient and merging their output items.
     * @return The modified stack.
     */
    default ItemStack modifyStackForDisplay(ItemStack stack)
    {
        return stack;
    }
}
