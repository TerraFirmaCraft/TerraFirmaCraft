/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.objects.blocks.soil.SandBlockType;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.objects.types.RockManager;
import net.dries007.tfc.util.collections.FiniteLinkedHashMap;
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

    private final Map<ChunkPos, ChunkData> cachedChunkData;
    private final IWorld world;
    private final LazyArea seedArea;

    private final INoise2D regionalTempNoise;
    private final INoise2D rainfallNoise;
    private final INoise2D layerHeightNoise;

    public ChunkDataProvider(IWorld world, TFCGenerationSettings settings, Random seedGenerator)
    {
        this.cachedChunkData = new FiniteLinkedHashMap<>(1024);
        this.world = world;

        this.seedArea = TFCLayerUtil.createOverworldRockLayers(world.getSeed(), settings).make();

        int baseHeight = TFCConfig.COMMON.rockLayerHeight.get();
        int range = TFCConfig.COMMON.rockLayerSpread.get();
        this.layerHeightNoise = new SimplexNoise2D(world.getSeed()).octaves(2).scaled(baseHeight - range, baseHeight + range).spread(0.1f);

        // Climate
        // todo: config values
        this.regionalTempNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).scaled(-5.5f, 5.5f).flattened(-5, 5).spread(0.002f);
        this.rainfallNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).scaled(-25, 525).flattened(0, 500).spread(0.002f);
    }

    public ChunkData get(BlockPos pos)
    {
        return get(new ChunkPos(pos));
    }

    public ChunkData get(ChunkPos pos)
    {
        if (world.chunkExists(pos.x, pos.z))
        {
            return get(world.getChunk(pos.x, pos.z));
        }
        return getOrCreate(pos);
    }

    public ChunkData get(IChunk chunkIn)
    {
        if (chunkIn instanceof Chunk)
        {
            LazyOptional<ChunkData> capability = ((Chunk) chunkIn).getCapability(ChunkDataCapability.CAPABILITY);
            return capability.orElseGet(() -> getOrCreate(chunkIn.getPos()));
        }
        return getOrCreate(chunkIn.getPos());
    }

    /**
     * Gets the chunk data from the local cache, or creates new chunk data
     * Does NOT query the chunk capability for chunk data
     * Used during world gen to avoid deadlocks where the chunk is in the process of being loaded
     */
    public ChunkData getOrCreate(BlockPos pos)
    {
        return getOrCreate(new ChunkPos(pos));
    }

    /**
     * Gets the chunk data from the local cache, or creates new chunk data
     * Does NOT query the chunk capability for chunk data
     * Used during world gen to avoid deadlocks where the chunk is in the process of being loaded
     */
    public ChunkData getOrCreate(ChunkPos pos)
    {
        if (cachedChunkData.containsKey(pos))
        {
            return cachedChunkData.get(pos);
        }
        return createData(pos);
    }

    private ChunkData createData(ChunkPos pos)
    {
        ChunkData data = new ChunkData();
        int chunkX = pos.getXStart(), chunkZ = pos.getZStart();
        cachedChunkData.put(pos, data);

        // Temperature / Rainfall
        data.setRainfall(rainfallNoise.noise(chunkX, chunkZ));
        data.setRegionalTemp(regionalTempNoise.noise(chunkX, chunkZ));

        // Rocks
        Rock[] bottomLayer = new Rock[256];
        Rock[] topLayer = new Rock[256];
        SoilBlockType.Variant[] soilLayer = new SoilBlockType.Variant[256];
        SandBlockType[] sandLayer = new SandBlockType[256];
        int[] rockLayerHeight = new int[256];

        int totalRocks = RockManager.INSTANCE.getValues().size();
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                // From the seed, generate a combination of rock, sand, and soil profile
                int seed = seedArea.getValue(chunkX + x, chunkZ + z);
                int topRockValue = seed % totalRocks;
                topLayer[x + 16 * z] = RockManager.INSTANCE.get(topRockValue);
                seed /= totalRocks;

                int bottomRockValue = seed % totalRocks;
                bottomLayer[x + 16 * z] = RockManager.INSTANCE.get(bottomRockValue);
                seed /= totalRocks;

                int soilValue = seed % 3; // Only generate silty, sandy, and loamy
                soilLayer[x + 16 * z] = SoilBlockType.Variant.valueOf(soilValue);
                seed /= 3;

                int sandValue = seed % SandBlockType.TOTAL;
                sandLayer[x + 16 * z] = SandBlockType.valueOf(sandValue);

                rockLayerHeight[x + 16 * z] = (int) layerHeightNoise.noise(x, z);
            }
        }

        data.setRockData(new RockData(bottomLayer, topLayer, soilLayer, sandLayer, rockLayerHeight));
        data.setValid(true);
        return data;
    }
}
