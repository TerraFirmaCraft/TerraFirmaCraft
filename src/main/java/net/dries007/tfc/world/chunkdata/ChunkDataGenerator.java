/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.List;
import java.util.Random;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;

import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.biome.TFCBiomeProvider;
import net.dries007.tfc.world.layer.LayerFactory;
import net.dries007.tfc.world.layer.TFCLayerUtil;
import net.dries007.tfc.world.layer.traits.FastArea;
import net.dries007.tfc.world.noise.INoise1D;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

/**
 * This is TFC's default chunk data generator.
 * If you want to use a vanilla or custom chunk generator, you can use this chunk data generator, or attach your own.
 *
 * @see net.dries007.tfc.world.chunkdata.IChunkDataGenerator
 */
public class ChunkDataGenerator implements IChunkDataGenerator
{
    private final LayerFactory<Rock> bottomRockLayer, middleRockLayer, topRockLayer;
    private final LayerFactory<ForestType> forestTypeLayer;

    private final INoise2D temperatureNoise;
    private final INoise2D rainfallNoise;
    private final INoise2D layerHeightNoise;
    private final INoise2D forestWeirdnessNoise;
    private final INoise2D forestDensityNoise;

    private final LayerFactory<PlateTectonicsClassification> plateTectonicsInfo;

    public ChunkDataGenerator(long worldSeed, Random seedGenerator, TFCBiomeProvider.LayerSettings layerSettings)
    {
        List<IAreaFactory<FastArea>> rockLayers = TFCLayerUtil.createOverworldRockLayers(seedGenerator.nextLong(), layerSettings);
        this.bottomRockLayer = LayerFactory.rocks(rockLayers.get(0), layerSettings);
        this.middleRockLayer = LayerFactory.rocks(rockLayers.get(1), layerSettings);
        this.topRockLayer = LayerFactory.rocks(rockLayers.get(2), layerSettings);

        this.layerHeightNoise = new OpenSimplex2D(seedGenerator.nextLong()).octaves(2).scaled(40, 60).spread(0.015f);

        // Climate
        temperatureNoise = INoise1D.triangle(1, 0, 1f / (2f * TFCConfig.SERVER.temperatureScale.get()), 0)
            .extendX()
            .scaled(Climate.MINIMUM_TEMPERATURE_SCALE, Climate.MAXIMUM_TEMPERATURE_SCALE)
            .add(new OpenSimplex2D(seedGenerator.nextLong())
                .octaves(2)
                .spread(12f / TFCConfig.SERVER.temperatureScale.get())
                .scaled(-Climate.REGIONAL_TEMPERATURE_SCALE, Climate.REGIONAL_TEMPERATURE_SCALE));
        rainfallNoise = INoise1D.triangle(1, 0, 1f / (2f * TFCConfig.SERVER.rainfallScale.get()), 0)
            .extendY()
            .scaled(Climate.MINIMUM_RAINFALL, Climate.MAXIMUM_RAINFALL)
            .add(new OpenSimplex2D(seedGenerator.nextLong())
                .octaves(2)
                .spread(12f / TFCConfig.SERVER.rainfallScale.get())
                .scaled(-Climate.REGIONAL_RAINFALL_SCALE, Climate.REGIONAL_RAINFALL_SCALE))
            .flattened(Climate.MINIMUM_RAINFALL, Climate.MAXIMUM_RAINFALL);

        // Flora
        forestTypeLayer = LayerFactory.forest(TFCLayerUtil.createOverworldForestLayer(seedGenerator.nextLong(), layerSettings, IArtist.nope()));
        forestWeirdnessNoise = new OpenSimplex2D(seedGenerator.nextLong()).octaves(4).spread(0.0025f).map(x -> 1.1f * Math.abs(x)).flattened(0, 1);
        forestDensityNoise = new OpenSimplex2D(seedGenerator.nextLong()).octaves(4).spread(0.0025f).scaled(-0.2f, 1.2f).flattened(0, 1);

        // Plate Tectonics
        plateTectonicsInfo = LayerFactory.plateTectonics(TFCLayerUtil.createOverworldPlateTectonicInfoLayer(worldSeed, layerSettings));
    }

    @Override
    public void generate(ChunkData data, ChunkData.Status status)
    {
        ChunkPos pos = data.getPos();
        int chunkX = pos.getMinBlockX(), chunkZ = pos.getMinBlockZ();
        switch (status)
        {
            case EMPTY:
            case CLIENT:
                throw new IllegalStateException("Should not ever generate EMPTY or CLIENT status!");
            case PLATE_TECTONICS:
                generatePlateTectonics(data);
                break;
            case CLIMATE:
                generateClimate(data, chunkX, chunkZ);
                break;
            case ROCKS:
                generateRocks(data, chunkX, chunkZ);
                break;
            case FLORA:
                generateFlora(data, chunkX, chunkZ);
                break;
        }
    }

    @VisibleForTesting
    public INoise2D getTemperatureNoise()
    {
        return temperatureNoise;
    }

    @VisibleForTesting
    public INoise2D getRainfallNoise()
    {
        return rainfallNoise;
    }

    private void generatePlateTectonics(ChunkData data)
    {
        data.setPlateTectonicsInfo(plateTectonicsInfo.get(data.getPos().x, data.getPos().z));
    }

    private void generateClimate(ChunkData data, int chunkX, int chunkZ)
    {
        // Temperature / Rainfall
        float rainNW = rainfallNoise.noise(chunkX, chunkZ);
        float rainNE = rainfallNoise.noise(chunkX + 16, chunkZ);
        float rainSW = rainfallNoise.noise(chunkX, chunkZ + 16);
        float rainSE = rainfallNoise.noise(chunkX + 16, chunkZ + 16);
        data.setRainfall(rainNW, rainNE, rainSW, rainSE);

        float tempNW = temperatureNoise.noise(chunkX, chunkZ);
        float tempNE = temperatureNoise.noise(chunkX + 16, chunkZ);
        float tempSW = temperatureNoise.noise(chunkX, chunkZ + 16);
        float tempSE = temperatureNoise.noise(chunkX + 16, chunkZ + 16);
        data.setAverageTemp(tempNW, tempNE, tempSW, tempSE);
    }

    private void generateRocks(ChunkData data, int chunkX, int chunkZ)
    {
        // Rocks
        Rock[] bottomLayer = new Rock[256];
        Rock[] middleLayer = new Rock[256];
        Rock[] topLayer = new Rock[256];
        int[] rockLayerHeight = new int[256];

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

        data.setRockData(new RockData(bottomLayer, middleLayer, topLayer, rockLayerHeight));
    }

    private void generateFlora(ChunkData data, int chunkX, int chunkZ)
    {
        final ForestType forestType = forestTypeLayer.get(chunkX >> 4, chunkZ >> 4); // This layer is sampled per-chunk, to avoid the waste of two additional zoom layers
        final float forestWeirdness = forestWeirdnessNoise.noise(chunkX + 8, chunkZ + 8);
        final float forestDensity = forestDensityNoise.noise(chunkX + 8, chunkZ + 8);

        data.setFloraData(forestType, forestWeirdness, forestDensity);
    }
}
