/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import java.util.Random;

/**
 * A 2D Implementation of <a href="https://en.wikipedia.org/wiki/Metaballs">Metaballs</a>, primarily using the techniques outlined in <a href="http://jamie-wong.com/2014/08/19/metaballs-and-marching-squares/">this blog</a>
 */
public class Metaballs2D
{
    private final Ball[] balls; // x, y, weight

    public Metaballs2D(int size, Random random)
    {
        balls = new Ball[3 + random.nextInt(5)];
        for (int i = 0; i < balls.length; i++)
        {
            float ballSize = (0.1f + random.nextFloat() * 0.2f) * size;
            balls[i] = new Ball((random.nextFloat() - random.nextFloat()) * size * 0.5f, (random.nextFloat() - random.nextFloat()) * size * 0.5f, ballSize);
        }
    }

    public boolean inside(float x, float z)
    {
        float f = 0;
        for (Ball ball : balls)
        {
            f += ball.weight * Math.abs(ball.weight) / ((x - ball.x) * (x - ball.x) + (z - ball.z) * (z - ball.z));
            if (f > 1)
            {
                return true;
            }
        }
        return false;
    }

    record Ball(float x, float z, float weight) {}
}