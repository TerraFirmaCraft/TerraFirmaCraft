/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.climate;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Helper class
 * Determines which IRL biome you should have in the collection of TFC's rainfall, temperature and flora density
 * Keep in mind that this won't check for oceans and mountains, you should check for this and the biomes provided in BiomesTFC for accuracy
 *
 * in 1.15+ this should be dropped for the proper Biome system MC uses
 */
public final class BiomeHelper
{
    /**
     * Return the first valid biome type in this region, if any
     * Almost certainly will return a value, but be sure to check for null
     *
     * @param temperature the average temperature
     * @param rainfall    the average rainfall
     * @param density     the average flora density
     * @return a BiomeType, if found any
     */
    @Nullable
    public static BiomeType getBiomeType(float temperature, float rainfall, float density)
    {
        for (BiomeType biomeType : BiomeType.values())
        {
            if (biomeType.isValid(temperature, rainfall, density))
            {
                // Return the first valid, should make forests have the highest priority and deserts the lowest
                return biomeType;
            }
        }
        return null;
    }

    /**
     * Get a list of valid biome types in this region. (probably useless, but w/e)
     *
     * @param temperature the average temperature
     * @param rainfall    the average rainfall
     * @param density     the average flora density
     * @return a list of BiomeType
     */
    @Nonnull
    public static List<BiomeType> getValidBiomeTypes(float temperature, float rainfall, float density)
    {
        List<BiomeType> biomeTypes = new ArrayList<>();
        for (BiomeType biomeType : BiomeType.values())
        {
            if (biomeType.isValid(temperature, rainfall, density))
            {
                biomeTypes.add(biomeType);
            }
        }
        return biomeTypes;
    }

    private BiomeHelper() {}

    public enum BiomeType
    {
        // Ordered by priority.
        TROPICAL_FOREST(19, 100, 60, 500, 0.25f, 1), // Forests in a hot region
        TEMPERATE_FOREST(-2, 22, 60, 500, 0.25f, 1), // Forests in a mild temperature region
        TAIGA(-15, 6, 60, 500, 0.25f, 1), // Forests in a cold region

        // Regions where you won't find much trees
        PLAINS(0, 22, 60, 500, 0, 0.25f), // Low number of trees and mild temperatures
        SAVANNA(19, 100, 60, 500, 0, 0.3f), // Low number of trees and high temperatures

        TUNDRA(-100, 0, 0, 500, 0, 1), // Cold deserts
        DESERT(0, 100, 0, 60, 0, 1); // No trees and mild-high temperature

        private final float temperatureMin, temperatureMax;
        private final float rainfallMin, rainfallMax;
        private final float densityMin, densityMax;

        BiomeType(float temperatureMin, float temperatureMax, float rainfallMin, float rainfallMax, float densityMin, float densityMax)
        {
            this.temperatureMin = temperatureMin;
            this.temperatureMax = temperatureMax;

            this.rainfallMin = rainfallMin;
            this.rainfallMax = rainfallMax;

            this.densityMin = densityMin;
            this.densityMax = densityMax;
        }

        public boolean isValid(float temperature, float rainfall, float density)
        {
            return rainfall >= rainfallMin && rainfall <= rainfallMax &&
                temperature >= temperatureMin && temperature <= temperatureMax &&
                density >= densityMin && density <= densityMax;
        }
    }
}
