/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import net.minecraft.util.Mth;

/**
 * An abstraction for trilinear interpolation.
 * Source coordinates are full resolution but only sampled at values that are divisible by the cell width / cell height.
 * The iteration order here is changed to go:
 * 1) Sample / swap slices (cell x)
 * 2) Select slice y/z (cell y, cell z)
 * 3) Select x/z (local x, local z)
 * 4) Select y (local y)
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
    private double valueX0Z, valueX1Z;
    private double valueXYZ;

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

    public void updateForXZ(double x, double z)
    {
        final double valueX00 = Mth.lerp(x, noise000, noise100);
        final double valueX10 = Mth.lerp(x, noise010, noise110);
        final double valueX01 = Mth.lerp(x, noise001, noise101);
        final double valueX11 = Mth.lerp(x, noise011, noise111);

        valueX0Z = Mth.lerp(z, valueX00, valueX01);
        valueX1Z = Mth.lerp(z, valueX10, valueX11);
    }

    public void updateForY(double y)
    {
        valueXYZ = Mth.lerp(y, valueX0Z, valueX1Z);
    }

    public double sample()
    {
        return this.valueXYZ;
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

    @FunctionalInterface
    public interface Source
    {
        double sample(int x, int y, int z);
    }
}
