/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import net.dries007.tfc.world.biome.BiomeNoise;

public class OceanRidgePlacement extends PlacementModifier
{
    public static final MapCodec<OceanRidgePlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.optionalFieldOf("min_distance", Integer.MIN_VALUE).forGetter(c -> c.minDist),
        Codec.INT.optionalFieldOf("max_distance", Integer.MAX_VALUE).forGetter(c -> c.maxDist)
    ).apply(instance, OceanRidgePlacement::new));

    private final int minDist;
    private final int maxDist;

    public OceanRidgePlacement(int minDist, int maxDist)
    {
        this.minDist = minDist;
        this.maxDist = maxDist;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos)
    {
        return isValid(pos, context.getLevel().getSeed()) ? Stream.of(pos) : Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.OCEAN_RIDGE.get();
    }

    final boolean isValid(BlockPos pos, long seed)
    {
        final double distance = BiomeNoise.getOceanRidgeWarpedEdgeDistanceAndScale(pos.getX(), pos.getZ(), seed, false).x;
        return minDist <= distance && distance <= maxDist;
    }
}
