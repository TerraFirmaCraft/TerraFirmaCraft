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
     * Generate the provided chunk data
     */
    ChunkData generate(ChunkData data);

    default ChunkData generate(ChunkAccess chunk)
    {
        return generate(ChunkData.get(chunk));
    }

    default ChunkData createAndGeneratePartial(ChunkPos pos)
    {
        return generate(new ChunkData(this, pos));
    }

    /**
     * Generate the rock at the given {@code (x, y, z)} position. Coordinates must be <strong>block coordinates</strong>, not chunk-local. {@code surfaceY} should be the view provided in {@link RockData}
     * @param cache An optional cache. If present, this will be used to reduce repeated computation when invoking {@code generateRock()} repeatedly on points within the same chunk.
     */
    RockSettings generateRock(int x, int y, int z, int surfaceY, @Nullable ChunkRockDataCache cache);

    default void displayDebugInfo(List<String> tooltip, BlockPos pos, int surfaceY) {}
}
