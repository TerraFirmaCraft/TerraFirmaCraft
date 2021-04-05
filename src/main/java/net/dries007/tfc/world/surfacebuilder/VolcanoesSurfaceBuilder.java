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
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.biome.VolcanoNoise;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.CellularNoiseType;

public class VolcanoesSurfaceBuilder extends SeededSurfaceBuilder<ParentedSurfaceBuilderConfig>
{
    private Cellular2D cellNoise;

    public VolcanoesSurfaceBuilder(Codec<ParentedSurfaceBuilderConfig> codec)
    {
        super(codec);
    }

    @Override
    public void apply(SurfaceBuilderContext context, Biome biome, int x, int z, int startHeight, double noise, double slope, float temperature, float rainfall, boolean saltWater, ParentedSurfaceBuilderConfig config)
    {
        final BiomeVariants variants = TFCBiomes.getExtensionOrThrow(context.getWorld(), biome).getVariants();
        if (variants.isVolcanic())
        {
            // Sample volcano noise
            final float distance = cellNoise.noise(x, z, CellularNoiseType.F1);
            final float value = cellNoise.noise(x, z, CellularNoiseType.VALUE);
            final float easing = VolcanoNoise.calculateEasing(distance);
            final double heightNoise = noise * 2f + startHeight;
            if (value < variants.getVolcanoChance() && easing > 0.7f && heightNoise > variants.getVolcanoBasaltHeight())
            {
                final BlockState basalt = TFCBlocks.ROCK_BLOCKS.get(Rock.Default.BASALT).get(Rock.BlockType.RAW).get().defaultBlockState();
                final ISurfaceState basaltState = (rockData, xIn, yIn, zIn, temperature1, rainfall1, salty) -> basalt;
                TFCSurfaceBuilders.NORMAL.get().apply(context, biome, x, z, startHeight, noise, slope, temperature, rainfall, saltWater, config, basaltState, basaltState, basaltState);
                return;
            }
        }
        context.apply(config.getParent(), biome, x, z, startHeight, noise, slope);
    }

    @Override
    protected void initSeed(long seed)
    {
        cellNoise = VolcanoNoise.cellNoise(seed);
    }
}
