/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.minecraft.world.level.levelgen.Heightmap;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.volcano.CenteredFeatureNoise;
import net.dries007.tfc.world.volcano.CenteredFeatureNoiseSampler;
import net.dries007.tfc.world.volcano.VolcanoVariant;

public class StratovolcanoSurfaceBuilder implements SurfaceBuilder
{
    public static SurfaceBuilderFactory create(SurfaceBuilderFactory parent)
    {
        return seed -> new StratovolcanoSurfaceBuilder(parent.apply(seed), seed);
    }

    private final SurfaceBuilder parent;
    private final Seed seed;

    public StratovolcanoSurfaceBuilder(SurfaceBuilder parent, Seed seed)
    {
        this.seed = seed;
        this.parent = parent;
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final BiomeExtension biome = context.stratovolcanoBiome();
        if (biome.hasStratovolcanoes())
        {
            final CenteredFeatureNoiseSampler sampler = CenteredFeatureNoise.stratovolcano(seed);
            final int preVolcanicHeight = context.getPreVolcanicHeight();
            final int oceanFloorY;
            if (startY == context.getSeaLevel())
            {
                oceanFloorY = context.chunk().getHeight(Heightmap.Types.OCEAN_FLOOR_WG, context.pos().getX(), context.pos().getZ());
            }
            else
            {
                oceanFloorY = startY;
            }

            VolcanoVariant variant = sampler.getVolcanoVariant(sampler.getCellularNoise().cell(context.pos().getX(), context.pos().getZ()));
            if (variant != null)
            {
                if (variant.buildSurface(context, oceanFloorY, preVolcanicHeight, sampler))
                {
                    return;
                }
            }
        }
        parent.buildSurface(context, startY, endY);
    }
}
