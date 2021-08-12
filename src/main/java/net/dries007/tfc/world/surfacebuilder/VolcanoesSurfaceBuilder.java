/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.biome.VolcanoNoise;
import net.dries007.tfc.world.noise.Cellular2D;

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
            final float value = cellNoise.noise(x, z);
            final float distance = cellNoise.f1();
            final float easing = VolcanoNoise.calculateEasing(distance);
            final double heightNoise = noise * 2f + startHeight;
            if (value < variants.getVolcanoChance() && easing > 0.6f && heightNoise > variants.getVolcanoBasaltHeight())
            {
                buildVolcanicSurface(context, x, z, startHeight, slope, temperature, rainfall, saltWater, easing);
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

    private void buildVolcanicSurface(SurfaceBuilderContext context, int x, int z, int startHeight, double slope, float temperature, float rainfall, boolean saltWater, float easing)
    {
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        final BlockState basalt = TFCBlocks.ROCK_BLOCKS.get(Rock.Default.BASALT).get(Rock.BlockType.RAW).get().defaultBlockState();

        int surfaceDepth = -1;
        int localX = x & 15;
        int localZ = z & 15;

        for (int y = startHeight; y >= 0; --y)
        {
            pos.set(localX, y, localZ);
            BlockState stateAt = context.getBlockState(pos);
            if (stateAt.isAir())
            {
                // Reached air, reset surface depth
                surfaceDepth = -1;
            }
            else if (stateAt.getBlock() == context.getDefaultBlock().getBlock())
            {
                if (surfaceDepth == -1)
                {
                    // Reached surface. Place top state and switch to subsurface layers
                    surfaceDepth = calculateAltitudeSlopeSurfaceDepth(y, slope, 13, 0, 4);
                    surfaceDepth = Mth.clamp((int) (surfaceDepth * (easing - 0.6f) / 0.4f), 2, 11);
                    context.setBlockState(pos, basalt);
                }
                else if (surfaceDepth > 0)
                {
                    // Subsurface layers
                    surfaceDepth--;
                    context.setBlockState(pos, basalt);
                }
                else if (surfaceDepth == 0)
                {
                    // Underground layers
                    context.setBlockState(pos, SurfaceStates.RAW, temperature, rainfall, saltWater);
                }
            }
            else // Default fluid
            {
                context.setBlockState(pos, SurfaceStates.WATER, temperature, rainfall, saltWater);
            }
        }
    }
}
