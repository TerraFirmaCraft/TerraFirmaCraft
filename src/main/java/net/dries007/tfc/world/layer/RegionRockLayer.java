/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.layer.framework.TypedArea;
import net.dries007.tfc.world.layer.framework.TypedAreaFactory;
import net.dries007.tfc.world.region.Region;

public enum RegionRockLayer
{
    INSTANCE;

    public AreaFactory apply(TypedAreaFactory<Region.Point> prev)
    {
        return () -> {
            final TypedArea<Region.Point> prevArea = prev.get();
            return new Area((x, z) -> prevArea.get(x, z).rock, 1024);
        };
    }
}
