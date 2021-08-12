/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

public class OpenSimplex3D implements Noise3D
{
    final FastNoiseLite fnl;
    private float frequency;
    private float midpoint, amplitude;

    public OpenSimplex3D(long seed)
    {
        this((int) (seed ^ (seed >> 32)));
    }

    public OpenSimplex3D(int seed)
    {
        fnl = new FastNoiseLite(seed);
        fnl.SetFrequency(1f);
        fnl.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        fnl.SetFractalOctaves(1);
        fnl.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        frequency = 1;
        midpoint = 0;
        amplitude = 1;
    }

    @Override
    public float noise(float x, float y, float z)
    {
        return midpoint + fnl.GetNoise(x, y, z) * amplitude;
    }

    @Override
    public OpenSimplex3D octaves(int octaves)
    {
        fnl.SetFractalOctaves(octaves);
        fnl.SetFractalType(FastNoiseLite.FractalType.FBm);
        return spread(1f / (1 << (octaves - 1))); // Due to legacy reasons, most callers expect this scale factor
    }

    @Override
    public OpenSimplex3D spread(float scaleFactor)
    {
        frequency *= scaleFactor;
        fnl.SetFrequency(frequency);
        return this;
    }

    @Override
    public OpenSimplex3D scaled(float min, float max)
    {
        return scaled(-1, 1, min, max);
    }

    @Override
    public OpenSimplex3D scaled(float oldMin, float oldMax, float min, float max)
    {
        assert oldMin == -1 && oldMax == 1;
        midpoint = (max + min) / 2;
        amplitude = (max - min) / 2;
        return this;
    }

    float getAmplitude()
    {
        return amplitude;
    }
}
