/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Map;
import com.google.common.collect.MapMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.settings.RockLayerSettings;

/**
 * This acts as a bridge between the {@link ChunkGenerator}, TFC's chunk data caches and tracking, and the {@link ChunkDataGenerator}.
 * In order to customize the chunk data generation, see {@link ChunkDataGenerator}
 */
public final class ChunkDataProvider
{
    public static ChunkDataProvider get(WorldGenLevel level)
    {
        return get(((ServerChunkCache) level.getChunkSource()).getGenerator());
    }

    public static ChunkDataProvider get(ChunkGenerator chunkGenerator)
    {
        if (chunkGenerator instanceof ChunkGeneratorExtension extension)
        {
            return extension.chunkDataProvider();
        }
        throw new IllegalStateException("Tried to access ChunkDataProvider but none was present on " + chunkGenerator);
    }

    private final ChunkDataGenerator generator;

    private final Map<ProtoChunk, ChunkData> partialChunkData;
    private final Map<ChunkPos, ProtoChunk> partialChunkLookup; // Needed in order to find pos -> chunks, as when we promote partial -> full, we don't have access to the protochunk.

    public ChunkDataProvider(ChunkDataGenerator generator)
    {
        this.generator = generator;

        // All references to chunks are kept as weak, and thus are removed automatically.
        this.partialChunkData = new MapMaker().weakKeys().concurrencyLevel(4).makeMap();
        this.partialChunkLookup = new MapMaker().weakValues().concurrencyLevel(4).makeMap();
    }

    public ChunkDataGenerator generator()
    {
        return generator;
    }

    public ChunkData get(WorldGenLevel level, BlockPos pos)
    {
        return get(level.getChunk(pos));
    }

    public ChunkData get(WorldGenLevel level, ChunkPos pos)
    {
        return get(level.getChunk(pos.x, pos.z));
    }

    /**
     * Get a chunk data for use during world generation, and generates it up to {@link ChunkData.Status#PARTIAL} status.
     * Note: the chunk data still may be incomplete, as parts can be set later during generation.
     */
    public ChunkData get(ChunkAccess chunk)
    {
        if (chunk instanceof ImposterProtoChunk imposter)
        {
            // Imposter chunks need to query the underlying level chunk data, which is stored via capability
            return ChunkData.get(imposter.getWrapped());
        }
        else if (chunk instanceof ProtoChunk proto)
        {
            // Ensure we only generate data for proto chunks
            final ChunkData data = partialChunkData.computeIfAbsent(proto, c -> {
                final ChunkData d = new ChunkData(generator, c.getPos());
                partialChunkLookup.put(c.getPos(), c);
                return d;
            });
            if (data.status() == ChunkData.Status.EMPTY)
            {
                generator.generate(data);
            }
            return data;
        }
        else if (chunk instanceof LevelChunk levelChunk)
        {
            // Use the level chunk data, although this should generally not happen (when accessing through world gen)
            return ChunkData.get(levelChunk);
        }
        throw new IllegalStateException("Cannot get chunk data from an unknown chunk: " + chunk.getClass() + " at " + chunk.getPos());
    }

    /**
     * Get chunk data, by looking up the position in the partial chunk lookup.
     * Use {@link #get(ChunkAccess)}, or one of the variants that uses a level if that is available as this may return an {@link ChunkData#EMPTY} instance.
     * As a result, should <strong>only</strong> be used for read-only access of the chunk data, when a level is not available.
     */
    public ChunkData get(ChunkPos pos)
    {
        final ChunkAccess chunk = partialChunkLookup.get(pos);
        if (chunk != null)
        {
            return get(chunk);
        }
        return ChunkData.EMPTY;
    }

    /**
     * Create, and load a partial chunk data from NBT.
     */
    public void loadPartial(ProtoChunk chunk, CompoundTag nbt)
    {
        partialChunkData.computeIfAbsent(chunk, c -> {
            ChunkData d = new ChunkData(generator, c.getPos());
            partialChunkLookup.put(c.getPos(), c);
            return d;
        }).deserializeNBT(nbt);
    }

    /**
     * Writes a chunk data's partial data to a tag, if it exists.
     */
    @Nullable
    public CompoundTag savePartial(ProtoChunk chunk)
    {
        final ChunkData data = partialChunkData.get(chunk);
        return data == null ? null : data.serializeNBT();
    }

    /**
     * Promote a partial chunk data instance to a full level chunk, or creates a new instance if none exists.
     * Removes the partial instance from internal cache.
     */
    public ChunkData promotePartialOrCreate(ChunkPos pos)
    {
        final ProtoChunk partial = partialChunkLookup.remove(pos);
        final ChunkData partialData = partialChunkData.remove(partial);
        if (partialData != null)
        {
            // Partial data exists, this is usually for a proto chunk.
            return partialData;
        }
        // No partial data, so we initialize a new chunk data. This is for data read from disk, which will then be initialized later.
        return new ChunkData(generator, pos);
    }

    @Override
    public String toString()
    {
        return "ChunkDataProvider[" + generator.getClass().getSimpleName() + ']';
    }
}