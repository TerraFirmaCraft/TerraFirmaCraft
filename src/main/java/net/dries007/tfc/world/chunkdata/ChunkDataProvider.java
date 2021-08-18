/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;

import net.dries007.tfc.world.settings.RockLayerSettings;

/**
 * This acts as a bridge between the {@link ChunkGenerator}, TFC's chunk data caches and tracking, and the {@link ChunkDataGenerator}.
 * In order to customize the chunk data generation, see {@link ChunkDataGenerator}
 */
public final class ChunkDataProvider
{
    public static ChunkDataProvider get(ChunkGenerator chunkGenerator)
    {
        if (chunkGenerator instanceof ChunkGeneratorExtension extension)
        {
            return extension.getChunkDataProvider();
        }
        throw new IllegalStateException("Tried to access ChunkDataProvider but none was present on " + chunkGenerator);
    }

    private final ChunkDataGenerator generator;
    private final RockLayerSettings rockLayerSettings;

    public ChunkDataProvider(ChunkDataGenerator generator, RockLayerSettings rockLayerSettings)
    {
        this.generator = generator;
        this.rockLayerSettings = rockLayerSettings;
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
        final ChunkData data = ChunkDataCache.WORLD_GEN.getOrCreate(pos, rockLayerSettings);
        if (data.getStatus() == ChunkData.Status.EMPTY)
        {
            generator.generate(data);
            data.setStatus(ChunkData.Status.FULL);
        }
        return data;
    }

    @Override
    public String toString()
    {
        return "ChunkDataProvider[" + generator.getClass().getSimpleName() + ']';
    }
}