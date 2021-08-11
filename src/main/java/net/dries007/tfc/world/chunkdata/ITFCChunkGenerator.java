/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.world.level.chunk.ChunkGenerator;

import net.dries007.tfc.world.biome.ITFCBiomeSource;

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
    ChunkDataProvider getChunkDataProvider();

    /**
     * Override in {@link ChunkGenerator} to return a narrower type
     *
     * @return The biome provider / source for this generator
     */
    ITFCBiomeSource getBiomeSource();

    default ChunkGenerator chunkGenerator()
    {
        return (ChunkGenerator) this;
    }
}