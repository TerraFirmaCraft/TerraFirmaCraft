/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.soil.SandBlockType;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.types.TFCTypeManager;
import net.dries007.tfc.util.collections.FiniteLinkedHashMap;
import net.dries007.tfc.world.gen.TFCGenerationSettings;
import net.dries007.tfc.world.gen.TFCOverworldChunkGenerator;
import net.dries007.tfc.world.gen.layer.TFCLayerUtil;
import net.dries007.tfc.world.gen.rock.RockData;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class ChunkDataProvider
{
    /**
     * This is our equivalent to world.getChunkProvider() for rock layers, soil variants, and other chunk data
     * Only valid on server
     * Data is synced to client via custom packets
     */
    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static ChunkDataProvider get(World world)
    {
        AbstractChunkProvider chunkProvider = world.getChunkProvider();
        // Chunk provider can be null during the attach capabilities event
        if (chunkProvider instanceof ServerChunkProvider)
        {
            ChunkGenerator<?> chunkGenerator = ((ServerChunkProvider) chunkProvider).getChunkGenerator();
            if (chunkGenerator != null)
            {
                if (chunkGenerator instanceof TFCOverworldChunkGenerator)
                {
                    return ((TFCOverworldChunkGenerator) chunkGenerator).getChunkDataProvider();
                }
            }
        }
        return null;
    }

    private final Map<ChunkPos, ChunkData> cachedChunkData;
    private final IWorld world;
    private final LazyArea seedArea;
    private final int bottomLayerBaseHeight, middleLayerBaseHeight;

    private final INoise2D regionalTempNoise;
    private final INoise2D rainfallNoise;

    public ChunkDataProvider(IWorld world, TFCGenerationSettings settings, Random seedGenerator)
    {
        this.cachedChunkData = new FiniteLinkedHashMap<>(1024);
        this.world = world;

        this.seedArea = TFCLayerUtil.createOverworldRockLayers(world.getSeed(), settings).make();

        this.bottomLayerBaseHeight = 30;//settings.getBottomRockLayerBaseHeight();
        this.middleLayerBaseHeight = 30;//settings.getMiddleRockLayerBaseHeight();

        //this.layerHeightNoise = new SimplexNoise2D(world.getSeed()).octaves(2).spread(0.1f);

        // Climate
        // todo: config values
        this.regionalTempNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).scaled(-5.5f, 5.5f).flattened(-5, 5).spread(0.002f);
        this.rainfallNoise = new SimplexNoise2D(seedGenerator.nextLong()).octaves(4).scaled(-25, 525).flattened(0, 500).spread(0.002f);
    }

    @Nonnull
    public ChunkData get(ChunkPos pos)
    {
        if (world.chunkExists(pos.x, pos.z))
        {
            return get(world.getChunk(pos.x, pos.z));
        }
        return getOrCreate(pos);
    }

    @Nonnull
    public ChunkData get(IChunk chunkIn)
    {
        if (chunkIn instanceof Chunk)
        {
            LazyOptional<ChunkData> capability = ((Chunk) chunkIn).getCapability(ChunkDataCapability.CAPABILITY);
            return capability.orElseGet(() -> getOrCreate(chunkIn.getPos()));
        }
        return getOrCreate(chunkIn.getPos());
    }

    @Nonnull
    public ChunkData getOrCreate(ChunkPos pos)
    {
        if (cachedChunkData.containsKey(pos))
        {
            return cachedChunkData.get(pos);
        }
        return createData(pos);
    }

    @Nonnull
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

        int totalRocks = TFCTypeManager.ROCKS.getValues().size();
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                // From the seed, generate a combination of rock, sand, and soil profile
                int seed = seedArea.getValue(chunkX + x, chunkZ + z);
                int topRockValue = seed % totalRocks;
                topLayer[x + 16 * z] = TFCTypeManager.ROCKS.get(topRockValue);
                seed /= totalRocks;

                int bottomRockValue = seed % totalRocks;
                bottomLayer[x + 16 * z] = TFCTypeManager.ROCKS.get(bottomRockValue);
                seed /= totalRocks;

                int soilValue = seed % 3; // Only generate silty, sandy, and loamy
                soilLayer[x + 16 * z] = SoilBlockType.Variant.valueOf(soilValue);
                seed /= 3;

                int sandValue = seed % SandBlockType.TOTAL;
                sandLayer[x + 16 * z] = SandBlockType.valueOf(sandValue);
            }
        }

        data.setRockData(new RockData(bottomLayer, topLayer, soilLayer, sandLayer));
        data.setValid(true);
        return data;
    }
}
