/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.coral;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.coral.CoralWallFanBlock;
import net.dries007.tfc.common.fluids.TFCFluids;

public final class CoralHelpers
{
    /**
     * Vanilla copy pasta
     * Copied out into it's own static method so all our subclasses can call to this
     * Replaces references to vanilla blocks with TFC ones
     *
     * {@link net.minecraft.world.level.levelgen.feature.CoralFeature#placeCoralBlock(LevelAccessor, Random, BlockPos, BlockState)}
     */
    public static boolean placeCoralBlock(LevelAccessor world, Random rand, BlockPos pos, BlockState coralBlockState)
    {
        BlockPos abovePos = pos.above();
        BlockState blockstate = world.getBlockState(pos);
        if ((blockstate.is(TFCBlocks.SALT_WATER.get()) || blockstate.is(TFCTags.Blocks.CORALS)) && world.getBlockState(abovePos).is(TFCBlocks.SALT_WATER.get()))
        {
            world.setBlock(pos, coralBlockState, 3);
            if (rand.nextFloat() < 0.25F)
            {
                world.setBlock(abovePos, salty(TFCTags.Blocks.CORALS.getRandomElement(rand).defaultBlockState()), 2);
            }
            else if (rand.nextFloat() < 0.05F)
            {
                world.setBlock(abovePos, salty(TFCBlocks.SEA_PICKLE.get().defaultBlockState().setValue(SeaPickleBlock.PICKLES, rand.nextInt(4) + 1)), 2);
            }

            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                if (rand.nextFloat() < 0.2F)
                {
                    BlockPos relativePos = pos.relative(direction);
                    if (world.getBlockState(relativePos).is(TFCBlocks.SALT_WATER.get()))
                    {
                        BlockState wallCoralState = salty(TFCTags.Blocks.WALL_CORALS.getRandomElement(rand).defaultBlockState()).setValue(CoralWallFanBlock.FACING, direction);
                        world.setBlock(relativePos, wallCoralState, 2);
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private static BlockState salty(BlockState state)
    {
        return state.setValue(TFCBlockStateProperties.SALT_WATER, TFCBlockStateProperties.SALT_WATER.keyFor(TFCFluids.SALT_WATER.getSource()));
    }
}
