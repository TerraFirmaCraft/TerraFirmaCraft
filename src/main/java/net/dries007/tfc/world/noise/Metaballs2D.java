/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import net.minecraft.util.RandomSource;

import net.dries007.tfc.util.Helpers;

/**
 * A 2D Implementation of <a href="https://en.wikipedia.org/wiki/Metaballs">Metaballs</a>, primarily using the techniques outlined in <a href="http://jamie-wong.com/2014/08/19/metaballs-and-marching-squares/">this blog</a>
 */
public class Metaballs2D
{
    public static Metaballs2D simple(RandomSource random, int size)
    {
        return new Metaballs2D(random, 3, 8, 0.1f * size, 0.3f * size, 0.5f * size);
    }

    private final Ball[] balls; // x, y, weight

    public Metaballs2D(RandomSource random, int minBalls, int maxBalls, double minSize, double maxSize, double radius)
    {
        final int ballCount = Helpers.uniform(random, minBalls, maxBalls);

        balls = new Ball[ballCount];
        for (int i = 0; i < balls.length; i++)
        {
            balls[i] = new Ball(
                Helpers.triangle(random, radius),
                Helpers.triangle(random, radius),
                Helpers.uniform(random, minSize, maxSize)
            );
        }
    }

    public boolean inside(double x, double z)
    {
        return sample(x, z) > 1f;
    }

    public double sample(double x, double z)
    {
        double f = 0;
        for (Ball ball : balls)
        {
            f += ball.weight * Math.abs(ball.weight) / ((x - ball.x) * (x - ball.x) + (z - ball.z) * (z - ball.z));
        }
        return f;
    }

    record Ball(double x, double z, double weight) {}
}