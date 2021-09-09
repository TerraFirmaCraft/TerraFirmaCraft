/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class JavelinItem extends SwordItem
{
    public JavelinItem(Tier tier, float attackDamage, float attackSpeed, Properties properties)
    {
        super(tier, (int) ToolItem.calculateVanillaAttackDamage(attackDamage, tier), attackSpeed, properties);
    }

    // todo implement throwing
}