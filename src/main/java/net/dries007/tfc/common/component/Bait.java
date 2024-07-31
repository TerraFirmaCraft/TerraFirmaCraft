/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.component.item.ItemComponent;

public final class Bait
{
    public static final ItemComponent EMPTY = ItemComponent.EMPTY;

    /**
     * @return The bait currently attached to this {@code fishingRod}, or {@link ItemStack#EMPTY} if no bait exists
     */
    public static ItemStack getBait(ItemStack fishingRod)
    {
        return fishingRod.getOrDefault(TFCComponents.BAIT, EMPTY).stack();
    }

    /**
     * Attaches the {@code bait} to the {@code fishingRod} item.
     */
    public static void setBait(ItemStack fishingRod, ItemStack bait)
    {
        fishingRod.set(TFCComponents.BAIT, new ItemComponent(bait.copyWithCount(1)));
    }
}
