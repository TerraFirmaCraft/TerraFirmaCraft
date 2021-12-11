/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import java.util.Random;

import net.minecraft.world.level.levelgen.RandomSource;

import net.dries007.tfc.util.Helpers;

public class Metaballs3D
{
    public static Metaballs3D simple(RandomSource random, int size)
    {
        return new Metaballs3D(random, 5, 7, 0.1f * size, 0.3f * size, 0.5f * size);
    }

    private final Ball[] balls;

    public Metaballs3D(RandomSource random, int minBalls, int maxBalls, float minSize, float maxSize, float radius)
    {
        final int ballCount = Helpers.uniform(random, minBalls, maxBalls);
        final int negativeBallCount = minSize < 0 ? (int) (ballCount * (-minSize / (maxSize - minSize))) : 0;
        balls = new Ball[ballCount];
        for (int i = 0; i < balls.length; i++)
        {
            balls[i] = new Ball(
                Helpers.triangle(random, radius),
                Helpers.triangle(random, radius),
                Helpers.triangle(random, radius),
                i < negativeBallCount ? Helpers.uniform(random, minSize, 0) : Helpers.uniform(random, 0, maxSize)
            );
        }
    }

    public boolean inside(float x, float y, float z)
    {
        float f = 0;
        for (Ball ball : balls)
        {
            f += ball.weight * Math.abs(ball.weight) / ((x - ball.x) * (x - ball.x) + (y - ball.y) * (y - ball.y) + (z - ball.z) * (z - ball.z));
            if (f > 1)
            {
                return true;
            }
        }
        return false;
    }

    record Ball(float x, float y, float z, float weight) {}
}