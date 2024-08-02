/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.component.item.ItemComponent;


public final class Bowl
{
    public static final ItemComponent DISPLAY = of(new ItemStack(Items.BOWL)); // Used for display purposes, in getOrDefault()

    public static ItemComponent of(ItemStack stack)
    {
        return new ItemComponent(stack.copyWithCount(1));
    }

    public static ItemStack onItemUse(ItemComponent bowl, ItemStack original, ItemStack result, LivingEntity entity)
    {
        // This is a rare stackable-with-remainder-after-finished-using item
        // See: vanilla honey bottles
        if (entity instanceof ServerPlayer player)
        {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, original);
            player.awardStat(Stats.ITEM_USED.get(original.getItem()));
        }

        // Pull the bowl out first, before we shrink the stack in super.finishUsingItem()
        final ItemStack bowlStack = bowl.stack().copy();

        if (result.isEmpty())
        {
            return bowlStack;
        }
        else if (entity instanceof Player player && !player.getAbilities().instabuild)
        {
            // In non-creative, we still need to give the player an empty bowl, but we must also return the result here, as it is non-empty
            // The super() call to finishUsingItem will handle decrementing the stack - only in non-creative - for us already.
            ItemHandlerHelper.giveItemToPlayer(player, bowlStack);
        }
        return result;
    }
}
