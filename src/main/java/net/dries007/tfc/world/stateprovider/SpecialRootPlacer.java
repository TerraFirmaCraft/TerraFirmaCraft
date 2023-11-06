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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.feature.tree.RootConfig;

public record SpecialRootPlacer(float skewChance)
{
    public static final Codec<SpecialRootPlacer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("skew_chance").forGetter(c -> c.skewChance)
    ).apply(instance, SpecialRootPlacer::new));

    public boolean placeRoots(WorldGenLevel level, RandomSource random, BlockPos pos, BlockPos trunkOrigin, RootConfig config)
    {
        final List<BlockPos> positions = Lists.newArrayList();
        final BlockPos.MutableBlockPos cursor = pos.mutable();

        while (cursor.getY() < trunkOrigin.getY())
        {
            if (!this.canPlaceRoot(level, cursor, config))
                return false;

            cursor.move(0, 1, 0);
        }

        positions.add(trunkOrigin.below());

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            final BlockPos relativePos = trunkOrigin.relative(direction);
            final List<BlockPos> used = Lists.newArrayList();
            if (!this.simulateRoots(level, random, relativePos, direction, trunkOrigin, used, 0, config))
            {
                return false;
            }

            positions.addAll(used);
            positions.add(trunkOrigin.relative(direction));
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
        return FluidHelpers.isAirOrEmptyFluid(state) || config.blocks().get(state.getBlock()) != null;
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
