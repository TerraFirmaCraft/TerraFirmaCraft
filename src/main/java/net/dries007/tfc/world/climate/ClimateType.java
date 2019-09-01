/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.climate;

import javax.annotation.Nonnull;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.RainType;

import static net.minecraft.world.biome.Biome.RainType.*;
import static net.minecraft.world.biome.Biome.TempCategory.*;

public enum ClimateType
{
    ARID_COLD(COLD, 0f, NONE, 0.1f),
    NORMAL_COLD(COLD, 0f, SNOW, 0.5f),
    HUMID_COLD(COLD, 0f, SNOW, 0.9f),
    ARID_NORMAL(MEDIUM, 0.5f, NONE, 0.1f),
    NORMAL_NORMAL(MEDIUM, 0.5f, RAIN, 0.5f),
    HUMID_NORMAL(MEDIUM, 0.5f, RAIN, 0.9f),
    ARID_HOT(WARM, 1.5f, NONE, 0.1f),
    NORMAL_HOT(WARM, 1.5f, RAIN, 0.5f),
    HUMID_HOT(WARM, 1.5f, RAIN, 0.9f);

    public static final int SIZE = values().length;

    private final Biome.TempCategory tempCategory;
    private final float temperature;
    private final RainType rainType;
    private final float downfall;

    ClimateType(Biome.TempCategory tempCategory, float temperature, RainType rainType, float downfall)
    {
        this.tempCategory = tempCategory;
        this.temperature = temperature;
        this.rainType = rainType;
        this.downfall = downfall;
    }

    @Nonnull
    public RainType getRainType()
    {
        return rainType;
    }

    public float getTemperature()
    {
        return temperature;
    }

    public float getDownfall()
    {
        return downfall;
    }
}
