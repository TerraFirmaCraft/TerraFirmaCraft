/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.noise;

/**
 * A Worley noise implementation
 * Adapted from <a href="https://github.com/Auburns/FastNoise_Java">Fast Noise</a>
 */
public class WorleyNoise2D implements INoise2D
{

    private final long seed;

    public WorleyNoise2D(long seed)
    {
        this.seed = seed;
    }

    @Override
    public float noise(float x, float y)
    {
        int x0 = NoiseUtil.fastRound(x);
        int y0 = NoiseUtil.fastRound(y);

        float distance = Float.MAX_VALUE, distance2 = Float.MAX_VALUE;
        for (int x1 = x0 - 1; x1 <= x0 + 1; x1++)
        {
            for (int y1 = y0 - 1; y1 <= y0 + 1; y1++)
            {
                Vec2 vec = NoiseUtil.CELL_2D[NoiseUtil.hash(seed, x1, y1) & 255];

                float vecX = x1 - x + vec.x;
                float vecY = y1 - y + vec.y;

                float newDistance = vecX * vecX + vecY * vecY;

                distance2 = Math.max(Math.min(distance2, newDistance), distance);
                distance = Math.min(distance, newDistance);
            }
        }

        return distance / distance2 - 1;
    }
}
