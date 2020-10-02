package net.dries007.tfc.world.noise;

/**
 * Wrapper for a 1D Noise Layer
 */
@FunctionalInterface
public interface INoise1D
{
    static INoise1D sin(float amplitude, float midpoint, float frequency, float phaseShift)
    {
        final float pi = (float) Math.PI;
        return q -> midpoint + amplitude * (float) Math.sin(pi * (phaseShift + frequency * q));
    }

    static INoise1D triangle(float amplitude, float midpoint, float frequency, float phaseShift)
    {
        return q -> {
            float p = phaseShift + frequency * q;
            return midpoint + amplitude * (Math.abs(2f * p + 1f - 4f * NoiseUtil.fastFloor(p / 2f + 0.75f)) - 1f);
        };
    }

    float noise(float in);

    /**
     * Applies a transformation to the input coordinates. This is useful for clamp / scale operations on the input coordinates.
     *
     * @param transform the input function
     * @return a new noise function
     */
    default INoise1D transformed(INoise1D transform)
    {
        return in -> INoise1D.this.noise(transform.noise(in));
    }

    /**
     * Extends to a 2D noise layer
     *
     * @return a new noise function
     */
    default INoise2D extendX()
    {
        return (x, y) -> noise(y);
    }

    /**
     * Extends to a 2D noise layer
     *
     * @return a new noise function
     */
    default INoise2D extendY()
    {
        return (x, y) -> noise(x);
    }
}
