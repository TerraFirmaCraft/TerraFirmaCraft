/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0)
        {
            if (worldIn.getDimensionType().isUltrawarm())
            {
                worldIn.removeBlock(pos, false);
                return;
            }

            Material material = worldIn.getBlockState(pos.down()).getMaterial();
            if (material.blocksMovement() || material.isLiquid())
            {
                worldIn.setBlockState(pos, TFCBlocks.SALT_WATER.get().getDefaultState());
            }
        }
    }

    @Override
    protected void turnIntoWater(BlockState state, World worldIn, BlockPos pos)
    {
        if (worldIn.getDimensionType().isUltrawarm())
        {
            worldIn.removeBlock(pos, false);
        }
        else
        {
            // Use salt water here
            worldIn.setBlockState(pos, TFCBlocks.SALT_WATER.get().getDefaultState());
            worldIn.neighborChanged(pos, TFCBlocks.SALT_WATER.get(), pos);
        }
    }
}
