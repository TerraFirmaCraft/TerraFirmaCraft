/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import net.minecraft.util.Mth;

/**
 * A collection of fast noise utility functions
 */
public final class NoiseUtil
{
    public static final int PRIME_X = 501125321;
    public static final int PRIME_Y = 1136930381;

    public static float lerp(float start, float end, float t)
    {
        return start * (1 - t) + end * t;
    }

    public static float lerp4(float valueNE, float valueNW, float valueSE, float valueSW, float tNS, float tEW)
    {
        final float valueN = lerp(valueNE, valueNW, tEW);
        final float valueS = lerp(valueSE, valueSW, tEW);
        return lerp(valueN, valueS, tNS);
    }

    public static int hash(int seed, int x, int y)
    {
        return hashPrimed(seed, x * PRIME_X, y * PRIME_Y);
    }

    public static int hashPrimed(int seed, int xPrimed, int yPrimed)
    {
        long hash = seed ^ xPrimed ^ yPrimed;
        hash *= 0x27d4eb2d;
        return (int) hash;
    }

    public static float triangle(float amplitude, float midpoint, float frequency, float phaseShift, float q)
    {
        float p = phaseShift + frequency * q;
        return midpoint + amplitude * (Math.abs(2f * p + 1f - 4f * Mth.floor(p / 2f + 0.75f)) - 1f);
    }
}