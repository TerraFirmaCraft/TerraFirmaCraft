/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.size;

import net.minecraft.world.item.ItemStack;

/**
 * Size and weight data attached to every item via {@link ItemSizeManager}
 * If a specific matching size/weight combination is not defined, it will try and infer ones for each item
 *
 * TFC will also attempt, to the best of it's ability, to mutate individual item's stack size limit to match up to the stack sizes imposed by {@link Weight}.
 * This will NOT be based on the stack, despite the ability for an item's size and/or weight to change based on the stack. For example: large vessels.
 */
public interface IItemSize
{
    Size getSize(ItemStack stack);

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
