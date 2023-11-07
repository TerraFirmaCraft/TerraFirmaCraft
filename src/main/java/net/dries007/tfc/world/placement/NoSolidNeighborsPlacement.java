/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.stream.Stream;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;

public class NoSolidNeighborsPlacement extends PlacementModifier
{
    public static final Codec<NoSolidNeighborsPlacement> PLACEMENT_CODEC = Codec.unit(new NoSolidNeighborsPlacement());

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos)
    {
        return !hasSolidNeighbor(context, pos) ? Stream.of(pos) : Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.NO_SOLID_NEIGHBORS.get();
    }

    private boolean hasSolidNeighbor(PlacementContext level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            if (!isNonSolid(level, cursor.setWithOffset(pos, dir)))
            {
                return false;
            }
        }
        return true;
    }

    private boolean isNonSolid(PlacementContext level, BlockPos pos)
    {
        final BlockState state = level.getBlockState(pos);
        return EnvironmentHelpers.isWorldgenReplaceable(state) || Helpers.isBlock(state, TFCTags.Blocks.CAN_BE_SNOW_PILED);
    }
}
