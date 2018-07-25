/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.dries007.tfc.ConfigTFC;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class CalenderTFC
{
    public static int currentDay;
    public static int lastMonth = 11; //This default ensures the 0 ticks is January 1st, then startTime moves spawn date as April 1st.
    public static int currentMonth;
    public static int currentYear;
    private static long time; // todo: handle better

    public static final int JANUARY = 10;
    public static final int FEBRUARY = 11;
    public static final int MARCH = 0;
    public static final int APRIL = 1;
    public static final int MAY = 2;
    public static final int JUNE = 3;
    public static final int JULY = 4;
    public static final int AUGUST = 5;
    public static final int SEPTEMBER = 6;
    public static final int OCTOBER = 7;
    public static final int NOVEMBER = 8;
    public static final int DECEMBER = 9;

    public static final int TICKS_IN_HOUR = 1000;
    public static final int TICKS_IN_MINUTE = TICKS_IN_HOUR / 60;
    public static final int TICKS_IN_DAY = 24000;
    public static final int HOURS_IN_DAY = (int) (TICKS_IN_DAY / TICKS_IN_HOUR);

    private static int daysInYear;
    private static int daysInMonth;
    private static int ticksInYear;
    private static int ticksInMonth;
    private static int startTime;

    public static void updateTime(World world)
    {
        time = world.getWorldInfo().getWorldTime();

        if (time < startTime)
        {
            world.getWorldInfo().setWorldTime(startTime);
            world.getWorldInfo().setWorldTotalTime(startTime);
        }

        int m = getMonthOfYear();
        int m1 = m - 1;

        if (m1 < 0)
            m1 = 11;

        lastMonth = m1;
        currentDay = getDayOfMonth();
        currentMonth = m;
        currentYear = (int) getTotalYears();
    }

    public static String getDateStringFromTicks(int time)
    {
        int year = currentYear + 1000;

        String date = currentDay + " " + currentMonth + " " + year;

        return date;
    }

    public static int getMonthFromDayOfYear(long day)
    {
        while (day < 0) day += daysInYear;
        return (int) ((day / (daysInMonth)) % 12);
    }

    public static int getSeasonFromMonthOfYear(long day)
    {
        int month = getMonthFromDayOfYear(day);
        return (month % 4);

    }

    public static int getDayOfMonthFromDayOfYear(long day)
    {
        while (day < 0) day += daysInYear;
        return (int) (day - ((int) Math.floor(day / daysInMonth) * daysInMonth));
    }

    public static long getTotalDays()
    {
        return time / TICKS_IN_DAY;
    }

    public static long getTotalHours()
    {
        return time / TICKS_IN_HOUR;
    }

    public static long getTotalMonths()
    {
        return time / ticksInMonth;
    }

    public static long getTotalYears()
    {
        return time / ticksInYear;
    }

    public static int getMinuteOfHour()
    {
        return (int) ((time / TICKS_IN_MINUTE) % 60);
    }

    public static int getHourOfDay()
    {
        return (int) ((time / TICKS_IN_HOUR) % HOURS_IN_DAY);
    }

    //Calls to get info about Days

    public static int getDayOfWeek()
    {
        return (int) ((time / TICKS_IN_DAY) % 7);
    }

    public static int getDayOfMonth()
    {
        return (int) ((time / TICKS_IN_DAY) % daysInMonth);
    }

    public static int getDayOfYear()
    {
        return (int) ((time / TICKS_IN_DAY) % daysInYear);
    }

    public static int getMonthOfYear()
    {
        return (int) ((time / ticksInMonth) % 12);
    }

    public static void reload()
    {
        daysInYear = daysInMonth * 12;
        daysInMonth = ConfigTFC.GENERAL.monthLength;
        ticksInMonth = daysInMonth * TICKS_IN_DAY;
        ticksInYear = daysInYear * TICKS_IN_DAY;
        startTime = ticksInMonth * 3; // This sets that starting date to April 1st.
    }

    public static int getDaysInMonth()
    {
        return daysInMonth;
    }

    public static int getDaysInYear()
    {
        return daysInYear;
    }

    @SubscribeEvent
    public static void onTickWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END) return;

        time = event.world.getTotalWorldTime();
    }

}
