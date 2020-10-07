/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

/**
 * These are biome rainfall levels. We apply them to all biomes based on their rainfall threshold.
 */
public enum BiomeRainfall
{
    ARID(0.0f),
    DRY(0.2f),
    NORMAL(0.45f),
    DAMP(0.7f),
    WET(0.9f);

    private final float downfall;

    BiomeRainfall(float downfall)
    {
        this.downfall = downfall;
    }

    /**
     * @return Vanilla downfall value (approx)
     */
    public float getDownfall()
    {
        return downfall;
    }
}