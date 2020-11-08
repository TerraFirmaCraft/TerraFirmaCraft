package net.dries007.tfc.world.carver;

import java.util.BitSet;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.WorldGenRegion;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.RockData;

/**
 * Common logic for liquid carvers.
 */
public class LiquidBlockCarver extends BlockCarver
{
    @Override
    @SuppressWarnings("deprecation")
    public boolean carve(WorldGenRegion world, IChunk chunk, BlockPos pos, Random random, int seaLevel, BitSet airMask, BitSet liquidMask, RockData rockData)
    {
        final int maskIndex = Helpers.getCarvingMaskIndex(pos);
        if (!liquidMask.get(maskIndex) && !airMask.get(maskIndex))
        {
            liquidMask.set(maskIndex);

            final BlockPos posUp = pos.above();
            final BlockState state = chunk.getBlockState(pos);
            final BlockState stateAbove = chunk.getBlockState(posUp);

            if (carvableBlocks.contains(state.getBlock()) && isSupportable(stateAbove))
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
                    chunk.setBlockState(pos, Fluids.WATER.defaultFluidState().createLegacyBlock(), false);
                    for (Direction direction : Direction.Plane.HORIZONTAL)
                    {
                        if (world.getBlockState(pos.relative(direction, 1)).isAir())
                        {
                            chunk.getLiquidTicks().scheduleTick(pos, Fluids.WATER, 0);
                            break;
                        }
                    }
                }
                else
                {
                    // Above sea level, replace with air (however unlikely)
                    // Mark as carved in the air mask as well
                    airMask.set(maskIndex);
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
