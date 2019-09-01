/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.noise;

import net.minecraft.util.math.MathHelper;

/**
 * Simple sin wave wrapper, using the approximate calculation in {@link MathHelper}
 * Can be extended to a {@link INoise2D}
 */
public class SinNoise
{
    private final float amplitude;
    private final float midpoint;
    private final float frequency;
    private final float phaseShift;

    public SinNoise(float amplitude, float midpoint, float frequency, float phaseShift)
    {
        this.amplitude = amplitude;
        this.midpoint = midpoint;
        this.frequency = frequency;
        this.phaseShift = phaseShift;
    }

    public INoise2D extendX()
    {
        return (x, y) -> sin(y);
    }

    public INoise2D extendY()
    {
        return (x, y) -> sin(x);
    }

    private float sin(float q)
    {
        return midpoint + amplitude * MathHelper.sin(phaseShift + frequency * q);
    }
}
