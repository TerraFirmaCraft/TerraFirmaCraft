/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class MaceItem extends SwordItem
{
    public MaceItem(Tier tier, Properties properties)
    {
        super(tier, properties);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility toolAction)
    {
        return super.canPerformAction(stack, toolAction) && toolAction != ItemAbilities.SWORD_SWEEP;
    }
}
