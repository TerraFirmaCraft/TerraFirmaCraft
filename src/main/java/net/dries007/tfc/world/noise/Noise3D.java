/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

/**
 * Wrapper for a 3D Noise Layer
 */
@FunctionalInterface
public interface Noise3D
{
    float noise(float x, float y, float z);

    /**
     * @param octaves The number of octaves
     */
    default Noise3D octaves(int octaves)
    {
        final float[] frequency = new float[octaves];
        final float[] amplitude = new float[octaves];
        for (int i = 0; i < octaves; i++)
        {
            frequency[i] = 1 << i;
            amplitude[i] = (float) Math.pow(0.5f, octaves - i);
        }
        return (x, y, z) -> {
            float value = 0;
            for (int i = 0; i < octaves; i++)
            {
                value += Noise3D.this.noise(x / frequency[i], y / frequency[i], z / frequency[i]) * amplitude[i];
            }
            return value;
        };
    }

    /**
     * Spreads out the noise via the input parameters
     *
     * @param scaleFactor The scale for the input params
     * @return a new noise function
     */
    default Noise3D spread(float scaleFactor)
    {
        return (x, y, z) -> Noise3D.this.noise(x * scaleFactor, y * scaleFactor, z * scaleFactor);
    }

    default Noise3D scaled(float min, float max)
    {
        return scaled(-1, 1, min, max);
    }

    /**
     * Re-scales the output of the noise to a new range
     *
     * @param oldMin the old minimum value (typically -1)
     * @param oldMax the old maximum value (typically 1)
     * @param min    the new minimum value
     * @param max    the new maximum value
     * @return a new noise function
     */
    default Noise3D scaled(float oldMin, float oldMax, float min, float max)
    {
        return (x, y, z) -> {
            float value = Noise3D.this.noise(x, y, z);
            return (value - oldMin) / (oldMax - oldMin) * (max - min) + min;
        };
    }

    default Noise3D warped(OpenSimplex3D warp)
    {
        warp.fnl.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
        warp.fnl.SetFractalType(FastNoiseLite.FractalType.DomainWarpIndependent);
        warp.fnl.SetDomainWarpAmp(warp.getAmplitude() * 2);
        final FastNoiseLite.Vector3 cursor = new FastNoiseLite.Vector3(0, 0, 0);
        return (x, y, z) -> {
            cursor.x = x;
            cursor.y = y;
            cursor.z = z;
            warp.fnl.DomainWarp(cursor);
            return Noise3D.this.noise(cursor.x, cursor.y, cursor.z);
        };
    }
}