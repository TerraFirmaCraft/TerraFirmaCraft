package net.dries007.tfc.world.feature;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.soil.IGrassBlock;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;

public class FloodFillLakeFeature extends Feature<NoFeatureConfig>
{
    public FloodFillLakeFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator chunkGenerator, Random random, BlockPos pos, NoFeatureConfig config)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final MutableBoundingBox box = new MutableBoundingBox(chunkPos.getMinBlockX() - 14, chunkPos.getMinBlockZ() - 14, chunkPos.getMaxBlockX() + 14, chunkPos.getMaxBlockZ() + 14); // Leeway so we can check outside this box

        // Begin a flood fill, using a depth first search (in order to quickly eliminate locations due to invalid boundary conditions)
        final Set<BlockPos> filled = new HashSet<>();
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final BlockPos startPos = pos.above();
        final BlockState water = Blocks.WATER.defaultBlockState();

        // Initial placement is surface level, so start filling one block above
        boolean bounded = tryFloodFill(worldIn, startPos, box, filled, mutablePos);
        if (bounded)
        {
            Set<BlockPos> lowestFilled = new HashSet<>(filled);

            // Iterate upwards, but only add the found blocks if the flood fill was bounded. Iterate upwards until the fill is unbounded
            int y = 2;
            Set<BlockPos> possibleFilled = new HashSet<>();
            while (tryFloodFill(worldIn, pos.above(y), box, possibleFilled, mutablePos))
            {
                y++;
                filled.addAll(possibleFilled);
                possibleFilled.clear();
            }

            // Down fill, from the initial flood fill. This relies on the terrain not having any overhangs (and may cause weird interactions with caves if it does)
            while (!lowestFilled.isEmpty())
            {
                lowestFilled = tryDownFill(worldIn, lowestFilled, mutablePos);
                filled.addAll(lowestFilled);
            }

            for (BlockPos filledPos : filled)
            {
                worldIn.setBlock(filledPos, water, 2);
                worldIn.getLiquidTicks().scheduleTick(filledPos, Fluids.WATER, 0);

                // If we're at the bottom
                mutablePos.set(filledPos).move(0, -1, 0);
                if (!filled.contains(mutablePos))
                {
                    BlockState stateDown = worldIn.getBlockState(mutablePos);
                    if (stateDown.getBlock() instanceof IGrassBlock)
                    {
                        BlockState dirtState = ((IGrassBlock) stateDown.getBlock()).getDirt(worldIn, mutablePos, stateDown);
                        worldIn.setBlock(mutablePos, dirtState, 2);
                    }
                }
            }
        }
        return true;
    }

    private boolean tryFloodFill(ISeedReader worldIn, BlockPos startPos, MutableBoundingBox box, Set<BlockPos> filled, BlockPos.Mutable mutablePos)
    {
        // First check the start position, this must be fillable
        if (!isFloodFillabe(worldIn.getBlockState(startPos)))
        {
            return false;
        }

        final LinkedList<BlockPos> queue = new LinkedList<>();
        filled.add(startPos);
        queue.addFirst(startPos);

        while (!queue.isEmpty())
        {
            BlockPos posAt = queue.removeFirst();
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                mutablePos.set(posAt).move(direction);
                if (!filled.contains(mutablePos))
                {
                    BlockState stateAt = worldIn.getBlockState(mutablePos);
                    if (isFloodFillabe(stateAt))
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

    private Set<BlockPos> tryDownFill(ISeedReader worldIn, Set<BlockPos> lowestFilled, BlockPos.Mutable mutablePos)
    {
        Set<BlockPos> nextLowestFilled = new HashSet<>();
        for (BlockPos pos : lowestFilled)
        {
            mutablePos.set(pos).move(0, -1, 0);
            BlockState state = worldIn.getBlockState(mutablePos);
            if (isFloodFillabe(state))
            {
                nextLowestFilled.add(mutablePos.immutable());
            }
        }
        return nextLowestFilled;
    }

    @SuppressWarnings("deprecation")
    private boolean isFloodFillabe(BlockState state)
    {
        return state.isAir() || state.is(Blocks.SNOW) || state.getBlock() instanceof ILeavesBlock;
    }
}
