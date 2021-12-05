/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AdjacentTransformLayer;
import net.dries007.tfc.world.layer.framework.AreaContext;

public enum SmoothLayer implements AdjacentTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int north, int west, int south, int east, int center)
    {
        final boolean equalX = west == east, equalZ = north == south;
        if (equalX == equalZ)
        {
            if (equalX)
            {
                return context.choose(east, north);
            }
            return center;
        }
        return equalX ? east : north;
    }
}
