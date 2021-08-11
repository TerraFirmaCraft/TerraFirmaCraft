/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items.tools;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ShearsItem;

import net.minecraft.world.item.Item.Properties;

/**
 * Extends vanilla shears to add durability
 */
public class TFCShearsItem extends ShearsItem
{
    public TFCShearsItem(Tier tier, Properties builder)
    {
        super(builder.defaultDurability(tier.getUses()));
    }

    // todo implement
}