/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import net.dries007.tfc.world.noise.Cellular2D;

public enum AddContinents implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        for (int dx = -Units.REGION_RADIUS_IN_GRID; dx <= Units.REGION_RADIUS_IN_GRID; dx++)
        {
            for (int dz = -Units.REGION_RADIUS_IN_GRID; dz <= Units.REGION_RADIUS_IN_GRID; dz++)
            {
                final int gridX = context.region.minX() + Units.REGION_RADIUS_IN_GRID + dx;
                final int gridZ = context.region.minZ() + Units.REGION_RADIUS_IN_GRID + dz;
                final Cellular2D.Cell otherCell = context.generator().sampleCell(gridX, gridZ);

                if (otherCell.x() == context.regionCell.x() && otherCell.y() == context.regionCell.y())
                {
                    final Region.Point point = context.region.atInit(gridX, gridZ);
                    final double continent = context.generator().continentNoise.noise(gridX, gridZ);

                    if (continent > 4.4)
                    {
                        point.setLand();
                    }

                    if (gridX < context.minX)
                    {
                        context.minX = gridX;
                    }
                    if (gridZ < context.minZ)
                    {
                        context.minZ = gridZ;
                    }
                    if (gridX > context.maxX)
                    {
                        context.maxX = gridX;
                    }
                    if (gridZ > context.maxZ)
                    {
                        context.maxZ = gridZ;
                    }
                }
            }
        }
    }
}
