package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;

import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Indicates that a surface builder requires more (or possibly just different) context in order to generate
 *
 * @param <C> The config type. MUST be a supertype of the surface builder config type.
 */
public interface IContextSurfaceBuilder<C extends ISurfaceBuilderConfig>
{
    void apply(Random random, ChunkData chunkData, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, long seed, C config);
}
