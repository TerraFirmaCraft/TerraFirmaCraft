/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.noise;

public class VoronoiNoise2D implements INoise2D
{
    private final long seed;
    private final float normalizeFactor;
    private final int searchRadius;
    private int centerX, centerZ;

    public VoronoiNoise2D(long seed)
    {
        this(seed, 1, 1);
    }

    public VoronoiNoise2D(long seed, float normalizeFactor, int searchRadius)
    {
        this.seed = seed;
        this.normalizeFactor = normalizeFactor;
        this.searchRadius = searchRadius;
    }

    @Override
    public float noise(float x, float z)
    {
        calculateCell(x, z);
        return NoiseUtil.random(seed, centerX, centerZ);
    }

    public Vec2 voronoiCenter(float x, float z)
    {
        calculateCell(x, z);
        Vec2 cell = NoiseUtil.CELL_2D[NoiseUtil.hash(seed, centerX, centerZ) & 255];
        return new Vec2(centerX + cell.x, centerZ + cell.y);
    }

    public void calculateCell(float x, float z)
    {
        // Target center
        int startX = NoiseUtil.fastFloor(x);
        int startZ = NoiseUtil.fastFloor(z);

        float distance = Float.MAX_VALUE;

        for (int cellX = startX - searchRadius; cellX <= startX + searchRadius; cellX++)
        {
            for (int cellZ = startZ - searchRadius; cellZ <= startZ + searchRadius; cellZ++)
            {
                Vec2 center = NoiseUtil.CELL_2D[NoiseUtil.hash(seed, cellX, cellZ) & 255];
                float vecX = cellX - x + center.x * normalizeFactor;
                float vecZ = cellZ - z + center.y * normalizeFactor;
                float newDistance = vecX * vecX + vecZ * vecZ;
                if (newDistance < distance)
                {
                    distance = newDistance;
                    centerX = cellX;
                    centerZ = cellZ;
                }
            }
        }
    }
}
