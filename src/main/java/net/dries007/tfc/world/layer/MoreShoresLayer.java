/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

import net.dries007.tfc.world.layer.framework.AdjacentTransformLayer;
import net.dries007.tfc.world.layer.framework.AreaContext;

public enum MoreShoresLayer implements AdjacentTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int north, int east, int south, int west, int center)
    {
        Predicate<IntPredicate> matcher = p -> p.test(north) || p.test(east) || p.test(south) || p.test(west);
        if (matcher.test(TFCLayers::isOcean))
        {
            if (matcher.test(layer -> layer == TFCLayers.TERRACE_LOWER))
            {
                return TFCLayers.TERRACE_UPPER;
            }
            if (matcher.test(layer -> layer == TFCLayers.SEA_STACKS))
            {
                return TFCLayers.SEA_STACKS;
            }
            if (matcher.test(layer -> layer == TFCLayers.TIDAL_FLATS))
            {
                return TFCLayers.SHORE;
            }
            if (matcher.test(layer -> layer == TFCLayers.COASTAL_DUNES))
            {
                return TFCLayers.COASTAL_DUNES;
            }
            if (matcher.test(layer -> layer == TFCLayers.SETBACK_CLIFFS))
            {
                return TFCLayers.SETBACK_CLIFFS;
            }
            if (matcher.test(layer -> layer == TFCLayers.ROCKY_SHORES))
            {
                return TFCLayers.ROCKY_SHORES;
            }
            if (matcher.test(layer -> layer == TFCLayers.EMBAYMENTS))
            {
                return TFCLayers.EMBAYMENTS;
            }
        }
        return center;
    }
}