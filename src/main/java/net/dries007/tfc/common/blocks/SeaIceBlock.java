/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;

public class SeaIceBlock extends IceBlock
{
    public SeaIceBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance)
    {
        if (fallDistance > 5)
        {
            melt(state, level, pos);
            level.addDestroyBlockEffect(pos, state);
            Helpers.playSound(level, pos, SoundEvents.GLASS_BREAK);
            for (BlockPos testPos : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 0, 1)))
            {
                if (level.getBlockState(testPos).getBlock() instanceof SeaIceBlock)
                {
                    melt(state, level, testPos);
                    level.addDestroyBlockEffect(testPos, state);
                }
            }
        }
        else
        {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }

    /**
     * Override to change a reference to water to salt water
     */
    @Override
    @SuppressWarnings("deprecation")
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack)
    {
        super.playerDestroy(level, player, pos, state, te, stack);
        if (EnchantmentHelper.hasTag(stack, EnchantmentTags.PREVENTS_ICE_MELTING))
        {
            if (level.dimensionType().ultraWarm())
            {
                level.removeBlock(pos, false);
                return;
            }

            final BlockState belowState = level.getBlockState(pos.below());
            if (belowState.blocksMotion() || belowState.liquid())
            {
                level.setBlockAndUpdate(pos, TFCBlocks.SALT_WATER.get().defaultBlockState());
            }
        }
    }

    @Override
    protected void melt(BlockState state, Level level, BlockPos pos)
    {
        if (level.dimensionType().ultraWarm())
        {
            level.removeBlock(pos, false);
        }
        else
        {
            // Use salt water here
            level.setBlockAndUpdate(pos, TFCBlocks.SALT_WATER.get().defaultBlockState());
            level.neighborChanged(pos, TFCBlocks.SALT_WATER.get(), pos);
        }
    }
}
