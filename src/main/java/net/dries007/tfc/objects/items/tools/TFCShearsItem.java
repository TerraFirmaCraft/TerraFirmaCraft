/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.tools;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ShearsItem;

/**
 * Extends vanilla shears to add durability
 */
public class TFCShearsItem extends ShearsItem
{
    public TFCShearsItem(IItemTier tier, Properties builder)
    {
        super(builder.defaultMaxDamage(tier.getMaxUses()));
    }

    // todo implement
}
