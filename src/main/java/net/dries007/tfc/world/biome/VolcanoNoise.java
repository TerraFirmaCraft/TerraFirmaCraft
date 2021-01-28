/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.CellularNoiseType;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

/**
 * Collection of noise functions used by volcanoes
 */
public final class VolcanoNoise
{
    private static final long SEED_MODIFIER = 23423123523L;
    private static final float SEPARATION_MODIFIER = 0.0035f;
    private static final float SIZE_MODIFIER = 0.05f;

    public static INoise2D easingNoise(long seed)
    {
        return cellNoise(seed).map(VolcanoNoise::calculateEasing);
    }

    /**
     * Represents a mapping from a volcano distance map (obtained via {@link VolcanoNoise#cellNoise(long)}) to a scalar value representing how close to a volcano we are, in  [0, 1].
     *
     * @param distance The square distance to the volcano
     * @return A value in [0, 1], where 0 = not close to a volcano, (0, 1) = nearing a volcano, and 1 = near the center of the volcano
     */
    public static float calculateEasing(float distance)
    {
        if (distance > SIZE_MODIFIER)
        {
            return 0;
        }
        else
        {
            return 1f - distance / SIZE_MODIFIER;
        }
    }

    /**
     * @param distance The unscaled square distance from the volcano, roughly in [0, 1.2]
     * @return A noise function determining the volcano's height at any given position, in the range [0, 1]
     */
    public static float calculateHeight(float distance)
    {
        // Scale distance to [0, 1]
        if (distance > SIZE_MODIFIER)
        {
            return 0;
        }
        else if (distance > 0)
        {
            distance /= SIZE_MODIFIER;
        }

        // Compute the height based on the distance
        if (distance > 0.025f)
        {
            return (5f / (9f * distance + 1) - 0.5f) * 0.279173646008f;
        }
        else
        {
            float a = (distance * 9f + 0.05f);
            return (8f * a * a + 2.97663265306f) * 0.279173646008f;
        }
    }

    /**
     * The noise affecting the distance that volcanoes use for both easing and height maps
     *
     * @param seed The world seed
     * @return A noise function which, when added to the distance noise from {@link VolcanoNoise#cellNoise(long)}, produces the actual distance used for further noise calculations
     */
    public static INoise2D distanceVariationNoise(long seed)
    {
        return new OpenSimplex2D(seed + SEED_MODIFIER * 2).octaves(2).scaled(-0.0016f, 0.0016f).spread(0.128f);
    }

    /**
     * The base cellular noise that volcanoes use for placement
     *
     * @param seed The world seed
     * @return A cellular noise function
     */
    public static Cellular2D cellNoise(long seed)
    {
        return new Cellular2D(seed + SEED_MODIFIER, 0.8f, CellularNoiseType.F1).spread(SEPARATION_MODIFIER);
    }
}
