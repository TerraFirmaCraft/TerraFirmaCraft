/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.overworld.SolarCalculator;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.config.TimeDeltaTooltipStyle;
import net.dries007.tfc.util.Helpers;

/**
 * @see Calendars
 */
public interface ICalendar
{
    /* Constants */
    int TICKS_IN_HOUR = 1000;
    int HOURS_IN_DAY = 24;
    int TICKS_IN_DAY = TICKS_IN_HOUR * HOURS_IN_DAY;
    int MONTHS_IN_YEAR = 12;

    /* This needs to be a float, otherwise there are ~62 minutes per hour */
    float TICKS_IN_MINUTE = TICKS_IN_HOUR / 60f;

    /* Delta Calculation Methods */

    static long getCalendarTicksInMonth(int daysInMonth)
    {
        return (long) TICKS_IN_DAY * daysInMonth;
    }

    static long getCalendarTicksInYear(int daysInMonth)
    {
        return (long) TICKS_IN_DAY * daysInMonth * MONTHS_IN_YEAR;
    }

    /* Total Calculation Methods */

    static long getTotalHours(long time)
    {
        return time / TICKS_IN_HOUR;
    }

    static long getTotalDays(long time)
    {
        return time / TICKS_IN_DAY;
    }

    static long getTotalMonths(long time, long daysInMonth)
    {
        return time / (daysInMonth * TICKS_IN_DAY);
    }

    static long getTotalYears(long time, long daysInMonth)
    {
        return 1000 + (time / (MONTHS_IN_YEAR * daysInMonth * TICKS_IN_DAY));
    }

    /* Fraction Calculation Methods */

    static int getMinuteOfHour(long time)
    {
        return (int) ((time % TICKS_IN_HOUR) / TICKS_IN_MINUTE);
    }

    static int getHourOfDay(long time)
    {
        return (int) ((time / TICKS_IN_HOUR) % HOURS_IN_DAY);
    }

    static int getDayOfMonth(long time, long daysInMonth)
    {
        return 1 + (int) ((time / TICKS_IN_DAY) % daysInMonth);
    }

    static float getFractionOfDay(long time)
    {
        return (float) (time % TICKS_IN_DAY) / TICKS_IN_DAY;
    }

    static float getFractionOfMonth(long time, long daysInMonth)
    {
        long ticksInMonth = daysInMonth * TICKS_IN_DAY;
        return (float) (time % ticksInMonth) / ticksInMonth;
    }

    static float getFractionOfYear(long time, long daysInMonth)
    {
        long ticksInYear = MONTHS_IN_YEAR * daysInMonth * TICKS_IN_DAY;
        return (float) (time % ticksInYear) / ticksInYear;
    }

    static Month getMonthOfYear(long time, long daysInMonth)
    {
        long ticksInMonth = daysInMonth * TICKS_IN_DAY;
        return Month.valueOf((int) ((time / ticksInMonth) % MONTHS_IN_YEAR));
    }

    /* Format Methods */

    static MutableComponent getTimeAndDate(long time, long daysInMonth)
    {
        return ICalendar.getTimeAndDate(ICalendar.getHourOfDay(time), ICalendar.getMinuteOfHour(time), ICalendar.getMonthOfYear(time, daysInMonth), ICalendar.getDayOfMonth(time, daysInMonth), ICalendar.getTotalYears(time, daysInMonth));
    }

    static MutableComponent getTimeAndDate(int hour, int minute, Month month, int day, long years)
    {
        return Component.translatable("tfc.tooltip.calendar_hour_minute_month_day_year", String.format("%d:%02d", hour, minute), Helpers.translateEnum(month), day, years);
    }

    static MutableComponent getTimeDelta(long ticks, int daysInMonth)
    {
        final long hours = getTotalHours(ticks);
        if (hours < 1)
        {
            return Component.translatable("tfc.tooltip.time_delta_hours_minutes", "00", String.format("%02d", getMinuteOfHour(ticks)));
        }
        final long days = getTotalDays(ticks);
        if (days < 1)
        {
            return Component.translatable("tfc.tooltip.time_delta_hours_minutes", hours, String.format("%02d", getMinuteOfHour(ticks)));
        }
        final long months = getTotalMonths(ticks, daysInMonth);
        final TimeDeltaTooltipStyle style = TFCConfig.CLIENT.timeDeltaTooltipStyle.get();
        if (months < 1 || style == TimeDeltaTooltipStyle.DAYS)
        {
            return Component.translatable("tfc.tooltip.time_delta_days", days);
        }
        final long years = getTotalYears(ticks, daysInMonth) - 1000; // Since years starts at 1k
        if (years < 1 || style == TimeDeltaTooltipStyle.DAYS_MONTHS)
        {
            return Component.translatable("tfc.tooltip.time_delta_months_days", months, days % daysInMonth);
        }
        return Component.translatable("tfc.tooltip.time_delta_years_months_days", years, months % MONTHS_IN_YEAR, days % daysInMonth);
    }

    /**
     * Opens a calendar transaction, which allows you to safely manipulate time to perform a sequence of actions, without
     * possibility of distributing the state of the global calendar. Note that this should generally <strong>only</strong> be used
     * on {@link Calendars#SERVER}. The only case where this is useful to use on the client is during unit tests, where
     * the existing calendar will always be inferred to be on client.
     *
     * @return A new {@link CalendarTransaction}
     * @see CalendarTransaction
     */
    CalendarTransaction transaction();

    /**
     * Gets the absolute amount of ticks passed since the world was created. This stops when no players are logged on.
     * This is safe to store timestamps.
     *
     * @return the amount of ticks since the world was created
     */
    long getTicks();

    /**
     * Gets the amount of ticks since the current date.
     * DO NOT store this in a timestamp, EVER.
     *
     * @return the amount of ticks since Jan 1, 1000
     */
    long getCalendarTicks();

    /**
     * @return the number of days in a month
     */
    int getCalendarDaysInMonth();

    /**
     * @return The corresponding calendar tick of the player tick passed
     */
    default long ticksToCalendarTicks(long tick)
    {
        return getCalendarTicks() - getTicks() + tick;
    }

    /**
     * Gets the total amount of days passed
     */
    default long getTotalDays()
    {
        return ICalendar.getTotalDays(getTicks());
    }

    /**
     * Gets the total amount of days passed since Jan 1, 1000
     */
    default long getTotalCalendarDays()
    {
        return ICalendar.getTotalDays(getCalendarTicks());
    }

    /**
     * Gets the total amount of months passed since Jan 1, 1000
     */
    default long getTotalCalendarMonths()
    {
        return ICalendar.getTotalMonths(getCalendarTicks(), getCalendarDaysInMonth());
    }

    /**
     * Gets the total amount of years passed since Jan 1, 1000
     */
    default long getTotalCalendarYears()
    {
        return ICalendar.getTotalYears(getCalendarTicks(), getCalendarDaysInMonth());
    }

    /**
     * Get the equivalent total world time
     * World time 0 = 6:00 AM, which is calendar time 6000
     *
     * @return a value in [0, 24000) which should match the result of {@link Level#getDayTime()}
     *
     * @deprecated This should not be used, as it will not be accurate on client (which has variable daytime), and will not be
     * accurate to what the name is on server (as daytime is variable depending on the location). Instead, consider the use case
     * and switch to using one of the other methods:
     * <ul>
     *     <li>If you want to calculate "is the sun in the sky", or vanilla-equivalent day time on client at a given position,
     *     supply a position-dependent calculation to {@link SolarCalculator}</li>
     *     <li>If you want to know the vanilla-equivalent day time on client, simply use {@link Level#getDayTime()}</li>
     *     <li>If you simply want to know the fraction of a day (as a 24-hour period) and do not care about sun positioning,
     *     then use {@link #getCalendarFractionOfDay()}</li>
     * </ul>
     */
    @Deprecated
    default long getCalendarDayTime()
    {
        return (getCalendarTicks() - (6 * ICalendar.TICKS_IN_HOUR)) % ICalendar.TICKS_IN_DAY;
    }

    /**
     * Calculates the day of a month from the calendar time (i.e. 01 - ??)
     */
    default int getCalendarDayOfMonth()
    {
        return ICalendar.getDayOfMonth(getCalendarTicks(), getCalendarDaysInMonth());
    }

    default float getCalendarFractionOfDay()
    {
        return ICalendar.getFractionOfDay(getCalendarTicks());
    }

    /**
     * Returns the progress through the month from a calendar time (i.e. 0 - 1)
     */
    default float getCalendarFractionOfMonth()
    {
        return ICalendar.getFractionOfMonth(getCalendarTicks(), getCalendarDaysInMonth());
    }

    /**
     * Returns the progress through the year from a calendar time (i.e. 0 - 1, where Jan 1 = 0)
     */
    default float getCalendarFractionOfYear()
    {
        return ICalendar.getFractionOfYear(getCalendarTicks(), getCalendarDaysInMonth());
    }

    /**
     * Calculates the current day from a calendar time.
     */
    default MutableComponent getCalendarDayOfYear()
    {
        return Day.getDayName(getTotalCalendarDays(), getCalendarMonthOfYear(), getCalendarDayOfMonth());
    }

    /**
     * Gets the current month of the year in calendar time
     */
    default Month getCalendarMonthOfYear()
    {
        return ICalendar.getMonthOfYear(getCalendarTicks(), getCalendarDaysInMonth());
    }

    /**
     * Gets the total number of ticks in a month.
     */
    default long getCalendarTicksInMonth()
    {
        return ICalendar.getCalendarTicksInMonth(getCalendarDaysInMonth());
    }

    /**
     * Gets the total number of ticks in a year.
     */
    default long getCalendarTicksInYear()
    {
        return ICalendar.getCalendarTicksInYear(getCalendarDaysInMonth());
    }

    /**
     * @return A formatted component for displaying an exact time stamp. Like "00:00 January 1, 1000"
     */
    default MutableComponent getCalendarTimeAndDate()
    {
        return ICalendar.getTimeAndDate(getCalendarTicks(), getCalendarDaysInMonth());
    }

    /**
     * @param ticks An amount of ticks
     * @return A formatted component for displaying a length of time. Exact format depends on the length of time, using a dynamic precision. May display minutes, hours, days, months, or years.
     */
    default MutableComponent getTimeDelta(long ticks)
    {
        return ICalendar.getTimeDelta(ticks, getCalendarDaysInMonth());
    }
}