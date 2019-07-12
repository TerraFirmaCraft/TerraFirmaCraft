/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketCalendarUpdate;

/**
 * This is the central calendar / time tracker for TFC
 * There are two methods you will want to use:
 * - {@link CalendarTFC#getTotalTime()} will get you the same as `world.getTotalTime()`
 * - {@link CalendarTFC#getCalendarTime()} will get you the current time as displayed on the calendar
 *
 * World generation, seasonal variations, etc. should use calendar time
 * Recipes, aging, growth, etc. should use total time.
 *
 * @author AlcatrazEscapee
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum CalendarTFC
{
    INSTANCE;

    public static final int TICKS_IN_DAY = 24000;
    public static final int TICKS_IN_HOUR = 1000;
    /* This needs to be a float, otherwise there are ~62 minutes per hour */
    public static final float TICKS_IN_MINUTE = TICKS_IN_HOUR / 60f;

    public static final int HOURS_IN_DAY = 24;

    public static final String[] DAY_NAMES = new String[] {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    public static final Map<String, String> BIRTHDAYS = new HashMap<>();

    /* The offset in ticks between the world time (sun position) and the calendar (since world time 0 = 6 AM) */
    public static final int WORLD_TIME_OFFSET = 6 * TICKS_IN_HOUR;

    static final int DEFAULT_DAYS_IN_MONTH = 8;
    static final int DEFAULT_CALENDAR_OFFSET = (6 * DEFAULT_DAYS_IN_MONTH * TICKS_IN_DAY) + (6 * TICKS_IN_HOUR);

    static
    {
        // Original developers, all hail their glorious creation
        BIRTHDAYS.put("JULY7", "Bioxx's Birthday");
        BIRTHDAYS.put("JUNE18", "Kitty's Birthday");
        BIRTHDAYS.put("OCTOBER2", "Dunk's Birthday");

        // 1.12+ Dev Team and significant contributors
        BIRTHDAYS.put("MAY1", "Dries's Birthday");
        BIRTHDAYS.put("DECEMBER9", "Alcatraz's Birthday");
        BIRTHDAYS.put("FEBRUARY31", "Bunsan's Birthday");
        BIRTHDAYS.put("MARCH14", "Claycorp's Birthday");
        BIRTHDAYS.put("DECEMBER1", "LightningShock's Birthday");
        BIRTHDAYS.put("JANUARY20", "Therighthon's Birthday");
        BIRTHDAYS.put("FEBRUARY21", "CtrlAltDavid's Birthday");
        BIRTHDAYS.put("MARCH10", "Disastermoo's Birthday");
    }

    /**
     * Updates the world data and the calendar with the provided values
     *
     * @param world the world
     */
    public static void update(World world, long calendarOffset, int daysInMonth, boolean doCalendarCycle)
    {
        // Get and update world data
        CalendarWorldData data = CalendarWorldData.get(world);
        data.calendarOffset = calendarOffset;
        data.daysInMonth = daysInMonth;
        data.doCalendarCycle = doCalendarCycle;
        data.markDirty();

        // Update calendar
        CalendarTFC.INSTANCE.reset(data.calendarOffset, data.daysInMonth, data.doCalendarCycle);

        // Sync to clients
        if (!world.isRemote)
        {
            TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(calendarOffset, daysInMonth, doCalendarCycle));
        }
    }

    /* Internal calendar variables */
    private long totalTime;
    private long calendarTime;
    private long calendarOffset;
    private boolean doCalendarCycle;
    private int daysInMonth;

    CalendarTFC()
    {
        // Set everything to default values so we don't hit any div/0 exceptions with any accessors prior to world load
        // Calendar starts a new world on June 1, 1000
        daysInMonth = DEFAULT_DAYS_IN_MONTH;
        calendarOffset = DEFAULT_CALENDAR_OFFSET;
    }

    /**
     * Sends an update packet to a player on log in
     *
     * @param player the server player to send to
     */
    public void update(EntityPlayerMP player)
    {
        TerraFirmaCraft.getNetwork().sendTo(new PacketCalendarUpdate(calendarOffset, daysInMonth, doCalendarCycle), player);
    }

    /**
     * Reads the calendar from saved world data, then updates clients
     * Called on {@link CalendarEventHandler#onWorldLoad}
     *
     * @param world the world
     */
    public void update(World world)
    {
        // Update calendar from saved world data
        CalendarWorldData data = CalendarWorldData.get(world);
        calendarOffset = data.calendarOffset;
        daysInMonth = data.daysInMonth;
        doCalendarCycle = data.doCalendarCycle;

        reset(data.calendarOffset, data.daysInMonth, data.doCalendarCycle);

        // Sync to clients
        if (!world.isRemote)
        {
            TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(calendarOffset, daysInMonth, doCalendarCycle));
        }
    }

    /**
     * Calendar time, calculated as an offset from totalTime, influenced by timetfc command, changing config, etc.
     * Synced via packets on world load
     * Usage: Anything that requires seasonal change, i.e. flower growth, weather, etc.
     */
    public long getCalendarTime()
    {
        return calendarTime;
    }

    /**
     * Total time for the world, directly from world#getTotalTime
     * Synced via two event handlers, one on Client Tick, one on World Tick
     * Usage: Anything that requires TOTAL TIME PASSED, i.e. temperature change, tree growth, etc.
     */
    public long getTotalTime()
    {
        return totalTime;
    }

    /**
     * Sets the calendar time.
     * This actually sets the calendar offset - i.e. the difference between the current world time and the calendar time
     * It then will save that offset to world data, and sync to client
     *
     * @param world        The world
     * @param calendarTime The calendar time
     */
    public void setCalendarTime(World world, long calendarTime)
    {
        // Calendar time is not allowed to be negative (i.e. before January 1, 1000)
        if (calendarTime < 0)
        {
            calendarTime = 0;
            TerraFirmaCraft.getLog().warn("Something tried to set the calendar time to a negative value! This is likely a programming mistake!");
        }
        // Don't set the calendar time directly, instead set the offset from the total time
        this.calendarOffset = calendarTime - totalTime;
        this.calendarTime = totalTime + calendarOffset;

        // Then update clients
        update(world, calendarOffset, daysInMonth, doCalendarCycle);
    }

    /**
     * Sets the per-world month length. Will also recalculate all time that has passed so far
     *
     * @param world       The current world
     * @param daysInMonth the days per each month
     */
    public void setMonthLength(World world, int daysInMonth)
    {
        // Current amount of months and remainder - these will stay the same
        long totalMonths = getTotalMonths();
        long remainder = calendarTime - (totalMonths * ticksInMonth());

        // New calendar time based on the same amount of months + remainder
        this.daysInMonth = daysInMonth;
        long newCalendarTime = (totalMonths * ticksInMonth()) + remainder;

        // Reset and update
        setCalendarTime(world, newCalendarTime);
    }

    public String getTimeAndDate()
    {
        return getTimeAndDate(getHourOfDay(), getMinuteOfHour(), getMonthOfYear(), getDayOfMonth(), getDisplayTotalYears());
    }

    /* Display Methods */

    public String getTimeAndDate(long calendarTime)
    {
        return getTimeAndDate(getHourOfDay(calendarTime), getMinuteOfHour(calendarTime), getMonthOfYear(calendarTime), getDayOfMonth(calendarTime), getDisplayTotalYears(calendarTime));
    }

    public long getDisplayTotalYears()
    {
        return getDisplayTotalYears(calendarTime);
    }

    public String getSeasonDisplayName()
    {
        return TerraFirmaCraft.getProxy().getMonthName(getMonthOfYear(), true);
    }

    public String getDisplayDayName()
    {
        return TerraFirmaCraft.getProxy().getDayName(getDayOfMonth(), getTotalDays());
    }

    public long getTotalMonths()
    {
        return getTotalMonths(calendarTime);
    }

    /* Total Time Passed */

    public Month getMonthOfYear()
    {
        return getMonthOfYear(calendarTime);
    }

    public long getTotalDays()
    {
        return calendarTime / TICKS_IN_DAY;
    }

    public long getTotalHours()
    {
        return calendarTime / TICKS_IN_HOUR;
    }

    /* Increments */

    public int getMinuteOfHour()
    {
        return getMinuteOfHour(calendarTime);
    }

    public int getHourOfDay()
    {
        return getHourOfDay(calendarTime);
    }

    /**
     * Sets the total time
     * Called from tick handlers on world and client tick to keep this accurate
     *
     * @param totalTime the total world time to set to
     */
    void setTotalTime(long totalTime)
    {
        if (totalTime < 0)
        {
            totalTime = 0;
            TerraFirmaCraft.getLog().warn("Something tried to set the calendar time to a negative value! This is likely a programming mistake!");
        }
        this.totalTime = totalTime;
        if (doCalendarCycle)
        {
            // Set the calendar time based on the current offset
            this.calendarTime = totalTime + calendarOffset;
        }
        else
        {
            // Re-calculate the offset to keep the calendar time constant
            this.calendarOffset = calendarTime - totalTime;
        }
    }

    public int getDayOfMonth()
    {
        return getDayOfMonth(calendarTime);
    }

    public int getDaysInMonth()
    {
        return daysInMonth;
    }

    /**
     * Gets the calendar offset
     *
     * @return the offset of the calendar from the total time
     */
    long getCalendarOffset()
    {
        return calendarOffset;
    }

    private int ticksInYear()
    {
        return 12 * daysInMonth * TICKS_IN_DAY;
    }

    private int ticksInMonth()
    {
        return daysInMonth * TICKS_IN_DAY;
    }

    private String getTimeAndDate(int hour, int minute, Month month, int day, long years)
    {
        String monthName = TerraFirmaCraft.getProxy().getMonthName(month, false);
        return String.format("%02d:%02d %s %02d, %04d", hour, minute, monthName, day, years);
    }

    /**
     * Resets the calendar to the provided values
     */
    private void reset(long calendarOffset, int daysInMonth, boolean doCalendarCycle)
    {
        this.calendarOffset = calendarOffset;
        this.daysInMonth = daysInMonth;
        this.doCalendarCycle = doCalendarCycle;
    }

    /**
     * Get the total number of years for display (i.e 1000, 1001, etc.)
     *
     * @param calendarTime the calendar time
     */
    private long getDisplayTotalYears(long calendarTime)
    {
        return 1000 + (calendarTime / ticksInYear());
    }

    /**
     * Calculate the total amount of months
     *
     * @param calendarTime the calendar time to calculate
     * @return an amount of months
     */
    private long getTotalMonths(long calendarTime)
    {
        return calendarTime / ticksInMonth();
    }

    /**
     * Calculates the current month from a calendar time
     */
    private Month getMonthOfYear(long calendarTime)
    {
        return Month.valueOf((int) ((calendarTime / ticksInMonth()) % 12));
    }

    /**
     * Calculates the day of a month from the calendar time (i.e. 01 - ??)
     */
    private int getDayOfMonth(long calendarTime)
    {
        return 1 + (int) ((calendarTime / TICKS_IN_DAY) % daysInMonth);
    }

    /**
     * Calculates the hour of the day from a calendar time, military time (i.e 00 - 23)
     */
    private int getHourOfDay(long calendarTime)
    {
        return (int) ((calendarTime / TICKS_IN_HOUR) % HOURS_IN_DAY);
    }

    /**
     * Calculates the minute of the hour from a calendar time (i.e. 00 - 59)
     */
    private int getMinuteOfHour(long calendarTime)
    {
        return (int) ((calendarTime % TICKS_IN_HOUR) / TICKS_IN_MINUTE);
    }

}
