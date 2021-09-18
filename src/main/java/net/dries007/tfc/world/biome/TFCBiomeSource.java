/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.TFCChunkDataGenerator;
import net.dries007.tfc.world.layer.TFCLayerUtil;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.river.Flow;
import net.dries007.tfc.world.river.MidpointFractal;
import net.dries007.tfc.world.river.Watershed;
import net.dries007.tfc.world.settings.ClimateSettings;
import net.dries007.tfc.world.settings.RockLayerSettings;

public class TFCBiomeSource extends BiomeSource implements BiomeSourceExtension
{
    public static final Codec<TFCBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.LONG.fieldOf("seed").forGetter(c -> c.seed),
        Codec.INT.fieldOf("spawn_distance").forGetter(TFCBiomeSource::getSpawnDistance),
        Codec.INT.fieldOf("spawn_center_x").forGetter(c -> c.spawnCenterX),
        Codec.INT.fieldOf("spawn_center_z").forGetter(c -> c.spawnCenterZ),
        RockLayerSettings.CODEC.fieldOf("rock_layer_settings").forGetter(c -> c.rockLayerSettings),
        ClimateSettings.CODEC.fieldOf("climate_settings").forGetter(c -> c.climateSettings),
        RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(c -> c.biomeRegistry)
    ).apply(instance, TFCBiomeSource::new));

    public static TFCBiomeSource defaultBiomeSource(long seed, Registry<Biome> biomeRegistry)
    {
        return new TFCBiomeSource(seed, 8_000, 0, 0, RockLayerSettings.getDefault(), ClimateSettings.getDefault(), biomeRegistry);
    }

    // Set from codec
    private final long seed;
    private final int spawnDistance;
    private final int spawnCenterX, spawnCenterZ;
    private final RockLayerSettings rockLayerSettings;
    private final ClimateSettings climateSettings;
    private final Registry<Biome> biomeRegistry;

    private final ConcurrentArea<BiomeVariants> biomeLayer;
    private final ChunkDataProvider chunkDataProvider;
    private final Watershed.Context watershedContext;

    public TFCBiomeSource(long seed, int spawnDistance, int spawnCenterX, int spawnCenterZ, RockLayerSettings rockLayerSettings, ClimateSettings climateSettings, Registry<Biome> biomeRegistry)
    {
        super(TFCBiomes.getAllKeys().stream().map(biomeRegistry::getOrThrow).collect(Collectors.toList()));

        this.seed = seed;
        this.spawnDistance = spawnDistance;
        this.spawnCenterX = spawnCenterX;
        this.spawnCenterZ = spawnCenterZ;
        this.rockLayerSettings = rockLayerSettings;
        this.climateSettings = climateSettings;
        this.biomeRegistry = biomeRegistry;
        this.chunkDataProvider = new ChunkDataProvider(new TFCChunkDataGenerator(seed, rockLayerSettings), rockLayerSettings);
        this.watershedContext = new Watershed.Context(TFCLayerUtil.createEarlyPlateLayers(seed), seed, 0.5f, 0.8f, 14, 0.2f);
        this.biomeLayer = new ConcurrentArea<>(TFCLayerUtil.createOverworldBiomeLayerWithRivers(seed, watershedContext, IArtist.nope(), IArtist.nope()), TFCLayerUtil::getFromLayerId);
    }

    public Flow getRiverFlow(int x, int z)
    {
        final float scale = 1f / (1 << 7);
        final float x0 = x * scale, z0 = z * scale;
        for (MidpointFractal fractal : watershedContext.getFractalsByPartition(x, z))
        {
            // maybeIntersect will skip the more expensive calculation if it fails
            if (fractal.maybeIntersect(x0, z0, Watershed.RIVER_WIDTH))
            {
                final Flow flow = fractal.intersectWithFlow(x0, z0, Watershed.RIVER_WIDTH);
                if (flow != Flow.NONE)
                {
                    return flow;
                }
                else if (fractal.intersect(x0, z0, Watershed.RIVER_WIDTH))
                {
                    System.out.println("wut?");
                }
            }
        }
        return Flow.NONE;
    }

    @Override
    public ChunkDataProvider getChunkDataProvider()
    {
        return chunkDataProvider;
    }

    @Override
    public RockLayerSettings getRockLayerSettings()
    {
        return rockLayerSettings;
    }

    @Override
    public int getSpawnDistance()
    {
        return spawnDistance;
    }

    @Override
    public int getSpawnCenterX()
    {
        return spawnCenterX;
    }

    @Override
    public int getSpawnCenterZ()
    {
        return spawnCenterZ;
    }

    /**
     * A version of {@link BiomeSource#findBiomeHorizontal(int, int, int, int, Predicate, Random)} with a few modifications
     * - It does not query the climate layers - requiring less chunk data generation and is faster.
     * - It's slightly optimized for finding a random biome, and using mutable positions.
     */
    @Nullable
    public BlockPos findBiomeIgnoreClimate(int x, int y, int z, int radius, int increment, Predicate<Biome> biomesIn, Random rand)
    {
        final int centerBiomeX = x >> 2;
        final int centerBiomeZ = z >> 2;
        final int biomeRadius = radius >> 2;
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int found = 0;

        for (int stepZ = -biomeRadius; stepZ <= biomeRadius; stepZ += increment)
        {
            for (int stepX = -biomeRadius; stepX <= biomeRadius; stepX += increment)
            {
                int biomeX = centerBiomeX + stepX;
                int biomeZ = centerBiomeZ + stepZ;
                if (biomesIn.test(getNoiseBiomeIgnoreClimate(biomeX, biomeZ)))
                {
                    if (found == 0 || rand.nextInt(found + 1) == 0)
                    {
                        mutablePos.set(biomeX << 2, y, biomeZ << 2);
                    }
                    found++;
                }
            }
        }
        return mutablePos.immutable();
    }

    @Override
    public Biome getNoiseBiome(int quartX, int quartY, int quartZ)
    {
        final ChunkPos chunkPos = new ChunkPos(quartX >> 2, quartZ >> 2);
        final BlockPos pos = chunkPos.getWorldPosition();
        final ChunkData data = chunkDataProvider.get(chunkPos);

        BiomeVariants variants = biomeLayer.get(quartX, quartZ);

        final BiomeTemperature temperature = calculateTemperature(data.getAverageTemp(pos));
        final BiomeRainfall rainfall = calculateRainfall(data.getRainfall(pos));
        final BiomeExtension extension = variants.get(temperature, rainfall);
        return biomeRegistry.getOrThrow(extension.getRegistryKey());
    }

    public Biome getNoiseBiomeIgnoreClimate(int biomeCoordX, int biomeCoordZ)
    {
        final BiomeVariants variants = biomeLayer.get(biomeCoordX, biomeCoordZ);
        final BiomeExtension extension = variants.get(BiomeTemperature.NORMAL, BiomeRainfall.NORMAL);
        return biomeRegistry.getOrThrow(extension.getRegistryKey());
    }

    public BiomeTemperature calculateTemperature(float averageTemperature)
    {
        if (averageTemperature < climateSettings.frozenColdCutoff())
        {
            return BiomeTemperature.FROZEN;
        }
        else if (averageTemperature < climateSettings.coldNormalCutoff())
        {
            return BiomeTemperature.COLD;
        }
        else if (averageTemperature < climateSettings.normalLukewarmCutoff())
        {
            return BiomeTemperature.NORMAL;
        }
        else if (averageTemperature < climateSettings.lukewarmWarmCutoff())
        {
            return BiomeTemperature.LUKEWARM;
        }
        else
        {
            return BiomeTemperature.WARM;
        }
    }

    public BiomeRainfall calculateRainfall(float rainfall)
    {
        if (rainfall < climateSettings.aridDryCutoff())
        {
            return BiomeRainfall.ARID;
        }
        else if (rainfall < climateSettings.dryNormalCutoff())
        {
            return BiomeRainfall.DRY;
        }
        else if (rainfall < climateSettings.normalDampCutoff())
        {
            return BiomeRainfall.NORMAL;
        }
        else if (rainfall < climateSettings.dampWetCutoff())
        {
            return BiomeRainfall.DAMP;
        }
        else
        {
            return BiomeRainfall.WET;
        }
    }

    @Override
    protected Codec<TFCBiomeSource> codec()
    {
        return CODEC;
    }

    @Override
    public TFCBiomeSource withSeed(long seedIn)
    {
        return new TFCBiomeSource(seedIn, spawnDistance, spawnCenterX, spawnCenterZ, rockLayerSettings, climateSettings, biomeRegistry);
    }
}