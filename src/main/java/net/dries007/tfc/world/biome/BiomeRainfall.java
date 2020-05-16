package net.dries007.tfc.world.biome;

import net.dries007.tfc.config.TFCConfig;

/**
 * These are biome rainfall levels. We apply them to all biomes based on their rainfall threshold.
 */
public enum BiomeRainfall
{
    ARID(0.0f),
    NORMAL(0.4f),
    WET(0.9f);

    public static BiomeRainfall get(float rainfall)
    {
        if (rainfall < TFCConfig.COMMON.aridRainfallCutoff.get())
        {
            return ARID;
        }
        else if (rainfall < TFCConfig.COMMON.normalRainfallCutoff.get())
        {
            return NORMAL;
        }
        else
        {
            return WET;
        }
    }

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
