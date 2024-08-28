/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

/**
 * This is purely used for decoration and visualization purposes. Based on <a href="https://en.wikipedia.org/wiki/K%C3%B6ppen_climate_classification> Koppen Climate Classification</a>
 */
public enum KoppenClimateClassification
{
    AF,
    AM,
    AW,
    AS,
    BWH,
    BWK,
    BSH,
    BSK,
    CSA,
    CSB,
    CSC,
    CWA,
    CWB,
    CWC,
    CFA,
    CFB,
    CFC,
    DSA,
    DSB,
    DSC,
    DSD,
    DWA,
    DWB,
    DWC,
    DWD,
    DFA,
    DFB,
    DFC,
    DFD,
    ET,
    EF;

    public static KoppenClimateClassification classify(float averageTemperature, float rainfall, float rainVar)
    {
        // True Koppen: When none of the year is above freezing, temp var when avg = -17C ~= 17C
        if (averageTemperature < -17f)
        {
            return EF;
        }
        // True Koppen: When summer temps don't exceed 10C, but this would be avg temp = -1C which would make for massive tundras
        else if (averageTemperature <= -12f)
        {
            return ET;
        }
        else if (rainfall < 75f)
        {
            if (averageTemperature > 18f)
            {
                return BWH;
            }
            else
            {
                return BWK;
            }
        }
        else if (rainfall < 150f)
        {
            if (averageTemperature > 18)
            {
                return BSH;
            }
            else
            {
                return BSK;
            }
        }
        // True Koppen: Lowest monthly temp > 18C, temp var when avg = 21C ~= 3C
        else if (averageTemperature > 21f)
        {
            if (rainfall * (1 + rainVar) > 750f)
            {
                return AM;
            }
            else if (rainVar > 0.5f)
            {
                return AW;
            }
            else if (rainVar < -0.5f)
            {
                return AS;
            }
            else
            {
                return AF;
            }
        }
        // True Koppen: Lowest monthly temp > 0C, temp var when avg = 8C ~= 8C
        else if (averageTemperature > 8f)
        {
            if (averageTemperature > 17f)
            {
                if (rainVar > 0.5f)
                {
                    return CWA;
                }
                else if (rainVar < -0.5f)
                {
                    return CSA;
                }
                else
                {
                    return CFA;
                }
            }
            else if (averageTemperature > 12f)
            {
                if (rainVar > 0.5f)
                {
                    return CWB;
                }
                else if (rainVar < -0.5f)
                {
                    return CSB;
                }
                else
                {
                    return CFB;
                }
            }
            else
            {
                if (rainVar > 0.5f)
                {
                    return CWC;
                }
                else if (rainVar < -0.5f)
                {
                    return CSC;
                }
                else
                {
                    return CFC;
                }
            }

        }
        // Otherwise, has to be in Group D
        else if (averageTemperature > 3f)
        {
            if (rainVar > 0.5f)
            {
                return DWA;
            }
            else if (rainVar < -0.5f)
            {
                return DSA;
            }
            else
            {
                return DFA;
            }
        }
        else if (averageTemperature > -2f)
        {
            if (rainVar > 0.5f)
            {
                return DWB;
            }
            else if (rainVar < -0.5f)
            {
                return DSB;
            }
            else
            {
                return DFB;
            }
        }
        else if (averageTemperature > -8f)
        {
            if (rainVar > 0.5f)
            {
                return DWC;
            }
            else if (rainVar < -0.5f)
            {
                return DSC;
            }
            else
            {
                return DFC;
            }
        }
        else if (rainVar > 0.5f)
        {
            return DWD;
        }
        else if (rainVar < -0.5f)
        {
            return DSD;
        }
        else
        {
            return DFD;
        }
    }
}