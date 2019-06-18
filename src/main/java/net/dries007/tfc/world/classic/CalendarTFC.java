/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.GameRuleChangeEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.WorldEvent;
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
 * {@link CalendarTFC#getTotalTime()} will always return you the result of world#getTotalTime
 * Any other method will return you the CALENDAR TIME (which can be offset / changed from the total world time)
 */
@SuppressWarnings("WeakerAccess")
public class CalendarTFC
{
    public static final int TICKS_IN_DAY = 24000;
    public static final int TICKS_IN_HOUR = 1000;
    /* This needs to be a float, otherwise there are ~62 minutes per hour */
    public static final float TICKS_IN_MINUTE = TICKS_IN_HOUR / 60f;
    public static final int HOURS_IN_DAY = 24;
    private static final String[] DAY_NAMES = new String[] {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    private static final Map<String, String> BIRTHDAYS = new HashMap<>();
    /**
     * Total time for the world, directly from world#getTotalTime
     * Synced via two event handlers, one on Client Tick, one on World Tick
     * Usage: Anything that requires TOTAL TIME PASSED, i.e. temperature change, tree growth, etc.
     */
    private static long totalTime;
    /**
     * Calendar time, calculated as an offset from totalTime, influenced by timetfc command, changing config, etc.
     * Synced via packets on world load
     * Usage: Anything that requires seasonal change, i.e. flower growth, weather, etc.
     */
    private static long calendarTime;
    private static long calendarOffset;
    private static boolean doCalendarCycle;
    /* This is set via the timetfc command */
    private static int daysInMonth;
    /* These are calculated from above */
    private static int daysInYear;
    private static int ticksInYear;
    private static int ticksInMonth;

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
    }

    public static void preInit()
    {
        // Set everything to default values so we don't hit any div/0 exceptions with any accessors prior to world load
        CalendarTFC.calendarOffset = 0;
        CalendarTFC.daysInMonth = 8;

        // Re-calculate values for calendar
        CalendarTFC.daysInYear = 8 * 12;
        CalendarTFC.ticksInMonth = 8 * TICKS_IN_DAY;
        CalendarTFC.ticksInYear = 8 * 12 * TICKS_IN_DAY;
    }

    public static long getCalendarTime()
    {
        return calendarTime;
    }

    public static void setCalendarTime(World world, long calendarTime)
    {
        // Don't set the calendar time directly, instead set the offset from the total time
        calendarOffset = calendarTime - totalTime;
        CalendarTFC.calendarTime = totalTime + calendarOffset;
        // Then update clients
        CalendarWorldData.update(world, calendarOffset, daysInMonth, doCalendarCycle);
    }

    public static void setMonthLength(World world, int daysInMonth)
    {
        CalendarTFC.daysInMonth = daysInMonth;
        CalendarWorldData.update(world, calendarOffset, daysInMonth, doCalendarCycle);
    }

    public static long getTotalTime()
    {
        return totalTime;
    }

    private static void setTotalTime(long totalTime)
    {
        CalendarTFC.totalTime = totalTime;
        if (doCalendarCycle)
        {
            // Set the calendar time based on the current offset
            calendarTime = totalTime + calendarOffset;
        }
        else
        {
            // Re-calculate the offset to keep the calendar time static
            calendarOffset = calendarTime - totalTime;
        }
    }

    public static int getMinuteOfHour()
    {
        return getMinuteOfHour(calendarTime);
    }

    @Nonnull
    public static String getTimeAndDate()
    {
        return getTimeAndDate(getHourOfDay(), getMinuteOfHour(), getMonthOfYear().getShortName(), getDayOfMonth(), getTotalYears());
    }

    @Nonnull
    public static String getDayName()
    {
        String date = getMonthOfYear().name() + getDayOfMonth();
        String birthday = BIRTHDAYS.get(date);
        if (birthday != null)
        {
            return birthday;
        }
        long days = getTotalDays();
        return "tfc.enum.day." + DAY_NAMES[(int) (days % 7)];
    }

    @Nonnull
    public static String getTimeAndDate(long calendarTime)
    {
        return getTimeAndDate(getHourOfDay(calendarTime), getMinuteOfHour(calendarTime), getMonthOfYear(calendarTime).getShortName(), getDayOfMonth(calendarTime), getTotalYears(calendarTime));
    }

    @Nonnull
    public static String getTimeAndDate(int hour, int minute, String month, int day, long years)
    {
        return String.format("%02d:%02d %s %02d, %04d", hour, minute, month, day, years);
    }

    public static long getTotalYears()
    {
        return getTotalYears(calendarTime);
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

    public static int getHourOfDay()
    {
        return getHourOfDay(calendarTime);
    }

    public static int getDayOfMonth()
    {
        return getDayOfMonth(calendarTime);
    }

    @Nonnull
    public static Month getMonthOfYear()
    {
        return getMonthOfYear(calendarTime);
    }

    public static int getDaysInMonth()
    {
        return daysInMonth;
    }

    public static int getDaysInYear()
    {
        return daysInYear;
    }

    // Calculation based functions - do not change these unless broken
    private static long getTotalYears(long calendarTime)
    {
        // Years start at 1000, and begin at Jan, but month is indexed starting at March
        return 1000 + (calendarTime + 2 * ticksInMonth) / ticksInYear;
    }

    private static int getMinuteOfHour(long calendarTime)
    {
        return (int) ((calendarTime % TICKS_IN_HOUR) / TICKS_IN_MINUTE);
    }

    private static int getHourOfDay(long calendarTime)
    {
        return (int) ((6 + (calendarTime / TICKS_IN_HOUR)) % HOURS_IN_DAY);
    }

    private static int getDayOfMonth(long calendarTime)
    {
        return (int) ((calendarTime / TICKS_IN_DAY) % daysInMonth);
    }

    private static Month getMonthOfYear(long calendarTime)
    {
        return Month.getById((int) ((calendarTime / ticksInMonth) % 12));
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

        public static float getAverageTempMod()
        {
            return averageTempMod;
        }

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

        public int id()
        {
            return index;
        }

        public float getTempMod()
        {
            return tMod;
        }

        public String getShortName()
        {
            return abrev;
        }

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
                CalendarTFC.setTotalTime(event.world.getTotalWorldTime());
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
                CalendarTFC.setTotalTime(Minecraft.getMinecraft().world.getTotalWorldTime());
            }
        }

        @SubscribeEvent
        public static void onGameRuleChange(GameRuleChangeEvent event)
        {
            // This is only called on server, so it needs to sync to client
            GameRules rules = event.getRules();
            if ("doDaylightCycle".equals(event.getRuleName()))
            {
                CalendarTFC.doCalendarCycle = rules.getBoolean("doDaylightCycle");
                CalendarWorldData.update(event.getServer().getEntityWorld());
            }
        }

        @SubscribeEvent
        public static void onCommandFire(CommandEvent event)
        {
            if ("time".equals(event.getCommand().getName()))
            {
                event.setCanceled(true);
                event.getSender().sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.time_command_disabled"));
            }
        }

        /**
         * This allows beds to function correctly with TFC's calendar
         *
         * @param event {@link PlayerWakeUpEvent}
         */
        @SubscribeEvent
        public static void onPlayerWakeUp(PlayerWakeUpEvent event)
        {
            // Set the calendar time to time=0. This will implicitly call CalendarTFC#update
            long newCalendarTime = (CalendarTFC.getTotalDays() + 1) * TICKS_IN_DAY;
            setCalendarTime(event.getEntityPlayer().getEntityWorld(), newCalendarTime);
        }

        @SubscribeEvent
        public static void onWorldLoad(WorldEvent.Load event)
        {
            // Calendar Sync / Initialization
            final World world = event.getWorld();
            if (world.provider.getDimension() == 0 && !world.isRemote)
            {
                CalendarTFC.CalendarWorldData.onLoad(event.getWorld());
            }
        }
    }

    @ParametersAreNonnullByDefault
    public static class CalendarWorldData extends WorldSavedData
    {
        private static final String NAME = MOD_ID + ":calendar";

        public static void update(World world)
        {
            update(world, CalendarTFC.calendarOffset, CalendarTFC.daysInMonth, CalendarTFC.doCalendarCycle);
        }

        public static void update(World world, long calendarOffset, int daysInMonth, boolean doCalendarCycle)
        {
            // Updates world data and sends changes to client if this is run on server
            CalendarWorldData data = get(world);
            data.calendarOffset = calendarOffset;
            data.daysInMonth = daysInMonth;
            data.doCalendarCycle = doCalendarCycle;
            data.markDirty();

            if (!world.isRemote)
            {
                TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(calendarOffset, daysInMonth, doCalendarCycle));
            }
        }

        public static void onLoad(World world)
        {
            CalendarWorldData data = get(world);
            CalendarTFC.calendarOffset = data.calendarOffset;
            CalendarTFC.daysInMonth = data.daysInMonth;
            CalendarTFC.doCalendarCycle = data.doCalendarCycle;

            // Re-calculate values for calendar
            CalendarTFC.daysInYear = data.daysInMonth * 12;
            CalendarTFC.ticksInMonth = data.daysInMonth * TICKS_IN_DAY;
            CalendarTFC.ticksInYear = data.daysInMonth * 12 * TICKS_IN_DAY;

            if (!world.isRemote)
            {
                TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(CalendarTFC.calendarOffset, CalendarTFC.daysInMonth, CalendarTFC.doCalendarCycle));
            }
        }

        @Nonnull
        public static CalendarWorldData get(@Nonnull World world)
        {
            MapStorage mapStorage = world.getMapStorage();
            if (mapStorage != null)
            {
                CalendarWorldData data = (CalendarWorldData) mapStorage.getOrLoadData(CalendarWorldData.class, NAME);
                if (data == null)
                {
                    // Unable to load data, so assign default values
                    data = new CalendarWorldData();
                    data.daysInMonth = 8;
                    data.calendarOffset = 0;
                    data.doCalendarCycle = true;
                    data.markDirty();
                    mapStorage.setData(NAME, data);
                }
                return data;
            }
            throw new IllegalStateException("Map Storage is NULL!");
        }

        private long calendarOffset;
        private int daysInMonth;
        private boolean doCalendarCycle;

        public CalendarWorldData()
        {
            super(NAME);
        }

        @SuppressWarnings("unused")
        public CalendarWorldData(String name)
        {
            super(name);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt)
        {
            this.calendarOffset = nbt.getLong("calendarOffset");
            this.daysInMonth = nbt.getInteger("daysInMonth");
            this.doCalendarCycle = nbt.getBoolean("doCalendarCycle");
        }

        @Override
        @Nonnull
        public NBTTagCompound writeToNBT(NBTTagCompound nbt)
        {
            nbt.setLong("calendarOffset", calendarOffset);
            nbt.setInteger("daysInMonth", daysInMonth);
            nbt.setBoolean("doCalendarCycle", doCalendarCycle);
            return nbt;
        }
    }
}
