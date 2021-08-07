/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.TransformLayer;

public enum ExactZoomLayer implements TransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, Area area, int x, int z)
    {
        return area.get(x >> 1, z >> 1);
    }
}