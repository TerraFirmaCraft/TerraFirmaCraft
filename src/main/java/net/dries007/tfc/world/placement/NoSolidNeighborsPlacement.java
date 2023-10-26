/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.stream.Stream;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.EnvironmentHelpers;

public class NoSolidNeighborsPlacement extends PlacementModifier
{
    public static final Codec<NoSolidNeighborsPlacement> CODEC = Codec.unit(new NoSolidNeighborsPlacement());

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
        return !(isNonSolid(level, pos.offset(1, 0, 0)) &&
            isNonSolid(level, pos.offset(-1, 0, 0)) &&
            isNonSolid(level, pos.offset(0, 0, 1)) &&
            isNonSolid(level, pos.offset(0, 0, -1)));
    }

    private boolean isNonSolid(PlacementContext level, BlockPos pos)
    {
        final BlockState state = level.getBlockState(pos);
        return (EnvironmentHelpers.isWorldgenReplaceable(state) || state.is(TFCTags.Blocks.CAN_BE_SNOW_PILED));
    }
}
