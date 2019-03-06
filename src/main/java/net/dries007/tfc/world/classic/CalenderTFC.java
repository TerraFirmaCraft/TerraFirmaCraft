/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic;

import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketCalendarUpdate;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

/**
 * Info: For those wanting to use this class:
 * There are two main accessors:
 * {@link CalenderTFC#getTotalTime()} will always return you the result of world#getTotalTime
 * Any other method will return you the CALENDAR TIME (which can be offset from the total world time)
 */
@SuppressWarnings("WeakerAccess")
public class CalenderTFC
{
    public static final int TICKS_IN_DAY = 24000;
    public static final int TICKS_IN_HOUR = 1000;
    public static final int TICKS_IN_MINUTE = TICKS_IN_HOUR / 60;
    public static final int HOURS_IN_DAY = 24;

    /**
     * Total time for the world, directly from world#getTotalTime
     * Synced via two event handlers, one on Client Tick, one on World Tick
     * Usage: Anything that requires TOTAL DAYPERIOD PASSED, i.e. temperature change, tree growth, etc.
     */
    private static long totalTime;
    /**
     * Calendar time, calculated as an offset from totalTime, influenced by timetfc command, changing config, etc.
     * Synced via packets on world load
     * Usage: Anything that requires seasonal change, i.e. flower growth, weather, etc.
     */
    private static long calendarTime;
    private static long calendarOffset;

    private static int daysInYear;
    /* This is set via the timetfc command */
    private static int daysInMonth;
    private static int ticksInYear;
    private static int ticksInMonth;

    public static void preInit()
    {
        // Set everything to default values so we don't hit any div/0 exceptions with any accessors prior to world load
        CalenderTFC.calendarOffset = 0;
        CalenderTFC.daysInMonth = 8;

        // Re-calculate values for calendar
        CalenderTFC.daysInYear = 8 * 12;
        CalenderTFC.ticksInMonth = 8 * TICKS_IN_DAY;
        CalenderTFC.ticksInYear = 8 * 12 * TICKS_IN_DAY;
    }

    public static long getCalendarTime()
    {
        return calendarTime;
    }

    public static void setCalendarTime(World world, long calendarTime)
    {
        // Don't set the calendar time directly, instead set the offset from the total time
        calendarOffset = calendarTime - totalTime;
        // Assert that the calendar offset is a valid multiple of days (otherwise day/night becomes de-synced)
        calendarOffset = (calendarOffset / TICKS_IN_DAY) * TICKS_IN_DAY;
        // Then update clients
        CalendarWorldData.update(world, calendarOffset, daysInMonth);
    }

    public static void setMonthLength(World world, int daysInMonth)
    {
        CalenderTFC.daysInMonth = daysInMonth;
        CalendarWorldData.update(world, calendarOffset, daysInMonth);
    }

    public static long getTotalTime()
    {
        return totalTime;
    }

    private static void setTotalTime(long totalTime)
    {
        CalenderTFC.totalTime = totalTime;
        CalenderTFC.calendarTime = totalTime + calendarOffset;
    }

    @Nonnull
    public static String getTimeAndDate()
    {
        return String.format("%02d:%02d %s %02d, %04d", getHourOfDay(), getMinuteOfHour(), getMonthOfYear().getShortName(), getDayOfMonth(), getTotalYears());
    }

    public static int getDayOfMonthFromDayOfYear(long day)
    {
        return (int) (day % daysInMonth);
    }

    public static long getTotalDays()
    {
        return calendarTime / TICKS_IN_DAY;
    }

    public static long getTotalHours()
    {
        return calendarTime / TICKS_IN_HOUR;
    }

    public static long getTotalMonths()
    {
        return calendarTime / ticksInMonth;
    }

    public static long getTotalYears()
    {
        return calendarTime / ticksInYear;
    }

    public static int getMinuteOfHour()
    {
        return (int) ((calendarTime / TICKS_IN_MINUTE) % 60);
    }

    public static int getHourOfDay()
    {
        return (int) ((calendarTime / TICKS_IN_HOUR) % HOURS_IN_DAY);
    }

    public static int getDayOfMonth()
    {
        return (int) ((calendarTime / TICKS_IN_DAY) % daysInMonth);
    }

    public static Month getMonthOfYear()
    {
        return Month.getById((int) ((calendarTime / ticksInMonth) % 12));
    }

    public static int getDaysInMonth()
    {
        return daysInMonth;
    }

    public static int getDaysInYear()
    {
        return daysInYear;
    }

    public enum Month
    {
        JANUARY(10, 66.5f, "Jan"),
        FEBRUARY(11, 65.5f, "Feb"),
        MARCH(0, 56f, "Mar"),
        APRIL(1, 47.5f, "Apr"),
        MAY(2, 38f, "May"),
        JUNE(3, 29.5f, "June"),
        JULY(4, 27f, "July"),
        AUGUST(5, 29.5f, "Aug"),
        SEPTEMBER(6, 38f, "Sept"),
        OCTOBER(7, 47.5f, "Oct"),
        NOVEMBER(8, 56f, "Nov"),
        DECEMBER(9, 65.5f, "Dec");

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
        private final String abrev;

        Month(int index, float tMod, String abrev)
        {
            this.index = index;
            this.tMod = tMod;
            this.abrev = abrev;
        }

        public int id() { return index; }

        public float getTempMod() { return tMod; }

        public String getShortName() { return abrev; }

        public String getLongName() { return name().substring(0, 1) + name().substring(1).toLowerCase(); }

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

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class EventHandler
    {
        @SubscribeEvent
        public static void onWorldTick(TickEvent.WorldTickEvent event)
        {
            // Does not get called on DEDICATED CLIENT
            if (event.phase == TickEvent.Phase.START)
            {
                CalenderTFC.setTotalTime(event.world.getTotalWorldTime());
            }
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void onClientTick(TickEvent.ClientTickEvent event)
        {
            // On LOGICAL CLIENT, this will be overwritten by onWorldTick
            // On DEDICATED CLIENT, this will be the sole time-tracking for each player (hopefully it works)
            if (event.phase == TickEvent.Phase.START && !Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().player != null)
            {
                CalenderTFC.setTotalTime(Minecraft.getMinecraft().world.getTotalWorldTime());
            }
        }
    }

    @ParametersAreNonnullByDefault
    public static class CalendarWorldData extends WorldSavedData
    {
        private static final String NAME = MOD_ID + ":calendar";

        public static void update(World world, long calendarOffset, int daysInMonth)
        {
            // Updates world data and sends changes to client if this is run on server
            CalendarWorldData data = get(world);
            data.calendarOffset = calendarOffset;
            data.daysInMonth = daysInMonth;
            data.markDirty();

            if (!world.isRemote)
            {
                TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(calendarOffset, daysInMonth));
            }
        }

        public static void onLoad(World world)
        {
            CalendarWorldData data = get(world);
            CalenderTFC.calendarOffset = data.calendarOffset;
            CalenderTFC.daysInMonth = data.daysInMonth;

            // Re-calculate values for calendar
            CalenderTFC.daysInYear = data.daysInMonth * 12;
            CalenderTFC.ticksInMonth = data.daysInMonth * TICKS_IN_DAY;
            CalenderTFC.ticksInYear = data.daysInMonth * 12 * TICKS_IN_DAY;
        }

        @Nonnull
        public static CalendarWorldData get(World world)
        {
            MapStorage mapStorage = world.getMapStorage();
            if (mapStorage != null)
            {
                CalendarWorldData data = (CalendarWorldData) mapStorage.getOrLoadData(CalendarWorldData.class, NAME);
                if (data == null)
                {
                    // Unable to load data, so assign default values
                    TerraFirmaCraft.getLog().info("Initializing Default Calendar Data for world.");
                    data = new CalendarWorldData();
                    data.daysInMonth = 8;
                    data.calendarOffset = data.daysInMonth * 3 * TICKS_IN_DAY;
                    mapStorage.setData(NAME, data);
                }
                return data;
            }
            throw new IllegalStateException("Map Storage is NULL!");
        }

        private long calendarOffset;
        private int daysInMonth;

        CalendarWorldData()
        {
            super(NAME);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt)
        {
            this.calendarOffset = nbt.getLong("calendarOffset");
            this.daysInMonth = nbt.getInteger("daysInMonth");
        }

        @Override
        @Nonnull
        public NBTTagCompound writeToNBT(NBTTagCompound nbt)
        {
            nbt.setLong("calendarOffset", calendarOffset);
            nbt.setInteger("daysInMonth", daysInMonth);
            return nbt;
        }
    }
}
