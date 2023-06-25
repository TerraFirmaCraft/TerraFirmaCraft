/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.IcebergFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;

/**
 * This is a modified version which overrides two functions which only handle vanilla water blocks
 * Both of these require AT's, for the override, and for the called methods
 */
public class TFCIcebergFeature extends IcebergFeature
{
    public TFCIcebergFeature(Codec<BlockStateConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public void carve(int int1_, int yDiff, BlockPos pos, LevelAccessor level, boolean placeWater, double double_, BlockPos pos1, int int2_, int int_)
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
                    BlockState state = level.getBlockState(blockpos);
                    if (isIcebergState(state) || state.getBlock() == Blocks.SNOW_BLOCK)
                    {
                        if (placeWater)
                        {
                            this.setBlock(level, blockpos, TFCBlocks.SALT_WATER.get().defaultBlockState());
                        }
                        else
                        {
                            this.setBlock(level, blockpos, Blocks.AIR.defaultBlockState());
                            this.removeFloatingSnowLayer(level, blockpos);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setIcebergBlock(BlockPos pos, LevelAccessor level, RandomSource random, int int_, int int1_, boolean boolean_, boolean boolean1_, BlockState state)
    {
        BlockState blockstate = level.getBlockState(pos);
        if (blockstate.isAir() || Helpers.isBlock(blockstate, Blocks.SNOW_BLOCK) || Helpers.isBlock(blockstate, Blocks.ICE) || Helpers.isBlock(blockstate, TFCBlocks.SALT_WATER.get()))
        {
            boolean flag = !boolean_ || random.nextDouble() > 0.05D;
            int i = boolean_ ? 3 : 2;
            if (boolean1_ && !Helpers.isBlock(blockstate, TFCBlocks.SALT_WATER.get()) && (double) int_ <= (double) random.nextInt(Math.max(1, int1_ / i)) + (double) int1_ * 0.6D && flag)
            {
                this.setBlock(level, pos, Blocks.SNOW_BLOCK.defaultBlockState());
            }
            else
            {
                this.setBlock(level, pos, state);
            }
        }
    }
}
