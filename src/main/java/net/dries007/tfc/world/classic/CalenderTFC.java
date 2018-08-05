/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic;

import java.util.Arrays;

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

    public static Month getMonthOfYear()
    {
        return Month.getById((int) ((time / ticksInMonth) % 12));
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

    public enum Month
    {
        JANUARY(10, 66.5f),
        FEBRUARY(11, 65.5f),
        MARCH(0, 56f),
        APRIL(1, 47.5f),
        MAY(2, 38f),
        JUNE(3, 29.5f),
        JULY(4, 27f),
        AUGUST(5, 29.5f),
        SEPTEMBER(6, 38f),
        OCTOBER(7, 47.5f),
        NOVEMBER(8, 56f),
        DECEMBER(9, 65.5f);

        private static float averageTempMod;

        static
        {
            averageTempMod = 0.0f;
            Arrays.stream(Month.values()).forEach(m -> averageTempMod += m.getTempMod());
            averageTempMod /= 12f;
        }

        public static float getAverageTempMod() { return averageTempMod; }

        public static Month getById(int id)
        {
            return Arrays.stream(Month.values()).filter(m -> m.index == id).findFirst().orElse(MARCH);
        }

        private final int index;
        private final float tMod;

        Month(int index, float tMod)
        {
            this.index = index;
            this.tMod = tMod;
        }

        public int id() { return index; }

        public float getTempMod() { return tMod; }

        public Month next()
        {
            if (this == FEBRUARY)
                return MARCH;
            return Month.getById(this.index + 1);
        }

        public Month previous()
        {
            if (this == MARCH)
                return FEBRUARY;
            return Month.getById(this.index - 1);
        }
    }
}
