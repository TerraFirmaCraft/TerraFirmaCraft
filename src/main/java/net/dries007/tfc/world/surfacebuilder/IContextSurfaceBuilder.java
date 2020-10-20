package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;

import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Indicates that a surface builder requires more (or possibly just different) context in order to generate.
 * In order to support this with a custom world generator, call {@link IContextSurfaceBuilder#applyIfPresent(ConfiguredSurfaceBuilder, Random, ChunkData, IChunk, Biome, int, int, int, double, long, BlockState, BlockState, int)} during surface building rather than {@link Biome#buildSurfaceAt(Random, IChunk, int, int, int, double, BlockState, BlockState, int, long)}
 *
 * @param <C> The config type. MUST be a supertype of the surface builder config type.
 */
public interface IContextSurfaceBuilder<C extends ISurfaceBuilderConfig>
{
    /**
     * Tries to apply a {@link IContextSurfaceBuilder} if it exists, otherwise delegates to the standard method.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static <C extends ISurfaceBuilderConfig> void applyIfPresent(ConfiguredSurfaceBuilder<C> configuredSurfaceBuilder, Random random, ChunkData chunkData, IChunk chunk, Biome biome, int posX, int posZ, int posY, double noise, long seed, BlockState defaultBlock, BlockState defaultFluid, int seaLevel)
    {
        configuredSurfaceBuilder.initNoise(seed);
        if (configuredSurfaceBuilder.surfaceBuilder instanceof IContextSurfaceBuilder)
        {
            // Need an ugly cast here to verify the config type
            ((IContextSurfaceBuilder) configuredSurfaceBuilder.surfaceBuilder).applyWithContext(chunkData, biome, random, chunk, posX, posZ, posY, noise, defaultBlock, defaultFluid, seaLevel, seed, configuredSurfaceBuilder.config);
        }
        else
        {
            configuredSurfaceBuilder.surfaceBuilder.apply(random, chunk, biome, posX, posZ, posY, noise, defaultBlock, defaultFluid, seaLevel, seed, configuredSurfaceBuilder.config);
        }
    }

    /**
     * @param chunkData Chunk data, generated to at least {@link net.dries007.tfc.world.chunkdata.ChunkData.Status#ROCKS}
     */
    void applyWithContext(ChunkData chunkData, Biome biomeIn, Random random, IChunk chunkIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, C config);
}
