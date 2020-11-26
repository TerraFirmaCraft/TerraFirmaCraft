package net.dries007.tfc.world.biome;

import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.noise.*;

/**
 * Collection of noise functions used by volcanoes
 * Graphs: https://www.desmos.com/calculator/juyhjgnxxg
 */
public final class VolcanoNoise
{
    private static final long SEED_MODIFIER = 24341L;
    private static final float C1 = 9;
    private static final float C2 = 0.05f;
    private static final float C3 = 18;
    private static final float D1 = -f1(C1);
    private static final float D2 = D1 + f1(C2) - f2(C2);

    /**
     * @param seed The world seed
     * @return A noise function determining the volcano's height at any given position
     */
    public static INoise2D height(long seed, int baseHeight, int maxHeight)
    {
        final int seaLevel = TFCChunkGenerator.SEA_LEVEL;
        return distance(seed).map(q -> {
            if (q > C1)
            {
                return 0;
            }
            else if (q > C2)
            {
                return f1(q) + D1;
            }
            else
            {
                return f2(q) + D2;
            }
        }).scaled(0, 4.1f, seaLevel + baseHeight, seaLevel + maxHeight);
    }

    /**
     * @param seed The world seed
     * @return A function which scales linearly from 0 to 1 based on the distance from a volcano center. This is the range that the volcano influences the surrounding terrain height.
     */
    public static INoise2D easing(long seed)
    {
        return distance(seed).map(q -> {
            if (q > C1)
            {
                return 0;
            }
            else
            {
                return 1 - q / C1;
            }
        });
    }

    /**
     * @param seed The world seed
     * @return A function which returns the nearest center position for any given location.
     */
    public static ITyped2D<BlockPos> centers(long seed)
    {
        final Cellular2D cellNoise = new Cellular2D(seed + SEED_MODIFIER, 1.0f, CellularNoiseType.DISTANCE)
            .spread(0.026f / C3);
        final BlockPos.Mutable cursor = new BlockPos.Mutable();
        return (x, z) -> {
            cellNoise.noise(x, z);
            cursor.setX((int) cellNoise.getCenterX());
            cursor.setZ((int) cellNoise.getCenterY());
            return cursor;
        };
    }

    /**
     * @param seed The world seed
     * @return A noise function representing the square distance from a given point to the nearest volcano
     */
    private static INoise2D distance(long seed)
    {
        return new Cellular2D(seed + SEED_MODIFIER, 1.0f, CellularNoiseType.DISTANCE)
            .spread(0.026f / C3)
            .scaled(-C3 * C3, C3 * C3)
            .add(new OpenSimplex2D(seed + 1)
                .octaves(2)
                .scaled(-0.08f, 0.08f)
                .spread(0.026f * 7f));
    }

    private static float f1(float x)
    {
        return 5 / (x + 1);
    }

    private static float f2(float x)
    {
        return 90 * (x + 0.05f) * (x + 0.05f);
    }
}
