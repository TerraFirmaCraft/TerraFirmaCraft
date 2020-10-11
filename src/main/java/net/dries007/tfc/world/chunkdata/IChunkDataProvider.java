package net.dries007.tfc.world.chunkdata;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * Marker for chunk data generators and providers.
 * This is responsible for generating all of TFC custom world generation data which is not part of vanilla chunk generation
 *
 * Any addons, or compat mods wishing to use non-TFC world generation SHOULD provide an implementation of this on their chunk generator, via {@link IChunkDataProvidingChunkGenerator}
 */
public interface IChunkDataProvider
{
    /**
     * Directly tries to access the chunk data provider through the overworld.
     */
    static IChunkDataProvider getOrThrow()
    {
        return getOrThrow(ServerLifecycleHooks.getCurrentServer().overworld());
    }

    static IChunkDataProvider getOrThrow(IWorld world)
    {
        AbstractChunkProvider chunkProvider = world.getChunkSource();
        if (chunkProvider instanceof ServerChunkProvider)
        {
            return getOrThrow(((ServerChunkProvider) chunkProvider).getGenerator());
        }
        throw new IllegalStateException("Tried to access ChunkDataProvider but no ServerChunkProvider was found on world: " + world);
    }

    /**
     * Tries to access the chunk data provider through the chunk generator, mostly used during feature generation when we have direct access to the generator.
     */
    static IChunkDataProvider getOrThrow(ChunkGenerator chunkGenerator)
    {
        if (chunkGenerator instanceof IChunkDataProvidingChunkGenerator)
        {
            return ((IChunkDataProvidingChunkGenerator) chunkGenerator).getChunkDataProvider();
        }
        throw new IllegalStateException("Tried to access ChunkDataProvider but none was present on " + chunkGenerator);
    }

    /**
     * Gets the chunk data for a chunk, during world generation.
     * The default implementation generates chunk data using TFC semantics, and stores generated data in {@link ChunkDataCache#WORLD_GEN}
     * Implementors are free to return any form of data.
     *
     * @param pos            The chunk position
     * @param requiredStatus The minimum status of the chunk data returned
     * @return A chunk data for the provided chunk pos
     */
    ChunkData get(ChunkPos pos, ChunkData.Status requiredStatus);

    default ChunkData get(BlockPos pos, ChunkData.Status requiredStatus)
    {
        return get(new ChunkPos(pos), requiredStatus);
    }
}
