/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import java.util.Random;

public class Metaballs3D
{
    private final Ball[] balls;

    public Metaballs3D(int size, Random random)
    {
        balls = new Ball[5 + random.nextInt(7)];
        for (int i = 0; i < balls.length; i++)
        {
            float ballSize = (0.1f + random.nextFloat() * 0.2f) * size;
            balls[i] = new Ball((random.nextFloat() - random.nextFloat()) * size * 0.5f, (random.nextFloat() - random.nextFloat()) * size * 0.5f, (random.nextFloat() - random.nextFloat()) * size * 0.5f, ballSize);
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

    // todo: record in j16
    static final class Ball
    {
        final float x, y, z, weight;

        Ball(float x, float y, float z, float weight)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.weight = weight;
        }
    }
}