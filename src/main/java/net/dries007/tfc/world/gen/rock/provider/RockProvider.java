/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.rock.provider;

import java.util.List;
import java.util.Map;
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
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.soil.SandBlockType;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.types.TFCTypeManager;
import net.dries007.tfc.util.collections.FiniteLinkedHashMap;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;
import net.dries007.tfc.world.gen.TFCGenerationSettings;
import net.dries007.tfc.world.gen.TFCOverworldChunkGenerator;
import net.dries007.tfc.world.gen.layer.TFCLayerUtil;
import net.dries007.tfc.world.gen.rock.RockData;

public class RockProvider
{
    /**
     * This is our equivalent to world.getChunkProvider() for rock layers
     * Only valid on server
     */
    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static RockProvider getProvider(World world)
    {
        AbstractChunkProvider chunkProvider = world.getChunkProvider();
        // Chunk provider can be null during the attach capabilities event
        if (chunkProvider != null)
        {
            ChunkGenerator<?> chunkGenerator = chunkProvider.getChunkGenerator();
            if (chunkGenerator instanceof TFCOverworldChunkGenerator)
            {
                return ((TFCOverworldChunkGenerator) chunkGenerator).getRockProvider();
            }
        }
        return null;
    }

    private final Map<ChunkPos, RockData> cachedRockData;
    private final IWorld world;
    private final LazyArea seedArea;
    private final int bottomLayerBaseHeight, middleLayerBaseHeight;

    public RockProvider(IWorld world, TFCGenerationSettings settings)
    {
        this.cachedRockData = new FiniteLinkedHashMap<>(256);
        this.world = world;

        this.seedArea = TFCLayerUtil.createOverworldRockLayers(world.getSeed(), settings).make();

        this.bottomLayerBaseHeight = 30;//settings.getBottomRockLayerBaseHeight();
        this.middleLayerBaseHeight = 30;//settings.getMiddleRockLayerBaseHeight();

        //this.layerHeightNoise = new SimplexNoise2D(world.getSeed()).octaves(2).spread(0.1f);
    }

    public int getBottomLayerHeight(int yLevel)
    {
        return 52;
    }

    public int getMiddleLayerHeight(int yLevel)
    {
        return getBottomLayerHeight(yLevel) + 64;
    }

    @Nonnull
    public RockData getRockData(ChunkPos pos)
    {
        if (world.chunkExists(pos.x, pos.z))
        {
            return getRockData(world.getChunk(pos.x, pos.z));
        }
        return getOrCreateRockData(pos);
    }

    @Nonnull
    public RockData getRockData(IChunk chunkIn)
    {
        if (chunkIn instanceof Chunk)
        {
            LazyOptional<ChunkData> capability = ((Chunk) chunkIn).getCapability(ChunkDataCapability.CAPABILITY);
            return capability.map(ChunkData::getRockData).orElseGet(() -> getOrCreateRockData(chunkIn.getPos()));
        }
        return getOrCreateRockData(chunkIn.getPos());
    }

    @Nonnull
    public RockData getOrCreateRockData(ChunkPos pos)
    {
        if (cachedRockData.containsKey(pos))
        {
            return cachedRockData.get(pos);
        }
        return createData(pos);
    }

    @Nonnull
    private RockData createData(ChunkPos pos)
    {
        Rock[] bottomLayer = new Rock[256];
        Rock[] topLayer = new Rock[256];
        SoilBlockType[] soilLayer = new SoilBlockType[256];
        SandBlockType[] sandLayer = new SandBlockType[256];

        List<Rock> orderedRocks = TFCTypeManager.ROCKS.getOrderedValues();
        int totalSize = orderedRocks.size();
        int chunkX = pos.getXStart(), chunkZ = pos.getZStart();
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                // From the seed, generate a combination of rock, sand, and soil profile
                int seed = seedArea.getValue(chunkX + x, chunkZ + z);
                int topRockValue = seed % totalSize;
                topLayer[x + 16 * z] = TFCTypeManager.ROCKS.get(topRockValue);
                seed /= totalSize;

                int bottomRockValue = seed % totalSize;
                bottomLayer[x + 16 * z] = TFCTypeManager.ROCKS.get(bottomRockValue);
                seed /= totalSize;

                int soilValue = seed % SoilBlockType.TOTAL;
                soilLayer[x + 16 * z] = SoilBlockType.valueOf(soilValue);
                seed /= SoilBlockType.TOTAL;

                int sandValue = seed % SandBlockType.TOTAL;
                sandLayer[x + 16 * z] = SandBlockType.valueOf(sandValue);
            }
        }

        RockData data = new RockData(bottomLayer, topLayer, soilLayer, sandLayer);
        cachedRockData.put(pos, data);
        return data;
    }
}
