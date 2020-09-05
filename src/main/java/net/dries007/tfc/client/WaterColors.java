/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import net.minecraft.util.math.MathHelper;

public final class WaterColors
{
    private static int[] waterColorsCache = new int[256 * 256];

    public static void setWaterColors(int[] waterColors)
    {
        WaterColors.waterColorsCache = waterColors;
    }

    public static int getWaterColor(float temperature, float rainfall)
    {
        int temperatureIndex = MathHelper.clamp((int) ((temperature + 30f) * 255f / 60f), 0, 255);
        int rainfallIndex = 255 - MathHelper.clamp((int) (rainfall * 255f / 500f), 0, 255);
        return waterColorsCache[temperatureIndex | (rainfallIndex << 8)];
    }
}
