/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.settings.RockSettings;

public class FissureFeature extends Feature<FissureConfig>
{
    public static void placeFissure(WorldGenLevel level, BlockPos startPos, BlockPos centerPos, BlockPos.MutableBlockPos mutablePos, RandomSource random, BlockState insideState, BlockState wallState, int minPieces, int maxPieces, int maxPieceLength, int minDepth, int radius, @Nullable FissureConfig.Decoration decoration)
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
                level.setBlock(mutablePos.setWithOffset(topPos, 0, -dy, 0), insideState, 2);
                level.setBlock(mutablePos.setWithOffset(topPos, -1, -dy, 0), wallState, 2);
                level.setBlock(mutablePos.setWithOffset(topPos, 1, -dy, 0), wallState, 2);
                level.setBlock(mutablePos.setWithOffset(topPos, 0, -dy, -1), wallState, 2);
                level.setBlock(mutablePos.setWithOffset(topPos, 0, -dy, 1), wallState, 2);

                if (decoration != null)
                {
                    // At each step, place count / rarity blocks (average) within radius, and +/- 2 blocks vertically
                    for (int j = 0; j < decoration.count(); j++)
                    {
                        if (random.nextInt(decoration.rarity()) == 0)
                        {
                            mutablePos.setWithOffset(topPos, random.nextInt(decoration.radius()) - random.nextInt(decoration.radius()), random.nextInt(3) - random.nextInt(3) - dy, random.nextInt(decoration.radius()) - random.nextInt(decoration.radius()));
                            final BlockState stoneState = level.getBlockState(mutablePos);
                            final BlockState decorationState = decoration.getState(stoneState, random);
                            if (decorationState != null)
                            {
                                level.setBlock(mutablePos, decorationState, 2);
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
            level.setBlock(mutablePos.set(topPos), insideState, 2);
            level.setBlock(mutablePos.setWithOffset(topPos, 0, 1, 0), wallState, 2);
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                if (direction != branchDirection.getOpposite()) // Fill all sides except the one we came from
                {
                    level.setBlock(mutablePos.setWithOffset(topPos, direction), wallState, 2);
                }
            }

            if (topPos.getY() < minDepth)
            {
                break;
            }
        }
    }

    private static Direction randomBoundedDirection(RandomSource random, BlockPos center, BlockPos target, int radius)
    {
        // Adjust the branch to stay within a bounded region
        final Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        final int distX = target.getX() - center.getX(), distZ = target.getZ() - center.getZ();
        // Adjust the branch to stay within a bounded region
        switch (direction)
        {
            case EAST:
                if (distX > radius)
                {
                    return Direction.WEST;
                }
                return direction;
            case WEST:
                if (distX < -radius)
                {
                    return Direction.EAST;
                }
                return direction;
            case NORTH:
                if (distZ < -radius)
                {
                    return Direction.SOUTH;
                }
                return direction;
            case SOUTH:
                if (distZ > radius)
                {
                    return Direction.NORTH;
                }
                return direction;
            default:
                return direction;
        }
    }

    public FissureFeature(Codec<FissureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<FissureConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final RandomSource rand = context.random();
        final FissureConfig config = context.config();

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final int placeCount = 1 + rand.nextInt(config.count());
        final int minDepth = config.minDepth().resolveY(new WorldGenerationContext(context.chunkGenerator(), level));
        final BlockState insideState = config.wallState().orElseGet(() -> {
            final ChunkData data = ChunkData.get(context.level(), pos);
            final RockSettings rock = data.getRockData().getRock(pos.getX(), context.chunkGenerator().getMinY() + 1, pos.getZ());
            return rock.raw().defaultBlockState();
        });

        for (int i = 0; i < placeCount; i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(config.radius()) - rand.nextInt(config.radius()), 0, rand.nextInt(config.radius()) - rand.nextInt(config.radius()));
            mutablePos.setY(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ()));
            FissureFeature.placeFissure(level, pos, mutablePos.immutable(), mutablePos, rand, config.fluidState(), insideState, config.minPieces(), config.maxPieces(), config.maxPieceLength(), minDepth, config.radius(), config.decoration().orElse(null));
        }
        return true;
    }
}
