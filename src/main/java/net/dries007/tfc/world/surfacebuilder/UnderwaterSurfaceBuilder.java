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

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class UnderwaterSurfaceBuilder extends SeededSurfaceBuilder<SurfaceBuilderConfig>
{
    private INoise2D variantNoise;

    public UnderwaterSurfaceBuilder(Codec<SurfaceBuilderConfig> codec)
    {
        super(codec);
    }

    @Override
    public void apply(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        TFCSurfaceBuilders.applySurfaceBuilder(TFCSurfaceBuilders.NORMAL.get(), random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, getUnderwaterConfig(x, z, seed)); // todo: the getting of the underwater config here only actually applies to the above water section, this is quite irrelevant and should be rewritten
    }

    public SurfaceBuilderConfig getUnderwaterConfig(int x, int z, long seed)
    {
        initNoise(seed);
        float variantValue = variantNoise.noise(x, z);
        return variantValue > 0 ? SurfaceBuilder.CONFIG_FULL_SAND : SurfaceBuilder.CONFIG_GRAVEL;
    }

    @Override
    public void initSeed(long seed)
    {
        variantNoise = new OpenSimplex2D(seed).octaves(2).spread(0.015f);
    }
}