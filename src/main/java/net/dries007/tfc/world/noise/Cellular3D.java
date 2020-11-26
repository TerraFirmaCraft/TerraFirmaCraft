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


import static net.dries007.tfc.world.noise.NoiseUtil.*;

public class Cellular3D implements INoise3D
{
    private final int seed;
    private final float jitter;
    private final CellularNoiseType returnType;

    private float centerX, centerY, centerZ;
    private float frequency;

    public Cellular3D(long seed)
    {
        this(seed, 1.0f, CellularNoiseType.VALUE);
    }

    public Cellular3D(long seed, float jitter, CellularNoiseType returnType)
    {
        this.seed = (int) seed;
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

    public float getCenterZ()
    {
        return centerZ;
    }

    @Override
    public float noise(float x, float y, float z)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        int xr = NoiseUtil.fastRound(x);
        int yr = NoiseUtil.fastRound(y);
        int zr = NoiseUtil.fastRound(z);

        float distance0 = Float.MAX_VALUE;
        float distance1 = Float.MAX_VALUE;
        int closestHash = 0;

        float cellularJitter = 0.39614353f * jitter;

        int xPrimed = (xr - 1) * PRIME_X;
        int yPrimedBase = (yr - 1) * PRIME_Y;
        int zPrimedBase = (zr - 1) * PRIME_Z;

        for (int xi = xr - 1; xi <= xr + 1; xi++)
        {
            int yPrimed = yPrimedBase;

            for (int yi = yr - 1; yi <= yr + 1; yi++)
            {
                int zPrimed = zPrimedBase;

                for (int zi = zr - 1; zi <= zr + 1; zi++)
                {
                    int hash = NoiseUtil.hashPrimed(seed, xPrimed, yPrimed, zPrimed);
                    int idx = hash & (255 << 2);

                    float vecX = (xi - x) + RANDOM_VECTORS_3D[idx] * cellularJitter;
                    float vecY = (yi - y) + RANDOM_VECTORS_3D[idx | 1] * cellularJitter;
                    float vecZ = (zi - z) + RANDOM_VECTORS_3D[idx | 2] * cellularJitter;

                    float newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ;

                    distance1 = NoiseUtil.fastMax(NoiseUtil.fastMin(distance1, newDistance), distance0);
                    if (newDistance < distance0)
                    {
                        distance0 = newDistance;
                        closestHash = hash;
                        centerX = vecX + x;
                        centerY = vecY + y;
                        centerZ = vecZ + z;
                    }
                    zPrimed += PRIME_Z;
                }
                yPrimed += PRIME_Y;
            }
            xPrimed += PRIME_X;
        }

        centerX /= frequency;
        centerY /= frequency;
        centerZ /= frequency;

        return returnType.calculate(distance0, distance1, closestHash);
    }

    @Override
    public Cellular3D spread(float scaleFactor)
    {
        this.frequency *= scaleFactor;
        return this;
    }
}
