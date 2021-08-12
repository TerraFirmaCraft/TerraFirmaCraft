/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

/**
 * This is the object responsible for generating TFC chunk data, in parallel with normal chunk generation.
 *
 * In order to apply this to a custom chunk generator: the chunk generator MUST implement {@link ChunkGeneratorExtension} and return a {@link ChunkDataProvider}, which contains an instance of this generator.
 */
public interface ChunkDataGenerator
{
    /**
     * Generate the provided chunk data
     */
    void generate(ChunkData data);
}
