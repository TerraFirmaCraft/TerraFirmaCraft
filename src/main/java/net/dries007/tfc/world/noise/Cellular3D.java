/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

public class Cellular3D implements Noise3D
{
    private final FastNoiseLite fnl;
    private float frequency;

    public Cellular3D(long seed)
    {
        this((int) (seed ^ (seed >> 32)));
    }

    public Cellular3D(int seed)
    {
        fnl = new FastNoiseLite(seed);
        fnl.SetFrequency(1);
        fnl.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        fnl.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean);
        fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.CellValue);

        frequency = 1;
    }

    public Cellular3D type(FastNoiseLite.CellularReturnType type)
    {
        fnl.SetCellularReturnType(type);
        return this;
    }

    @Override
    public float noise(float x, float y, float z)
    {
        return fnl.GetNoise(x, y, z);
    }

    @Override
    public Cellular3D spread(float scaleFactor)
    {
        frequency *= scaleFactor;
        fnl.SetFrequency(frequency);
        return this;
    }

    public float centerX()
    {
        return fnl.lastCenterX;
    }

    public float centerY()
    {
        return fnl.lastCenterY;
    }

    public float centerZ()
    {
        return fnl.lastCenterZ;
    }

    public float f1()
    {
        return fnl.lastDistance0;
    }

    public float f2()
    {
        return fnl.lastDistance1;
    }
}
