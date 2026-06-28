/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;

import static net.dries007.tfc.world.biome.BiomeNoise.*;

public class OceanRidgeSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = OceanRidgeSurfaceBuilder::new;

    final Seed seed;

    public OceanRidgeSurfaceBuilder(Seed seed)
    {
        this.seed = seed;
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final BlockPos pos = context.pos();
        final int x = pos.getX();
        final int z = pos.getZ();
        final double distance = getOceanRidgeWarpedEdgeDistanceAndScale(x, z, seed.seed(), false).x;

        if (distance >= 50)
        {
            // Increase mud depth away from ridge
            final int endYOut = (int) Mth.clampedMap(distance, 50, 200, startY, endY);
            SimpleSurfaceBuilder.OCEAN_MUD.apply(seed).buildSurface(context, startY, endYOut);
        }
    }
}
