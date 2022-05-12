/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraftforge.registries.DeferredRegister;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.TFCChunkDataGenerator;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.river.Flow;
import net.dries007.tfc.world.river.MidpointFractal;
import net.dries007.tfc.world.river.Watershed;
import net.dries007.tfc.world.settings.ClimateSettings;
import net.dries007.tfc.world.settings.RockLayerSettings;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCBiomeSource extends BiomeSource implements BiomeSourceExtension, RiverSource
{
    public static final Codec<TFCBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.LONG.fieldOf("seed").forGetter(c -> c.seed),
        Codec.INT.fieldOf("spawn_distance").forGetter(TFCBiomeSource::getSpawnDistance),
        Codec.INT.fieldOf("spawn_center_x").forGetter(c -> c.spawnCenterX),
        Codec.INT.fieldOf("spawn_center_z").forGetter(c -> c.spawnCenterZ),
        RockLayerSettings.CODEC.fieldOf("rock_layer_settings").forGetter(c -> c.rockLayerSettings),
        ClimateSettings.CODEC.fieldOf("temperature_settings").forGetter(c -> c.temperatureSettings),
        ClimateSettings.CODEC.fieldOf("rainfall_settings").forGetter(c -> c.rainfallSettings),
        RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(c -> c.biomeRegistry)
    ).apply(instance, TFCBiomeSource::new));
    
    public static final DeferredRegister<Codec<? extends BiomeSource>> BIOME_SOURCE = DeferredRegister.create(Registry.BIOME_SOURCE_REGISTRY, MOD_ID);

    static
    {
        BIOME_SOURCE.register("overworld", () -> CODEC);
    }

    public static TFCBiomeSource defaultBiomeSource(long seed, Registry<Biome> biomeRegistry)
    {
        return new TFCBiomeSource(seed, 8_000, 0, 0, RockLayerSettings.getDefault(), ClimateSettings.DEFAULT_TEMPERATURE, ClimateSettings.DEFAULT_RAINFALL, biomeRegistry);
    }

    // Set from codec
    private final long seed;
    private final int spawnDistance;
    private final int spawnCenterX, spawnCenterZ;
    private final RockLayerSettings rockLayerSettings;
    private final ClimateSettings temperatureSettings, rainfallSettings;
    private final Registry<Biome> biomeRegistry;

    private final ConcurrentArea<BiomeVariants> biomeLayer;
    private final ChunkDataProvider chunkDataProvider;
    private final Watershed.Context watersheds;

    public TFCBiomeSource(long seed, int spawnDistance, int spawnCenterX, int spawnCenterZ, RockLayerSettings rockLayerSettings, ClimateSettings temperatureSettings, ClimateSettings rainfallSettings, Registry<Biome> biomeRegistry)
    {
        super(TFCBiomes.getAllKeys().stream().map(biomeRegistry::getHolderOrThrow).collect(Collectors.toList()));

        this.seed = seed;
        this.spawnDistance = spawnDistance;
        this.spawnCenterX = spawnCenterX;
        this.spawnCenterZ = spawnCenterZ;
        this.rockLayerSettings = rockLayerSettings;
        this.temperatureSettings = temperatureSettings;
        this.rainfallSettings = rainfallSettings;
        this.biomeRegistry = biomeRegistry;
        this.chunkDataProvider = new ChunkDataProvider(new TFCChunkDataGenerator(seed, rockLayerSettings, temperatureSettings, rainfallSettings), rockLayerSettings);
        this.watersheds = new Watershed.Context(TFCLayers.createEarlyPlateLayers(seed), seed, 0.5f, 0.8f, 14, 0.2f);
        this.biomeLayer = new ConcurrentArea<>(TFCLayers.createOverworldBiomeLayerWithRivers(seed, watersheds, IArtist.nope(), IArtist.nope()), TFCLayers::getFromLayerId);
    }

    @Override
    public Flow getRiverFlow(int quartX, int quartZ)
    {
        final float scale = 1f / (1 << 7);
        final float x0 = quartX * scale, z0 = quartZ * scale;
        for (MidpointFractal fractal : watersheds.getFractalsByPartition(quartX, quartZ))
        {
            // maybeIntersect will skip the more expensive calculation if it fails
            if (fractal.maybeIntersect(x0, z0, Watershed.RIVER_WIDTH))
            {
                final Flow flow = fractal.intersectWithFlow(x0, z0, Watershed.RIVER_WIDTH);
                if (flow != Flow.NONE)
                {
                    return flow;
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
    public ClimateSettings getTemperatureSettings()
    {
        return temperatureSettings;
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

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, @Nullable Climate.Sampler sampler)
    {
        return getNoiseBiome(quartX, quartZ);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartZ)
    {
        final BiomeVariants variants = getNoiseBiomeVariants(quartX, quartZ);
        final BiomeExtension extension = getBiomeExtension(variants, quartX, quartZ);
        return biomeRegistry.getHolderOrThrow(extension.key());
    }

    @Override
    public Holder<Biome> getNoiseBiomeIgnoreClimate(int quartX, int quartZ)
    {
        final BiomeVariants variants = getNoiseBiomeVariants(quartX, quartZ);
        final BiomeExtension extension = variants.get(BiomeTemperature.NORMAL, BiomeRainfall.NORMAL);
        return biomeRegistry.getHolderOrThrow(extension.key());
    }

    @Override
    public BiomeVariants getNoiseBiomeVariants(int quartX, int quartZ)
    {
        return biomeLayer.get(quartX, quartZ);
    }

    @Override
    public Holder<Biome> getClimateForBiome(BiomeVariants variants, int quartX, int quartZ)
    {
        final BiomeExtension extension = getBiomeExtension(variants, quartX, quartZ);
        return biomeRegistry.getHolderOrThrow(extension.key());
    }

    @Override
    public TFCBiomeSource withSeed(long seed)
    {
        return new TFCBiomeSource(seed, spawnDistance, spawnCenterX, spawnCenterZ, rockLayerSettings, temperatureSettings, rainfallSettings, biomeRegistry);
    }

    @Override
    protected Codec<TFCBiomeSource> codec()
    {
        return CODEC;
    }

    @Override
    @Nullable
    public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int blockX, int blockY, int blockZ, int maxRadius, int step, Predicate<Holder<Biome>> biome, Random random, boolean findClosest, @Nullable Climate.Sampler sampler)
    {
        // todo: can we avoid querying getNoiseBiome and instead query getNoiseBiomeIgnoreClimate ? as it causes a chunk data lookup which we don't have
        final int minQuartX = QuartPos.fromBlock(blockX);
        final int minQuartZ = QuartPos.fromBlock(blockZ);
        final int maxQuartRadius = QuartPos.fromBlock(maxRadius);

        Pair<BlockPos, Holder<Biome>> pair = null;
        int count = 0;
        for (int radius = findClosest ? 0 : maxQuartRadius; radius <= maxQuartRadius; radius += step)
        {
            for (int dz = -radius; dz <= radius; dz += step)
            {
                final boolean atZEdge = Math.abs(dz) == radius;
                for (int dx = -radius; dx <= radius; dx += step)
                {
                    if (findClosest)
                    {
                        boolean atXEdge = Math.abs(dx) == radius;
                        if (!atXEdge && !atZEdge)
                        {
                            continue;
                        }
                    }

                    final int x = minQuartX + dx, z = minQuartZ + dz;
                    Holder<Biome> found = getNoiseBiome(x, z);
                    if (biome.test(found))
                    {
                        if (pair == null || random.nextInt(count + 1) == 0)
                        {
                            BlockPos pos = new BlockPos(QuartPos.toBlock(x), blockY, QuartPos.toBlock(z));
                            if (findClosest)
                            {
                                return Pair.of(pos, found);
                            }
                            pair = Pair.of(pos, found);
                        }
                        count++;
                    }
                }
            }
        }
        return pair;
    }

    private BiomeExtension getBiomeExtension(BiomeVariants variants, int quartX, int quartZ)
    {
        final boolean debugNoiseBiomeQueriesWithInvalidClimate = false;

        final ChunkPos chunkPos = new ChunkPos(QuartPos.toSection(quartX), QuartPos.toSection(quartZ));
        final ChunkData data = chunkDataProvider.get(chunkPos);

        // noinspection ConstantConditions
        if (debugNoiseBiomeQueriesWithInvalidClimate && data == ChunkData.EMPTY)
        {
            System.out.println("getNoiseBiome() called but no climate data could be found at " + quartX + ", " + quartZ);
            new Exception("Stacktrace").printStackTrace();
        }

        final BiomeTemperature temperature = calculateTemperature(data.getAverageTemp(QuartPos.toBlock(quartX), QuartPos.toBlock(quartZ)));
        final BiomeRainfall rainfall = calculateRainfall(data.getRainfall(QuartPos.toBlock(quartX), QuartPos.toBlock(quartZ)));
        return variants.get(temperature, rainfall);
    }

    private BiomeRainfall calculateRainfall(float rainfall)
    {
        if (rainfall < rainfallSettings.lowThreshold())
        {
            return BiomeRainfall.DRY;
        }
        else if (rainfall > rainfallSettings.highThreshold())
        {
            return BiomeRainfall.WET;
        }
        return BiomeRainfall.NORMAL;
    }

    private BiomeTemperature calculateTemperature(float averageTemperature)
    {
        if (averageTemperature < temperatureSettings.lowThreshold())
        {
            return BiomeTemperature.COLD;
        }
        else if (averageTemperature > temperatureSettings.highThreshold())
        {
            return BiomeTemperature.WARM;
        }
        return BiomeTemperature.NORMAL;
    }
}