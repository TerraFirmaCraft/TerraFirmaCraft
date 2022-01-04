package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;

public class ScytheItem extends ToolItem
{
    public ScytheItem(Tier tier, float attackDamage, float attackSpeed, Tag<Block> mineableBlocks, Properties properties)
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
                if (!pos.equals(origin) && isCorrectToolForDrops(stack, level.getBlockState(pos)))
                {
                    Helpers.quickHarvest(level, player, pos);
                }
            }
        }
        return super.mineBlock(stack, level, state, origin, entity);
    }
}
