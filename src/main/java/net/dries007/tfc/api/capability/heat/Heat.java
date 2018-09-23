/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.heat;

import net.minecraft.util.text.TextFormatting;

public enum Heat
{
    WARMING(1f, 80f, TextFormatting.GRAY),
    HOT(80f, 210f, TextFormatting.GRAY),
    VERY_HOT(210f, 480f, TextFormatting.GRAY),
    FAINT_RED(480f, 580f, TextFormatting.DARK_RED),
    DARK_RED(580f, 730f, TextFormatting.DARK_RED),
    BRIGHT_RED(730f, 930f, TextFormatting.RED),
    ORANGE(930f, 1100f, TextFormatting.GOLD),
    YELLOW(1100f, 1300f, TextFormatting.YELLOW),
    YELLOW_WHITE(1300f, 1400f, TextFormatting.YELLOW),
    WHITE(1400f, 1500f, TextFormatting.WHITE),
    BRILLIANT_WHITE(1500f, 1600f, TextFormatting.WHITE);

    final float min;
    final float max;
    final TextFormatting format;

    Heat(float min, float max, TextFormatting format)
    {
        this.min = min;
        this.max = max;
        this.format = format;
    }
}
