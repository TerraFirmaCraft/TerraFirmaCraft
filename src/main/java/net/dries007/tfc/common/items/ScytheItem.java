/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;

public class ScytheItem extends ToolItem implements CreativeMiningTool
{
    public ScytheItem(Tier tier, Properties properties)
    {
        super(tier, TFCTags.Blocks.MINEABLE_WITH_SCYTHE, properties);
    }

    @Override
    public void mineBlockInCreative(ItemStack stack, Level level, BlockState state, BlockPos pos, Player player)
    {
        doScytheMining(stack, level, state, pos, player);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos origin, LivingEntity entity)
    {
        doScytheMining(stack, level, state, origin, entity);
        return super.mineBlock(stack, level, state, origin, entity);
    }

    private void doScytheMining(ItemStack stack, Level level, BlockState state, BlockPos origin, LivingEntity entity)
    {
        if (entity instanceof ServerPlayer player)
        {
            for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-1, -1, -1), origin.offset(1, 1, 1)))
            {
                final BlockState stateAt = level.getBlockState(pos);
                if (!pos.equals(origin) && isCorrectToolForDrops(stack, stateAt))
                {
                    if (!player.isCreative())
                    {
                        Block.dropResources(stateAt, level, pos, stateAt.hasBlockEntity() ? level.getBlockEntity(pos) : null, player, player.getMainHandItem());
                    }

                    level.destroyBlock(pos, false, player); // we have to drop resources manually as breaking from the level means the tool is ignored
                }
            }
        }
    }
}
