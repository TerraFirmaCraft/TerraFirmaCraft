/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import java.util.function.ToDoubleFunction;
import it.unimi.dsi.fastutil.HashCommon;

import net.dries007.tfc.util.Helpers;

/**
 * Modified from {@link FastNoiseLite#SingleCellular(int, double, double)}
 */
public class Cellular2D implements Noise2D
{
    private final double jitter;
    private final int seed;
    private final int sample;
    private double frequency;

    public Cellular2D(long seed)
    {
        this(seed, 0.43701595f, 1);
    }

    public Cellular2D(long seed, int sample)
    {
        this(seed, 0.43701595f, sample);
    }

    public Cellular2D(long seed, float jitter, int sample)
    {
        this.seed = HashCommon.long2int(seed);
        this.frequency = 1;
        this.jitter = jitter;
        this.sample = sample;
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
        double angle0 = -1;
        double thisCenterX = 0;
        double thisCenterY = 0;
        double neighborCenterX = 0;
        double neighborCenterY = 0;
        int closestHash = 0;
        int noJitterCenterX = 0;
        int noJitterCenterY = 0;

        int xPrimed = (xr - sample) * primeX;
        int yPrimedBase = (yr - sample) * primeY;

        for (int xi = xr - sample; xi <= xr + sample; xi++)
        {
            int yPrimed = yPrimedBase;

             for (int yi = yr - sample; yi <= yr + sample; yi++)
            {
                int hash = FastNoiseLite.Hash(seed, xPrimed, yPrimed);
                int idx = hash & (255 << 1);

                double vecX = xi + FastNoiseLite.RandVecs2D[idx] * jitter;
                double vecY = yi + FastNoiseLite.RandVecs2D[idx | 1] * jitter;

                double newDistanceX = vecX - x;
                double newDistanceY = vecY - y;
                double newAngle = Helpers.diamondAngle(newDistanceX, newDistanceY);
                double newDistance = newDistanceX * newDistanceX + newDistanceY * newDistanceY;

                final double oldDist1 = distance1;
                distance1 = FastNoiseLite.FastMax(FastNoiseLite.FastMin(distance1, newDistance), distance0);
                if (newDistance < distance0)
                {
                    // If the current distance is the smallest so far, update data
                    distance0 = newDistance;
                    angle0 = newAngle;
                    closestHash = hash;

                    // Store the last computed centers
                    neighborCenterX = thisCenterX; // Cell 2 X
                    neighborCenterY = thisCenterY; // Cell 2 Y
                    thisCenterX = vecX; // Cell 1 X
                    thisCenterY = vecY; // Cell 1 Y
                    noJitterCenterX = xi; // Cell 1 Grid X
                    noJitterCenterY = yi; // Cell 1 Grid Y
                }
                else if (distance1 != oldDist1)
                {
                    // If the distance to Cell 2 changed, without updating Cell 1, update data
                    neighborCenterX = vecX;
                    neighborCenterY = vecY;
                }
                yPrimed += primeY;
            }
            xPrimed += primeX;
        }

        return new Cell(thisCenterX / frequency, thisCenterY / frequency, noJitterCenterX, noJitterCenterY, neighborCenterX / frequency, neighborCenterY / frequency, distance0, distance1, closestHash * (1 / 2147483648.0f), angle0);
    }

    /**
     * @param x     "X"-coordinate of cell center
     * @param y     "Y"-coordinate of cell center
     * @param cx    "X"-coordinate of cell center before jitter, unscaled
     * @param cy    "Y"-coordinate of cell center before jitter, unscaled
     * @param nx    "X"-coordinate of neighboring cell center
     * @param ny    "Y"-coordinate of neighboring cell center
     * @param f1    Distance to x, y
     * @param f2    Distance to nx, ny
     * @param noise Hash value of the cell, range 0-1
     * @param angle Diamond angle to the center
     */
    public record Cell(double x, double y, int cx, int cy, double nx, double ny, double f1, double f2, double noise, double angle) {}
}