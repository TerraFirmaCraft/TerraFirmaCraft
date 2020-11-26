/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
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

public class OpenSimplex2D implements INoise2D
{
    private static final float SQRT3 = (float) 1.7320508075688772935274463415059;
    private static final float F2 = 0.5f * (SQRT3 - 1);
    private static final float G2 = (3 - SQRT3) / 6;

    private final int seed;

    public OpenSimplex2D(long seed)
    {
        this.seed = (int) seed;
    }

    @Override
    @SuppressWarnings("NumericOverflow")
    public float noise(float x, float y)
    {
        float skew = (x + y) * F2;
        x += skew;
        y += skew;

        int i = NoiseUtil.fastFloor(x);
        int j = NoiseUtil.fastFloor(y);
        float xi = x - i;
        float yi = y - j;

        i *= NoiseUtil.PRIME_X;
        j *= NoiseUtil.PRIME_Y;
        int i1 = i + NoiseUtil.PRIME_X;
        int j1 = j + NoiseUtil.PRIME_Y;

        float t = (xi + yi) * G2;
        float x0 = xi - t;
        float y0 = yi - t;

        float a0 = (2.0f / 3.0f) - x0 * x0 - y0 * y0;
        float value = (a0 * a0) * (a0 * a0) * NoiseUtil.gradientCoord(seed, i, j, x0, y0);

        float a1 = 2 * (1 - 2 * G2) * (1 / G2 - 2) * t + ((-2 * (1 - 2 * G2) * (1 - 2 * G2)) + a0);
        float x1 = x0 - (1 - 2 * G2);
        float y1 = y0 - (1 - 2 * G2);
        value += (a1 * a1) * (a1 * a1) * NoiseUtil.gradientCoord(seed, i1, j1, x1, y1);

        // Nested conditionals were faster than compact bit logic/arithmetic.
        float xmyi = xi - yi;
        if (t > G2)
        {
            if (xi + xmyi > 1)
            {
                float x2 = x0 + (3 * G2 - 2);
                float y2 = y0 + (3 * G2 - 1);
                float a2 = (2.0f / 3.0f) - x2 * x2 - y2 * y2;
                if (a2 > 0)
                {
                    value += (a2 * a2) * (a2 * a2) * NoiseUtil.gradientCoord(seed, i + (NoiseUtil.PRIME_X << 1), j + NoiseUtil.PRIME_Y, x2, y2);
                }
            }
            else
            {
                float x2 = x0 + G2;
                float y2 = y0 + (G2 - 1);
                float a2 = (2.0f / 3.0f) - x2 * x2 - y2 * y2;
                if (a2 > 0)
                {
                    value += (a2 * a2) * (a2 * a2) * NoiseUtil.gradientCoord(seed, i, j + NoiseUtil.PRIME_Y, x2, y2);
                }
            }

            if (yi - xmyi > 1)
            {
                float x3 = x0 + (3 * G2 - 1);
                float y3 = y0 + (3 * G2 - 2);
                float a3 = (2.0f / 3.0f) - x3 * x3 - y3 * y3;
                if (a3 > 0)
                {
                    value += (a3 * a3) * (a3 * a3) * NoiseUtil.gradientCoord(seed, i + NoiseUtil.PRIME_X, j + (NoiseUtil.PRIME_Y << 1), x3, y3);
                }
            }
            else
            {
                float x3 = x0 + (G2 - 1);
                float y3 = y0 + G2;
                float a3 = (2.0f / 3.0f) - x3 * x3 - y3 * y3;
                if (a3 > 0)
                {
                    value += (a3 * a3) * (a3 * a3) * NoiseUtil.gradientCoord(seed, i + NoiseUtil.PRIME_X, j, x3, y3);
                }
            }
        }
        else
        {
            if (xi + xmyi < 0)
            {
                float x2 = x0 + (1 - G2);
                float y2 = y0 - G2;
                float a2 = (2.0f / 3.0f) - x2 * x2 - y2 * y2;
                if (a2 > 0)
                {
                    value += (a2 * a2) * (a2 * a2) * NoiseUtil.gradientCoord(seed, i - NoiseUtil.PRIME_X, j, x2, y2);
                }
            }
            else
            {
                float x2 = x0 + (G2 - 1);
                float y2 = y0 + G2;
                float a2 = (2.0f / 3.0f) - x2 * x2 - y2 * y2;
                if (a2 > 0)
                {
                    value += (a2 * a2) * (a2 * a2) * NoiseUtil.gradientCoord(seed, i + NoiseUtil.PRIME_X, j, x2, y2);
                }
            }

            if (yi < xmyi)
            {
                float x2 = x0 - G2;
                float y2 = y0 - (G2 - 1);
                float a2 = (2.0f / 3.0f) - x2 * x2 - y2 * y2;
                if (a2 > 0)
                {
                    value += (a2 * a2) * (a2 * a2) * NoiseUtil.gradientCoord(seed, i, j - NoiseUtil.PRIME_Y, x2, y2);
                }
            }
            else
            {
                float x2 = x0 + G2;
                float y2 = y0 + (G2 - 1);
                float a2 = (2.0f / 3.0f) - x2 * x2 - y2 * y2;
                if (a2 > 0)
                {
                    value += (a2 * a2) * (a2 * a2) * NoiseUtil.gradientCoord(seed, i, j + NoiseUtil.PRIME_Y, x2, y2);
                }
            }
        }

        return value * 18.24196194486065f;
    }
}
