/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents a <strong>partition</strong> of a single {@link Region} into a larger coordinate scale. At each point in the partition, we collect river segments that may influence that point.
 * Ultimately this allows querying rivers at a much smaller scale than on a per-region basis, which is essential to generate rivers as part of terrain.
 */
public record RegionPartition(int minPartX, int minPartZ, Point[] data)
{
    RegionPartition(int cellX, int cellZ)
    {
        this(Units.cellToPart(cellX), Units.cellToPart(cellZ), new Point[Units.CELL_WIDTH_IN_PARTITION * Units.CELL_WIDTH_IN_PARTITION]);

        for (int i = 0; i < data.length; i++)
        {
            data[i] = new Point(new ArrayList<>());
        }
    }

    public Point getFromPart(int partX, int partZ)
    {
        return data[index(partX, partZ)];
    }

    public Point get(int gridX, int gridZ)
    {
        return data[index(Units.gridToPart(gridX) - minPartX, Units.gridToPart(gridZ) - minPartZ)];
    }

    public boolean isIn(int partX, int partZ)
    {
        final int localX = partX - minPartX;
        final int localZ = partZ - minPartZ;

        return localX >= 0 && localZ >= 0 && localX < Units.CELL_WIDTH_IN_PARTITION && localZ < Units.CELL_WIDTH_IN_PARTITION;
    }

    private int index(int partX, int partZ)
    {
        return (partX & Units.PARTITION_BIT_MASK) | ((partZ & Units.PARTITION_BIT_MASK) << Units.PARTITION_BITS);
    }

    public record Point(List<RiverEdge> rivers) {}
}
