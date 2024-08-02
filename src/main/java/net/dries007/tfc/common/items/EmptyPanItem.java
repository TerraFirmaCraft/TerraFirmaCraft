/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;


import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.util.data.Deposit;

public class EmptyPanItem extends Item
{
    public EmptyPanItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Player player = context.getPlayer();
        if (player != null)
        {
            final Level level = context.getLevel();
            final BlockPos pos = context.getClickedPos();
            final ItemStack stack = player.getItemInHand(context.getHand());
            final ItemStack depositStack = new ItemStack(level.getBlockState(pos).getBlock());
            if (Deposit.get(depositStack) != null)
            {
                if (level.isClientSide) return InteractionResult.SUCCESS;

                level.destroyBlock(pos, false, player);
                if (!player.isCreative()) stack.shrink(1);
                player.awardStat(Stats.ITEM_USED.get(this));

                final ItemStack putStack = new ItemStack(TFCItems.FILLED_PAN.get());
                putStack.set(TFCComponents.DEPOSIT, new ItemComponent(depositStack));
                if (!player.getInventory().add(putStack)) player.drop(putStack, false);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }
}
