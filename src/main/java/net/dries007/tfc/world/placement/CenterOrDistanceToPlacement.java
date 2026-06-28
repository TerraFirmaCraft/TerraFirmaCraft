/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import com.mojang.datafixers.util.Function3;
import java.util.stream.Stream;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.volcano.CenteredFeatureNoiseSampler;

/**
 * A placement modifier for an arbitrary {@link CenteredFeatureNoiseSampler} instance. A subclass only
 * needs to override {@link #createContext(Seed)} to be able to provide either "generate at the center of",
 * or "generate within a distance of", these features.
 */
public abstract class CenterOrDistanceToPlacement<T extends CenteredFeatureNoiseSampler> extends PlacementModifier
{
    public static <E extends CenterOrDistanceToPlacement<?>> MapCodec<E> codec(Function3<Boolean, Float, Float, E> factory)
    {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("center", false).forGetter(c -> c.center),
            Codecs.UNIT_FLOAT.optionalFieldOf("min_easing", 0f).forGetter(c -> c.minEasing),
            Codecs.UNIT_FLOAT.optionalFieldOf("max_easing", 1f).forGetter(c -> c.maxEasing)
        ).apply(instance, factory));
    }

    final boolean center;
    final float minEasing;
    final float maxEasing;

    private final ThreadLocal<LocalContext<T>> localContext;

    public CenterOrDistanceToPlacement(boolean center, float distance, float maxEasing)
    {
        this.center = center;
        this.minEasing = distance;
        this.maxEasing = maxEasing;
        this.localContext = ThreadLocal.withInitial(() -> null);
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos)
    {
        final WorldGenLevel level = context.getLevel();
        final long seed = level.getSeed();

        LocalContext<T> local = localContext.get();
        if (local == null || local.seed != seed)
        {
            local = new LocalContext<>(seed, createContext(Seed.unsafeOf(seed)));
            localContext.set(local);
        }

        final Biome biome = level.getBiome(pos).value();
        final BiomeExtension extension = TFCBiomes.getExtensionOrThrow(level, biome);
        if (local.context.isValidBiome(extension))
        {
            if (center)
            {
                final BlockPos centerPos = local.context.calculateCenter(pos, extension);
                if (centerPos != null &&
                    SectionPos.blockToSectionCoord(centerPos.getX()) == SectionPos.blockToSectionCoord(pos.getX()) &&
                    SectionPos.blockToSectionCoord(centerPos.getZ()) == SectionPos.blockToSectionCoord(pos.getZ()) &&
                    // We only check whether the center biome is correct for the center version of the feature, because this check
                    // only works when the center is in the chunk we are placing within
                    local.context.isValidBiome(TFCBiomes.getExtensionOrThrow(level, level.getBiome(centerPos).value())))
                {
                    return Stream.of(centerPos);
                }
            }
            else if (local.context.calculateEasing(pos, extension) > this.minEasing && local.context.calculateEasing(pos, extension) < this.maxEasing)
            {
                return Stream.of(pos);
            }
        }
        return Stream.empty();
    }

    protected abstract T createContext(Seed seed);

    private record LocalContext<T>(long seed, T context) {}
}
