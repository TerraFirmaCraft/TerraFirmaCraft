/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import net.dries007.tfc.world.chunkdata.TFCChunkDataGenerator;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.Units;
import net.dries007.tfc.world.river.Flow;
import net.dries007.tfc.world.river.MidpointFractal;

public class RegionBiomeSource extends TFCBiomeSource
{
    public static final Codec<RegionBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Settings.CODEC.forGetter(BiomeSourceExtension::settings),
        RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(c -> c.biomeRegistry)
    ).apply(instance, RegionBiomeSource::new));

    // Experiments show that 0.05 is the smallest size we can really support, as any smaller and rivers get spliced.
    // Likewise, 0.2 is about as large as I'd want to go, as larger causes more artifacts at the edge of rivers. Probably in reality [0.05, 0.12] or so is a good range.
    // 0.052 is equivalent to current rivers.
    public static final float RIVER_WIDTH = 0.052f;

    private final RegionGenerator regionGenerator;
    private final ConcurrentArea<BiomeExtension> biomeLayer;

    public RegionBiomeSource(Settings settings, Registry<Biome> biomeRegistry)
    {
        // todo: link chunk data generator to region rainfall/temperature
        super(settings, biomeRegistry, new TFCChunkDataGenerator(settings), TFCBiomes.getAllKeys());

        final RandomSource random = new XoroshiroRandomSource(settings.seed());

        this.regionGenerator = new RegionGenerator(random.nextLong());
        this.biomeLayer = new ConcurrentArea<>(TFCLayers.createRegionBiomeLayerWithRivers(regionGenerator, random.nextLong()), TFCLayers::getFromLayerId);
    }

    @Override
    public BiomeExtension getNoiseBiomeVariants(int quartX, int quartZ)
    {
        return biomeLayer.get(quartX, quartZ);
    }

    @Override
    public Flow getRiverFlow(int quartX, int quartZ)
    {
        final int gridX = Units.quartToGrid(quartX);
        final int gridZ = Units.quartToGrid(quartZ);

        final float exactGridX = Units.quartToGridExact(quartX);
        final float exactGridZ = Units.quartToGridExact(quartZ);

        for (MidpointFractal fractal : regionGenerator.getOrCreatePartition(gridX, gridZ)
            .get(gridX, gridZ)
            .rivers())
        {
            if (fractal.maybeIntersect(exactGridX, exactGridZ, RIVER_WIDTH))
            {
                final Flow flow = fractal.intersectWithFlow(exactGridX, exactGridZ, RIVER_WIDTH);
                if (flow != Flow.NONE)
                {
                    return flow;
                }
            }
        }
        return Flow.NONE;
    }

    @Override
    protected Codec<? extends BiomeSource> codec()
    {
        return CODEC;
    }

    @Override
    public TFCBiomeSource withSeed(long seed)
    {
        return new RegionBiomeSource(settings.withSeed(seed), biomeRegistry);
    }
}
