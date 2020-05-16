/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.climate;

/**
 * This is a wrapper around climate data which is calculated during world generation
 */
public class ClimateData
{
    public static final ClimateData DEFAULT = new ClimateData(10, 250);

    private final float averageTemp;
    private final float rainfall;

    ClimateData(float averageTemp, float rainfall)
    {
        this.averageTemp = averageTemp;
        this.rainfall = rainfall;
    }

    public float getRainfall()
    {
        return rainfall;
    }

    public float getAverageTemp()
    {
        return averageTemp;
    }
}
