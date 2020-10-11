/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

/**
 * Identifier interface for the TFC enabled chunk generators
 *
 * Any custom chunk generator wishing to use features from TFC MUST implement this and return a valid chunk data provider
 */
public interface IChunkDataProvidingChunkGenerator
{
    /**
     * @return The chunk data provider for this generator.
     */
    ChunkDataProvider getChunkDataProvider();
}