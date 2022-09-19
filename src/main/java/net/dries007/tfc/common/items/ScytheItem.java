/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ScytheItem extends ToolItem
{
    public ScytheItem(Tier tier, float attackDamage, float attackSpeed, TagKey<Block> mineableBlocks, Properties properties)
    {
        super(tier, attackDamage, attackSpeed, mineableBlocks, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos origin, LivingEntity entity)
    {
        if (entity instanceof ServerPlayer player)
        {
            for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-1, -1, -1), origin.offset(1, 1, 1)))
            {
                final BlockState stateAt = level.getBlockState(pos);
                if (!pos.equals(origin) && isCorrectToolForDrops(stack, stateAt))
                {
                    Block.dropResources(stateAt, level, pos, stateAt.hasBlockEntity() ? level.getBlockEntity(pos) : null, player, player.getMainHandItem());

                    level.destroyBlock(pos, false, player); // we have to drop resources manually as breaking from the level means the tool is ignored
                }
            }
        }
        return super.mineBlock(stack, level, state, origin, entity);
    }
}
