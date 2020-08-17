/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorldReader;

import net.dries007.tfc.util.Helpers;

/**
 * Cache of chunk data
 * Used for various purposes:
 * {@link ChunkDataCache#CLIENT} and {@link ChunkDataCache#SERVER} are logical sided caches, used for when chunk data is needed without a world context. Care must be taken to choose the cache for the correct logical side
 * {@link ChunkDataCache#WORLD_GEN} is used for chunk data during world generation, as it's being generated. It is cleared once the chunk is completely generated
 */
public class ChunkDataCache
{
    /**
     * This is a cache of client side chunk data, used for when there is no world context available.
     * It is serialized and synced on chunk watch
     */
    public static final ChunkDataCache CLIENT = new ChunkDataCache();


    /**
     * This is a cache of server side chunk data.
     */
    public static final ChunkDataCache SERVER = new ChunkDataCache();

    /**
     * This is a cache of incomplete chunk data used by world generation
     * It is generated in stages:
     * - {@link ChunkData.Status#CLIMATE} during biome generation to generate climate variants
     * - {@link ChunkData.Status#ROCKS} during surface generation, later used for feature generation
     * When the chunk is finished generating on server, this cache is cleared and the data is saved to the chunk capability for long term storage
     */
    public static final ChunkDataCache WORLD_GEN = new ChunkDataCache();

    /**
     * Gets the normal (not world gen) cache of chunk data for the current logical side
     */
    public static ChunkDataCache get(IWorldReader world)
    {
        return Helpers.isRemote(world) ? CLIENT : SERVER;
    }

    /**
     * Gets the normal (not world en) cache of chunk data based on a heuristic - pick the one that is not empty
     * On dedicated servers / clients only one will be non-empty
     * On logical server / clients, this will default to using the server cache
     * DO NOT call this unless absolutely necessary, e.g. returning from a vanilla method which is called from both logical sides
     */
    public static ChunkDataCache getUnsided()
    {
        return SERVER.cache.isEmpty() ? CLIENT : SERVER;
    }

    private final Map<ChunkPos, ChunkData> cache;

    /**
     * Creates an infinite size cache that must be managed to not create memory leaks
     */
    private ChunkDataCache()
    {
        cache = new HashMap<>();
    }

    public ChunkData getOrEmpty(BlockPos pos)
    {
        return getOrEmpty(new ChunkPos(pos));
    }

    public ChunkData getOrEmpty(ChunkPos pos)
    {
        return cache.getOrDefault(pos, ChunkData.EMPTY);
    }

    @Nullable
    public ChunkData get(BlockPos pos)
    {
        return get(new ChunkPos(pos));
    }

    @Nullable
    public ChunkData get(ChunkPos pos)
    {
        return cache.get(pos);
    }

    @Nullable
    public ChunkData remove(ChunkPos pos)
    {
        return cache.remove(pos);
    }

    public void update(ChunkPos pos, ChunkData data)
    {
        cache.put(pos, data);
    }

    public ChunkData getOrCreate(ChunkPos pos)
    {
        return cache.computeIfAbsent(pos, ChunkData::new);
    }
}
