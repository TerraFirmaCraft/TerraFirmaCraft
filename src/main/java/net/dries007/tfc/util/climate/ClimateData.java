/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.climate;

public class ClimateData
{
    public static final ClimateData DEFAULT = new ClimateData(0, 250);

    private final float regionalTemp;
    private final float rainfall;

    ClimateData(float regionalTemp, float rainfall)
    {
        this.regionalTemp = regionalTemp;
        this.rainfall = rainfall;
    }

    public float getRainfall()
    {
        return rainfall;
    }

    public float getRegionalTemp()
    {
        return regionalTemp;
    }
}
