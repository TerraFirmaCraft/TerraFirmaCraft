/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

/**
 * An {@link TransformLayer} which uses the center and four diagonal positions to generate the next location.
 */
public interface DiagonalTransformLayer extends TransformLayer
{
    @Override
    default int apply(AreaContext context, Area area, int x, int z)
    {
        return apply(context, area.get(x, z), area.get(x + 1, z - 1), area.get(x + 1, z + 1), area.get(x - 1, z + 1), area.get(x - 1, z - 1));
    }

    int apply(AreaContext context, int center, int northEast, int southEast, int southWest, int northWest);
}
