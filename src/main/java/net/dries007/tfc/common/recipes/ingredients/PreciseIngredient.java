/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

/**
 * This is a type of {@link ICustomIngredient} which represents a precise condition that is intended to only
 * be used in conjunction with an intersection ingredient, and as such makes some compromises:
 * <ul>
 *     <li>{@link #getItems()} is overridden to return a single, nonsensical item (to prevent from being seen as empty), even though the ingredient *technically* accepts any items</li>
 *     <li>{@link #isSimple()} is obviously {@code false}</li>
 * </ul>
 * Note that for most precise ingredients, they don't function correctly w.r.t {@link #getItems()} showing all possible available items. The solution
 * for ingredients like heat, or food, is to use {@link AndIngredient} which uses {@link #modifyStackForDisplay(ItemStack)} to produce "display" stacks,
 * based on a primary ingredient, plus additional custom ingredients.
 */
public interface PreciseIngredient extends ICustomIngredient
{
    @Override
    default Stream<ItemStack> getItems()
    {
        return Stream.of(ItemStack.EMPTY);
    }

    @Override
    default boolean isSimple()
    {
        return false;
    }

    default ItemStack modifyStackForDisplay(ItemStack stack)
    {
        return stack;
    }
}
