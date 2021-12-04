package net.dries007.tfc.world.noise;

import net.minecraft.util.Mth;

/**
 * An abstraction for trilinear interpolation.
 * Source coordinates are full resolution but only sampled at values that are divisible by the cell width / cell height
 *
 * @see net.minecraft.world.level.levelgen.NoiseChunk.NoiseInterpolator
 */
public final class TrilinearInterpolator
{
    private final ChunkNoiseSamplingSettings settings;
    private final Source source;

    private double[][] slice0;
    private double[][] slice1;
    private double noise000, noise001, noise100, noise101, noise010, noise011, noise110, noise111;
    private double valueXZ00, valueXZ10, valueXZ01, valueXZ11;
    private double valueZ0, valueZ1;
    private double value;

    public TrilinearInterpolator(ChunkNoiseSamplingSettings settings, Source source)
    {
        this.settings = settings;
        this.source = source;

        this.slice0 = allocateSlice(settings.cellCountY(), settings.cellCountXZ());
        this.slice1 = allocateSlice(settings.cellCountY(), settings.cellCountXZ());
    }

    public void initializeForFirstCellX()
    {
        fillSlice(slice0, settings.firstCellX());
    }

    public void advanceCellX(int cellX)
    {
        fillSlice(slice1, settings.firstCellX() + cellX + 1);
    }

    public void selectCellYZ(int cellY, int cellZ)
    {
        noise000 = slice0[cellZ][cellY];
        noise001 = slice0[cellZ + 1][cellY];
        noise100 = slice1[cellZ][cellY];
        noise101 = slice1[cellZ + 1][cellY];
        noise010 = slice0[cellZ][cellY + 1];
        noise011 = slice0[cellZ + 1][cellY + 1];
        noise110 = slice1[cellZ][cellY + 1];
        noise111 = slice1[cellZ + 1][cellY + 1];
    }

    public void updateForY(double y)
    {
        valueXZ00 = Mth.lerp(y, noise000, noise010);
        valueXZ10 = Mth.lerp(y, noise100, noise110);
        valueXZ01 = Mth.lerp(y, noise001, noise011);
        valueXZ11 = Mth.lerp(y, noise101, noise111);
    }

    public void updateForX(double x)
    {
        valueZ0 = Mth.lerp(x, valueXZ00, valueXZ10);
        valueZ1 = Mth.lerp(x, valueXZ01, valueXZ11);
    }

    public void updateForZ(double z)
    {
        value = Mth.lerp(z, valueZ0, valueZ1);
    }

    public double sample()
    {
        return this.value;
    }

    public void swapSlices()
    {
        final double[][] temp = slice0;
        slice0 = slice1;
        slice1 = temp;
    }

    private void fillSlice(double[][] slice, int cellX)
    {
        final int cellWidth = settings.cellWidth();
        final int cellHeight = settings.cellHeight();
        for (int dz = 0; dz < settings.cellCountXZ() + 1; ++dz)
        {
            final int cellZ = settings.firstCellZ() + dz;
            for (int dy = 0; dy < settings.cellCountY() + 1; ++dy)
            {
                final int cellY = dy + settings.firstCellY();
                slice[dz][dy] = source.sample(cellX * cellWidth, cellY * cellHeight, cellZ * cellWidth);
            }
        }
    }

    private double[][] allocateSlice(int cellCountY, int cellCountXZ)
    {
        final int sizeXZ = cellCountXZ + 1;
        final int sizeY = cellCountY + 1;
        final double[][] slice = new double[sizeXZ][sizeY];
        for (int k = 0; k < sizeXZ; ++k)
        {
            slice[k] = new double[sizeY];
        }
        return slice;
    }

    public interface Source
    {
        double sample(int x, int y, int z);
    }
}
