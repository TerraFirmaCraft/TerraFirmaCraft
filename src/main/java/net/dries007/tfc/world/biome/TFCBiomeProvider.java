/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.layer.LayerFactory;
import net.dries007.tfc.world.layer.TFCLayerUtil;

public class TFCBiomeProvider extends BiomeProvider implements ITFCBiomeProvider
{
    public static final Codec<TFCBiomeProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.LONG.fieldOf("seed").forGetter(c -> c.seed),
        Codec.INT.optionalFieldOf("spawn_distance", 8_000).forGetter(TFCBiomeProvider::getSpawnDistance),
        Codec.INT.optionalFieldOf("spawn_center_x", 0).forGetter(c -> c.spawnCenterX),
        Codec.INT.optionalFieldOf("spawn_center_z", 0).forGetter(c -> c.spawnCenterZ),
        LayerSettings.CODEC.forGetter(TFCBiomeProvider::getLayerSettings),
        ClimateSettings.CODEC.forGetter(c -> c.climateSettings),
        RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(c -> c.biomeRegistry)
    ).apply(instance, TFCBiomeProvider::new));

    // Set from codec
    private final long seed;
    private final int spawnDistance;
    private final int spawnCenterX, spawnCenterZ;
    private final LayerSettings layerSettings;
    private final ClimateSettings climateSettings;
    private final Registry<Biome> biomeRegistry;

    private final LayerFactory<BiomeVariants> biomeLayer;
    private ChunkDataProvider chunkDataProvider;

    public TFCBiomeProvider(long seed, int spawnDistance, int spawnCenterX, int spawnCenterZ, LayerSettings layerSettings, ClimateSettings climateSettings, Registry<Biome> biomeRegistry)
    {
        super(TFCBiomes.getAllKeys().stream().map(biomeRegistry::getOrThrow).collect(Collectors.toList()));

        this.seed = seed;
        this.spawnDistance = spawnDistance;
        this.spawnCenterX = spawnCenterX;
        this.spawnCenterZ = spawnCenterZ;
        this.layerSettings = layerSettings;
        this.climateSettings = climateSettings;
        this.biomeRegistry = biomeRegistry;

        this.biomeLayer = LayerFactory.biomes(TFCLayerUtil.createOverworldBiomeLayer(seed, layerSettings, IArtist.nope(), IArtist.nope()));
    }

    public LayerSettings getLayerSettings()
    {
        return layerSettings;
    }

    public void setChunkDataProvider(ChunkDataProvider chunkDataProvider)
    {
        this.chunkDataProvider = chunkDataProvider;
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
     * A version of {  BiomeProvider#findBiomeHorizontal(int, int, int, int, Predicate, Random)} with a few modifications
     * - It does not query the climate layers - requiring less chunk data generation and is faster.
     * - It's slightly optimized for finding a random biome, and using mutable positions.
     */
    @Nullable
    public BlockPos findBiomeIgnoreClimate(int x, int y, int z, int radius, int increment, Predicate<Biome> biomesIn, Random rand)
    {
        final int centerBiomeX = x >> 2;
        final int centerBiomeZ = z >> 2;
        final int biomeRadius = radius >> 2;
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
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
                        mutablePos.setPos(biomeX << 2, y, biomeZ << 2);
                    }
                    found++;
                }
            }
        }
        return mutablePos.toImmutable();
    }

    /**
     * In {  net.minecraft.world.biome.BiomeContainer}, we can see that the x, y, z positions are not absolute block coordinates.
     * Rather, since MC now samples biomes once per 4x4x4 area basis, these are not accurate for our chunk data purposes
     * So, we need to make them accurate.
     */
    @Override
    public Biome getNoiseBiome(int biomeCoordX, int biomeCoordY, int biomeCoordZ)
    {
        final ChunkPos chunkPos = new ChunkPos(biomeCoordX >> 2, biomeCoordZ >> 2);
        final BlockPos pos = chunkPos.asBlockPos();
        final ChunkData data = chunkDataProvider.get(chunkPos, ChunkData.Status.CLIMATE);
        final BiomeVariants variants = biomeLayer.get(biomeCoordX, biomeCoordZ);
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
        if (averageTemperature < climateSettings.frozenColdCutoff)
        {
            return BiomeTemperature.FROZEN;
        }
        else if (averageTemperature < climateSettings.coldNormalCutoff)
        {
            return BiomeTemperature.COLD;
        }
        else if (averageTemperature < climateSettings.normalLukewarmCutoff)
        {
            return BiomeTemperature.NORMAL;
        }
        else if (averageTemperature < climateSettings.lukewarmWarmCutoff)
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
        if (rainfall < climateSettings.aridDryCutoff)
        {
            return BiomeRainfall.ARID;
        }
        else if (rainfall < climateSettings.dryNormalCutoff)
        {
            return BiomeRainfall.DRY;
        }
        else if (rainfall < climateSettings.normalDampCutoff)
        {
            return BiomeRainfall.NORMAL;
        }
        else if (rainfall < climateSettings.dampWetCutoff)
        {
            return BiomeRainfall.DAMP;
        }
        else
        {
            return BiomeRainfall.WET;
        }
    }

    @Override
    protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
        return CODEC;
    }

    @Override
    public BiomeProvider getBiomeProvider(long seedIn) {
        return new TFCBiomeProvider(seedIn, spawnDistance, spawnCenterX, spawnCenterZ, layerSettings, climateSettings, biomeRegistry);

    }

    public static final class LayerSettings
    {
        private static final MapCodec<LayerSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(0, 100).optionalFieldOf("ocean_percent", 30).forGetter(LayerSettings::getOceanPercent),
            Codecs.POSITIVE_INT.optionalFieldOf("rock_layer_scale", 7).forGetter(LayerSettings::getRockLayerScale),
            ResourceLocation.CODEC.listOf().fieldOf("rocks").forGetter(LayerSettings::getRocks)
        ).apply(instance, LayerSettings::new));

        private final int oceanPercent;
        private final int rockLayerScale;
        private final List<ResourceLocation> rocks;

        public LayerSettings()
        {
            this(45, 7, Arrays.stream(Rock.Default.values()).map(rock -> Helpers.identifier(rock.name().toLowerCase())).collect(Collectors.toList()));
        }

        public LayerSettings(int oceanPercent, int rockLayerScale, List<ResourceLocation> rocks)
        {
            this.oceanPercent = oceanPercent;
            this.rockLayerScale = rockLayerScale;
            this.rocks = rocks;
        }

        public int getOceanPercent()
        {
            return oceanPercent;
        }

        public int getRockLayerScale()
        {
            return rockLayerScale;
        }

        public List<ResourceLocation> getRocks()
        {
            return rocks;
        }
    }

    public static final class ClimateSettings
    {
        public static final MapCodec<ClimateSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("frozen_cold_cutoff", -17.25f).forGetter(c -> c.frozenColdCutoff),
            Codec.FLOAT.optionalFieldOf("cold_normal_cutoff", -3.75f).forGetter(c -> c.coldNormalCutoff),
            Codec.FLOAT.optionalFieldOf("normal_lukewarm_cutoff", 9.75f).forGetter(c -> c.normalLukewarmCutoff),
            Codec.FLOAT.optionalFieldOf("lukewarm_warm_cutoff", 23.25f).forGetter(c -> c.lukewarmWarmCutoff),
            Codec.FLOAT.optionalFieldOf("arid_dry_cutoff", 125f).forGetter(c -> c.aridDryCutoff),
            Codec.FLOAT.optionalFieldOf("dry_normal_cutoff", 200f).forGetter(c -> c.dryNormalCutoff),
            Codec.FLOAT.optionalFieldOf("normal_damp_cutoff", 300f).forGetter(c -> c.normalDampCutoff),
            Codec.FLOAT.optionalFieldOf("damp_wet_cutoff", 375f).forGetter(c -> c.dampWetCutoff)
        ).apply(instance, ClimateSettings::new));

        private final float frozenColdCutoff;
        private final float coldNormalCutoff;
        private final float normalLukewarmCutoff;
        private final float lukewarmWarmCutoff;
        private final float aridDryCutoff;
        private final float dryNormalCutoff;
        private final float normalDampCutoff;
        private final float dampWetCutoff;

        public ClimateSettings()
        {
            this(-17.25f, -3.75f, 9.75f, 23.25f, 125, 200, 300, 375);
        }

        public ClimateSettings(float frozenColdCutoff, float coldNormalCutoff, float normalLukewarmCutoff, float lukewarmWarmCutoff, float aridDryCutoff, float dryNormalCutoff, float normalDampCutoff, float dampWetCutoff)
        {
            this.frozenColdCutoff = frozenColdCutoff;
            this.coldNormalCutoff = coldNormalCutoff;
            this.normalLukewarmCutoff = normalLukewarmCutoff;
            this.lukewarmWarmCutoff = lukewarmWarmCutoff;
            this.aridDryCutoff = aridDryCutoff;
            this.dryNormalCutoff = dryNormalCutoff;
            this.normalDampCutoff = normalDampCutoff;
            this.dampWetCutoff = dampWetCutoff;
        }
    }
}