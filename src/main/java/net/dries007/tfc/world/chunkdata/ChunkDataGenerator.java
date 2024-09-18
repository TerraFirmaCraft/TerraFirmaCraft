/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

import net.dries007.tfc.common.TFCAttachments;

public interface ChunkDataGenerator extends RockGenerator
{
    /**
     * Generate the provided chunk data
     */
    ChunkData generate(ChunkData data);

    default ChunkData generate(ChunkAccess chunk)
    {
        return Objects.requireNonNull(chunk.setData(TFCAttachments.CHUNK_DATA.get(), generate(ChunkData.get(chunk))));
    }

    default ChunkData createAndGeneratePartial(ChunkPos pos)
    {
        return generate(new ChunkData(this, pos));
    }

    default void displayDebugInfo(List<String> tooltip, BlockPos pos, int surfaceY) {}
}
