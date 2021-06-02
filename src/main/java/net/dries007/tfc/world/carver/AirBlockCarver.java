/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.BitSet;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import net.dries007.tfc.world.chunkdata.RockData;

/**
 * Common logic for air block carvers
 */
public class AirBlockCarver extends BlockCarver
{
    private BitSet waterAdjacencyMask;

    @Override
    public void setContext(long worldSeed, BitSet airCarvingMask, BitSet liquidCarvingMask, RockData rockData, @Nullable BitSet waterAdjacencyMask)
    {
        this.waterAdjacencyMask = Objects.requireNonNull(waterAdjacencyMask, "Air block carver was supplied with a null waterAdjacencyMask - this is not allowed!");
        super.setContext(worldSeed, airCarvingMask, liquidCarvingMask, rockData, waterAdjacencyMask);
    }

    @Override
    public boolean carve(IChunk chunk, BlockPos pos, Random random, int seaLevel)
    {
        // First, check if the location has already been carved by the current carving mask
        final int maskIndex = CarverHelpers.maskIndex(pos);
        if (!liquidCarvingMask.get(maskIndex) && !airCarvingMask.get(maskIndex))
        {
            airCarvingMask.set(maskIndex);

            final BlockPos posUp = pos.above();
            final BlockState stateAt = chunk.getBlockState(pos);
            final BlockState stateAbove = chunk.getBlockState(posUp);

            if (isCarvable(stateAt) && (pos.getY() > seaLevel || !waterAdjacencyMask.get(maskIndex)))
            {
                if (pos.getY() < 11)
                {
                    chunk.setBlockState(pos, Blocks.LAVA.defaultBlockState(), false);
                }
                else
                {
                    chunk.setBlockState(pos, Blocks.CAVE_AIR.defaultBlockState(), false);
                }

                // Support adjacent blocks
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
}