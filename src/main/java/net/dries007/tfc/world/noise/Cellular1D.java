/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.function.ToDoubleFunction;

import net.dries007.tfc.util.Helpers;

/**
 * Modified from {@link FastNoiseLite#SingleCellular(int, double, double)}
 */
public class Cellular1D
{
    private final double jitter;
    private final int seed;
    private double frequency;

    public Cellular1D(long seed)
    {
        this(seed, 0.43701595f);
    }

    public Cellular1D(long seed, float jitter)
    {
        this.seed = HashCommon.long2int(seed);
        this.frequency = 1;
        this.jitter = jitter;
    }

    public double noise(double x)
    {
        return cell(x).noise();
    }

    public Cellular1D spread(double scaleFactor)
    {
        frequency *= scaleFactor;
        return this;
    }

    public Cell cell(double x)
    {
        x *= frequency;

        final int primeX = 501125321;
        int xr = FastNoiseLite.FastFloor(x);
        double distance0 = Double.MAX_VALUE;
        double distance1 = Double.MAX_VALUE;
        double thisCenterX = 0;
        double neighborCenterX = 0;
        int closestHash = 0;
        int noJitterCenterX = 0;

        int xPrimed = (xr - 1) * primeX;

        for (int xi = xr - 1; xi <= xr + 1; xi++)
        {

            int hash = FastNoiseLite.Hash(seed, xPrimed);
            int idx = hash & (255 << 1);

            double vecX = xi + FastNoiseLite.RandVecs2D[idx] * jitter;

            double newDistance = vecX - x;

            final double oldDist1 = distance1;
            distance1 = FastNoiseLite.FastMax(FastNoiseLite.FastMin(distance1, newDistance), distance0);
            if (newDistance < distance0)
            {
                // If the current distance is the smallest so far, update data
                distance0 = newDistance;
                closestHash = hash;

                // Store the last computed centers
                neighborCenterX = thisCenterX; // Cell 2 X
                thisCenterX = vecX; // Cell 1 X
                noJitterCenterX = xi; // Cell 1 Grid X
            }
            else if (distance1 != oldDist1)
            {
                // If the distance to Cell 2 changed, without updating Cell 1, update data
                neighborCenterX = vecX;
            }

            xPrimed += primeX;
        }

        return new Cell(thisCenterX / frequency, noJitterCenterX, neighborCenterX / frequency, distance0, distance1, closestHash * (1 / 2147483648.0f));
    }

    /**
     * @param x     "X"-coordinate of cell center
     * @param cx    "X"-coordinate of cell center before jitter, unscaled
     * @param nx    "X"-coordinate of neighboring cell center
     * @param f1    Distance to x
     * @param f2    Distance to nx
     * @param noise Hash value of the cell, range 0-1
     */
    public record Cell(double x, int cx, double nx, double f1, double f2, double noise) {}
}