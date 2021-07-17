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

import it.unimi.dsi.fastutil.HashCommon;

import static net.dries007.tfc.world.noise.NoiseUtil.*;

public class Cellular2D implements INoise2D
{
    private final int seed;
    private final float jitter;
    private final CellularNoiseType returnType;

    // Last values
    private float lastX, lastY;
    private float centerX, centerY;
    private int centerHash;
    private float f1, f2;

    // Modifiers
    private float frequency;

    public Cellular2D(long seed)
    {
        this(seed, 1.0f, CellularNoiseType.VALUE);
    }

    public Cellular2D(long seed, float jitter, CellularNoiseType returnType)
    {
        this.seed = (int) HashCommon.mix(seed);
        this.jitter = jitter;
        this.returnType = returnType;
        this.frequency = 1;
    }

    public float getCenterX()
    {
        return centerX;
    }

    public float getCenterY()
    {
        return centerY;
    }

    public float get(CellularNoiseType alternateType)
    {
        return alternateType.apply(f1, f2, 1, centerHash);
    }

    @Override
    public float noise(float x, float y)
    {
        return noise(x, y, returnType);
    }

    @Override
    public Cellular2D spread(float scaleFactor)
    {
        this.frequency *= scaleFactor;
        return this;
    }

    public float noise(float x, float y, CellularNoiseType type)
    {
        if (lastX == x && lastY == y)
        {
            return type.apply(f1, f2, 1, centerHash);
        }
        lastX = x;
        lastY = y;

        x *= frequency;
        y *= frequency;

        final int xr = NoiseUtil.fastRound(x);
        final int yr = NoiseUtil.fastRound(y);

        f1 = Float.MAX_VALUE;
        f2 = Float.MAX_VALUE;
        centerHash = 0;

        final float cellularJitter = 0.43701595f * jitter;

        int xPrimed = (xr - 1) * PRIME_X;
        final int yPrimedBase = (yr - 1) * PRIME_Y;

        for (int xi = xr - 1; xi <= xr + 1; xi++)
        {
            int yPrimed = yPrimedBase;
            for (int yi = yr - 1; yi <= yr + 1; yi++)
            {
                final int hash = hashPrimed(seed, xPrimed, yPrimed);
                final int idx = hash & (255 << 1);

                final float cellX = xi + RANDOM_VECTORS_2D[idx] * cellularJitter;
                final float cellY = yi + RANDOM_VECTORS_2D[idx | 1] * cellularJitter;

                final float vecX = (x - cellX);
                final float vecY = (y - cellY);
                final float f = vecX * vecX + vecY * vecY;

                // Minimum effort to compute two things:
                // 1. The shortest two distances (f1, f2)
                // 2. The center + hash of the shortest distance
                if (f < f1)
                {
                    centerHash = hash;
                    centerX = cellX;
                    centerY = cellY;
                }

                float temp;
                if (f < f2)
                {
                    f2 = f;
                    if (f2 < f1)
                    {
                        temp = f1;
                        f1 = f2;
                        f2 = temp;
                    }
                }
                yPrimed += PRIME_Y;
            }
            xPrimed += PRIME_X;
        }

        centerX /= frequency;
        centerY /= frequency;

        return type.apply(f1, f2, 1, centerHash);
    }
}
