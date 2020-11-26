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
 * In order to support this with a custom world generator, call {@link TFCSurfaceBuilders#applyIfPresent(ConfiguredSurfaceBuilder, Random, ChunkData, IChunk, Biome, int, int, int, double, long, BlockState, BlockState, int)} during surface building rather than {@link Biome#buildSurfaceAt(Random, IChunk, int, int, int, double, BlockState, BlockState, int, long)}
 *
 * @param <C> The config type. MUST be a supertype of the surface builder config type.
 */
public interface IContextSurfaceBuilder<C extends ISurfaceBuilderConfig>
{

    /**
     * @param chunkData Chunk data, generated to at least {@link net.dries007.tfc.world.chunkdata.ChunkData.Status#ROCKS}
     */
    void applyWithContext(ChunkData chunkData, Biome biomeIn, Random random, IChunk chunkIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, C config);
}
