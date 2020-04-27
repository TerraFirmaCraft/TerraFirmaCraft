package net.dries007.tfc.world.noise;

import java.util.Random;

public class Metaballs3D implements INoise3D
{
    private final Vec4[] balls; // x, y, z, weight

    public Metaballs3D(int size, Random random)
    {
        balls = new Vec4[5 + random.nextInt(7)];
        for (int i = 0; i < balls.length; i++)
        {
            float ballSize = (0.1f + random.nextFloat() * 0.2f) * size;
            balls[i] = new Vec4((random.nextFloat() - random.nextFloat()) * size * 0.5f, (random.nextFloat() - random.nextFloat()) * size * 0.5f, (random.nextFloat() - random.nextFloat()) * size * 0.5f, ballSize);
        }
    }

    @Override
    public float noise(float x, float y, float z)
    {
        float f = 0;
        for (Vec4 ball : balls)
        {
            f += ball.w * Math.abs(ball.w) / ((x - ball.x) * (x - ball.x) + (y - ball.y) * (y - ball.y) + (z - ball.z) * (z - ball.z));
        }
        return f > 1 ? 1 : 0;
    }
}
