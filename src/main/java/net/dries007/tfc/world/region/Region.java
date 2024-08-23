/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.List;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.FastNoiseLite;

public final class Region
{
    private final double cellX;
    private final double cellY;
    private final double noise;
    private int minX;
    private int minZ;
    private int maxX;
    private int maxZ;
    private int sizeX;
    private int sizeZ;
    private Point[] data;
    private @Nullable List<RiverEdge> rivers;

    Region(Cellular2D.Cell cell)
    {
        this.cellX = cell.x();
        this.cellY = cell.y();
        this.noise = cell.noise();

        final int cellX = FastNoiseLite.FastRound(cell.x());
        final int cellZ = FastNoiseLite.FastRound(cell.y());

        this.minX = cellX - Units.REGION_RADIUS_IN_GRID;
        this.minZ = cellZ - Units.REGION_RADIUS_IN_GRID;
        this.maxX = cellX + Units.REGION_RADIUS_IN_GRID;
        this.maxZ = cellZ + Units.REGION_RADIUS_IN_GRID;

        this.sizeX = 1 + maxX - minX;
        this.sizeZ = 1 + maxZ - minZ;

        this.data = new Point[Units.REGION_WIDTH_IN_GRID * Units.REGION_WIDTH_IN_GRID];
    }

    public Point atInit(int gridX, int gridZ)
    {
        final int index = index(gridX, gridZ);
        final Point point = new Point();

        assert data[index] == null;
        data[index] = point;
        return point;
    }

    public Point requireAt(int gridX, int gridZ)
    {
        final Point point = at(gridX, gridZ);
        assert point != null : "Region %s does not contain point at (%d, %d)".formatted(this, gridX, gridZ);
        return point;
    }

    /**
     * @return The {@link Point} at the specified grid coordinates. Errors if the coordinates are out of range of this {@link Region}'s bounding box and returns {@code null} if they are outside this {@link Region}.
     */
    @Nullable
    public Point at(int gridX, int gridZ)
    {
        return data[index(gridX, gridZ)];
    }

    /**
     * @return The {@link Point} at the specified grid coordinates. Returns {@code null} if the coordinates are out of range of this {@link Region}'s bounding box or outside this {@link Region}.
     */
    @Nullable
    public Point maybeAt(int gridX, int gridZ)
    {
        return isIn(gridX, gridZ) ? data[index(gridX, gridZ)] : null;
    }

    /**
     * @return {@code true} if the specified grid coordinates {@code (gridX, gridZ)} are within this {@link Region}'s bounding box.
     */
    public boolean isIn(int gridX, int gridZ)
    {
        return gridX >= minX && gridX <= maxX && gridZ >= minZ && gridZ <= maxZ;
    }

    /**
     * @return An index into {@link #data()}, based on the target index, plus a coordinate offset of {@code (offsetX, offsetZ)}. Returns {@code -1} if this is out of this {@link Region}'s bounding box.
     */
    public int offset(int index, int offsetX, int offsetZ)
    {
        final int localX = offsetX + (index % sizeX);
        final int localZ = offsetZ + (index / sizeX);
        return localX >= 0 && localX < sizeX && localZ >= 0 && localZ < sizeZ ? localX + sizeX * localZ : -1;
    }

    /**
     * @return An index into {@link #data()}, based on the global grid coordinates.
     */
    public int index(int gridX, int gridZ)
    {
        assert isIn(gridX, gridZ) : "Point (" + gridX + ", " + gridZ + ") not in region [" + minX + ", " + maxX + "] x [" + minZ + ", " + maxZ + "]";

        final int localX = gridX - minX;
        final int localZ = gridZ - minZ;

        return localX + sizeX * localZ;
    }

    public double noise() { return noise; }

    public int minX() { return minX; }
    public int minZ() { return minZ; }
    public int maxX() { return maxX; }
    public int maxZ() { return maxZ; }
    public int sizeX() { return sizeX; }
    public int sizeZ() { return sizeZ; }

    public void setRegionArea(Point[] data, int minX, int minZ, int maxX, int maxZ)
    {
        this.data = data;
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
        this.sizeX = 1 + maxX - minX;
        this.sizeZ = 1 + maxZ - minZ;

        assert data.length == sizeX * sizeZ : "setRegionArea() data.length = %d != sizeX (%d) * sizeZ (%d)".formatted(data.length, sizeX, sizeZ);
    }

    public void setRivers(List<RiverEdge> rivers)
    {
        assert this.rivers == null;
        this.rivers = rivers;
    }


    public Point[] data() { return data; }
    public List<RiverEdge> rivers() { assert rivers != null; return rivers; }

    @Override
    public String toString()
    {
        return "Region [%d, %d] x [%d, %d] at cell (%f, %f)".formatted(minX, maxX, minZ, maxZ, cellX, cellY);
    }

    public static class Point
    {
        static final short FLAG_LAND = 0b1;
        static final short FLAG_ISLAND = 0b10;
        static final short FLAG_RIVER = 0b100;
        static final short FLAG_LAKE = 0b1000;
        static final short FLAG_MOUNTAIN = 0b10000;
        static final short FLAG_COASTAL_MOUNTAIN = 0b100000;

        /** Distance to the nearest ocean. Note the actual distance may be lower if {@code distanceToEdge} is smaller than this. Negative values indicate an ocean, where {@code -2} indicates an ocean adjacent to land. */
        public byte distanceToOcean = 0;
        /** Distance to the nearest edge of the region. This is important because certain tasks need to not go too near to the edge to avoid continuity issues */
        public byte distanceToEdge = 0;
        public byte distanceToWestCoast;
        public byte baseOceanDepth = 0;
        public byte baseLandHeight = 0;
        public byte biomeAltitude = 0;

        public float rainfall;
        public float rainfallVariance;
        public float temperature;

        public int biome = TFCLayers.OCEAN;
        public int rock = 0;

        private short flags;

        public boolean land() { return (flags & FLAG_LAND) != 0; }
        public boolean island() { return (flags & FLAG_ISLAND) != 0; }
        public boolean shore() { return distanceToOcean == -2; }
        public boolean river() { return (flags & FLAG_RIVER) != 0; }
        public boolean lake() { return (flags & FLAG_LAKE) != 0; }
        public boolean mountain() { return (flags & FLAG_MOUNTAIN) != 0; }
        public boolean coastalMountain() { return (flags & FLAG_COASTAL_MOUNTAIN) != 0; }

        public int discreteBiomeAltitude() { return Math.floorDiv(biomeAltitude, AnnotateBiomeAltitude.WIDTH); }

        public void setLand() { flags |= FLAG_LAND; }
        public void setIsland() { flags |= FLAG_ISLAND; }
        public void setShore() { distanceToOcean = -2; }
        public void setRiver() { flags |= FLAG_RIVER; }
        public void setLake() { flags |= FLAG_LAKE; }
        public void setMountain() { flags |= FLAG_MOUNTAIN; }
        public void setCoastalMountain() { flags |= FLAG_COASTAL_MOUNTAIN; }
    }
}
