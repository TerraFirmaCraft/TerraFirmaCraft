/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public enum BaitType
{
    NONE,
    SMALL,
    LARGE;

    /**
     * Return the type given {@code bait} item obtained from a fishing rod. Note that this <strong>does not query a fishing rod!</strong>
     */
    public static BaitType getType(ItemStack bait)
    {
        if (!bait.isEmpty())
        {
            if (Helpers.isItem(bait, TFCTags.Items.SMALL_FISHING_BAIT))
            {
                return SMALL;
            }
            else if (Helpers.isItem(bait, TFCTags.Items.LARGE_FISHING_BAIT))
            {
                return LARGE;
            }
        }
        return NONE;
    }
}
