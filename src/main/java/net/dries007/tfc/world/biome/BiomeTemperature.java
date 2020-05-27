/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.dries007.tfc.config.TFCConfig;

/**
 * These are biome temperatures. They mimic the vanilla ocean variants, except we apply them to all biomes, based on our temperature layer generation.
 */
public enum BiomeTemperature
{
    FROZEN(0.0f, 3750089, 329011),
    COLD(0.25f, 4020182, 329011),
    NORMAL(0.5f, 4159204, 329011),
    LUKEWARM(0.75f, 4566514, 267827),
    WARM(1.0f, 4445678, 270131);

    public static BiomeTemperature get(float avgTemperature)
    {
        if (avgTemperature < TFCConfig.COMMON.frozenTemperatureCutoff.get())
        {
            return FROZEN;
        }
        else if (avgTemperature < TFCConfig.COMMON.coldTemperatureCutoff.get())
        {
            return COLD;
        }
        else if (avgTemperature < TFCConfig.COMMON.normalTemperatureCutoff.get())
        {
            return NORMAL;
        }
        else if (avgTemperature < TFCConfig.COMMON.lukewarmTemperatureCutoff.get())
        {
            return LUKEWARM;
        }
        else
        {
            return WARM;
        }
    }

    private final float temperature;
    private final int waterColor;
    private final int waterFogColor;

    BiomeTemperature(float temperature, int waterColor, int waterFogColor)
    {
        this.temperature = temperature;
        this.waterColor = waterColor;
        this.waterFogColor = waterFogColor;
    }

    /**
     * @return vanilla biome temperature (approx)
     */
    public float getTemperature()
    {
        return temperature;
    }

    /**
     * @return vanilla biome water color
     */
    public int getWaterColor()
    {
        return waterColor;
    }

    /**
     * @return vanilla biome water fog color
     */
    public int getWaterFogColor()
    {
        return waterFogColor;
    }
}
