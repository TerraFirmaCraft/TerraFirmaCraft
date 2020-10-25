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
import net.dries007.tfc.world.biome.TFCBiomeProvider;
import net.dries007.tfc.world.layer.TFCLayerUtil;
import net.dries007.tfc.world.noise.INoise1D;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

/**
 * This is TFC's default chunk data generator.
 * If you want to use a vanilla or custom chunk generator, you can use this chunk data generator, or attach your own.
 *
 * @see net.dries007.tfc.world.chunkdata.IChunkDataGenerator
 */
public class ChunkDataGenerator implements IChunkDataGenerator
{
    private final RockFactory bottomRockLayer, middleRockLayer, topRockLayer;

    private final INoise2D temperatureNoise;
    private final INoise2D rainfallNoise;
    private final INoise2D layerHeightNoise;
    private final INoise2D forestBaseNoise;
    private final INoise2D forestWeirdnessNoise;
    private final INoise2D forestDensityNoise;

    public ChunkDataGenerator(Random seedGenerator, TFCBiomeProvider.LayerSettings layerSettings)
    {
        List<IAreaFactory<LazyArea>> rockLayers = TFCLayerUtil.createOverworldRockLayers(seedGenerator.nextLong(), layerSettings);
        this.bottomRockLayer = new RockFactory(rockLayers.get(0));
        this.middleRockLayer = new RockFactory(rockLayers.get(1));
        this.topRockLayer = new RockFactory(rockLayers.get(2));

        this.layerHeightNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(2).scaled(40, 60).spread(0.015f);

        // Climate
        temperatureNoise = INoise1D.triangle(1, 0, 1f / (2f * TFCConfig.SERVER.temperatureScale.get()), 0)
            .extendX()
            .scaled(Climate.MINIMUM_TEMPERATURE_SCALE, Climate.MAXIMUM_TEMPERATURE_SCALE)
            .add(new SimplexNoise2D(seedGenerator.nextLong())
                .octaves(2)
                .spread(12f / TFCConfig.SERVER.temperatureScale.get())
                .scaled(-Climate.REGIONAL_TEMPERATURE_SCALE, Climate.REGIONAL_TEMPERATURE_SCALE));
        rainfallNoise = INoise1D.triangle(1, 0, 1f / (2f * TFCConfig.SERVER.rainfallScale.get()), 0)
            .extendY()
            .scaled(Climate.MINIMUM_RAINFALL, Climate.MAXIMUM_RAINFALL)
            .add(new SimplexNoise2D(seedGenerator.nextLong())
                .octaves(2)
                .spread(12f / TFCConfig.SERVER.rainfallScale.get())
                .scaled(-Climate.REGIONAL_RAINFALL_SCALE, Climate.REGIONAL_RAINFALL_SCALE))
            .flattened(Climate.MINIMUM_RAINFALL, Climate.MAXIMUM_RAINFALL);

        // Flora
        forestBaseNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).spread(0.002f).abs();
        forestWeirdnessNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).spread(0.0015f).map(x -> 0.5f * Math.abs(x));
        forestDensityNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).spread(0.0015f).scaled(0, 1);
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

    @VisibleForTesting
    public INoise2D getForestDensityNoise()
    {
        return forestDensityNoise;
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
        // Tree generation layers
        float forestBase = forestBaseNoise.noise(chunkX + 8, chunkZ + 8);
        ForestType forestType = ForestType.NONE;
        if (forestBase > 0.4f)
        {
            forestType = ForestType.OLD_GROWTH;
        }
        else if (forestBase > 0.18f)
        {
            forestType = ForestType.NORMAL;
        }
        else if (forestBase > 0.06f)
        {
            forestType = ForestType.SPARSE;
        }

        float forestWeirdness = forestWeirdnessNoise.noise(chunkX + 8, chunkZ + 8);
        float forestDensity = forestDensityNoise.noise(chunkX + 8, chunkZ + 8);

        data.setFloraData(forestType, forestWeirdness, forestDensity);
    }
}
