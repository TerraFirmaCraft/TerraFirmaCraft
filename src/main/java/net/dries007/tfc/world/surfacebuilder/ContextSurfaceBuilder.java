/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.TFCChunkGenerator;

public abstract class ContextSurfaceBuilder<C extends SurfaceBuilderConfiguration> extends SurfaceBuilder<C>
{
    protected ContextSurfaceBuilder(Codec<C> codec)
    {
        super(codec);
    }

    public abstract void apply(SurfaceBuilderContext context, Biome biome, int x, int z, int startHeight, double noise, double slope, float temperature, float rainfall, boolean saltWater, C config);

    @Override
    public void apply(Random random, ChunkAccess chunk, Biome biome, int x, int z, int height, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, int minSurfaceLevel, long seed, C config)
    {
        throw new IllegalStateException("Surface Builder [" + getRegistryName() + "] of class [" + getClass().getSimpleName() + "] is a ContextSurfaceBuilder and cannot be invoked directly.");
    }

    /**
     * Calculates a surface depth value, taking into account altitude and slope
     *
     * @param y                  The y value. Values over sea level (96) are treated as lower depth
     * @param slope              The slope. Expecting values roughly in the range [0, 13]. Higher values are treated as extreme slopes.
     * @param maxDepth           The maximum surface depth
     * @param falloff            A value between 0 and 1 indicating how quickly the depth decays w.r.t increasing slope or altitude.
     * @param minimumReturnValue The minimum possible slope. Typically 0, -1 is used as a flag value for not placing the top surface layer on occasion.
     * @return a surface depth in the range [minimumReturnValue, maxSlope]
     */
    protected int calculateAltitudeSlopeSurfaceDepth(int y, double slope, int maxDepth, double falloff, int minimumReturnValue)
    {
        final int seaLevel = TFCChunkGenerator.SEA_LEVEL_Y;
        double slopeFactor = Mth.clamp(slope / 15d, 0, 1); // in [0, 1]
        double altitudeFactor = Mth.clamp((y - seaLevel) / 100d, 0, 1);
        if (y < TFCChunkGenerator.SEA_LEVEL_Y)
        {
            // Below sea level, slope influence falls off, and levels off at 40% influence
            slopeFactor *= Mth.clamp(1 - (seaLevel - y) / 15d, 0.4, 1);
        }
        double t = (1 - altitudeFactor) * (1 - slopeFactor);
        t = (t - falloff) / (1 - falloff);
        t = (t * maxDepth) + 0.3d;
        return Mth.clamp((int) t, minimumReturnValue, maxDepth);
    }
}
