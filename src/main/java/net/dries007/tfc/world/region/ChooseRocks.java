/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.layer.framework.Area;

public enum ChooseRocks implements RegionTask
{
    INSTANCE;

    public static final int OCEAN = 0;
    public static final int VOLCANIC = 1;
    public static final int LAND = 2;
    public static final int UPLIFT = 3;

    public static final int TYPE_BITS = 2;
    public static final int TYPE_MASK = (1 << TYPE_BITS) - 1; // 0b11

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final Area rockArea = context.generator().rockArea.get();

        for (final var point : region.points())
        {
            // Lower two bits are the supertype, upper bits are seed
            point.rock = (rockArea.get(point.x, point.z) << TYPE_BITS) | findClosestType(region, point, point.index);
        }
    }

    private int findClosestType(Region region, Region.Point center, int index)
    {
        int type = center.land() ? LAND : OCEAN, minDist = Integer.MAX_VALUE;
        for (int dx = -2; dx <= 2; dx++)
        {
            for (int dz = 0; dz <= 2; dz++)
            {
                final @Nullable Region.Point point = region.atOffset(index, dx, dz);
                final int dist = Math.abs(dx) + Math.abs(dz);
                if (point != null && dist < minDist)
                {
                    if (point.island() && dist < 4)
                    {
                        type = VOLCANIC;
                        minDist = dist;
                    }
                    else if ((point.mountain() || point.coastalMountain()) && dist < 3)
                    {
                        type = UPLIFT;
                        minDist = dist;
                    }
                }
            }
        }
        return type;
    }
}
