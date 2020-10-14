/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import net.dries007.tfc.world.biome.ITFCBiomeProvider;

/**
 * Identifier interface for the TFC enabled chunk generators
 *
 * Any custom chunk generator wishing to use features from TFC MUST implement this and return a valid chunk data provider
 * This is also used in various places (such as spawn position placement) to identify TFC world generators
 */
public interface ITFCChunkGenerator
{
    /**
     * @return The chunk data provider for this generator.
     */
    IChunkDataProvider getChunkDataProvider();

    /**
     * Override in {@link net.minecraft.world.gen.ChunkGenerator} to return a narrower type
     *
     * @return The biome provider / source for this generator
     */
    ITFCBiomeProvider getBiomeProvider();
}