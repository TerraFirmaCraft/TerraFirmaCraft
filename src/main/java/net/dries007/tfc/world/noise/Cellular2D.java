/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import java.util.function.ToDoubleFunction;
import it.unimi.dsi.fastutil.HashCommon;

/**
 * Modified from {@link FastNoiseLite#SingleCellular(int, double, double)}
 */
public class Cellular2D implements Noise2D
{
    public static final double JITTER = 0.43701595f;

    private final int seed;
    private double frequency;

    public Cellular2D(long seed)
    {
        this.seed = HashCommon.long2int(seed);
        this.frequency = 1;
    }

    @Override
    public double noise(double x, double y)
    {
        return cell(x, y).noise();
    }

    @Override
    public Cellular2D spread(double scaleFactor)
    {
        frequency *= scaleFactor;
        return this;
    }

    public Noise2D then(ToDoubleFunction<Cell> f)
    {
        return (x, y) -> f.applyAsDouble(cell(x, y));
    }

    public Cell cell(double x, double y)
    {
        x *= frequency;
        y *= frequency;

        final int primeX = 501125321;
        final int primeY = 1136930381;

        int xr = FastNoiseLite.FastFloor(x);
        int yr = FastNoiseLite.FastFloor(y);

        double distance0 = Double.MAX_VALUE;
        double distance1 = Double.MAX_VALUE;
        double closestCenterX = 0;
        double closestCenterY = 0;
        int closestHash = 0;
        int closestCellX = 0;
        int closestCellY = 0;

        int xPrimed = (xr - 1) * primeX;
        int yPrimedBase = (yr - 1) * primeY;

        for (int xi = xr - 1; xi <= xr + 1; xi++)
        {
            int yPrimed = yPrimedBase;

            for (int yi = yr - 1; yi <= yr + 1; yi++)
            {
                int hash = FastNoiseLite.Hash(seed, xPrimed, yPrimed);
                int idx = hash & (255 << 1);

                double vecX = xi + FastNoiseLite.RandVecs2D[idx] * JITTER;
                double vecY = yi + FastNoiseLite.RandVecs2D[idx | 1] * JITTER;

                double newDistance = (vecX - x) * (vecX - x) + (vecY - y) * (vecY - y);

                distance1 = FastNoiseLite.FastMax(FastNoiseLite.FastMin(distance1, newDistance), distance0);
                if (newDistance < distance0)
                {
                    distance0 = newDistance;
                    closestHash = hash;

                    // Store the last computed centers
                    closestCenterX = vecX;
                    closestCenterY = vecY;
                    closestCellX = xi;
                    closestCellY = yi;
                }
                yPrimed += primeY;
            }
            xPrimed += primeX;
        }

        return new Cell(closestCenterX / frequency, closestCenterY / frequency, closestCellX, closestCellY, distance0, distance1, closestHash * (1 / 2147483648.0f));
    }

    public record Cell(double x, double y, int cx, int cy, double f1, double f2, double noise) {}
}
