/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.stateprovider;

import java.util.List;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.feature.tree.RootConfig;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public record SpecialRootPlacer(float skewChance)
{
    public static final Codec<SpecialRootPlacer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("skew_chance").forGetter(c -> c.skewChance)
    ).apply(instance, SpecialRootPlacer::new));

    public boolean placeRoots(WorldGenLevel level, RandomSource random, BlockPos.MutableBlockPos mutablePos, RootConfig config)
    {
        final List<BlockPos> positions = Lists.newArrayList();
        if (level instanceof WorldGenRegion)
        {
            final int oceanFloorY = level.getChunk(mutablePos).getHeight(Heightmap.Types.OCEAN_FLOOR_WG, mutablePos.getX(), mutablePos.getZ());
            if (oceanFloorY < SEA_LEVEL_Y - 4) {
                return false;
            } else {
                // We use a mutable pos so that mangrove roots can raise the height of the trunk base
                // Do not modify the mutable pos beyond this point
                mutablePos.setY(Math.max(oceanFloorY + random.nextInt(4), SEA_LEVEL_Y + 1));
            }
        }
        else {
            mutablePos.setY(mutablePos.getY() + random.nextInt(4));
        }

        positions.add(mutablePos.below());

        final Direction guaranteedDirection = Util.getRandom(Direction.Plane.HORIZONTAL.stream().toList(), random);
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            // TODO: Design a smarter root placement system
            //  The way this was structured previously failed placement for the entire tree if simulateRoots returned false for any of the 4 directions
            //  However, that system invalidated the spawns of most mangroves, which is what I would call bad
            if (direction == guaranteedDirection || random.nextInt(3) > 0)
            {
                final BlockPos relativePos = mutablePos.relative(direction);
                final List<BlockPos> used = Lists.newArrayList();
                this.simulateRoots(level, random, relativePos, direction, mutablePos, used, 0, config);
                positions.addAll(used);
            }
            positions.add(mutablePos.relative(direction));
        }

        for (BlockPos rootPos : positions)
        {
            this.placeRoot(level, random, rootPos, config);
        }
        return true;
    }


    private boolean simulateRoots(WorldGenLevel level, RandomSource random, BlockPos pos, Direction direction, BlockPos trunkOrigin, List<BlockPos> roots, int length, RootConfig config)
    {
        final int maxLength = config.height();
        if (length != maxLength && roots.size() <= maxLength)
        {
            for (BlockPos blockpos : this.potentialRootPositions(pos, direction, random, trunkOrigin, config))
            {
                if (this.canPlaceRoot(level, blockpos, config))
                {
                    roots.add(blockpos);
                    if (!this.simulateRoots(level, random, blockpos, direction, trunkOrigin, roots, length + 1, config))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private List<BlockPos> potentialRootPositions(BlockPos pos, Direction direction, RandomSource random, BlockPos origin, RootConfig config)
    {
        final BlockPos belowPos = pos.below();
        final BlockPos relativePos = pos.relative(direction);
        final int dist = pos.distManhattan(origin);
        final int width = config.width();
        final float f = skewChance;
        if (dist > width - 3 && dist <= width)
        {
            return random.nextFloat() < f ? List.of(belowPos, relativePos.below()) : List.of(belowPos);
        }
        else if (dist > width)
        {
            return List.of(belowPos);
        }
        else if (random.nextFloat() < f)
        {
            return List.of(belowPos);
        }
        else
        {
            return random.nextBoolean() ? List.of(relativePos) : List.of(belowPos);
        }
    }

    private boolean canPlaceRoot(WorldGenLevel level, BlockPos pos, RootConfig config)
    {
        final BlockState state = level.getBlockState(pos);
        return FluidHelpers.isAirOrEmptyFluid(state) || EnvironmentHelpers.isWorldgenReplaceable(state) || config.blocks().get(state.getBlock()) != null;
    }

    private void placeRoot(WorldGenLevel level, RandomSource random, BlockPos pos, RootConfig config)
    {
        if (this.canPlaceRoot(level, pos, config))
        {
            final BlockState stateAt = level.getBlockState(pos);
            BlockState toPlace;
            final IWeighted<BlockState> weighted = config.blocks().get(stateAt.getBlock());
            if (weighted != null)
            {
                toPlace = weighted.get(random);
            }
            else
            {
                toPlace = TFCBlocks.TREE_ROOTS.get().defaultBlockState();
            }

            final BlockState filled = FluidHelpers.fillWithFluid(toPlace, level.getFluidState(pos).getType());
            level.setBlock(pos, filled == null ? toPlace : filled, 19);
        }
    }

}
