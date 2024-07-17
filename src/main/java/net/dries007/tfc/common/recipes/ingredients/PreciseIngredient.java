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
 * todo: future implement this
 * This means this ingredient lacks one major feature: to specify, via the {@link #getItems()} of the containing intersection ingredient,
 * how the underlying items will be look. In order to solve this, we should have a custom intersection ingredient that is TFC-aware,
 * and is able to query {@link PreciseIngredient}s and use them to make a proper view of the potential items.
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
