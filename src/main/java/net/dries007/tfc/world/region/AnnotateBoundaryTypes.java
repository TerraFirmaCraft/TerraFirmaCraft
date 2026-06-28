/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;

import net.dries007.tfc.world.noise.Cellular2D;

public enum AnnotateBoundaryTypes implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;

        for (final var point : region.points())
        {
            point.divergence = (float) context.generator().getDivergence(point);
        }
    }
}
