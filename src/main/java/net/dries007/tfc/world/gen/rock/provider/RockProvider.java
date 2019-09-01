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
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Rock;
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
        // Chunk provider can be null during the attach capabilities event somehow
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
    private final LazyArea bottomRockFactory, middleRockFactory, topRockFactory;
    private final int bottomLayerBaseHeight, middleLayerBaseHeight;

    public RockProvider(IWorld world, TFCGenerationSettings settings)
    {
        this.cachedRockData = new FiniteLinkedHashMap<>(256);
        this.world = world;

        List<IAreaFactory<LazyArea>> factories = TFCLayerUtil.createOverworldRockLayers(settings, world.getSeed());
        this.bottomRockFactory = factories.get(0).make();
        this.middleRockFactory = factories.get(1).make();
        this.topRockFactory = factories.get(2).make();

        this.bottomLayerBaseHeight = 0;//settings.getBottomRockLayerBaseHeight();
        this.middleLayerBaseHeight = 0;//settings.getMiddleRockLayerBaseHeight();

        //this.layerHeightNoise = new SimplexNoise2D(world.getSeed()).octaves(2).spread(0.1f);
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
    private RockData getOrCreateRockData(ChunkPos pos)
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
        Rock[] middleLayer = new Rock[256];
        Rock[] topLayer = new Rock[256];

        int chunkX = pos.getXStart(), chunkZ = pos.getZStart();
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                bottomLayer[x + 16 * z] = TFCRegistries.ROCKS.getValue(bottomRockFactory.getValue(chunkX + x, chunkZ + z));
                middleLayer[x + 16 * z] = TFCRegistries.ROCKS.getValue(middleRockFactory.getValue(chunkX + x, chunkZ + z));
                topLayer[x + 16 * z] = TFCRegistries.ROCKS.getValue(topRockFactory.getValue(chunkX + x, chunkZ + z));

                // todo: create offsets
            }
        }

        RockData data = new RockData(bottomLayer, middleLayer, topLayer);
        cachedRockData.put(pos, data);
        return data;
    }
}
