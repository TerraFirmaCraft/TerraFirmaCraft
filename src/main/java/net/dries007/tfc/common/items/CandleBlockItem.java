/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;

public class CandleBlockItem extends BlockItem
{
    private final Supplier<? extends Block> candleCakeBlock;

    public CandleBlockItem(Properties properties, Block block, Supplier<? extends Block> candleCakeBlock)
    {
        super(block, properties);
        this.candleCakeBlock = candleCakeBlock;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = level.getBlockState(pos);
        final Player player = context.getPlayer();
        final ItemStack held = context.getItemInHand();
        if (Helpers.isBlock(state, TFCBlocks.CAKE.get()))
        {
            final Item item = held.getItem();
            if (state.hasProperty(CakeBlock.BITES) && state.getValue(CakeBlock.BITES) == 0)
            {
                if (player != null && !player.isCreative())
                {
                    held.shrink(1);
                    player.awardStat(Stats.ITEM_USED.get(item));
                }

                level.playSound(null, pos, SoundEvents.CAKE_ADD_CANDLE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.setBlockAndUpdate(pos, candleCakeBlock.get().defaultBlockState());
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.useOn(context);
    }
}
