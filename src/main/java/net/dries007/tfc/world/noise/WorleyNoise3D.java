/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.noise;

/**
 * A Worley noise implementation
 * Adapted from <a href="https://github.com/Auburns/FastNoise_Java">Fast Noise</a>
 */
public class WorleyNoise3D implements INoise3D
{

    private final long seed;
    private final float[] distances = new float[27];

    public WorleyNoise3D(long seed)
    {
        this.seed = seed;
    }

    public float noise(float x, float y, float z)
    {
        // Cell centers
        int x0 = NoiseUtil.fastRound(x);
        int y0 = NoiseUtil.fastRound(y);
        int z0 = NoiseUtil.fastRound(z);

        // Distance to each cell point
        int index = 0;
        for (int xi = x0 - 1; xi <= x0 + 1; xi++)
        {
            for (int yi = y0 - 1; yi <= y0 + 1; yi++)
            {
                for (int zi = z0 - 1; zi <= z0 + 1; zi++)
                {
                    Vec3 vec = NoiseUtil.CELL_3D[NoiseUtil.hash(seed, xi, yi, zi) & 255];

                    float vecX = xi + vec.x + 0.5f - x;
                    float vecY = yi + vec.y + 0.5f - y;
                    float vecZ = zi + vec.z + 0.5f - z;

                    distances[index++] = vecX * vecX + vecY * vecY + vecZ * vecZ;
                }
            }
        }

        // Find the smallest three distances
        for (int k = 0; k < 3; k++)
        {
            for (int j = distances.length - 1; j > k; j--)
            {
                if (distances[j] < distances[j - 1])
                {
                    float d = distances[j - 1];
                    distances[j - 1] = distances[j];
                    distances[j] = d;
                }
            }
        }

        // Return the ratio
        return distances[0] / distances[2] - 1;
    }
}
