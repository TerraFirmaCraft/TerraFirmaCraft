/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.List;
import java.util.Random;

import net.minecraft.world.level.ChunkPos;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.NoiseUtil;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.settings.RockLayer;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;

/**
 * This is TFC's default chunk data generator.
 * If you want to use a vanilla or custom chunk generator, you can use this chunk data generator, or attach your own.
 */
public class TFCChunkDataGenerator implements ChunkDataGenerator
{
    private static ConcurrentArea<RockSettings> createRockLayer(Random seedGenerator, RockLayerSettings settings, List<RockSettings> rocks)
    {
        return new ConcurrentArea<>(TFCLayers.createOverworldRockLayer(seedGenerator.nextLong(), settings.getScale(), rocks.size()), rocks::get);
    }

    private final ConcurrentArea<RockSettings> bottomRockLayer, middleRockLayer, topRockLayer;
    private final ConcurrentArea<ForestType> forestTypeLayer;

    private final Noise2D temperatureNoise;
    private final Noise2D rainfallNoise;
    private final Noise2D layerHeightNoise;
    private final Noise2D forestWeirdnessNoise;
    private final Noise2D forestDensityNoise;

    private final ConcurrentArea<PlateTectonicsClassification> plateTectonicsInfo;

    public TFCChunkDataGenerator(long worldSeed, RockLayerSettings settings)
    {
        final Random random = new Random(worldSeed);
        random.setSeed(worldSeed ^ random.nextLong());

        this.bottomRockLayer = createRockLayer(random, settings, settings.getRocksForLayer(RockLayer.BOTTOM));
        this.middleRockLayer = createRockLayer(random, settings, settings.getRocksForLayer(RockLayer.BOTTOM));
        this.topRockLayer = createRockLayer(random, settings, settings.getRocksForLayer(RockLayer.BOTTOM));

        this.layerHeightNoise = new OpenSimplex2D(random.nextInt()).octaves(2).scaled(-10, 10).spread(0.03f);

        // Climate
        temperatureNoise = ((Noise2D) (x, z) -> NoiseUtil.triangle(1, 0, 1f / (2f * TFCConfig.SERVER.temperatureScale.get()), 0, z))
            .scaled(Climate.MINIMUM_TEMPERATURE_SCALE, Climate.MAXIMUM_TEMPERATURE_SCALE)
            .add(new OpenSimplex2D(random.nextInt())
                .octaves(2)
                .spread(12f / TFCConfig.SERVER.temperatureScale.get())
                .scaled(-Climate.REGIONAL_TEMPERATURE_SCALE, Climate.REGIONAL_TEMPERATURE_SCALE));
        rainfallNoise = ((Noise2D) (x, z) -> NoiseUtil.triangle(1, 0, 1f / (2f * TFCConfig.SERVER.rainfallScale.get()), 0, x))
            .scaled(Climate.MINIMUM_RAINFALL, Climate.MAXIMUM_RAINFALL)
            .add(new OpenSimplex2D(random.nextInt())
                .octaves(2)
                .spread(12f / TFCConfig.SERVER.rainfallScale.get())
                .scaled(-Climate.REGIONAL_RAINFALL_SCALE, Climate.REGIONAL_RAINFALL_SCALE))
            .flattened(Climate.MINIMUM_RAINFALL, Climate.MAXIMUM_RAINFALL);

        // Flora
        forestTypeLayer = new ConcurrentArea<>(TFCLayers.createOverworldForestLayer(random.nextLong(), IArtist.nope()), ForestType::valueOf);
        forestWeirdnessNoise = new OpenSimplex2D(random.nextInt()).octaves(4).spread(0.0025f).map(x -> 1.1f * Math.abs(x)).flattened(0, 1);
        forestDensityNoise = new OpenSimplex2D(random.nextInt()).octaves(4).spread(0.0025f).scaled(-0.2f, 1.2f).flattened(0, 1);

        // Plate Tectonics
        plateTectonicsInfo = new ConcurrentArea<>(TFCLayers.createOverworldPlateTectonicInfoLayer(worldSeed), PlateTectonicsClassification::valueOf);
    }

    @Override
    public void generate(ChunkData data)
    {
        ChunkPos pos = data.getPos();
        int chunkX = pos.getMinBlockX(), chunkZ = pos.getMinBlockZ();

        // Temperature / Rainfall
        float rainNW = rainfallNoise.noise(chunkX, chunkZ);
        float rainNE = rainfallNoise.noise(chunkX + 16, chunkZ);
        float rainSW = rainfallNoise.noise(chunkX, chunkZ + 16);
        float rainSE = rainfallNoise.noise(chunkX + 16, chunkZ + 16);

        float tempNW = temperatureNoise.noise(chunkX, chunkZ);
        float tempNE = temperatureNoise.noise(chunkX + 16, chunkZ);
        float tempSW = temperatureNoise.noise(chunkX, chunkZ + 16);
        float tempSE = temperatureNoise.noise(chunkX + 16, chunkZ + 16);

        final ForestType forestType = forestTypeLayer.get(chunkX >> 4, chunkZ >> 4); // This layer is sampled per-chunk, to avoid the waste of two additional zoom layers
        final float forestWeirdness = forestWeirdnessNoise.noise(chunkX + 8, chunkZ + 8);
        final float forestDensity = forestDensityNoise.noise(chunkX + 8, chunkZ + 8);

        // Rocks
        final RockSettings[] bottomLayer = new RockSettings[256];
        final RockSettings[] middleLayer = new RockSettings[256];
        final RockSettings[] topLayer = new RockSettings[256];
        final int[] rockLayerHeight = new int[256];

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                bottomLayer[x + 16 * z] = bottomRockLayer.get(chunkX + x, chunkZ + z);
                middleLayer[x + 16 * z] = middleRockLayer.get(chunkX + x, chunkZ + z);
                topLayer[x + 16 * z] = topRockLayer.get(chunkX + x, chunkZ + z);

                rockLayerHeight[x + 16 * z] = (int) layerHeightNoise.noise(chunkX + x, chunkZ + z);
            }
        }

        data.setRainfall(rainNW, rainNE, rainSW, rainSE);
        data.setAverageTemp(tempNW, tempNE, tempSW, tempSE);
        data.setFloraData(forestType, forestWeirdness, forestDensity);
        data.setPlateTectonicsInfo(plateTectonicsInfo.get(data.getPos().x, data.getPos().z));
        data.setRockData(new RockData(bottomLayer, middleLayer, topLayer, rockLayerHeight));
    }
}
