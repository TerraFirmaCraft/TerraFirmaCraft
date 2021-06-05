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
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;

public class RivuletFeature extends Feature<BlockStateFeatureConfig>
{
    public RivuletFeature(Codec<BlockStateFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateFeatureConfig config)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final MutableBoundingBox box = new MutableBoundingBox(chunkPos.getMinBlockX() - 14, chunkPos.getMinBlockZ() - 14, chunkPos.getMaxBlockX() + 14, chunkPos.getMaxBlockZ() + 14); // Leeway so we can check outside this box

        // Basic pathfinding down the slope
        final Set<BlockPos> chosen = new HashSet<>();
        final LinkedList<BlockPos> branches = new LinkedList<>();

        final BlockPos startPos = new BlockPos(pos.getX(), world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ()), pos.getZ());
        if (!world.getFluidState(startPos.below()).isEmpty()) return false;
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        branches.add(startPos);

        boolean mainBranch = true;

        while (!branches.isEmpty())
        {
            // Explore this branch
            BlockPos branchStartPos = branches.removeFirst();
            int maxLength = 5 + rand.nextInt(8);
            if (mainBranch)
            {
                mainBranch = false;
                maxLength += 12;
            }

            BlockPos lastPos = branchStartPos;

            for (int i = 0; i < maxLength; i++)
            {
                chosen.add(branchStartPos);

                Direction chosenDirection = null;
                int chosenHeight = 0;
                int possibleDirections = 0;
                for (Direction direction : Direction.Plane.HORIZONTAL)
                {
                    // Check positions in each direction
                    mutablePos.setWithOffset(lastPos, direction);
                    if (!box.isInside(mutablePos) || !world.getFluidState(mutablePos.below()).isEmpty())
                    {
                        continue; // Outside of the bounding box, skip!
                    }
                    final int height = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ());
                    if (height <= mutablePos.getY())
                    {
                        // Check that the block below is solid
                        mutablePos.setY(height - 1);
                        if (world.getBlockState(mutablePos).getMaterial().isSolid())
                        {
                            // Valid next position
                            possibleDirections++;
                            if (chosenDirection == null || rand.nextInt(possibleDirections) == 0)
                            {
                                chosenHeight = height;
                                chosenDirection = direction;
                            }
                        }
                    }
                }

                if (possibleDirections == 0)
                {
                    // Nowhere to go from here
                    break;
                }
                else
                {
                    if (possibleDirections > 1 && rand.nextInt(3) == 0)
                    {
                        // Opportunity for a new branch
                        branches.add(lastPos);
                    }

                    // One direction, so proceed
                    mutablePos.setWithOffset(lastPos, chosenDirection).setY(chosenHeight);
                    if (!chosen.contains(mutablePos) && world.getFluidState(mutablePos.below()).isEmpty())
                    {
                        lastPos = mutablePos.immutable();
                        chosen.add(lastPos);
                    }
                    else
                    {
                        // Already reached this position, skip
                        break;
                    }
                }
            }
        }

        if (!chosen.isEmpty())
        {
            // We have found a path and can generate a magma rivulet
            for (BlockPos chosenPos : chosen)
            {
                BlockState setState = Blocks.AIR.defaultBlockState();
                // At each position, break the top block, and replace two blocks underneath with magma
                // The recorded positions are one above the topmost block due to how getHeight works
                mutablePos.setWithOffset(chosenPos, Direction.DOWN);
                for (Direction d : Direction.Plane.HORIZONTAL)
                {
                    mutablePos.move(d);
                    FluidState fluidState = world.getFluidState(mutablePos);
                    if (!fluidState.isEmpty())
                    {
                        setState = fluidState.createLegacyBlock();
                        mutablePos.move(d.getOpposite());
                        break;
                    }
                    mutablePos.move(d.getOpposite());
                }
                setBlock(world, mutablePos, setState);
                mutablePos.move(Direction.DOWN);
                setBlock(world, mutablePos, config.state);
                mutablePos.move(Direction.DOWN);
                setBlock(world, mutablePos, config.state);
            }
            return true;
        }
        return false;
    }
}
