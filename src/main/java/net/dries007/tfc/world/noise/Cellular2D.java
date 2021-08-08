package net.dries007.tfc.world.noise;

public class Cellular2D implements Noise2D
{
    final FastNoiseLite fnl;
    private float frequency;

    public Cellular2D(long seed)
    {
        this((int) (seed ^ (seed >> 32)));
    }

    public Cellular2D(int seed)
    {
        fnl = new FastNoiseLite(seed);
        fnl.SetFrequency(1);
        fnl.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        fnl.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean);
        fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.CellValue);

        frequency = 1;
    }

    public Cellular2D type(FastNoiseLite.CellularReturnType type)
    {
        fnl.SetCellularReturnType(type);
        return this;
    }

    @Override
    public float noise(float x, float z)
    {
        return fnl.GetNoise(x, z);
    }

    @Override
    public Cellular2D spread(float scaleFactor)
    {
        frequency *= scaleFactor;
        fnl.SetFrequency(frequency);
        return this;
    }

    public float centerX()
    {
        return fnl.lastCenterX;
    }

    public float centerZ()
    {
        return fnl.lastCenterY;
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
