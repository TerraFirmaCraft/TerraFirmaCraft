/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.biome.VolcanoNoise;

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
            local = new LocalContext(seed, new VolcanoNoise(seed));
            localContext.set(local);
        }

        final Biome biome = level.getBiome(pos).value();
        final BiomeExtension variants = TFCBiomes.getExtensionOrThrow(level, biome);
        if (variants.isVolcanic())
        {
            if (center)
            {
                final BlockPos center = local.volcanoNoise.calculateCenter(pos.getX(), pos.getY(), pos.getZ(), variants.getVolcanoRarity());
                if (center != null)
                {
                    if (level instanceof WorldGenRegion generating && !ensureCanWrite(generating, center))
                    {
                        return Stream.empty();
                    }
                    return Stream.of(center);
                }
            }
            else if (local.volcanoNoise.calculateEasing(pos.getX(), pos.getZ(), variants.getVolcanoRarity()) > this.distance)
            {
                return Stream.of(pos);
            }
        }
        return Stream.empty();
    }

    private boolean ensureCanWrite(WorldGenRegion level, BlockPos pos)
    {
        final int xSection = SectionPos.blockToSectionCoord(pos.getX());
        final int zSection = SectionPos.blockToSectionCoord(pos.getZ());
        final ChunkPos chunkpos = level.getCenter();
        return chunkpos.x == xSection && chunkpos.z == zSection;
    }

    record LocalContext(long seed, VolcanoNoise volcanoNoise) {}
}
