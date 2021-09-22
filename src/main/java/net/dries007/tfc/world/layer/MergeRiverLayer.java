/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.TransformLayer;
import net.dries007.tfc.world.river.MidpointFractal;
import net.dries007.tfc.world.river.Watershed;

public class MergeRiverLayer implements TransformLayer
{
    private final Watershed.Context watersheds;

    public MergeRiverLayer(Watershed.Context watersheds)
    {
        this.watersheds = watersheds;
    }

    @Override
    public int apply(AreaContext context, Area area, int x, int z)
    {
        final int value = area.get(x, z);
        if (TFCLayers.hasRiver(value))
        {
            final float scale = 1f / (1 << 7);
            final float x0 = x * scale, z0 = z * scale;
            for (MidpointFractal fractal : watersheds.getFractalsByPartition(x, z))
            {
                // maybeIntersect will skip the more expensive calculation if it fails
                if (fractal.maybeIntersect(x0, z0, Watershed.RIVER_WIDTH) && fractal.intersect(x0, z0, Watershed.RIVER_WIDTH))
                {
                    return TFCLayers.riverFor(value);
                }
            }
        }
        return value;
    }
}