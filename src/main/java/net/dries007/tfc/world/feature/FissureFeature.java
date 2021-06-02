/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class FissureFeature extends Feature<FissureConfig>
{
    public static void placeFissure(ISeedReader world, BlockPos startPos, BlockPos centerPos, BlockPos.Mutable mutablePos, Random random, BlockState insideState, BlockState wallState, int minPieces, int maxPieces, int maxPieceLength, int minDepth, int radius, @Nullable FissureConfig.Decoration decoration)
    {
        // Carve a fissure down from this position, by carving a series of tubes straight down
        final int pieces = minPieces + random.nextInt(maxPieces - minPieces);

        BlockPos topPos = startPos.immutable();
        for (int i = 0; i < pieces; i++)
        {
            // Tube
            final int pieceDepth = 1 + random.nextInt(maxPieceLength);
            for (int dy = 1; dy <= pieceDepth; dy++)
            {
                world.setBlock(mutablePos.setWithOffset(topPos, 0, -dy, 0), insideState, 2);
                world.setBlock(mutablePos.setWithOffset(topPos, -1, -dy, 0), wallState, 2);
                world.setBlock(mutablePos.setWithOffset(topPos, 1, -dy, 0), wallState, 2);
                world.setBlock(mutablePos.setWithOffset(topPos, 0, -dy, -1), wallState, 2);
                world.setBlock(mutablePos.setWithOffset(topPos, 0, -dy, 1), wallState, 2);

                if (decoration != null)
                {
                    // At each step, place count / rarity blocks (average) within radius, and +/- 2 blocks vertically
                    for (int j = 0; j < decoration.count; j++)
                    {
                        if (random.nextInt(decoration.rarity) == 0)
                        {
                            mutablePos.setWithOffset(topPos, random.nextInt(decoration.radius) - random.nextInt(decoration.radius), random.nextInt(3) - random.nextInt(3) - dy, random.nextInt(decoration.radius) - random.nextInt(decoration.radius));
                            final BlockState stoneState = world.getBlockState(mutablePos);
                            final BlockState decorationState = decoration.getState(stoneState, random);
                            if (decorationState != null)
                            {
                                world.setBlock(mutablePos, decorationState, 2);
                            }
                        }
                    }
                }
            }

            // Branch
            // topPos is now above the branch, ready for the next tube
            final Direction branchDirection = randomBoundedDirection(random, centerPos, topPos, radius);
            topPos = mutablePos.setWithOffset(topPos, 0, -pieceDepth, 0).move(branchDirection).immutable();

            // Place the joining pieces
            world.setBlock(mutablePos.set(topPos), insideState, 2);
            world.setBlock(mutablePos.setWithOffset(topPos, 0, 1, 0), wallState, 2);
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                if (direction != branchDirection.getOpposite()) // Fill all sides except the one we came from
                {
                    world.setBlock(mutablePos.setWithOffset(topPos, direction), wallState, 2);
                }
            }

            if (topPos.getY() < minDepth)
            {
                break;
            }
        }
    }

    private static Direction randomBoundedDirection(Random random, BlockPos center, BlockPos target, int radius)
    {
        final Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        final int distX = target.getX() - center.getX(), distZ = target.getZ() - center.getZ();
        switch (direction) // Adjust the branch to stay within a bounded region
        {
            case EAST:
                if (distX > radius)
                {
                    return Direction.WEST;
                }
                break;
            case WEST:
                if (distX < -radius)
                {
                    return Direction.EAST;
                }
                break;
            case NORTH:
                if (distZ < -radius)
                {
                    return Direction.SOUTH;
                }
                break;
            case SOUTH:
                if (distZ > radius)
                {
                    return Direction.NORTH;
                }
                break;
        }
        return direction;
    }

    public FissureFeature(Codec<FissureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, FissureConfig config)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final int placeCount = 1 + rand.nextInt(config.count);
        final BlockState insideState = config.wallState.orElseGet(() -> {
            final ChunkDataProvider provider = ChunkDataProvider.get(generator);
            final ChunkData data = provider.get(pos);
            final Rock rock = data.getRockData().getRock(pos.getX(), 0, pos.getZ());
            return rock.getBlock(Rock.BlockType.RAW).defaultBlockState();
        });

        for (int i = 0; i < placeCount; i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(config.radius) - rand.nextInt(config.radius), 0, rand.nextInt(config.radius) - rand.nextInt(config.radius));
            mutablePos.setY(world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ()));
            FissureFeature.placeFissure(world, pos, mutablePos.immutable(), mutablePos, rand, config.fluidState, insideState, config.minPieces, config.maxPieces, config.maxPieceLength, config.minDepth, config.radius, config.decoration.orElse(null));
        }
        return true;
    }
}
