/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.climate;

import java.util.Random;

import net.minecraft.util.math.MathHelper;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarFormatted;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.config.TemperatureMode;

import static net.dries007.tfc.world.classic.WorldTypeTFC.SEALEVEL;

public class ClimateHelper
{
    private static final Random RANDOM = new Random();

    /**
     * @return The month adjusted temperature. This gets the base temperature, before daily / hourly changes
     */
    public static float actualTemp(float regionalTemp, int y, int z, long timeOffset)
    {
        return dailyTemp(regionalTemp, z, timeOffset) - heightFactor(y);
    }

    /**
     * @return The exact temperature for a location, including day + hour variation, without height adjustment
     */
    public static float dailyTemp(float regionalTemp, int z, long timeOffset)
    {
        // Hottest part of the day at 12, coldest at 0
        int hourOfDay = ICalendarFormatted.getHourOfDay(CalendarTFC.CALENDAR_TIME.getTicks() + timeOffset);
        if (hourOfDay > 12)
        {
            // Range: 0 - 12
            hourOfDay = 24 - hourOfDay;
        }
        // Range: -1 - 1
        float hourModifier = (hourOfDay / 6f) - 1f;

        // Note: this does not use world seed, as that is not synced from server - client, resulting in the seed being different
        long day = ICalendar.getTotalDays(CalendarTFC.CALENDAR_TIME.getTicks() + timeOffset);
        RANDOM.setSeed(day);
        // Range: -1 - 1
        final float dailyModifier = RANDOM.nextFloat() - RANDOM.nextFloat();

        // Max daily / hourly variance is +/- 4 C
        return monthlyTemp(regionalTemp, z, timeOffset) + (dailyModifier + 0.3f * hourModifier) * 3f;
    }

    /**
     * @return The month adjusted temperature. This gets the base temperature, before daily / hourly changes
     */
    public static float monthlyTemp(float regionalTemp, int z, long timeOffset)
    {
        long time = CalendarTFC.CALENDAR_TIME.getTicks() + timeOffset;
        Month monthOfYear = ICalendarFormatted.getMonthOfYear(time, CalendarTFC.CALENDAR_TIME.getDaysInMonth());

        final float currentMonthFactor = monthFactor(regionalTemp, monthOfYear, z);
        final float nextMonthFactor = monthFactor(regionalTemp, monthOfYear.next(), z);

        final float delta = (float) ICalendarFormatted.getDayOfMonth(time, CalendarTFC.CALENDAR_TIME.getDaysInMonth()) / CalendarTFC.CALENDAR_TIME.getDaysInMonth();
        // Affine combination to smooth temperature transition
        return currentMonthFactor * (1 - delta) + nextMonthFactor * delta;
    }

    /**
     * Internationally accepted average lapse time is 6.49 K / 1000 m, for the first 11 km of the atmosphere. Our temperature is scales the 110 m against 2750 m, so that gives us a change of 1.6225 / 10 blocks.
     * The amount to reduce temperature by after applying height transformations
     *
     * @param y the y level
     * @return a value between 0 and 17.822
     */
    public static float heightFactor(int y)
    {
        if (y > SEALEVEL)
        {
            // This is much simpler and works just as well
            float scale = (y - SEALEVEL) * 0.16225f;
            if (scale > 17.822f)
            {
                scale = 17.822f;
            }
            return scale;
        }
        return 0;
    }

    /**
     * Range -32 to 35
     *
     * @param regionalTemp The base temp for the current location
     * @param month        The month (from Calendar)
     * @return the month factor for temp calculation
     */
    public static float monthFactor(float regionalTemp, Month month, int z)
    {
        return monthFactor(regionalTemp, month.getTemperatureModifier(), z);
    }

    public static float monthFactor(float regionalTemp, float monthTempModifier, int z)
    {
        return (41f - monthTempModifier * 1.1f * (1 - 0.8f * latitudeFactor(z))) + regionalTemp;
    }

    /**
     * Range 0 - 1
     *
     * @param chunkZ the chunk Z position (in block coordinates)
     * @return the latitude factor for temperature calculation
     */
    public static float latitudeFactor(int chunkZ)
    {
        int tempRange = ConfigTFC.General.WORLD.latitudeTemperatureModifier;
        if (ConfigTFC.General.WORLD.temperatureMode == TemperatureMode.ENDLESS)
        {
            chunkZ = MathHelper.clamp(chunkZ, -tempRange / 2, tempRange / 2);
        }
        return 0.5f + 0.5f * ConfigTFC.General.WORLD.hemisphereType.getValue() * (float) Math.sin(Math.PI * chunkZ / tempRange);
    }

    private ClimateHelper() {}
}
