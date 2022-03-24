/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.soil.IGrassBlock;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;

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
    public boolean place(FeaturePlaceContext<FloodFillLakeConfig> context)
    {
        final WorldGenLevel worldIn = context.level();
        BlockPos pos = context.origin();
        final FloodFillLakeConfig config = context.config();

        final ChunkPos chunkPos = new ChunkPos(pos);
        final BoundingBox box = new BoundingBox(chunkPos.getMinBlockX() - 14, Integer.MIN_VALUE, chunkPos.getMinBlockZ() - 14, chunkPos.getMaxBlockX() + 14, Integer.MAX_VALUE, chunkPos.getMaxBlockZ() + 14); // Leeway so we can check outside this box

        final Set<BlockPos> filled = new HashSet<>();
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        // First, make sure we're currently at the lowest point in the column.
        mutablePos.set(pos);
        while (worldIn.isEmptyBlock(mutablePos) && mutablePos.getY() > 11)
        {
            mutablePos.move(0, -1, 0);
        }
        pos = mutablePos.immutable();

        // Initial placement is surface level, so start filling one block above
        final BlockPos startPos = pos.above();
        final BlockState fill = config.getState();
        final Fluid fluid = fill.getFluidState().getType();
        if (floodFill(worldIn, startPos, box, filled, mutablePos, config))
        {
            // Minimum size, don't fill awkward tiny lakes
            if (filled.size() >= 20)
            {
                for (BlockPos filledPos : filled)
                {
                    worldIn.setBlock(filledPos, fill, 2);
                    worldIn.scheduleTick(filledPos, fluid, 0);

                    // If we're at the bottom
                    mutablePos.set(filledPos).move(0, -1, 0);
                    if (!filled.contains(mutablePos))
                    {
                        BlockState stateDown = worldIn.getBlockState(mutablePos);
                        if (stateDown.getBlock() instanceof IGrassBlock)
                        {
                            BlockState dirtState = ((IGrassBlock) stateDown.getBlock()).getDirt();
                            worldIn.setBlock(mutablePos, dirtState, 2);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean floodFill(WorldGenLevel worldIn, BlockPos startPos, BoundingBox box, Set<BlockPos> filled, BlockPos.MutableBlockPos mutablePos, FloodFillLakeConfig config)
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
        startPos = startPos.above();
        int prevSize = filled.size();

        while (floodFillLayer(worldIn, startPos, box, nextFilled, mutablePos, config))
        {
            filled.addAll(nextFilled);
            if (prevSize == filled.size())
            {
                // The last move upwards added no new filled area. We abort here to not endlessly advance upwards
                return true;
            }
            startPos = startPos.above();
        }
        return true;
    }

    private boolean floodFillLayer(WorldGenLevel worldIn, BlockPos startPos, BoundingBox box, Set<BlockPos> filled, BlockPos.MutableBlockPos mutablePos, FloodFillLakeConfig config)
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
        return !state.getMaterial().isSolid() && !(state.getBlock() instanceof ILeavesBlock) && config.shouldReplace(state.getFluidState().getType());
    }
}
