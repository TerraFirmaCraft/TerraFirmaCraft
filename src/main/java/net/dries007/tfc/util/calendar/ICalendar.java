/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.config.TimeDeltaTooltipStyle;
import net.dries007.tfc.util.Helpers;

/**
 * @see Calendars
 */
public interface ICalendar
{
    /** The number of calendar hours in the day. */
    int HOURS_IN_DAY = 24;

    /** The number of months in a year. */
    int MONTHS_IN_YEAR = 12;

    /**
     * For both these measurements, there is a distinction between the default value of an hour and a player-tick hour.
     * @deprecated because uses need to be audited to make sure they want calendar or player tick values
     */
    @Deprecated int TICKS_IN_HOUR = 1000;
    @Deprecated int TICKS_IN_DAY = TICKS_IN_HOUR * HOURS_IN_DAY;

    /** Use if you're specifically trying to measure calendar ticks in a day, not player ticks. */
    int CALENDAR_TICKS_IN_HOUR = 1000;
    int CALENDAR_TICKS_IN_DAY = CALENDAR_TICKS_IN_HOUR * HOURS_IN_DAY;

    static MutableComponent getTimeAndDate(long calendarTick, long daysInMonth)
    {
        return Component.translatable("tfc.tooltip.calendar_hour_minute_month_day_year",
            String.format("%d:%02d", getHourOfDay(calendarTick), getMinuteOfHour(calendarTick)),
            Helpers.translateEnum(getMonthOfYear(calendarTick, daysInMonth)),
            getDayOfMonth(calendarTick, daysInMonth),
            getCalendarYear(calendarTick, daysInMonth));
    }

    static MutableComponent getTimeDelta(long calendarTick, int daysInMonth)
    {
        final long hours = getTotalCalendarHours(calendarTick);
        if (hours < 1)
        {
            return Component.translatable("tfc.tooltip.time_delta_hours_minutes", "00", String.format("%02d", getMinuteOfHour(calendarTick)));
        }
        final long days = getTotalCalendarDays(calendarTick);
        if (days < 1)
        {
            return Component.translatable("tfc.tooltip.time_delta_hours_minutes", hours, String.format("%02d", getMinuteOfHour(calendarTick)));
        }
        final long months = getTotalMonths(calendarTick, daysInMonth);
        final TimeDeltaTooltipStyle style = TFCConfig.CLIENT.timeDeltaTooltipStyle.get();
        if (months < 1 || style == TimeDeltaTooltipStyle.DAYS)
        {
            return Component.translatable("tfc.tooltip.time_delta_days", days);
        }
        final long years = getCalendarYear(calendarTick, daysInMonth) - 1000; // Since years starts at 1k
        if (years < 1 || style == TimeDeltaTooltipStyle.DAYS_MONTHS)
        {
            return Component.translatable("tfc.tooltip.time_delta_months_days", months, days % daysInMonth);
        }
        return Component.translatable("tfc.tooltip.time_delta_years_months_days", years, months % MONTHS_IN_YEAR, days % daysInMonth);
    }

    // NOTE: These are private right now so I can prevent things from using them WITHOUT fully checkin everything that uses them
    // They can be made public later, I just don't want to use them accidentally while porting all this to the new calendar ticks

    private static int getMinuteOfHour(long calendarTick)
    {
        // N.B. The floating point calculation is here because a minute is not a precise number of calendar ticks
        return (int) (60f * (calendarTick % CALENDAR_TICKS_IN_HOUR) / CALENDAR_TICKS_IN_HOUR);
    }

    private static int getHourOfDay(long calendarTick)
    {
        return (int) ((calendarTick / CALENDAR_TICKS_IN_HOUR) % HOURS_IN_DAY);
    }

    private static int getDayOfMonth(long calendarTick, long daysInMonth)
    {
        return 1 + (int) ((calendarTick / CALENDAR_TICKS_IN_DAY) % daysInMonth);
    }

    private static long getTotalCalendarHours(long calendarTick)
    {
        return calendarTick / CALENDAR_TICKS_IN_HOUR;
    }

    private static long getTotalMonths(long calendarTick, long daysInMonth)
    {
        return calendarTick / (daysInMonth * CALENDAR_TICKS_IN_DAY);
    }

    private static long getCalendarYear(long calendarTick, long daysInMonth)
    {
        return 1000 + (calendarTick / (MONTHS_IN_YEAR * daysInMonth * CALENDAR_TICKS_IN_DAY));
    }

    static long getTotalCalendarDays(long calendarTick)
    {
        return calendarTick / CALENDAR_TICKS_IN_DAY;
    }

    static float getFractionOfDay(long calendarTick)
    {
        return (float) (calendarTick % CALENDAR_TICKS_IN_DAY) / CALENDAR_TICKS_IN_DAY;
    }

    static float getFractionOfMonth(long calendarTick, long daysInMonth)
    {
        long ticksInMonth = daysInMonth * CALENDAR_TICKS_IN_DAY;
        return (float) (calendarTick % ticksInMonth) / ticksInMonth;
    }

    static float getFractionOfYear(long calendarTick, long daysInMonth)
    {
        long ticksInYear = MONTHS_IN_YEAR * daysInMonth * CALENDAR_TICKS_IN_DAY;
        return (float) (calendarTick % ticksInYear) / ticksInYear;
    }

    static Month getMonthOfYear(long calendarTick, long daysInMonth)
    {
        long ticksInMonth = daysInMonth * TICKS_IN_DAY;
        return Month.valueOf((int) ((calendarTick / ticksInMonth) % MONTHS_IN_YEAR));
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
     * @return The number of <strong>player ticks</strong> in a single calendar tick hour.
     */
    default long getTicksInHour()
    {
        return getFixedCalendarTicksFromTick(CALENDAR_TICKS_IN_HOUR);
    }

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
     * Return the expected calendar tick at now + {@code offsetTick}. This must be stable (so {@code f(x) + a = f(x + a)}, so that it can be
     * relied upon to return an accurate estimated timestamp without jitter.
     * @param offsetTick The offset (forwards = positive) from the current calendar tick, in a number of player ticks)
     * @return The future calendar tick
     */
    long getCalendarTickFromOffset(long offsetTick);

    /**
     * Return the expected amount of calendar ticks represented by the fixed number of player ticks. This must be stable w.r.t time (so {@code f(x)}
     * is independent of time), but should <strong>NOT</strong> be used to increment from the calendar tick, due to the presence of partial ticks
     * (it is not stable for that purpose).
     * <p>
     * Use {@link #getCalendarTickFromOffset} if you are using a measure of now + {@code offsetTick} instead.
     *
     * @param playerTick The fixed amount of player ticks that should be represented.
     * @return The calendar tick closest to representing the target player tick
     */
    long getFixedCalendarTicksFromTick(long playerTick);

    /**
     * Gets the total amount of days passed
     * @deprecated almost everything counting days in player ticks is now wrong.
     */
    @Deprecated
    default long getTotalDays()
    {
        return ICalendar.getTotalCalendarDays(getTicks());
    }

    /**
     * Gets the total amount of days passed since Jan 1, 1000
     */
    default long getTotalCalendarDays()
    {
        return ICalendar.getTotalCalendarDays(getCalendarTicks());
    }

    /**
     * @return The display year, starting at {@code 1000}.
     */
    default long getCalendarYear()
    {
        return ICalendar.getCalendarYear(getCalendarTicks(), getCalendarDaysInMonth());
    }

    /**
     * Calculates the day of a month from the calendar time (i.e. 01 - ??)
     */
    default int getCalendarDayOfMonth()
    {
        return ICalendar.getDayOfMonth(getCalendarTicks(), getCalendarDaysInMonth());
    }

    /**
     * @return The progress through the current day from the calendar time. A value between {@code [0, 1]} where {@code 0} indicates midnight,
     * and {@code 0.5} indicates noon.
     */
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
        return (long) CALENDAR_TICKS_IN_DAY * getCalendarDaysInMonth();
    }

    /**
     * Gets the total number of ticks in a year.
     */
    default long getCalendarTicksInYear()
    {
        return MONTHS_IN_YEAR * getCalendarTicksInMonth();
    }

    /**
     * @return A formatted component for displaying an exact time stamp of the current tick. Like "00:00 January 1, 1000"
     */
    default MutableComponent getTimeAndDate()
    {
        return getOffsetTimeAndDate(0);
    }

    /**
     * @return A formatted component for displaying an exact timestamp of the specified player tick, scaled relative to the current calendar tick.
     */
    default MutableComponent getExactTimeAndDate(long playerTick)
    {
        return getOffsetTimeAndDate(playerTick - getTicks());
    }

    /**
     * @return A formatted component for displaying an exact timestamp of the current tick, plus a number of player ticks (scaled to the appropriate
     * calendar tick).
     */
    default MutableComponent getOffsetTimeAndDate(long offsetTick)
    {
        return ICalendar.getTimeAndDate(getCalendarTickFromOffset(offsetTick), getCalendarDaysInMonth());
    }

    /**
     * @return A formatted component for displaying a time-independent, arbitrary length of time measured in {@code playerTicks}. The returned component
     * has dynamic precision depending on the length of time.
     */
    default MutableComponent getTimeDelta(long playerTick)
    {
        return ICalendar.getTimeDelta(getFixedCalendarTicksFromTick(playerTick), getCalendarDaysInMonth());
    }

    /**
     * @return A formatted component for displaying a length of time <strong>from the current calendar tick</strong>, plus an {@code offsetTick}
     * measured in player ticks. The returned component has dynamic precision depending on the length of time.
     */
    default MutableComponent getCalendarTimeDelta(long offsetTick)
    {
        return ICalendar.getTimeDelta(getCalendarTickFromOffset(offsetTick), getCalendarDaysInMonth());
    }
}