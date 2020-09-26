/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.world.IWorld;

/**
 * Identifier interface for TFC-enabled chunk generators
 */
public interface IChunkDataProvidingChunkGenerator
{
    /**
     * @return The chunk data provider for this generator.
     * @see ChunkDataProvider#get(IWorld)
     */
    ChunkDataProvider getChunkDataProvider();
}
