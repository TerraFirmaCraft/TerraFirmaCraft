/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items.tools;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ShearsItem;

/**
 * Extends vanilla shears to add durability
 */
public class TFCShearsItem extends ShearsItem
{
    public TFCShearsItem(IItemTier tier, Properties builder)
    {
        super(builder.defaultDurability(tier.getUses()));
    }

    // todo implement
}