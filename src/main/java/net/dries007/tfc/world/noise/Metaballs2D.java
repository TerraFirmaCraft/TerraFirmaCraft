package net.dries007.tfc.world.noise;

import java.util.Random;

/**
 * A 2D Implementation of <a href="https://en.wikipedia.org/wiki/Metaballs">Metaballs</a>, primarily using the techniques outlined in <a href="http://jamie-wong.com/2014/08/19/metaballs-and-marching-squares/">This blog</a>
 */
public class Metaballs2D implements INoise2D
{
    private final Vec3[] balls; // x, y, weight

    public Metaballs2D(int size, Random random)
    {
        balls = new Vec3[3 + random.nextInt(5)];
        for (int i = 0; i < balls.length; i++)
        {
            float ballSize = (0.1f + random.nextFloat() * 0.2f) * size;
            balls[i] = new Vec3((random.nextFloat() - random.nextFloat()) * size * 0.5f, (random.nextFloat() - random.nextFloat()) * size * 0.5f, ballSize);
        }
    }

    @Override
    public float noise(float x, float z)
    {
        float f = 0;
        for (Vec3 ball : balls)
        {
            f += ball.z * Math.abs(ball.z) / ((x - ball.x) * (x - ball.x) + (z - ball.y) * (z - ball.y));
        }
        return f > 1 ? 1 : 0;
    }
}
