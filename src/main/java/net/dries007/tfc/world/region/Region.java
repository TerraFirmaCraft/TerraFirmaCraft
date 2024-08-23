/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import java.util.List;
import java.util.Objects;
import com.google.common.collect.AbstractIterator;
import net.minecraft.util.RandomSource;
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

        this.data = new Point[0]; // Must initialize via `setRegionArea()` first
    }

    /**
     * @return An iterator through all points present within this region.
     */
    public Iterable<Point> points()
    {
        return () -> new AbstractIterator<>()
        {
            int index = -1;

            @Override
            protected Point computeNext()
            {
                do { index++; } while (index < data.length && data[index] == null);
                return index < data.length ? data[index] : endOfData();
            }
        };
    }

    /**
     * @return A randomly chosen point within the region, possibly null.
     */
    @Nullable
    public Point random(RandomSource random)
    {
        return data[random.nextInt(data.length)];
    }

    /**
     * @return The {@link Point} at the specified grid coordinates. Returns {@code null} if the coordinates are out of the
     * region's bounding box.
     */
    @Nullable
    public Point at(int gridX, int gridZ)
    {
        return isIn(gridX, gridZ) ? data[index(gridX, gridZ)] : null;
    }

    /**
     * This is similar to {@link #atOffset} except with a zero offset, simply returns the point within the region
     * for a known point and index.
     */
    public Point atIndex(int index)
    {
        return data[index];
    }

    /**
     * @param index An index obtained from {@link Point#index} representing a point within this region.
     * @return The point at a given {@code index}, offset by {@code (dx, dz)}, or {@code null} if the point is out of the
     * region's bounding box.
     */
    @Nullable
    public Point atOffset(int index, int dx, int dz)
    {
        final int localX = dx + (index % sizeX);
        final int localZ = dz + (index / sizeX);
        return localX >= 0 && localX < sizeX && localZ >= 0 && localZ < sizeZ
            ? data[localX + sizeX * localZ]
            : null;
    }

    public void setRivers(List<RiverEdge> rivers)
    {
        assert this.rivers == null;
        this.rivers = rivers;
    }

    public List<RiverEdge> rivers()
    {
        return Objects.requireNonNull(rivers);
    }

    public double noise() { return noise; }

    public int minX() { return minX; }
    public int minZ() { return minZ; }
    public int maxX() { return maxX; }
    public int maxZ() { return maxZ; }
    public int sizeX() { return sizeX; }
    public int sizeZ() { return sizeZ; }

    /**
     * @return An estimate for the region's size, useful for pre-allocating bitsets to the correct capacity.
     */
    public int size()
    {
        return sizeX * sizeZ;
    }

    @Override
    public String toString()
    {
        return "Region [%d, %d] x [%d, %d] at cell (%f, %f)".formatted(minX, maxX, minZ, maxZ, cellX, cellY);
    }

    /**
     * Used by region generation, ensures that the queried point is present within the region
     */
    Point atOrThrow(int gridX, int gridZ)
    {
        final Point point = data[index(gridX, gridZ)];
        assert point != null : "Region %s does not contain point at (%d, %d)".formatted(this, gridX, gridZ);
        return point;
    }

    /**
     * @return An index into {@link #data}, based on the global grid coordinates.
     */
    int index(int gridX, int gridZ)
    {
        assert isIn(gridX, gridZ) : "Point (" + gridX + ", " + gridZ + ") not in region [" + minX + ", " + maxX + "] x [" + minZ + ", " + maxZ + "]";

        final int localX = gridX - minX;
        final int localZ = gridZ - minZ;

        return localX + sizeX * localZ;
    }

    /**
     * Used by the initialization step to first initialize the point array
     */
    void setRegionArea(int minX, int minZ, int maxX, int maxZ)
    {
        this.data = new Point[sizeX * sizeZ];
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
        this.sizeX = 1 + maxX - minX;
        this.sizeZ = 1 + maxZ - minZ;
    }

    /**
     * Initializes the point at the given x, z, marking it as within the region.
     */
    void init(int gridX, int gridZ)
    {
        final int index = index(gridX, gridZ);
        data[index] = new Point(gridX, gridZ, index);
    }

    /**
     * @return {@code true} if the specified grid coordinates {@code (gridX, gridZ)} are within this {@link Region}'s bounding box.
     */
    private boolean isIn(int gridX, int gridZ)
    {
        return gridX >= minX && gridX <= maxX && gridZ >= minZ && gridZ <= maxZ;
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
        public byte baseOceanDepth = 0;
        public byte baseLandHeight = 0;
        public byte biomeAltitude = 0;

        public float rainfall;
        public float temperature;

        public int biome = TFCLayers.OCEAN;
        public int rock = 0;

        private short flags;

        public final int x, z;
        public final int index;

        Point(int x, int z, int index)
        {
            this.x = x;
            this.z = z;
            this.index = index;
        }

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
