/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import it.unimi.dsi.fastutil.HashCommon;

/**
 * Modified from {@link FastNoiseLite#SingleCellular(int, float, float, float)}
 */
public class Cellular3D implements Noise3D
{
    private final int seed;
    private float frequency;

    public Cellular3D(long seed)
    {
        this.seed = HashCommon.long2int(seed);
        this.frequency = 1;
    }

    @Override
    public float noise(float x, float y, float z)
    {
        return cell(x, y, z).noise();
    }

    @Override
    public Cellular3D spread(float scaleFactor)
    {
        frequency *= scaleFactor;
        return this;
    }

    public Cell cell(float x, float y, float z)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        int xr = FastNoiseLite.FastRound(x);
        int yr = FastNoiseLite.FastRound(y);
        int zr = FastNoiseLite.FastRound(z);

        float distance0 = Float.MAX_VALUE;
        float distance1 = Float.MAX_VALUE;
        int closestHash = 0;
        float closestCenterX = 0;
        float closestCenterY = 0;
        float closestCenterZ = 0;

        float cellularJitter = 0.39614353f;

        int xPrimed = (xr - 1) * FastNoiseLite.PrimeX;
        int yPrimedBase = (yr - 1) * FastNoiseLite.PrimeY;
        int zPrimedBase = (zr - 1) * FastNoiseLite.PrimeZ;

        for (int xi = xr - 1; xi <= xr + 1; xi++)
        {
            int yPrimed = yPrimedBase;

            for (int yi = yr - 1; yi <= yr + 1; yi++)
            {
                int zPrimed = zPrimedBase;

                for (int zi = zr - 1; zi <= zr + 1; zi++)
                {
                    int hash = FastNoiseLite.Hash(seed, xPrimed, yPrimed, zPrimed);
                    int idx = hash & (255 << 2);

                    float vecX = xi + FastNoiseLite.RandVecs3D[idx] * cellularJitter;
                    float vecY = yi + FastNoiseLite.RandVecs3D[idx | 1] * cellularJitter;
                    float vecZ = zi + FastNoiseLite.RandVecs3D[idx | 2] * cellularJitter;

                    float newDistance = (vecX - x) * (vecX - x) + (vecY - y) * (vecY - y) + (vecZ - z) * (vecZ - z);

                    distance1 = FastNoiseLite.FastMax(FastNoiseLite.FastMin(distance1, newDistance), distance0);
                    if (newDistance < distance0)
                    {
                        distance0 = newDistance;
                        closestHash = hash;
                        closestCenterX = vecX;
                        closestCenterY = vecY;
                        closestCenterZ = vecZ;
                    }
                    zPrimed += FastNoiseLite.PrimeZ;
                }
                yPrimed += FastNoiseLite.PrimeY;
            }
            xPrimed += FastNoiseLite.PrimeX;
        }

        return new Cell(closestCenterX / frequency, closestCenterY / frequency, closestCenterZ / frequency, distance0, distance1, closestHash * (1 / 2147483648.0f));
    }

    public record Cell(float x, float y, float z, float f1, float f2, float noise) {}
}
