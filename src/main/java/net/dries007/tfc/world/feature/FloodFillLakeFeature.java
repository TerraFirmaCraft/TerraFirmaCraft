/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.soil.IGrassBlock;

/**
 * This fills in natural depressions using a localized flood fill.
 * It only operates within the allowed range of 3x3 chunks per the feature requirements.
 * If a potential location is unbounded within that area, the flood fill is aborted.
 */
public class FloodFillLakeFeature extends Feature<FloodFillLakeConfig>
{
    public FloodFillLakeFeature(Codec<FloodFillLakeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator chunkGenerator, Random random, BlockPos pos, FloodFillLakeConfig config)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final MutableBoundingBox box = new MutableBoundingBox(chunkPos.getXStart() - 14, chunkPos.getZStart() - 14, chunkPos.getXEnd() + 14, chunkPos.getZEnd() + 14); // Leeway so we can check outside this box

        final Set<BlockPos> filled = new HashSet<>();
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        // First, make sure we're currently at the lowest point in the column.
        mutablePos.set(pos);
        while (worldIn.isEmptyBlock(mutablePos) && mutablePos.getY() > 11)
        {
            mutablePos.move(0, -1, 0);
        }
        pos = mutablePos.immutable();

        // Initial placement is surface level, so start filling one block above
        final BlockPos startPos = pos.up();
        final BlockState fill = config.getState();
        final Fluid fluid = fill.getFluidState().getType();
        if (floodFill(worldIn, startPos, box, filled, mutablePos, config))
        {
            // Minimum size, don't fill awkward tiny lakes
            if (filled.size() >= 20)
            {
                for (BlockPos filledPos : filled)
                {
                    worldIn.setBlockState(filledPos, fill, 2);
                    worldIn.getLiquidTicks().scheduleTick(filledPos, fluid, 0);

                    // If we're at the bottom
                    mutablePos.set(filledPos).move(0, -1, 0);
                    if (!filled.contains(mutablePos))
                    {
                        BlockState stateDown = worldIn.getBlockState(mutablePos);
                        if (stateDown.getBlock() instanceof IGrassBlock)
                        {
                            BlockState dirtState = ((IGrassBlock) stateDown.getBlock()).getDirt();
                            worldIn.setBlockState(mutablePos, dirtState, 2);
                        }
                    }
                }
                //LOGGER.debug("Flood Fill Lake generated at {} {} {}", pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }
        return false;
    }

    private boolean floodFill(ISeedReader worldIn, BlockPos startPos, MutableBoundingBox box, Set<BlockPos> filled, BlockPos.Mutable mutablePos, FloodFillLakeConfig config)
    {
        boolean result = floodFillLayer(worldIn, startPos, box, filled, mutablePos, config);
        if (!result)
        {
            return false; // Failed the initial flood fill, exit early
        }
        if (!config.shouldOverfill())
        {
            return true; // No overfilling, result is valid, return valid
        }

        // Initial result is valid, overfill upwards
        Set<BlockPos> nextFilled = new HashSet<>(filled);
        startPos = startPos.up();
        int prevSize = filled.size();

        while (floodFillLayer(worldIn, startPos, box, nextFilled, mutablePos, config))
        {
            filled.addAll(nextFilled);
            if (prevSize == filled.size())
            {
                // The last move upwards added no new filled area. We abort here to not endlessly advance upwards
                return true;
            }
            startPos = startPos.up();
        }
        return true;
    }

    private boolean floodFillLayer(ISeedReader worldIn, BlockPos startPos, MutableBoundingBox box, Set<BlockPos> filled, BlockPos.Mutable mutablePos, FloodFillLakeConfig config)
    {
        // First check the start position, this must be fillable
        if (!isFloodFillable(worldIn.getBlockState(startPos), config))
        {
            return false;
        }

        final LinkedList<BlockPos> queue = new LinkedList<>();
        final Direction[] directions = Direction.values();
        final int maximumY = startPos.getY();
        filled.add(startPos);
        queue.addFirst(startPos);

        while (!queue.isEmpty())
        {
            BlockPos posAt = queue.removeFirst();
            for (Direction direction : directions)
            {
                mutablePos.set(posAt).move(direction);
                if (!filled.contains(mutablePos) && mutablePos.getY() <= maximumY)
                {
                    final BlockState stateAt = worldIn.getBlockState(mutablePos);
                    if (isFloodFillable(stateAt, config))
                    {
                        if (box.isInside(mutablePos))
                        {
                            // Valid flood fill location
                            BlockPos posNext = mutablePos.immutable();
                            queue.addFirst(posNext);
                            filled.add(posNext);
                        }
                        else
                        {
                            // Invalid boundary condition
                            return false;
                        }
                    }
                }
            }
        }
        return !filled.isEmpty();
    }

    private boolean isFloodFillable(BlockState state, FloodFillLakeConfig config)
    {
        return !state.getMaterial().isSolid() && config.shouldReplace(state.getFluidState().getType());
    }
}
