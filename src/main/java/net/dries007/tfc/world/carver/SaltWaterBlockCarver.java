/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;

import net.dries007.tfc.common.fluids.TFCFluids;

/**
 * Common logic for liquid carvers.
 */
public class SaltWaterBlockCarver extends BlockCarver
{
    @Override
    public boolean carve(ChunkAccess chunk, BlockPos pos, Random random, int seaLevel)
    {
        final int maskIndex = CarverHelpers.maskIndex(pos);
        if (!liquidCarvingMask.get(maskIndex) && !airCarvingMask.get(maskIndex))
        {
            liquidCarvingMask.set(maskIndex);

            final BlockPos posUp = pos.above();
            final BlockState state = chunk.getBlockState(pos);
            final BlockState stateAbove = chunk.getBlockState(posUp);

            if (isCarvable(state) && isCarvable(stateAbove))
            {
                if (pos.getY() == 10)
                {
                    // Top of lava level - create obsidian and magma
                    if (random.nextFloat() < 0.25f)
                    {
                        chunk.setBlockState(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), false);
                        chunk.getBlockTicks().scheduleTick(pos, Blocks.MAGMA_BLOCK, 0);
                    }
                    else
                    {
                        chunk.setBlockState(pos, Blocks.OBSIDIAN.defaultBlockState(), false);
                    }
                }
                else if (pos.getY() < 10)
                {
                    // Underneath lava level, fill with lava
                    chunk.setBlockState(pos, Blocks.LAVA.defaultBlockState(), false);
                }
                else if (pos.getY() <= seaLevel)
                {
                    // Below sea level, fill with water
                    chunk.setBlockState(pos, TFCFluids.SALT_WATER.getSourceBlock(), false);
                    for (Direction direction : Direction.Plane.HORIZONTAL)
                    {
                        // Always schedule update ticks if we're on a chunk edge as we cannot check if it's necessary
                        int neighborX = pos.getX() + direction.getStepX();
                        int neighborZ = pos.getZ() + direction.getStepZ();
                        if ((neighborX >> 4) != pos.getX() >> 4 || (neighborZ >> 4) != pos.getZ() >> 4 || chunk.getBlockState(pos.relative(direction, 1)).isAir())
                        {
                            chunk.getLiquidTicks().scheduleTick(pos, TFCFluids.SALT_WATER.getSource(), 0);
                            break;
                        }
                    }
                }
                else
                {
                    // Above sea level, replace with air (however unlikely)
                    // Mark as carved in the air mask as well
                    airCarvingMask.set(maskIndex);
                    chunk.setBlockState(pos, Blocks.CAVE_AIR.defaultBlockState(), false);

                    // Check below state for replacements
                    final BlockPos posDown = pos.below();
                    final BlockState stateBelow = chunk.getBlockState(posDown);
                    if (exposedBlockReplacements.containsKey(stateBelow.getBlock()))
                    {
                        chunk.setBlockState(posDown, exposedBlockReplacements.get(stateBelow.getBlock()).defaultBlockState(), false);
                    }
                }

                setSupported(chunk, posUp, stateAbove, rockData);
                return true;
            }
        }
        return false;
    }
}
