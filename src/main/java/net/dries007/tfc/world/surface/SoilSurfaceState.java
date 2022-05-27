/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

// todo: add loam in the center region, sand - sand/sandy - sandy - [NEW sandy/loam - loam - silty/loam] - silty - silty/silt - silt
public class SoilSurfaceState implements SurfaceState
{
    public static final float RAINFALL_SAND = 75;
    public static final float RAINFALL_SAND_SANDY_MIX = 125;
    public static final float RAINFALL_SANDY = 200; // Upper thresholds
    public static final float RAINFALL_SILTY = 275; // Lower thresholds
    public static final float RAINFALL_SILT_SILTY_MIX = 350;
    public static final float RAINFALL_SILT = 400;

    public static final float RAINFALL_SAND_SANDY_MEAN = (RAINFALL_SAND + RAINFALL_SAND_SANDY_MIX) / 2f;
    public static final float RAINFALL_SAND_SANDY_RANGE = (RAINFALL_SAND_SANDY_MIX - RAINFALL_SAND) / 2f;

    public static final long PATCH_NOISE_SEED = 18273952837592L;

    protected final SoilBlockType soil;
    protected final Noise2D patchNoise = new OpenSimplex2D(PATCH_NOISE_SEED).octaves(2).spread(0.04f);

    public SoilSurfaceState(SoilBlockType soil)
    {
        this.soil = soil;
    }

    @Override
    public BlockState getState(SurfaceBuilderContext context)
    {
        final BlockPos pos = context.pos();
        final float rainfall = context.rainfall();
        final ChunkData data = context.getChunkData();

        final float forest = data.getAdjustedForestDensity();
        if (rainfall < RAINFALL_SANDY)
        {
            if (rainfall > RAINFALL_SAND_SANDY_MIX)
            {
                // Sandy
                return soil(SoilBlockType.Variant.SANDY_LOAM);
            }
            else
            {
                // Patchy gravel deserts. Only places gravel in mostly-flat areas, otherwise defaults to sand
                final double slope = context.getSlope();
                final float weirdness = data.getForestWeirdness();
                if ((weirdness < 0.18f || (weirdness < 0.22f && (weirdness - context.random().nextFloat() * 0.5f < 0f))) && slope < 2 && (slope < 1 || context.random().nextBoolean()))
                {
                    return context.getRock().gravel().defaultBlockState();
                }
                else if (rainfall > RAINFALL_SAND)
                {
                    // Sandy - Sand Transition Zone
                    float noise = patchNoise.noise(pos.getX(), pos.getZ());
                    if (noise > 0.2f && forest > 0.5f)
                    {
                        // offset patches of rooted dirt theoretically forested areas
                        float noise2 = patchNoise.noise(pos.getX() + 2000, pos.getZ() + 2000);
                        if (noise2 > 0) return rooted(SoilBlockType.Variant.SANDY_LOAM);
                    }
                    return noise > 0.2f * (rainfall - RAINFALL_SAND_SANDY_MEAN) / RAINFALL_SAND_SANDY_RANGE ? context.getRock().sand().defaultBlockState() : soil(SoilBlockType.Variant.SANDY_LOAM);
                }
                else
                {
                    // Sand
                    return context.getRock().sand().defaultBlockState();
                }
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
                float noise = patchNoise.noise(pos.getX(), pos.getZ());
                return soil(noise > 0 ? SoilBlockType.Variant.SILTY_LOAM : SoilBlockType.Variant.SILT);
            }
            else
            {
                // Muddy areas at low elevation
                if (pos.getY() == context.getSeaLevel() -1 || pos.getY() == context.getSeaLevel())
                {
                    float noise = patchNoise.noise(pos.getX(), pos.getZ());
                    if (noise > 0) return mud(SoilBlockType.Variant.SILT);
                }
                // Silt
                return soil(SoilBlockType.Variant.SILT);
            }
        }
        else
        {
            // Sandy / Silty Transition Zone
            float noise = patchNoise.noise(pos.getX(), pos.getZ());
            if (noise > 0 && forest > 0.5f)
            {
                return rooted(SoilBlockType.Variant.SILTY_LOAM);
            }
            return soil(noise > 0 ? SoilBlockType.Variant.SILTY_LOAM : SoilBlockType.Variant.SANDY_LOAM);
        }
    }

    @Override
    public void setState(SurfaceBuilderContext context)
    {
        context.chunk().setBlockState(context.pos(), getState(context), false);
        if (soil == SoilBlockType.GRASS)
        {
            context.chunk().markPosForPostprocessing(context.pos());
        }
    }

    private BlockState soil(SoilBlockType.Variant variant)
    {
        return TFCBlocks.SOIL.get(soil).get(variant).get().defaultBlockState();
    }

    private BlockState rooted(SoilBlockType.Variant variant)
    {
        return TFCBlocks.SOIL.get(SoilBlockType.ROOTED_DIRT).get(variant).get().defaultBlockState();
    }

    private BlockState mud(SoilBlockType.Variant variant)
    {
        return TFCBlocks.SOIL.get(SoilBlockType.MUD).get(variant).get().defaultBlockState();
    }
}
