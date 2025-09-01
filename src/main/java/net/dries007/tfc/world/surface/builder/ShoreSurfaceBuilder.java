/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.minecraft.core.BlockPos;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

public class ShoreSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory NORMAL = seed -> new ShoreSurfaceBuilder(seed, SurfaceStates.SHORE_SURFACE, SurfaceStates.SHORE_UNDERLAYER, 6, false, false, false, false);
    public static final SurfaceBuilderFactory SANDY = seed -> new ShoreSurfaceBuilder(seed, SurfaceStates.SHORE_SAND, SurfaceStates.SHORE_SANDSTONE, 6, false, true, false, false);
    public static final SurfaceBuilderFactory FORCE_RARE_SAND = seed -> new ShoreSurfaceBuilder(seed, SurfaceStates.RARE_SHORE_SAND, SurfaceStates.RARE_SHORE_SANDSTONE, 6, false, true, false, false);
    public static final SurfaceBuilderFactory GRAVELLY = seed -> new ShoreSurfaceBuilder(seed, SurfaceStates.GRAVEL, SurfaceStates.RAW, 6, false, false, false, false);
    public static final SurfaceBuilderFactory OCEAN = seed -> new ShoreSurfaceBuilder(seed, SurfaceStates.SHORE_SURFACE, SurfaceStates.SHORE_UNDERLAYER, 6, true, false, false, false);
    public static final SurfaceBuilderFactory SEA_CLIFFS = seed -> new ShoreSurfaceBuilder(seed, SurfaceStates.SHORE_SURFACE, SurfaceStates.SHORE_UNDERLAYER, 2, false, false, false, false);
    public static final SurfaceBuilderFactory VOLCANIC = seed -> new ShoreSurfaceBuilder(seed, SurfaceStates.VOLCANIC_SHORE_SAND, SurfaceStates.VOLCANIC_SHORE_SANDSTONE, 6, false, true, true, false);
    public static final SurfaceBuilderFactory VOLCANIC_CLIFFS = seed -> new ShoreSurfaceBuilder(seed, SurfaceStates.VOLCANIC_SHORE_SAND, SurfaceStates.VOLCANIC_SHORE_SANDSTONE, 2, false, false, true, true);

    final Seed seed;
    final SurfaceState surface;
    final SurfaceState subsurface;
    final int sandHeight;
    final boolean isOcean;
    final boolean hasSandyLand;
    final boolean isVolcanic;
    final boolean hasLavaFlows;

    protected ShoreSurfaceBuilder(Seed seed, SurfaceState surface, SurfaceState subsurface, int sandHeight, boolean isOcean, boolean hasSandyLand, boolean isVolcanic, boolean hasLavaFlows)
    {
        this.seed = seed;
        this.surface = surface;
        this.subsurface = subsurface;
        this.isOcean = isOcean;
        this.sandHeight = sandHeight;
        this.hasSandyLand = hasSandyLand;
        this.isVolcanic = isVolcanic;
        this.hasLavaFlows = hasLavaFlows;
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final BlockPos pos = context.pos();
        final int x = pos.getX();
        final int z = pos.getZ();
        final int tideLevel = (int) BiomeNoise.shoreTideLevelNoise(seed).noise(x, z);
        final int sandHeightAbsolute = tideLevel + sandHeight;

        if (isOcean && startY < tideLevel - 6)
        {
            // Always use gravel in deeper ocean water
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, surface, surface, subsurface, SurfaceStates.GRAVEL, SurfaceStates.GRAVEL);
        }
        else if (startY <= sandHeightAbsolute)
        {
            // Shore surface at shore heights, above/below water
            if (isVolcanic)
            {
                if (hasLavaFlows)
                {
                    // Still want lava flows to continue onto beaches.
                    buildLavaFlowSurface(context, startY, endY, x, z);
                }
                else
                {
                    ShieldVolcanoSurfaceBuilder.SHORE.apply(seed).buildSurface(context, startY, endY);
                }
            }
            else
            {
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, surface, surface, subsurface, surface, surface);
            }
        }
        else if (isVolcanic)
        {
            // For shield volcano shores, we want to use the shield volcano surface builder to place basalt
            if (hasLavaFlows)
            {
                ShieldVolcanoSurfaceBuilder.ACTIVE.apply(seed).buildSurface(context, startY, endY);
            }
            else
            {
                ShieldVolcanoSurfaceBuilder.DORMANT.apply(seed).buildSurface(context, startY, endY);
            }

        }
        else if (hasSandyLand)
        {
            // If, in dry biomes, sand should be used instead of gravel on land
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.VOLCANIC_TOP_GRASS_TO_SHORE_SAND, SurfaceStates.VOLCANIC_MID_DIRT_TO_SHORE_SAND, SurfaceStates.UNDER_GRAVEL, SurfaceStates.GRAVEL, SurfaceStates.GRAVEL, surface, surface, subsurface, sandHeightAbsolute);
        }
        {
            // Normal land surface, with shore material at beach level in caves/below overhangs
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, surface, surface, subsurface, sandHeightAbsolute);
        }
    }

    /**
     * Essentially mimics @link ShieldVolcanoSurfaceBuilder for placement of fresh lava flows
     * but using different materials
     */
    private void buildLavaFlowSurface(SurfaceBuilderContext context, int startY, int endY, int x, int z)
    {
        final Noise2D smoothNoise = BiomeNoise.lavaFlowMaterial(seed.seed());
        final double noiseValue = smoothNoise.noise(x, z);
        final Noise2D lavaFlows = BiomeNoise.lavaFlow(seed.seed());
        final double flowValue = lavaFlows.noise(x, z);

        if (flowValue < 0.40)
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, surface, surface, subsurface, surface, surface);
        else if (flowValue < 0.50)
        {
            if (noiseValue > 0)
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT_GRAVEL, surface, subsurface, surface, surface);
            else
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, surface, surface, subsurface, surface, surface);
        }
        else if (flowValue < 0.75)
        {
            if (noiseValue > 0)
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT_GRAVEL, SurfaceStates.BASALT_GRAVEL, SurfaceStates.BASALT, SurfaceStates.BASALT_GRAVEL, surface);
            else
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT_COBBLE, SurfaceStates.BASALT_COBBLE, SurfaceStates.BASALT, SurfaceStates.BASALT_COBBLE, surface);
        }
        else
        {
            if (noiseValue > -0.6)
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT, SurfaceStates.BASALT, SurfaceStates.BASALT, SurfaceStates.BASALT, SurfaceStates.BASALT_COBBLE);
            else
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT_COBBLE, SurfaceStates.BASALT_COBBLE, SurfaceStates.BASALT, SurfaceStates.BASALT, SurfaceStates.BASALT_COBBLE);
        }
    }

}