/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.List;
import net.minecraft.util.RandomSource;

import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.ConcurrentArea;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.region.Units;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;

/**
 * This is the object responsible for generating TFC chunk data, in parallel with normal chunk generation.
 * <p>
 * In order to apply this to a custom chunk generator: the chunk generator MUST implement {@link ChunkGeneratorExtension} and return a {@link ChunkDataProvider}, which contains an instance of this generator.
 */
public interface ChunkDataGenerator
{
    static ConcurrentArea<RockSettings> createRockLayer(RandomSource seedGenerator, RockLayerSettings settings, List<RockSettings> rocks)
    {
        return new ConcurrentArea<>(TFCLayers.createOverworldRockLayer(seedGenerator.nextLong(), settings.getScale(), rocks.size()), rocks::get);
    }

    static void sampleRocksInLayers(ChunkData data, int chunkX, int chunkZ, ConcurrentArea<RockSettings> bottomRockLayer, ConcurrentArea<RockSettings> middleRockLayer, ConcurrentArea<RockSettings> topRockLayer, Noise2D layerHeightNoise)
    {
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

        data.setRockData(new RockData(bottomLayer, middleLayer, topLayer, rockLayerHeight));
    }

    static LerpFloatLayer sampleInterpolatedGridLayer(int chunkX, int chunkZ, LerpFloatLayer gridLayer)
    {
        final int gridTopLeftX = Units.blockToGrid(chunkX);
        final int gridTopLeftZ = Units.blockToGrid(chunkZ);

        return ChunkDataGenerator.sampleInterpolatedLayer(chunkX, chunkZ, (x, z) -> gridLayer.getValue(Units.blockToGridExact(x) - gridTopLeftX, Units.blockToGridExact(z) - gridTopLeftZ));
    }

    static LerpFloatLayer sampleInterpolatedLayer(int chunkX, int chunkZ, Noise2D noise)
    {
        final float valueNW = noise.noise(chunkX, chunkZ);
        final float valueNE = noise.noise(chunkX + 16, chunkZ);
        final float valueSW = noise.noise(chunkX, chunkZ + 16);
        final float valueSE = noise.noise(chunkX + 16, chunkZ + 16);

        return new LerpFloatLayer(valueNW, valueNE, valueSW, valueSE);
    }

    static void sampleForestLayers(ChunkData data, int chunkX, int chunkZ, ConcurrentArea<ForestType> forestTypeLayer, Noise2D forestWeirdnessNoise, Noise2D forestDensityNoise)
    {
        final ForestType forestType = forestTypeLayer.get(chunkX >> 4, chunkZ >> 4); // This layer is sampled per-chunk, to avoid the waste of two additional zoom layers
        final float forestWeirdness = forestWeirdnessNoise.noise(chunkX + 8, chunkZ + 8);
        final float forestDensity = forestDensityNoise.noise(chunkX + 8, chunkZ + 8);

        data.setFloraData(forestType, forestWeirdness, forestDensity);
    }

    /**
     * Generate the provided chunk data
     */
    void generate(ChunkData data);
}
