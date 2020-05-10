package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.datafixers.Dynamic;

public class TFCMountainSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig>
{
    @SuppressWarnings("unused")
    public TFCMountainSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> configFactory)
    {
        super(configFactory);
    }

    public TFCMountainSurfaceBuilder()
    {
        super(SurfaceBuilderConfig::deserialize);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {

    }
}
