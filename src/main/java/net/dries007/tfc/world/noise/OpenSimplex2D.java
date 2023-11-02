/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

public class OpenSimplex2D implements Noise2D
{
    final FastNoiseLite fnl;
    private double frequency;
    private double midpoint, amplitude;

    public OpenSimplex2D(long seed)
    {
        this((int) (seed ^ (seed >> 32)));
    }

    public OpenSimplex2D(int seed)
    {
        fnl = new FastNoiseLite(seed);
        fnl.SetFrequency(1f);
        fnl.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        fnl.SetFractalOctaves(1);

        frequency = 1;
        midpoint = 0;
        amplitude = 1;
    }

    @Override
    public double noise(double x, double z)
    {
        return midpoint + fnl.GetNoise(x, z) * amplitude;
    }

    @Override
    public OpenSimplex2D octaves(int octaves)
    {
        fnl.SetFractalOctaves(octaves);
        fnl.SetFractalType(FastNoiseLite.FractalType.FBm);
        return spread(1f / (1 << (octaves - 1))); // Due to legacy reasons, most callers expect this scale factor
    }

    @Override
    public OpenSimplex2D spread(double scaleFactor)
    {
        frequency *= scaleFactor;
        fnl.SetFrequency(frequency);
        return this;
    }

    @Override
    public OpenSimplex2D scaled(double min, double max)
    {
        return scaled(-1, 1, min, max);
    }

    @Override
    public OpenSimplex2D scaled(double oldMin, double oldMax, double min, double max)
    {
        assert oldMin == -1 && oldMax == 1;
        midpoint = (max + min) / 2;
        amplitude = (max - min) / 2;
        return this;
    }

    double getAmplitude()
    {
        return amplitude;
    }
}
