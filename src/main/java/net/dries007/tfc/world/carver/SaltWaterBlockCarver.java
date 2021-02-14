/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.fluids.TFCFluids;

/**
 * Common logic for liquid carvers.
 */
public class SaltWaterBlockCarver extends BlockCarver
{
    @Override
    @SuppressWarnings("deprecation")
    public boolean carve(IChunk chunk, BlockPos pos, Random random, int seaLevel)
    {
        final int maskIndex = CarverHelpers.maskIndex(pos);
        if (!liquidCarvingMask.get(maskIndex) && !airCarvingMask.get(maskIndex))
        {
            liquidCarvingMask.set(maskIndex);

            final BlockPos posUp = pos.up();
            final BlockState state = chunk.getBlockState(pos);
            final BlockState stateAbove = chunk.getBlockState(posUp);

            if (isCarvable(state) && isSupportable(stateAbove))
            {
                if (pos.getY() == 10)
                {
                    // Top of lava level - create obsidian and magma
                    if (random.nextFloat() < 0.25f)
                    {
                        chunk.setBlockState(pos, Blocks.MAGMA_BLOCK.getDefaultState(), false);
                        chunk.getBlockTicks().scheduleTick(pos, Blocks.MAGMA_BLOCK, 0);
                    }
                    else
                    {
                        chunk.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState(), false);
                    }
                }
                else if (pos.getY() < 10)
                {
                    // Underneath lava level, fill with lava
                    chunk.setBlockState(pos, Blocks.LAVA.getDefaultState(), false);
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
                        if ((neighborX >> 4) != pos.getX() >> 4 || (neighborZ >> 4) != pos.getZ() >> 4 || chunk.getBlockState(pos.offset(direction, 1)).isAir())
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
                    chunk.setBlockState(pos, Blocks.CAVE_AIR.getDefaultState(), false);

                    // Check below state for replacements
                    final BlockPos posDown = pos.down();
                    final BlockState stateBelow = chunk.getBlockState(posDown);
                    if (exposedBlockReplacements.containsKey(stateBelow.getBlock()))
                    {
                        chunk.setBlockState(posDown, exposedBlockReplacements.get(stateBelow.getBlock()).getDefaultState(), false);
                    }
                }

                setSupported(chunk, posUp, stateAbove, rockData);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void reload()
    {
        super.reload();

        // Sand can be carved for underwater carvers
        for (SandBlockType sand : SandBlockType.values())
        {
            carvableBlocks.add(TFCBlocks.SAND.get(sand).get());
        }
    }

    @Override
    protected boolean isSupportable(BlockState state)
    {
        return !state.getFluidState().isEmpty() || super.isSupportable(state);
    }
}
