/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

/**
 * This is purely used for decoration
 * Loosely based on https://en.wikipedia.org/wiki/K%C3%B6ppen_climate_classification
 * We do not have a measure of monthly rainfall variance (only average annual), and in interest of "who cares" this is quite simplified from the actual classification.
 */
public enum KoppenClimateClassification
{
    ARCTIC,
    TUNDRA,
    SUBARCTIC,
    COLD_DESERT,
    HOT_DESERT,
    TEMPERATE,
    SUBTROPICAL,
    HUMID_SUBTROPICAL,
    HUMID_OCEANIC,
    HUMID_SUBARCTIC,
    TROPICAL_SAVANNA,
    TROPICAL_RAINFOREST;

    public static KoppenClimateClassification classify(float averageTemperature, float rainfall)
    {
        if (averageTemperature < -20)
        {
            return ARCTIC;
        }
        else if (rainfall < 150)
        {
            if (averageTemperature > 4)
            {
                return HOT_DESERT;
            }
            else
            {
                return COLD_DESERT;
            }
        }
        else if (averageTemperature < -14)
        {
            if (rainfall > 300)
            {
                return SUBARCTIC;
            }
            else
            {
                return TUNDRA;
            }
        }
        else if (averageTemperature > 18)
        {
            if (rainfall > 300)
            {
                return TROPICAL_RAINFOREST;
            }
            else
            {
                return TROPICAL_SAVANNA;
            }
        }
        else if (rainfall > 350)
        {
            if (averageTemperature > 12)
            {
                return HUMID_SUBTROPICAL;
            }
            else if (averageTemperature > -5)
            {
                return HUMID_OCEANIC;
            }
            else
            {
                return HUMID_SUBARCTIC;
            }
        }
        else
        {
            if (averageTemperature > 12)
            {
                return SUBTROPICAL;
            }
            else if (averageTemperature > -5)
            {
                return TEMPERATE;
            }
            else
            {
                return SUBARCTIC;
            }
        }
    }
}