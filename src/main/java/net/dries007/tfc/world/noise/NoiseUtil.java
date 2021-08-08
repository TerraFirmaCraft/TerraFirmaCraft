/*
 * This was originally part of FastNoise (https://github.com/Auburn/FastNoise) and has been included as per the MIT license:
 *
 * MIT License
 *
 * Copyright(c) 2020 Jordan Peck (jordan.me2@gmail.com)
 * Copyright(c) 2020 Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files(the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions :
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.dries007.tfc.world.noise;

import net.minecraft.util.math.MathHelper;

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
        return midpoint + amplitude * (Math.abs(2f * p + 1f - 4f * MathHelper.floor(p / 2f + 0.75f)) - 1f);
    }
}