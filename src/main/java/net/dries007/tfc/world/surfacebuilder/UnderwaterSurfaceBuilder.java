/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class UnderwaterSurfaceBuilder extends SeedSurfaceBuilder<SurfaceBuilderConfig>
{
    private INoise2D variantNoise;

    public UnderwaterSurfaceBuilder()
    {
        super(SurfaceBuilderConfig::deserialize);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        TFCSurfaceBuilders.NORMAL.get().buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, getUnderwaterConfig(x, z, seed));
    }

    public SurfaceBuilderConfig getUnderwaterConfig(int x, int z, long seed)
    {
        setSeed(seed);
        float variantValue = variantNoise.noise(x, z);
        return variantValue > 0 ? SurfaceBuilder.SAND_CONFIG : SurfaceBuilder.GRAVEL_CONFIG;
    }

    @Override
    public void initSeed(long seed)
    {
        variantNoise = new SimplexNoise2D(seed).octaves(2).spread(0.015f);
    }
}
