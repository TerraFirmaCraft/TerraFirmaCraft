/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class SoilSurfaceState implements ISurfaceState
{
    public static final float RAINFALL_SAND = 75;
    public static final float RAINFALL_SAND_SANDY_MIX = 125;
    public static final float RAINFALL_SANDY = 200; // Upper thresholds
    public static final float RAINFALL_SILTY = 275; // Lower thresholds
    public static final float RAINFALL_SILT_SILTY_MIX = 350;
    public static final float RAINFALL_SILT = 400;

    public static final float RAINFALL_SAND_SANDY_MEAN = (RAINFALL_SAND + RAINFALL_SAND_SANDY_MIX) / 2f;
    public static final float RAINFALL_SAND_SANDY_RANGE = (RAINFALL_SAND_SANDY_MIX - RAINFALL_SAND) / 2f;

    private static final long PATCH_NOISE_SEED = 18273952837592L;

    protected final SoilBlockType soil;
    protected final INoise2D patchNoise = new OpenSimplex2D(PATCH_NOISE_SEED).octaves(2).spread(0.04f);

    public SoilSurfaceState(SoilBlockType soil)
    {
        this.soil = soil;
    }

    @Override
    public BlockState state(RockData rockData, int x, int y, int z, float temperature, float rainfall, boolean salty)
    {
        if (rainfall < RAINFALL_SANDY)
        {
            if (rainfall > RAINFALL_SAND_SANDY_MIX)
            {
                // Sandy
                return soil(SoilBlockType.Variant.SANDY_LOAM);
            }
            else if (rainfall > RAINFALL_SAND)
            {
                // Sandy - Sand Transition Zone
                float noise = patchNoise.noise(x, z);
                return noise > 0.2f * (rainfall - RAINFALL_SAND_SANDY_MEAN) / RAINFALL_SAND_SANDY_RANGE ? sand(rockData, x, z) : soil(SoilBlockType.Variant.SANDY_LOAM);
            }
            else
            {
                // Sand
                return sand(rockData, x, z);
            }
        }
        else if (rainfall > RAINFALL_SILTY)
        {
            if (rainfall < RAINFALL_SILT_SILTY_MIX)
            {
                // Silty
                return soil(SoilBlockType.Variant.SILTY_LOAM);
            }
            else if (rainfall < RAINFALL_SILT)
            {
                // Silty / Silt Transition Zone
                float noise = patchNoise.noise(x, z);
                return soil(noise > 0 ? SoilBlockType.Variant.SILTY_LOAM : SoilBlockType.Variant.SILT);
            }
            else
            {
                // Silt
                return soil(SoilBlockType.Variant.SILT);
            }
        }
        else
        {
            // Sandy / Silty Transition Zone
            float noise = patchNoise.noise(x, z);
            return soil(noise > 0 ? SoilBlockType.Variant.SILTY_LOAM : SoilBlockType.Variant.SANDY_LOAM);
        }
    }

    @Override
    public void place(SurfaceBuilderContext context, BlockPos pos, int x, int z, RockData rockData, float temperature, float rainfall, boolean salty)
    {
        ISurfaceState.super.place(context, pos, x, z, rockData, temperature, rainfall, salty);
        if (soil == SoilBlockType.GRASS)
        {
            context.getChunk().markPosForPostprocessing(pos);
        }
    }

    private BlockState sand(RockData rockData, int x, int z)
    {
        return rockData.getTopRock(x, z).getSand().defaultBlockState();
    }

    private BlockState soil(SoilBlockType.Variant variant)
    {
        return TFCBlocks.SOIL.get(soil).get(variant).get().defaultBlockState();
    }

}
