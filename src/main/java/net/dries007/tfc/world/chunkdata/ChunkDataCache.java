/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.network.PacketDistributor;

import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

/**
 * Sided cache of chunk data instances, for when a world context is unavailable.
 * Automatically synchronized on chunk watch / unwatch events, and updated on chunk load and unload.
 * This is only valid in the overworld.
 */
public final class ChunkDataCache
{
    /**
     * This is a cache of client side chunk data, used for when there is no world context available.
     * It is synced on chunk watch / unwatch
     */
    public static final ChunkDataCache CLIENT = new ChunkDataCache("client");

    /**
     * This is a cache of server side chunk data.
     * It is not synced, it is updated on chunk load / unload
     */
    public static final ChunkDataCache SERVER = new ChunkDataCache("server");

    /**
     * This is a set of chunk positions which have been queued for chunk watch, but were not loaded or generated at the time.
     * As a result, no data was able to be sent to the client cache. In these situations, we wait for chunk load on server, and if the chunk is present here, it is re-synchronized.
     */
    public static final WatchQueue WATCH_QUEUE = new WatchQueue();

    /**
     * Gets the normal (not world gen) cache of chunk data for the current logical side
     */
    public static ChunkDataCache get(LevelReader world)
    {
        return Helpers.isClientSide(world) ? CLIENT : SERVER;
    }

    private final Map<ChunkPos, ChunkData> cache;
    private final String name;

    private ChunkDataCache(String name)
    {
        this.name = name;
        this.cache = new HashMap<>();
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

    public ChunkData computeIfAbsent(ChunkPos pos, Function<ChunkPos, ChunkData> mappingFunction)
    {
        return cache.computeIfAbsent(pos, mappingFunction);
    }

    @Override
    public String toString()
    {
        return "ChunkDataCache[" + name + ']';
    }

    public static class WatchQueue
    {
        private final Map<ChunkPos, Set<ServerPlayer>> queue;

        private WatchQueue()
        {
            queue = new HashMap<>(256);
        }

        public void enqueueUnloadedChunk(ChunkPos pos, ServerPlayer player)
        {
            queue.computeIfAbsent(pos, key -> new HashSet<>()).add(player);
        }

        public void dequeueChunk(ChunkPos pos, ServerPlayer player)
        {
            Set<ServerPlayer> players = queue.get(pos);
            if (players != null)
            {
                players.remove(player);
                if (players.isEmpty())
                {
                    queue.remove(pos);
                }
            }
        }

        public void dequeueLoadedChunk(ChunkPos pos, ChunkData data)
        {
            if (queue.containsKey(pos))
            {
                final Set<ServerPlayer> players = queue.remove(pos);
                for (ServerPlayer player : players)
                {
                    PacketHandler.send(PacketDistributor.PLAYER.with(() -> player), data.getUpdatePacket());
                }
            }
        }
    }
}