/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

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
    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack)
    {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0)
        {
            if (worldIn.dimensionType().ultraWarm())
            {
                worldIn.removeBlock(pos, false);
                return;
            }

            Material material = worldIn.getBlockState(pos.below()).getMaterial();
            if (material.blocksMotion() || material.isLiquid())
            {
                worldIn.setBlockAndUpdate(pos, TFCBlocks.SALT_WATER.get().defaultBlockState());
            }
        }
    }

    @Override
    protected void melt(BlockState state, Level worldIn, BlockPos pos)
    {
        if (worldIn.dimensionType().ultraWarm())
        {
            worldIn.removeBlock(pos, false);
        }
        else
        {
            // Use salt water here
            worldIn.setBlockAndUpdate(pos, TFCBlocks.SALT_WATER.get().defaultBlockState());
            worldIn.neighborChanged(pos, TFCBlocks.SALT_WATER.get(), pos);
        }
    }
}
