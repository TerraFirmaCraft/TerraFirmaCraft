/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.dries007.tfc.util.LegacyMaterials;

public class SeaIceBlock extends IceBlock
{
    public SeaIceBlock(Properties properties)
    {
        super(properties);
    }

    /**
     * Override to change a reference to water to salt water
     */
    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack)
    {
        super.playerDestroy(level, player, pos, state, te, stack);
        if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0)
        {
            if (level.dimensionType().ultraWarm())
            {
                level.removeBlock(pos, false);
                return;
            }

            final BlockState belowState = level.getBlockState(pos.below());
            if (LegacyMaterials.blocksMotion(belowState) || LegacyMaterials.isLiquid(belowState))
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
