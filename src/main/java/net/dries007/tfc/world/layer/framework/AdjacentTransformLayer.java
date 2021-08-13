/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

/**
 * An {@link TransformLayer} which uses the targeted position and four adjacent positions to generate the next location.
 */
public interface AdjacentTransformLayer extends TransformLayer
{
    @Override
    default int apply(AreaContext context, Area area, int x, int z)
    {
        return apply(context, area.get(x, z - 1), area.get(x + 1, z), area.get(x, z + 1), area.get(x - 1, z), area.get(x, z));
    }

    int apply(AreaContext context, int north, int east, int south, int west, int center);
}
