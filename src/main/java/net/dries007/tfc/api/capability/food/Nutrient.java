/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import net.dries007.tfc.util.calendar.ICalendar;

public enum Nutrient
{
    CARBOHYDRATES,
    FAT,
    PROTEIN,
    VITAMINS,
    MINERALS;

    public static final int TOTAL = values().length;

    public float getDecayModifier()
    {
        // Nutrients lost / tick
        return 8f / ICalendar.TICKS_IN_DAY;
    }
}
