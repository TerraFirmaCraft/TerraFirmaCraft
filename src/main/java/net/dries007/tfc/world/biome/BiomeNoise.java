package net.dries007.tfc.world.biome;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.NoiseUtil;
import net.dries007.tfc.world.noise.SimplexNoise2D;
import net.dries007.tfc.world.noise.Vec2;

/**
 * Collections of biome noise factories
 * These are built by hand and assigned to different biomes
 */
public class BiomeNoise
{
    /**
     * A flat base, with inverse exponential scaled ridge noise subtracted from it
     * Relief carving generates twisting canyons similar to vanilla mesas
     */
    public static INoise2D badlands(long seed)
    {
        final int seaLevel = TFCChunkGenerator.SEA_LEVEL;
        return new SimplexNoise2D(seed)
            .octaves(4)
            .spread(0.025f)
            .scaled(seaLevel + 22, seaLevel + 32)
            .add(new SimplexNoise2D(seed + 1)
                .octaves(4)
                .ridged()
                .spread(0.04f)
                .map(x -> 1.3f * -(x > 0 ? x * x * x : 0.5f * x))
                .scaled(-1f, 0.3f, -1f, 1f)
                .terraces(15)
                .scaled(-19.5f, 0)
            )
            .map(x -> x < seaLevel ? seaLevel - 0.3f * (seaLevel - x) : x);
    }

    /**
     * Domain warping creates twisting land patterns
     */
    public static INoise2D canyons(long seed, int minHeight, int maxHeight)
    {
        final INoise2D warpX = new SimplexNoise2D(seed).octaves(4).spread(0.1f).scaled(-30, 30);
        final INoise2D warpZ = new SimplexNoise2D(seed + 1).octaves(4).spread(0.1f).scaled(-30, 30);
        return new SimplexNoise2D(seed).octaves(4).spread(0.2f).warped(warpX, warpZ).map(x -> x > 0.4 ? x - 0.8f : -x).scaled(-0.4f, 0.8f, TFCChunkGenerator.SEA_LEVEL + minHeight, TFCChunkGenerator.SEA_LEVEL + maxHeight).spread(0.3f);
    }

    /**
     * Simple noise with little variance.
     */
    public static INoise2D simple(long seed, int minHeight, int maxHeight)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.05f).scaled(TFCChunkGenerator.SEA_LEVEL + minHeight, TFCChunkGenerator.SEA_LEVEL + maxHeight);
    }

    public static INoise2D lake(long seed)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.15f).scaled(TFCChunkGenerator.SEA_LEVEL - 12, TFCChunkGenerator.SEA_LEVEL - 2);
    }

    public static INoise2D river(long seed)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.2f).scaled(TFCChunkGenerator.SEA_LEVEL - 8, TFCChunkGenerator.SEA_LEVEL - 2);
    }

    /**
     * Noise right around sea level which has been flattened, to produce lots of small pockets above and below water
     */
    public static INoise2D lowlands(long seed)
    {
        return new SimplexNoise2D(seed).octaves(6).spread(0.55f).scaled(TFCChunkGenerator.SEA_LEVEL - 6, TFCChunkGenerator.SEA_LEVEL + 7).flattened(TFCChunkGenerator.SEA_LEVEL - 4, TFCChunkGenerator.SEA_LEVEL + 3);
    }

    public static INoise2D mountains(long seed, int baseHeight, int scaleHeight)
    {
        final int seaLevel = TFCChunkGenerator.SEA_LEVEL;
        final INoise2D baseNoise = new SimplexNoise2D(seed) // A simplex noise forms the majority of the base
            .octaves(6) // High octaves to create highly fractal terrain
            .spread(0.14f)
            .add(new SimplexNoise2D(seed + 1) // Ridge noise is added to mimic real mountain ridges. It is scaled smaller than the base noise to not be overpowering
                .octaves(4)
                .ridged() // Ridges are applied after octaves as it creates less directional artifacts this way
                .spread(0.02f)
                .scaled(-0.7f, 0.7f))
            .map(x -> 0.125f * (x + 1) * (x + 1) * (x + 1)) // Power scaled, flattens most areas but maximizes peaks
            .map(x -> seaLevel + baseHeight + scaleHeight * x); // Scale the entire thing to mountain ranges

        // Cliff noise consists of noise that's been artificially clamped over half the domain, which is then selectively added above a base height level
        // This matches up with the distinction between dirt and stone
        final INoise2D cliffNoise = new SimplexNoise2D(seed + 2).octaves(2).map(x -> x > 0 ? x : 0).spread(0.01f).scaled(-25, 25);
        final INoise2D cliffHeightNoise = new SimplexNoise2D(seed + 3).octaves(2).spread(0.01f).scaled(-20, 20);

        return (x, z) -> {
            float height = baseNoise.noise(x, z);
            if (height > 120) // Only sample each cliff noise layer if the base noise could be influenced by it
            {
                float cliffHeight = cliffHeightNoise.noise(x, z);
                if (height > 140 + cliffHeight)
                {
                    float cliff = cliffNoise.noise(x, z);
                    return height + cliff;
                }
            }
            return height;
        };
    }

    /**
     * Uses domain warping to achieve a swirly hills effect
     */
    public static INoise2D ocean(long seed, int depthMin, int depthMax)
    {
        final INoise2D warpX = new SimplexNoise2D(seed).octaves(2).spread(0.015f).scaled(-30, 30);
        final INoise2D warpZ = new SimplexNoise2D(seed + 1).octaves(2).spread(0.015f).scaled(-30, 30);
        return new SimplexNoise2D(seed + 2).octaves(4).spread(0.11f).warped(warpX, warpZ).scaled(TFCChunkGenerator.SEA_LEVEL + depthMin, TFCChunkGenerator.SEA_LEVEL + depthMax);
    }

    /**
     * Applies elements from deep ocean and badlands.
     * Inverse power scaled ridge noise (cubic) is used to create ridges, inside the domain warped ocean noise
     */
    public static INoise2D oceanRidge(long seed, int depthMin, int depthMax)
    {
        final INoise2D warpX = new SimplexNoise2D(seed).octaves(2).spread(0.015f).scaled(-30, 30);
        final INoise2D warpZ = new SimplexNoise2D(seed + 1).octaves(2).spread(0.015f).scaled(-30, 30);
        final INoise2D ridgeNoise = new SimplexNoise2D(seed + 1).octaves(4).ridged().spread(0.015f).map(x -> { // In [-1, 1]
            if (x > -0.3f)
            {
                x = (x + 0.3f) / 1.3f;  // In [0, 1]
                x = x * x * x; // Power scaled
                return -35f * x; // In [0, -35]
            }
            return 0; // No modifications outside of ridge area
        });
        return new SimplexNoise2D(seed + 2).octaves(4).spread(0.11f).scaled(TFCChunkGenerator.SEA_LEVEL + depthMin, TFCChunkGenerator.SEA_LEVEL + depthMax).add(ridgeNoise).warped(warpX, warpZ);
    }

    public static INoise2D shore(long seed)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.17f).scaled(TFCChunkGenerator.SEA_LEVEL, TFCChunkGenerator.SEA_LEVEL + 1.8f);
    }

    /**
     * Simple pair of simplex noise which creates a somewhat noisy roof, and a smooth center line
     *
     * @return a pair of noise functions representing the center line, and height of the carving
     */
    public static Pair<INoise2D, INoise2D> riverCarving(long seed)
    {
        return Pair.of(
            new SimplexNoise2D(seed).octaves(2).spread(0.02f).scaled(TFCChunkGenerator.SEA_LEVEL - 3, TFCChunkGenerator.SEA_LEVEL + 3),
            new SimplexNoise2D(seed).octaves(4).spread(0.15f).scaled(8, 14)
        );
    }

    /**
     * Like {@link BiomeNoise#riverCarving(long)}, except also applies additional voronoi based noise to create "columns"
     */
    public static Pair<INoise2D, INoise2D> lakeCarving(long seed)
    {
        final float maxColumnThreshold = 0.5f;

        final Pair<INoise2D, INoise2D> riverPair = riverCarving(seed);
        final INoise2D baseNoise = riverPair.getSecond();
        final INoise2D columnNoise = ((INoise2D) (x, z) -> {
            // This was adapted from VoronoiNoise2D but modified to suit the purposes of finding both shortest distance, and not caring about distances over a specific threshold
            // Target center
            final int startX = NoiseUtil.fastFloor(x);
            final int startZ = NoiseUtil.fastFloor(z);
            float distance = maxColumnThreshold; // Distance is at most 0.5f - if nothing is found closer than the we don't care

            for (int cellX = startX - 1; cellX <= startX + 1; cellX++)
            {
                for (int cellZ = startZ - 1; cellZ <= startZ + 1; cellZ++)
                {
                    long cellSeed = NoiseUtil.hash(seed, cellX, cellZ);
                    if ((cellSeed & 0b111) == 0) // 1/4 chance for a cell at each location
                    {
                        cellSeed = NoiseUtil.hash(cellSeed >> 3, cellX, cellZ);
                        final Vec2 center = NoiseUtil.CELL_2D[(int) cellSeed & 255];
                        final float vecX = cellX - x + center.x;
                        final float vecZ = cellZ - z + center.y;
                        float newDistance = vecX * vecX + vecZ * vecZ;
                        cellSeed = NoiseUtil.hash(cellSeed >> 8, cellX, cellZ);
                        newDistance += 0.1f * NoiseUtil.random(cellSeed, cellX, cellZ);
                        if (newDistance < distance)
                        {
                            distance = newDistance;
                        }
                    }
                }
            }
            return distance;
        }).spread(0.13f);

        return Pair.of(
            riverPair.getFirst(),
            (x, z) -> {
                float maxBaseValue = 14;
                final float columnValue = columnNoise.noise(x, z);
                if (columnValue < maxColumnThreshold)
                {
                    // Near a column, scale the base noise to quickly clamp off inside the column radius
                    final float t = (columnValue - maxColumnThreshold) / (maxColumnThreshold - 0.1f);
                    maxBaseValue = NoiseUtil.lerp(14, 0, t * t);
                    if (maxBaseValue < 0)
                    {
                        return 0;
                    }
                }
                float baseValue = baseNoise.noise(x, z);
                return Math.min(maxBaseValue, baseValue);
            }
        );
    }
}
