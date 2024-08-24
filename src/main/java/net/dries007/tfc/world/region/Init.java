/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.BitSet;

import net.dries007.tfc.world.noise.Cellular2D;

public enum Init implements RegionTask
{
    INSTANCE;

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final BitSet cell = new BitSet(region.size());

        int minX = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        // Iterate the widest possible region radius, and sample the cell at that grid position. If the cell corresponds
        // to the same cell from the region, flag that point (using coordinates relative to the region x,z) as within the cell bitset
        //
        // Also while we iterate, update the min/max ranges that any points are present in. We will use this after in order
        // to shrink the range of the region array down to the minimum possible to contain the cell.
        for (int dx = 0; dx <= 2 * Units.REGION_RADIUS_IN_GRID; dx++)
        {
            for (int dz = 0; dz <= 2 * Units.REGION_RADIUS_IN_GRID; dz++)
            {
                final int gridX = context.region.minX() + dx;
                final int gridZ = context.region.minZ() + dz;
                final int index = region.index(gridX, gridZ);
                final Cellular2D.Cell otherCell = context.generator().sampleCell(gridX, gridZ);

                if (otherCell.x() == context.regionCell.x() && otherCell.y() == context.regionCell.y())
                {
                    cell.set(index);
                    if (gridX < minX)
                    {
                        minX = gridX;
                    }
                    if (gridZ < minZ)
                    {
                        minZ = gridZ;
                    }
                    if (gridX > maxX)
                    {
                        maxX = gridX;
                    }
                    if (gridZ > maxZ)
                    {
                        maxZ = gridZ;
                    }
                }
            }
        }

        // Calculate the modified size for the point array
        final int modifiedSizeX = 1 + maxX - minX;
        final int modifiedSizeZ = 1 + maxZ - minZ;

        final int offsetX = minX - region.minX();
        final int offsetZ = minZ - region.minZ();

        final int prevSizeX = 1 + 2 * Units.REGION_RADIUS_IN_GRID;

        // First, we need to shrink the region area, so we are accessing points relative to the correct min/max values
        context.region.setRegionArea(minX, minZ, maxX, maxZ);

        // And initialize all points within the array, based on their respective position within the original bitset.
        for (int dx = 0; dx < modifiedSizeX; dx++)
        {
            for (int dz = 0; dz < modifiedSizeZ; dz++)
            {
                if (cell.get((offsetX + dx) + prevSizeX * (offsetZ + dz)))
                {
                    region.init(minX + dx, minZ + dz);
                }
            }
        }
    }
}
