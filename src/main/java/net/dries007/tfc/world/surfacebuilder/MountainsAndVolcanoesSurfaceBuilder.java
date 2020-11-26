package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.biome.VolcanoNoise;
import net.dries007.tfc.world.noise.INoise2D;

public class MountainsAndVolcanoesSurfaceBuilder extends SeededSurfaceBuilder<SurfaceBuilderConfig>
{
    private INoise2D volcanoEasingNoise;

    public MountainsAndVolcanoesSurfaceBuilder(Codec<SurfaceBuilderConfig> codec)
    {
        super(codec);
    }

    @Override
    public void apply(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        float volcanoEasing = volcanoEasingNoise.noise(x, z);
        double heightNoise = noise * 2f + startHeight;
        if (volcanoEasing > 0.7f && heightNoise > 140)
        {
            TFCSurfaceBuilders.applySurfaceBuilder(TFCSurfaceBuilders.NORMAL.get(), random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, TFCSurfaceBuilders.BASALT_CONFIG.get());
        }
        else
        {
            TFCSurfaceBuilders.applySurfaceBuilder(TFCSurfaceBuilders.MOUNTAINS.get(), random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
        }
    }

    @Override
    protected void initSeed(long seed)
    {
        volcanoEasingNoise = VolcanoNoise.easing(seed);
    }
}
