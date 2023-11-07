/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import it.unimi.dsi.fastutil.HashCommon;

/**
 * Modified from {@link FastNoiseLite#SingleCellular(int, double, double, double)}
 */
public class Cellular3D implements Noise3D
{
    private final int seed;
    private double frequency;

    public Cellular3D(long seed)
    {
        this.seed = HashCommon.long2int(seed);
        this.frequency = 1;
    }

    @Override
    public double noise(double x, double y, double z)
    {
        return cell(x, y, z).noise();
    }

    @Override
    public Cellular3D spread(double scaleFactor)
    {
        frequency *= scaleFactor;
        return this;
    }

    public Cell cell(double x, double y, double z)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        int xr = FastNoiseLite.FastRound(x);
        int yr = FastNoiseLite.FastRound(y);
        int zr = FastNoiseLite.FastRound(z);

        double distance0 = Double.MAX_VALUE;
        double distance1 = Double.MAX_VALUE;
        int closestHash = 0;
        double closestCenterX = 0;
        double closestCenterY = 0;
        double closestCenterZ = 0;

        double cellularJitter = 0.39614353f;

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

                    double vecX = xi + FastNoiseLite.RandVecs3D[idx] * cellularJitter;
                    double vecY = yi + FastNoiseLite.RandVecs3D[idx | 1] * cellularJitter;
                    double vecZ = zi + FastNoiseLite.RandVecs3D[idx | 2] * cellularJitter;

                    double newDistance = (vecX - x) * (vecX - x) + (vecY - y) * (vecY - y) + (vecZ - z) * (vecZ - z);

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

    public record Cell(double x, double y, double z, double f1, double f2, double noise) {}
}
