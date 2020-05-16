/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.noise;

import net.dries007.tfc.util.function.FloatUnaryFunction;

/**
 * Wrapper for a 2D noise layer
 */
@FunctionalInterface
public interface INoise2D
{
    float noise(float x, float z);

    default INoise2D octaves(int octaves)
    {
        return octaves(octaves, 0.5f);
    }

    /**
     * Creates simple octaves, where the octaves all use the same base noise function
     *
     * @param octaves     The number of octaves
     * @param persistence The base for each octave's amplitude
     * @return A new noise function
     */
    default INoise2D octaves(int octaves, float persistence)
    {
        final float[] frequency = new float[octaves];
        final float[] amplitude = new float[octaves];
        for (int i = 0; i < octaves; i++)
        {
            frequency[i] = 1 << i;
            amplitude[i] = (float) Math.pow(persistence, octaves - i);
        }
        return (x, y) -> {
            float value = 0;
            for (int i = 0; i < octaves; i++)
            {
                value += INoise2D.this.noise(x / frequency[i], y / frequency[i]) * amplitude[i];
            }
            return value;
        };
    }

    /**
     * Creates ridged noise using absolute value
     *
     * @return a new noise function
     */
    default INoise2D ridged()
    {
        return (x, y) -> {
            float value = INoise2D.this.noise(x, y);
            value = value < 0 ? -value : value;
            return 1f - 2f * value;
        };
    }

    /**
     * Creates "terraces" by taking the nearest level and rounding
     * Input must be in range [-1, 1]
     *
     * @param levels The number of levels to round to
     * @return a new noise function
     */
    default INoise2D terraces(int levels)
    {
        return (x, y) -> {
            float value = 0.5f * INoise2D.this.noise(x, y) + 0.5f;
            float rounded = (int) (value * levels); // In range [0, levels)
            return (rounded * 2f) / levels - 1f;
        };
    }

    /**
     * Spreads out the noise via the input parameters
     *
     * @param scaleFactor The scale for the input params
     * @return a new noise function
     */
    default INoise2D spread(float scaleFactor)
    {
        return (x, y) -> INoise2D.this.noise(x * scaleFactor, y * scaleFactor);
    }

    default INoise2D scaled(float min, float max)
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
    default INoise2D scaled(float oldMin, float oldMax, float min, float max)
    {
        return (x, y) -> {
            float value = INoise2D.this.noise(x, y);
            return (value - oldMin) / (oldMax - oldMin) * (max - min) + min;
        };
    }

    /**
     * Applies domain warping to each input coordinate using the two input noise functions
     *
     * @param warpX the x warp noise
     * @param warpY the y warp noise
     * @return a new noise function
     */
    default INoise2D warped(INoise2D warpX, INoise2D warpY)
    {
        return (x, y) -> {
            float x0 = x + warpX.noise(x, y);
            float y0 = y + warpY.noise(x, y);
            return INoise2D.this.noise(x0, y0);
        };
    }

    /**
     * Creates flattened noise by cutting off values above or below a threshold
     *
     * @param min the minimum noise value
     * @param max the maximum noise value
     * @return a new noise function
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    default INoise2D flattened(float min, float max)
    {
        return (x, y) -> {
            float noise = INoise2D.this.noise(x, y);
            return noise > max ? max : noise < min ? min : noise;
        };
    }

    default INoise2D add(INoise2D other)
    {
        return (x, y) -> INoise2D.this.noise(x, y) + other.noise(x, y);
    }

    default INoise2D map(FloatUnaryFunction mappingFunction)
    {
        return (x, y) -> mappingFunction.applyAsFloat(INoise2D.this.noise(x, y));
    }
}
