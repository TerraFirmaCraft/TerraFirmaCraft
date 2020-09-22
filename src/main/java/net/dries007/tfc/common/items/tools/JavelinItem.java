/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.items.tools;

import net.minecraft.item.IItemTier;

import net.minecraft.item.Item.Properties;

public class JavelinItem extends WeaponItem
{
    public JavelinItem(IItemTier tier, float attackDamageMultiplier, float attackSpeed, Properties builder)
    {
        super(tier, attackDamageMultiplier, attackSpeed, builder);
    }

    // todo implement throwing
}