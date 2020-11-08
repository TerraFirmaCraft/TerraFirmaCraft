/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.carver;

import java.util.*;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.WorldGenRegion;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.RockData;

/**
 * This is a utility wrapper which is used to create caves, and applies all required transformations and checks in order to make that happen.
 * It checks the carving mask and target block for replacement.
 * It also
 */
public class AirBlockCarver extends BlockCarver
{
    @Override
    public boolean carve(WorldGenRegion world, IChunk chunk, BlockPos pos, Random random, int seaLevel, BitSet airMask, BitSet liquidMask, RockData rockData)
    {
        // First, check if the location has already been carved by the current carving mask
        final int maskIndex = Helpers.getCarvingMaskIndex(pos);
        if (!liquidMask.get(maskIndex) && !airMask.get(maskIndex))
        {
            airMask.set(maskIndex);

            final BlockPos posUp = pos.above();
            final BlockState stateAt = chunk.getBlockState(pos);
            final BlockState stateAbove = chunk.getBlockState(posUp);

            final boolean waterAdjacent = isAdjacentToWater(world, pos);
            if (carvableBlocks.contains(stateAt.getBlock()) && isSupportable(stateAbove) && (!waterAdjacent || pos.getY() < 11))
            {
                if (pos.getY() < 11 && waterAdjacent)
                {
                    // Adjacent to another liquid. Place obsidian and/or magma
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
                else if (pos.getY() < 11)
                {
                    // No water, but still below lava level
                    chunk.setBlockState(pos, Blocks.LAVA.defaultBlockState(), false);
                }
                else
                {
                    // Above lava level and no liquid adjacent as per previous checks
                    chunk.setBlockState(pos, Blocks.CAVE_AIR.defaultBlockState(), false);
                }

                // Adjust above and below blocks
                setSupported(chunk, posUp, stateAbove, rockData);

                // Check below state for replacements
                BlockPos posDown = pos.below();
                BlockState stateBelow = chunk.getBlockState(posDown);
                if (exposedBlockReplacements.containsKey(stateBelow.getBlock()))
                {
                    chunk.setBlockState(posDown, exposedBlockReplacements.get(stateBelow.getBlock()).defaultBlockState(), false);
                }
                return true;

            }
        }
        return false;
    }

    private boolean isAdjacentToWater(WorldGenRegion world, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            if (world.getFluidState(pos.relative(direction)).is(FluidTags.WATER))
            {
                return true;
            }
        }
        return false;
    }
}