/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.items.CandleBlockItem;
import net.dries007.tfc.common.player.IPlayerInfo;

public class TFCCakeBlock extends CakeBlock
{
    public static InteractionResult eatCake(Level level, BlockPos pos, BlockState state, Player player)
    {
        if (!player.canEat(false))
        {
            return InteractionResult.PASS;
        }
        else
        {
            IPlayerInfo.get(player).eat(FoodData.CAKE);
            player.awardStat(Stats.EAT_CAKE_SLICE);
            level.gameEvent(player, GameEvent.EAT, pos);

            final int bites = state.getValue(BITES);
            if (bites < 6)
            {
                level.setBlock(pos, state.setValue(BITES, bites + 1), 3);
            }
            else
            {
                level.removeBlock(pos, false);
                level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            }
            return InteractionResult.SUCCESS;
        }
    }

    public TFCCakeBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (player.getItemInHand(hand).getItem() instanceof CandleBlockItem)
        {
            return InteractionResult.PASS;
        }
        if (level.isClientSide)
        {
            if (eatCake(level, pos, state, player).consumesAction())
            {
                return InteractionResult.SUCCESS;
            }
            if (player.getItemInHand(hand).isEmpty())
            {
                return InteractionResult.CONSUME;
            }
        }
        return eatCake(level, pos, state, player);
    }
}
