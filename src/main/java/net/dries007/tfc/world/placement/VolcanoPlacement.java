/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.biome.VolcanoNoise;
import net.dries007.tfc.world.noise.Cellular2D;

public class VolcanoPlacement extends PlacementModifier
{
    public static final Codec<VolcanoPlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("center", false).forGetter(c -> c.center),
        Codecs.UNIT_FLOAT.optionalFieldOf("distance", 0f).forGetter(c -> c.distance)
    ).apply(instance, VolcanoPlacement::new));

    private final boolean center;
    private final float distance;

    private final ThreadLocal<LocalContext> localContext;

    public VolcanoPlacement(boolean center, float distance)
    {
        this.center = center;
        this.distance = distance;
        this.localContext = ThreadLocal.withInitial(() -> null);
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.VOLCANO.get();
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, Random random, BlockPos pos)
    {
        final WorldGenLevel level = context.getLevel();
        final long seed = level.getSeed();

        LocalContext local = localContext.get();
        if (local == null || local.seed != seed)
        {
            local = new LocalContext(seed, VolcanoNoise.cellNoise(seed));
            localContext.set(local);
        }

        final Biome biome = level.getBiome(pos);
        final BiomeVariants variants = TFCBiomes.getExtensionOrThrow(level, biome).variants();
        if (variants.isVolcanic())
        {
            // Sample volcano noise
            final float value = local.cellNoise.noise(pos.getX(), pos.getZ());
            final float distance = local.cellNoise.f1();
            if (value < variants.getVolcanoChance())
            {
                if (center)
                {
                    final BlockPos centerPos = new BlockPos((int) local.cellNoise.centerX(), pos.getY(), (int) local.cellNoise.centerZ());
                    if (centerPos.getX() >> 4 == pos.getX() >> 4 && centerPos.getZ() >> 4 == pos.getZ() >> 4)
                    {
                        return Stream.of(centerPos);
                    }
                }
                else
                {
                    final float easing = VolcanoNoise.calculateEasing(distance);
                    if (easing > distance)
                    {
                        return Stream.of(pos);
                    }
                }
            }
        }
        return Stream.empty();
    }

    record LocalContext(long seed, Cellular2D cellNoise) {}
}
