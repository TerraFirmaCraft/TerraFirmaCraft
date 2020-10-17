package net.dries007.tfc.world.chunkdata;

/**
 * This is the object responsible for generating TFC chunk data, in parallel with normal chunk generation.
 *
 * In order to apply this to a custom chunk generator: the chunk generator MUST implement {@link ITFCChunkGenerator} and return a {@link ChunkDataProvider}, which contains an instance of this generator.
 */
public interface IChunkDataGenerator
{
    void generate(ChunkData data, ChunkData.Status status);
}
