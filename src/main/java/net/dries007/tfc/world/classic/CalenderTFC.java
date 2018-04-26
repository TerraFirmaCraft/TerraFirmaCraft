package net.dries007.tfc.world.classic;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.dries007.tfc.ConfigTFC;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class CalenderTFC
{
    public static final int TICKS_IN_HOUR = 1000;
    public static final int TICKS_IN_MINUTE = TICKS_IN_HOUR / 60;
    public static final int TICKS_IN_DAY = 24000;
    public static final int HOURS_IN_DAY = (int) (TICKS_IN_DAY / TICKS_IN_HOUR);

    private static int daysInYear;
    private static int daysInMonth;
    private static int ticksInYear;
    private static int ticksInMonth;
    private static int startTime;

    private static long time; // todo: handle better

    public static int getSeasonFromDayOfYear(long day, boolean south)
    {
        while (day < 0) day += daysInYear;
        return (int) ((day / (daysInMonth) + (south ? 6 : 0)) % 12);
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

    public static int getDayOfMonth()
    {
        return (int) ((time / TICKS_IN_DAY) % daysInMonth);
    }

    public static int getMonthOfYear()
    {
        return (int) ((time / ticksInMonth) % 12);
    }

    public static void reload()
    {
        daysInYear = ConfigTFC.GENERAL.yearLength;
        daysInMonth = daysInYear / 12;
        ticksInMonth = daysInMonth * TICKS_IN_DAY;
        ticksInYear = daysInYear * TICKS_IN_DAY;
        startTime = ticksInMonth * 3;
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
