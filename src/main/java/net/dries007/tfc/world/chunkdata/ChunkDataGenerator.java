/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.settings.RockSettings;

public interface ChunkDataGenerator
{
    /**
     * Generate the provided chunk data up to {@link ChunkData.Status#PARTIAL}. Mutates {@code data} and returns it. If the chunk data
     * @throws IllegalStateException if called with invalid or client-side chunk data.
     */
    ChunkData generate(ChunkData data);

    /**
     * Access the chunk data for the provided chunk, which may be an impostor chunk. Generate the data - if valid - up to
     * {@link ChunkData.Status#PARTIAL}, and return it.
     * @throws IllegalStateException if called with an empty or client-side chunk.
     */
    default ChunkData generate(ChunkAccess chunk)
    {
        final ChunkData data = ChunkData.get(chunk);
        generate(data);
        return data;
    }

    /**
     * Generates a clone of the chunk data at the given position up to {@link ChunkData.Status#PARTIAL}. This should only be used if the
     * underlying chunk cannot be accessed, and should be cached if possible, as this will <strong>always</strong> trigger generation, even
     * if the chunk data for that chunk has already been generated or is available elsewhere.
     */
    default ChunkData createAndGenerate(ChunkPos pos)
    {
        return generate(new ChunkData(this, pos));
    }

    default RockSettings generateSurfaceRock(int x, int z)
    {
        return generateRock(x, 0, z, 0, null); // Use y=0 because above y>125 we adjust actual height
    }

    /**
     * Generate the rock at the given {@code (x, y, z)} position. Coordinates must be <strong>block coordinates</strong>, not chunk-local.
     * {@code surfaceY} should be the view provided in {@link RockData}
     * @param cache An optional cache. If present, this will be used to reduce repeated computation when invoking
     * {@code generateRock()} repeatedly on points within the same chunk.
     */
    RockSettings generateRock(int x, int y, int z, int surfaceY, @Nullable ChunkRockDataCache cache);

    default void displayDebugInfo(List<String> tooltip, BlockPos pos, int surfaceY) {}
}
