package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.noise.NoiseUtil;

public abstract class ContextSurfaceBuilder<C extends ISurfaceBuilderConfig> extends SurfaceBuilder<C>
{
    protected ContextSurfaceBuilder(Codec<C> codec)
    {
        super(codec);
    }

    public abstract void apply(SurfaceBuilderContext context, Biome biome, int x, int z, int startHeight, double noise, double slope, float temperature, float rainfall, boolean saltWater, C config);

    @Override
    public void apply(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, C config)
    {
        throw new IllegalStateException("Surface Builder [" + getRegistryName() + "] of class [" + getClass().getSimpleName() + "] is a ContextSurfaceBuilder and cannot be invoked directly.");
    }

    /**
     * Calculates a surface depth value, taking into account altitude and slope
     * @param y The y value. Values over sea level (96) are treated as lower depth
     * @param slope The slope. Expecting values roughly in the range [0, 13]. Higher values are treated as extreme slopes.
     * @param maxDepth The maximum surface depth
     * @param falloff A value between 0 and 1 indicating how quickly the depth decays w.r.t increasing slope or altitude.
     * @return a surface depth in the range [0, maxSlope]
     */
    protected int calculateAltitudeSlopeSurfaceDepth(int y, double slope, int maxDepth, double falloff)
    {
        final int seaLevel = TFCChunkGenerator.SEA_LEVEL;
        double slopeFactor = MathHelper.clamp(slope / 15d, 0, 1); // in [0, 1]
        double altitudeFactor = MathHelper.clamp((y - seaLevel) / 100d, 0, 1);
        if (y < TFCChunkGenerator.SEA_LEVEL)
        {
            // Below sea level, slope influence falls off, for the first 15m, then stays at a constant 0.4x influence
            slopeFactor *= MathHelper.clamp(1 - (seaLevel - y) / 15d, 0.4, 1);
        }
        double t = (1 - altitudeFactor) * (1 - slopeFactor);
        t = (t - falloff) / (1 - falloff);
        t = (t * maxDepth) + 0.3d;
        return MathHelper.clamp((int) t, 0, maxDepth);
    }
}
