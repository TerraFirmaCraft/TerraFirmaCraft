/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.server.ServerChunkProvider;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.TFCGenerationSettings;
import net.dries007.tfc.world.TFCOverworldChunkGenerator;
import net.dries007.tfc.world.layer.TFCLayerUtil;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class ChunkDataProvider
{
    /**
     * This is our equivalent to world.getChunkProvider() for rock layers, soil variants, and other chunk data
     * Only valid on server
     * Data is synced to client via custom packets
     */
    public static Optional<ChunkDataProvider> get(IWorld world)
    {
        // Chunk provider can be null during the attach capabilities event
        AbstractChunkProvider chunkProvider = world.getChunkProvider();
        if (chunkProvider instanceof ServerChunkProvider)
        {
            ChunkGenerator<?> chunkGenerator = ((ServerChunkProvider) chunkProvider).getChunkGenerator();
            if (chunkGenerator instanceof TFCOverworldChunkGenerator)
            {
                return Optional.of(((TFCOverworldChunkGenerator) chunkGenerator).getChunkDataProvider());
            }
        }
        return Optional.empty();
    }

    private final RockFactory bottomRockLayer, middleRockLayer, topRockLayer;

    private final INoise2D temperatureLayer;
    private final INoise2D rainfallLayer;
    private final INoise2D layerHeightNoise;

    public ChunkDataProvider(IWorld world, TFCGenerationSettings settings, Random seedGenerator)
    {
        List<IAreaFactory<LazyArea>> rockLayers = TFCLayerUtil.createOverworldRockLayers(world.getSeed(), settings);
        this.bottomRockLayer = new RockFactory(rockLayers.get(0));
        this.middleRockLayer = new RockFactory(rockLayers.get(1));
        this.topRockLayer = new RockFactory(rockLayers.get(2));

        int baseHeight = TFCConfig.COMMON.rockLayerHeight.get();
        int range = TFCConfig.COMMON.rockLayerSpread.get();
        this.layerHeightNoise = new SimplexNoise2D(world.getSeed()).octaves(2).scaled(baseHeight - range, baseHeight + range).spread(0.1f);

        // Climate
        this.temperatureLayer = TFCConfig.COMMON.temperatureLayerType.get().create(seedGenerator.nextLong(), TFCConfig.COMMON.temperatureLayerScale.get()).scaled(-10, 30);
        this.rainfallLayer = TFCConfig.COMMON.rainfallLayerType.get().create(seedGenerator.nextLong(), TFCConfig.COMMON.rainfallLayerScale.get()).scaled(0, 500).flattened(0, 500);
    }

    /**
     * Gets the chunk data from the cache, and generates it to the required status
     * Called during world generation when either a world context is unavailable (and we can't go world -> chunk generator -> chunk provider), or we need to avoid querying the capability as that would cause chunk loading deadlock.
     *
     * @see ChunkData#get(IWorld, ChunkPos, ChunkData.Status, boolean) for more generic uses
     * @see ChunkDataCache#get(ChunkPos) for directly querying the cache
     */
    public ChunkData get(ChunkPos pos, ChunkData.Status requiredStatus)
    {
        ChunkData data = ChunkDataCache.get(pos);
        return generateToStatus(pos, data, requiredStatus);
    }

    private ChunkData generateToStatus(ChunkPos pos, ChunkData data, ChunkData.Status status)
    {
        if (data.getStatus().isAtLeast(status))
        {
            return data;
        }
        int chunkX = pos.getXStart(), chunkZ = pos.getZStart();
        if (status.isAtLeast(ChunkData.Status.CLIMATE))
        {
            // Temperature / Rainfall
            float rainNW = rainfallLayer.noise(chunkX, chunkZ);
            float rainNE = rainfallLayer.noise(chunkX + 16, chunkZ);
            float rainSW = rainfallLayer.noise(chunkX, chunkZ + 16);
            float rainSE = rainfallLayer.noise(chunkX + 16, chunkZ + 16);
            data.setRainfall(rainNW, rainNE, rainSW, rainSE);

            float tempNW = temperatureLayer.noise(chunkX, chunkZ);
            float tempNE = temperatureLayer.noise(chunkX + 16, chunkZ);
            float tempSW = temperatureLayer.noise(chunkX, chunkZ + 16);
            float tempSE = temperatureLayer.noise(chunkX + 16, chunkZ + 16);
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
        return data;
    }
}
