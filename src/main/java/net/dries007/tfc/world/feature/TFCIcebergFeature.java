/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.IcebergFeature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlocks;

/**
 * This is a modified version which overrides two functions which only handle vanilla water blocks
 * Both of these require AT's, for the override, and for the called methods
 */
public class TFCIcebergFeature extends IcebergFeature
{
    public TFCIcebergFeature(Codec<BlockStateFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public void carve(int int1_, int yDiff, BlockPos pos, IWorld worldIn, boolean placeWater, double double_, BlockPos pos1, int int2_, int int_)
    {
        int i = int1_ + 1 + int2_ / 3;
        int j = Math.min(int1_ - 3, 3) + int_ / 2 - 1;

        for (int k = -i; k < i; ++k)
        {
            for (int l = -i; l < i; ++l)
            {
                double d0 = this.signedDistanceEllipse(k, l, pos1, i, j, double_);
                if (d0 < 0.0D)
                {
                    BlockPos blockpos = pos.offset(k, yDiff, l);
                    Block block = worldIn.getBlockState(blockpos).getBlock();
                    if (this.isIcebergBlock(block) || block == Blocks.SNOW_BLOCK)
                    {
                        if (placeWater)
                        {
                            this.setBlock(worldIn, blockpos, TFCBlocks.SALT_WATER.get().defaultBlockState());
                        }
                        else
                        {
                            this.setBlock(worldIn, blockpos, Blocks.AIR.defaultBlockState());
                            this.removeFloatingSnowLayer(worldIn, blockpos);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setIcebergBlock(BlockPos pos, IWorld worldIn, Random random, int int_, int int1_, boolean boolean_, boolean boolean1_, BlockState state)
    {
        BlockState blockstate = worldIn.getBlockState(pos);
        if (blockstate.getMaterial() == Material.AIR || blockstate.is(Blocks.SNOW_BLOCK) || blockstate.is(Blocks.ICE) || blockstate.is(TFCBlocks.SALT_WATER.get()))
        {
            boolean flag = !boolean_ || random.nextDouble() > 0.05D;
            int i = boolean_ ? 3 : 2;
            if (boolean1_ && !blockstate.is(TFCBlocks.SALT_WATER.get()) && (double) int_ <= (double) random.nextInt(Math.max(1, int1_ / i)) + (double) int1_ * 0.6D && flag)
            {
                this.setBlock(worldIn, pos, Blocks.SNOW_BLOCK.defaultBlockState());
            }
            else
            {
                this.setBlock(worldIn, pos, state);
            }
        }
    }
}
