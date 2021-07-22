/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.ChunkGenerator;

/**
 * This acts as a bridge between the {@link ChunkGenerator}, TFC's chunk data caches and tracking, and the {@link IChunkDataGenerator}.
 * In order to customize the chunk data generation, see {@link IChunkDataGenerator}
 */
public final class ChunkDataProvider
{
    public static ChunkDataProvider get(ChunkGenerator chunkGenerator)
    {
        if (chunkGenerator instanceof ITFCChunkGenerator)
        {
            return ((ITFCChunkGenerator) chunkGenerator).getChunkDataProvider();
        }
        throw new IllegalStateException("Tried to access ChunkDataProvider but none was present on " + chunkGenerator);
    }

    private final IChunkDataGenerator generator;

    public ChunkDataProvider(IChunkDataGenerator generator)
    {
        this.generator = generator;
    }

    /**
     * Gets the chunk data for a chunk, during world generation.
     * The default implementation generates chunk data using TFC semantics, and stores generated data in {@link ChunkDataCache#WORLD_GEN}
     * Implementors are free to return any form of data.
     *
     * @param pos The chunk position
     * @return A chunk data for the provided chunk pos
     */
    public final ChunkData get(BlockPos pos)
    {
        return get(new ChunkPos(pos));
    }

    /**
     * Gets the chunk data for a chunk, during world generation, fully generated.
     *
     * @param pos The chunk position
     * @return A chunk data for the provided chunk pos
     */
    public final ChunkData get(ChunkPos pos)
    {
        final ChunkData data = ChunkDataCache.WORLD_GEN.getOrCreate(pos);
        if (data.getStatus() == ChunkData.Status.EMPTY)
        {
            generator.generate(data);
            data.setStatus(ChunkData.Status.FULL);
        }
        return data;
    }

    @VisibleForTesting
    public IChunkDataGenerator getGenerator()
    {
        return generator;
    }

    @Override
    public String toString()
    {
        return "ChunkDataProvider[" + generator.getClass().getSimpleName() + ']';
    }
}