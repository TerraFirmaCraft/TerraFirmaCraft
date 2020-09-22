/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.server.ServerChunkProvider;

import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.world.TFCGenerationSettings;
import net.dries007.tfc.world.layer.TFCLayerUtil;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class ChunkDataProvider
{
    /**
     * This is our equivalent to world.getChunkProvider() for rock layers, soil variants, and other chunk data
     * Only valid on logical server
     * Data is synced to client via custom packets
     */
    public static Optional<ChunkDataProvider> get(IWorld world)
    {
        // Chunk provider can be null during the attach capabilities event
        AbstractChunkProvider chunkProvider = world.getChunkSource();
        if (chunkProvider instanceof ServerChunkProvider)
        {
            ChunkGenerator chunkGenerator = ((ServerChunkProvider) chunkProvider).getGenerator();
            if (chunkGenerator instanceof IChunkDataProvidingChunkGenerator)
            {
                return Optional.of(((IChunkDataProvidingChunkGenerator) chunkGenerator).getChunkDataProvider());
            }
        }
        return Optional.empty();
    }

    private final RockFactory bottomRockLayer, middleRockLayer, topRockLayer;

    private final INoise2D temperatureNoise;
    private final INoise2D rainfallNoise;
    private final INoise2D layerHeightNoise;
    private final INoise2D forestBaseNoise;
    private final INoise2D forestWeirdnessNoise;
    private final INoise2D forestDensityNoise;

    public ChunkDataProvider(ISeedReader world, TFCGenerationSettings settings, Random seedGenerator)
    {
        List<IAreaFactory<LazyArea>> rockLayers = TFCLayerUtil.createOverworldRockLayers(world.getSeed(), settings);
        this.bottomRockLayer = new RockFactory(rockLayers.get(0));
        this.middleRockLayer = new RockFactory(rockLayers.get(1));
        this.topRockLayer = new RockFactory(rockLayers.get(2));

        int baseHeight = TFCConfig.COMMON.rockLayerHeight.get();
        int range = TFCConfig.COMMON.rockLayerSpread.get();
        this.layerHeightNoise = new SimplexNoise2D(world.getSeed()).octaves(2).scaled(baseHeight - range, baseHeight + range).spread(0.1f);

        // Climate
        this.temperatureNoise = TFCConfig.COMMON.temperatureLayerType.get().create(seedGenerator.nextLong(), TFCConfig.COMMON.temperatureLayerScale.get()).scaled(Climate.MINIMUM_TEMPERATURE_SCALE, Climate.MAXIMUM_TEMPERATURE_SCALE);
        this.rainfallNoise = TFCConfig.COMMON.rainfallLayerType.get().create(seedGenerator.nextLong(), TFCConfig.COMMON.rainfallLayerScale.get()).scaled(0, 500).flattened(0, 500);

        // Flora
        forestBaseNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).spread(0.002f).abs();
        forestWeirdnessNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).spread(0.0015f).scaled(0, 1);
        forestDensityNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).spread(0.0015f).scaled(0, 1);
    }

    public ChunkData get(BlockPos pos, ChunkData.Status requiredStatus)
    {
        return get(new ChunkPos(pos), requiredStatus);
    }

    /**
     * Get's the current chunk data during world generation
     * This will use the current world generation cache, and generate the data up to the requested status
     * Gets the chunk data from the cache, and generates it to the required status
     */
    public ChunkData get(ChunkPos pos, ChunkData.Status requiredStatus)
    {
        ChunkData data = ChunkDataCache.WORLD_GEN.getOrCreate(pos);
        if (!data.getStatus().isAtLeast(requiredStatus))
        {
            generateToStatus(pos, data, requiredStatus);
            data.setStatus(requiredStatus);
        }
        return data;
    }

    private void generateToStatus(ChunkPos pos, ChunkData data, ChunkData.Status status)
    {
        int chunkX = pos.getMinBlockX(), chunkZ = pos.getMinBlockZ();
        if (status.isAtLeast(ChunkData.Status.CLIMATE))
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
        if (status.isAtLeast(ChunkData.Status.ROCKS))
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
        if (status.isAtLeast(ChunkData.Status.FLORA))
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
}