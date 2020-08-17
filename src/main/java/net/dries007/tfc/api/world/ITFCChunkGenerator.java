/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.world;

import net.minecraft.world.IWorld;

import net.dries007.tfc.world.ChunkBlockReplacer;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

/**
 * Identifier interface for TFC-enabled chunk generators
 */
public interface ITFCChunkGenerator
{
    /**
     * @return The chunk data provider for this generator.
     * @see ChunkDataProvider#get(IWorld)
     */
    ChunkDataProvider getChunkDataProvider();

    /**
     * This is used to replace all blocks in the chunk during generation
     * It is exposed
     *
     * @return The chunk block replacer for this generator.
     */
    ChunkBlockReplacer getBlockReplacer();
}
