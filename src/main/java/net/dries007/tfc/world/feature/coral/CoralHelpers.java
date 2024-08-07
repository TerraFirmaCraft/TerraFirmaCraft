/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.coral;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.coral.CoralWallFanBlock;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;

public final class CoralHelpers
{
    /**
     * Vanilla copy pasta
     * Copied out into its own static method so all our subclasses can call to this
     * Replaces references to vanilla blocks with TFC ones
     *
     * @see net.minecraft.world.level.levelgen.feature.CoralFeature#placeCoralBlock(LevelAccessor, RandomSource, BlockPos, BlockState)
     */
    public static boolean placeCoralBlock(LevelAccessor level, RandomSource rand, BlockPos pos, BlockState coralBlockState)
    {
        BlockPos abovePos = pos.above();
        BlockState blockstate = level.getBlockState(pos);
        if ((Helpers.isBlock(blockstate, TFCBlocks.SALT_WATER.get()) || Helpers.isBlock(blockstate, BlockTags.CORALS)) && Helpers.isBlock(level.getBlockState(abovePos), TFCBlocks.SALT_WATER.get()))
        {
            level.setBlock(pos, coralBlockState, 3);
            if (rand.nextFloat() < 0.25F)
            {
                Helpers.randomBlock(TFCTags.Blocks.SALT_WATER_CORALS, rand).ifPresent(block -> {
                    level.setBlock(abovePos, salty(block.defaultBlockState()), 2);
                });
            }
            else if (rand.nextFloat() < 0.05F)
            {
                level.setBlock(abovePos, salty(TFCBlocks.SEA_PICKLE.get().defaultBlockState().setValue(SeaPickleBlock.PICKLES, rand.nextInt(4) + 1)), 2);
            }

            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                if (rand.nextFloat() < 0.2F)
                {
                    BlockPos relativePos = pos.relative(direction);
                    if (Helpers.isBlock(level.getBlockState(relativePos), TFCBlocks.SALT_WATER.get()))
                    {
                        Helpers.randomBlock(TFCTags.Blocks.SALT_WATER_WALL_CORALS, rand).ifPresent(block -> {
                            BlockState wallCoralState = block.defaultBlockState();
                            if (wallCoralState.hasProperty(CoralWallFanBlock.FACING))
                            {
                                level.setBlock(relativePos, salty(wallCoralState.setValue(CoralWallFanBlock.FACING, direction)), 2);
                            }
                        });
                    }
                }
            }
            return true;
        }
        return false;
    }

    private static BlockState salty(BlockState state)
    {
        return state.setValue(TFCBlockStateProperties.SALT_WATER, TFCBlockStateProperties.SALT_WATER.keyFor(TFCFluids.SALT_WATER.getSource()));
    }
}
