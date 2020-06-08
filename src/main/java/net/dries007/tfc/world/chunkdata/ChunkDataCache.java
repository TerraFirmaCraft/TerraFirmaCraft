/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

/**
 * Client side position based cache of chunk data.
 * Used when there isn't a world context (so this assumes we are in the overworld)
 */
public class ChunkDataCache
{
    // This acts as both a server + client cache
    private static final Map<ChunkPos, ChunkData> CACHE = new HashMap<>();

    /**
     * @see ChunkDataCache#get(ChunkPos)
     */
    public static ChunkData get(BlockPos pos)
    {
        return get(new ChunkPos(pos));
    }

    /**
     * Directly get the chunk data from the cache, when no world context is available, or the logical side is unknown.
     *
     * @see ChunkData#get(IWorld, ChunkPos, ChunkData.Status, boolean) for when a world is available
     * @see ChunkDataProvider#get(ChunkPos, ChunkData.Status) for when the side (server) is known and generation should be forced
     */
    public static ChunkData get(ChunkPos pos)
    {
        return CACHE.computeIfAbsent(pos, key -> new ChunkData());
    }

    public static void remove(ChunkPos pos)
    {
        CACHE.remove(pos);
    }

    public static void update(ChunkPos pos, ChunkData data)
    {
        if (CACHE.containsKey(pos))
        {
            // There's original data here, so instead of remapping, copy into the existing data
            // This allows callers to get a piece of data and have it be populated when the server replies
            ChunkData original = CACHE.get(pos);
            if (original != data)
            {
                original.copyFrom(data);
            }
        }
        else
        {
            CACHE.put(pos, data);
        }
    }
}
