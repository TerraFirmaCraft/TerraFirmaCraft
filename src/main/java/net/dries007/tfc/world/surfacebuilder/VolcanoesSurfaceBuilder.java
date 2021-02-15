/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.biome.VolcanoNoise;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.CellularNoiseType;

public class VolcanoesSurfaceBuilder extends SeededSurfaceBuilder<ParentedSurfaceBuilderConfig> implements IContextSurfaceBuilder<ParentedSurfaceBuilderConfig>
{
    private Cellular2D cellNoise;

    public VolcanoesSurfaceBuilder(Codec<ParentedSurfaceBuilderConfig> codec)
    {
        super(codec);
    }

    @Override
    public void applyWithContext(IWorld worldIn, ChunkData chunkData, Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, ParentedSurfaceBuilderConfig config)
    {
        final BiomeVariants variants = TFCBiomes.getExtensionOrThrow(worldIn, biomeIn).getVariants();
        if (variants.isVolcanic())
        {
            // Sample volcano noise
            final float distance = cellNoise.noise(x, z, CellularNoiseType.F1);
            final float value = cellNoise.noise(x, z, CellularNoiseType.VALUE);
            final float easing = VolcanoNoise.calculateEasing(distance);
            final double heightNoise = noise * 2f + startHeight;
            if (value < variants.getVolcanoChance() && easing > 0.7f && heightNoise > variants.getVolcanoBasaltHeight())
            {
                TFCSurfaceBuilders.applySurfaceBuilder(TFCSurfaceBuilders.NORMAL.get(), random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, TFCSurfaceBuilders.BASALT_CONFIG.get());
                return;
            }
        }
        TFCSurfaceBuilders.applySurfaceBuilder(config.getParent(), random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, ParentedSurfaceBuilderConfig config)
    {
        throw new UnsupportedOperationException("VolcanoesSurfaceBuilder must be used with a chunk generator which supports IContextSurfaceBuilder!");
    }

    @Override
    protected void initSeed(long seed)
    {
        cellNoise = VolcanoNoise.cellNoise(seed);
    }
}
