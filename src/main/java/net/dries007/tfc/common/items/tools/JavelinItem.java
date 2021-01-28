/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items.tools;

import net.minecraft.item.IItemTier;

public class JavelinItem extends WeaponItem
{
    public JavelinItem(IItemTier tier, float attackDamageMultiplier, float attackSpeed, Properties builder)
    {
        super(tier, attackDamageMultiplier, attackSpeed, builder);
    }

    // todo implement throwing
}