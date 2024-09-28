/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.size;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * Represents a {@link Size} and {@link Weight} that can be attached to items. Item sizes can be sourced from several places, in order:
 * <ol>
 *     <li>An {@link Item} that implements {@link IItemSize} directly</li>
 *     <li>An {@link BlockItem} where the corresponding {@link Block} implements {@link IItemSize} directly</li>
 *     <li>Any matching {@code item_size} that is loaded from JSON, that matches the given item</li>
 *     <li>A default size/weight combination based on a very simple heuristic using different classes of items</li>
 * </ol>
 * TFC will attempt to override the constant stack size of each item stack, using the value returned by {@link #getDefaultStackSize(ItemStack)}.
 * Containers are responsible for determining if items can fit in the container by checking the size.
 */
public interface IItemSize
{
    /**
     * @return the size of this {@code stack}, determining what size containers this item can be placed within.
     */
    Size getSize(ItemStack stack);

    /**
     * @return the weight of this {@code stack}, determining the stack size of this item.
     */
    Weight getWeight(ItemStack stack);

    /**
     * Get the stack size of this item.
     * This may be inconsistent for implementations that want to modify their weight in order to selectively trigger overburdening, but want to have a consistent stack size.
     */
    default int getDefaultStackSize(ItemStack stack)
    {
        return getWeight(stack).stackSize;
    }
}
