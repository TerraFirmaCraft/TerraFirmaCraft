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
