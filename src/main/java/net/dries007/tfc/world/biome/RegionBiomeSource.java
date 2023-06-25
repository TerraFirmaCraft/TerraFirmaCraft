/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.FeatureCycleDetector;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.TFCChunkDataGenerator;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.Units;
import net.dries007.tfc.world.river.Flow;
import net.dries007.tfc.world.river.MidpointFractal;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class RegionBiomeSource extends BiomeSource implements BiomeSourceExtension
{
    public static final DeferredRegister<Codec<? extends BiomeSource>> BIOME_SOURCE = DeferredRegister.create(Registries.BIOME_SOURCE, MOD_ID);

    static
    {
        BIOME_SOURCE.register("overworld", () -> RegionBiomeSource.CODEC);
    }

    public static final Codec<RegionBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Settings.CODEC.forGetter(BiomeSourceExtension::settings),
        RegistryOps.retrieveGetter(Registries.BIOME)
    ).apply(instance, RegionBiomeSource::new));

    // Experiments show that 0.05 is the smallest size we can really support, as any smaller and rivers get spliced.
    // Likewise, 0.2 is about as large as I'd want to go, as larger causes more artifacts at the edge of rivers. Probably in reality [0.05, 0.12] or so is a good range.
    // 0.052 is equivalent to current rivers.
    public static final float RIVER_WIDTH = 0.052f;


    private final HolderGetter<Biome> biomeRegistry;

    private final Settings settings;

    private final RegionGenerator regionGenerator;
    private final ConcurrentArea<BiomeExtension> biomeLayer;

    private final ChunkDataProvider chunkDataProvider;

    public RegionBiomeSource(Settings settings, HolderGetter<Biome> biomeRegistry)
    {
        this.settings = settings;
        this.biomeRegistry = biomeRegistry;

        // todo: link chunk data generator to region rainfall/temperature
        final RandomSource random = new XoroshiroRandomSource(settings.seed());

        this.regionGenerator = new RegionGenerator(random.nextLong());
        this.biomeLayer = new ConcurrentArea<>(TFCLayers.createRegionBiomeLayerWithRivers(regionGenerator, random.nextLong()), TFCLayers::getFromLayerId);

        this.chunkDataProvider = new ChunkDataProvider(new TFCChunkDataGenerator(settings), settings.rockLayerSettings());
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartZ)
    {
        return biomeRegistry.getOrThrow(getBiomeExtension(quartX, quartZ).key());
    }

    @Override
    public BiomeExtension getBiomeExtension(int quartX, int quartZ)
    {
        return biomeLayer.get(quartX, quartZ);
    }

    @Override
    public Holder<Biome> getBiomeFromExtension(BiomeExtension variants)
    {
        return biomeRegistry.getOrThrow(variants.key());
    }

    @Override
    public ChunkDataProvider getChunkDataProvider()
    {
        return chunkDataProvider;
    }

    @Override
    public Settings settings()
    {
        return settings;
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
    protected Stream<Holder<Biome>> collectPossibleBiomes()
    {
        return TFCBiomes.getAllKeys().stream().map(biomeRegistry::getOrThrow);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, @Nullable Climate.Sampler sampler)
    {
        return getNoiseBiome(quartX, quartZ);
    }
}
