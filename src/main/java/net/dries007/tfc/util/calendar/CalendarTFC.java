/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
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

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketCalendarUpdate;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("WeakerAccess")
public enum CalendarTFC
{
    INSTANCE;

    public static final int TICKS_IN_DAY = 24000;
    public static final int TICKS_IN_HOUR = 1000;
    /* This needs to be a float, otherwise there are ~62 minutes per hour */
    public static final float TICKS_IN_MINUTE = TICKS_IN_HOUR / 60f;

    public static final int HOURS_IN_DAY = 24;

    private static final String[] DAY_NAMES = new String[] {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    private static final Map<String, String> BIRTHDAYS = new HashMap<>();

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

    private long totalTime;
    private long calendarTime;
    private long calendarOffset;
    private boolean doCalendarCycle;
    private int daysInMonth;
    private int ticksInYear;
    private int ticksInMonth;

    CalendarTFC()
    {
        // Set everything to default values so we don't hit any div/0 exceptions with any accessors prior to world load
        calendarOffset = 0;
        daysInMonth = 8;

        reset();
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
     * Sets the total time
     * Called from tick handlers on world and client tick to keep this accurate
     *
     * @param totalTime the total world time to set to
     */
    private void setTotalTime(long totalTime)
    {
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
        // Don't set the calendar time directly, instead set the offset from the total time
        this.calendarOffset = calendarTime - totalTime;
        this.calendarTime = totalTime + calendarOffset;

        // Then update clients
        CalendarWorldData.update(world, calendarOffset, daysInMonth, doCalendarCycle);
    }

    /**
     * Sets the per-world month length. Will also recalculate all time that has passed so far
     *
     * @param world       The current world
     * @param daysInMonth the days per each month
     */
    public void setMonthLength(World world, int daysInMonth)
    {
        this.daysInMonth = daysInMonth;

        CalendarWorldData.update(world, calendarOffset, daysInMonth, doCalendarCycle);
    }

    public int getMinuteOfHour()
    {
        return getMinuteOfHour(calendarTime);
    }

    public long getTotalYears()
    {
        return getTotalYears(calendarTime);
    }

    public long getTotalDays()
    {
        return calendarTime / TICKS_IN_DAY;
    }

    public long getTotalHours()
    {
        return calendarTime / TICKS_IN_HOUR;
    }

    public int getHourOfDay()
    {
        return getHourOfDay(calendarTime);
    }

    public int getDayOfMonth()
    {
        return getDayOfMonth(calendarTime);
    }

    @Nonnull
    public Month getMonthOfYear()
    {
        return getMonthOfYear(calendarTime);
    }

    public int getDaysInMonth()
    {
        return daysInMonth;
    }

    @Nonnull
    public String getTimeAndDate()
    {
        return getTimeAndDate(getHourOfDay(), getMinuteOfHour(), CalendarTFC.INSTANCE.getMonthOfYear().getShortName(), getDayOfMonth(), getTotalYears());
    }

    @Nonnull
    public String getTimeAndDate(long calendarTime)
    {
        return getTimeAndDate(getHourOfDay(calendarTime), getMinuteOfHour(calendarTime), getMonthOfYear(calendarTime).getShortName(), getDayOfMonth(calendarTime), getTotalYears(calendarTime));
    }

    @Nonnull
    public String getTimeAndDate(int hour, int minute, String month, int day, long years)
    {
        return String.format("%02d:%02d %s %02d, %04d", hour, minute, month, day, years);
    }

    @Nonnull
    public String getDayTranslationKey()
    {
        String date = CalendarTFC.INSTANCE.getMonthOfYear().name() + getDayOfMonth();
        String birthday = BIRTHDAYS.get(date);
        if (birthday != null)
        {
            return birthday;
        }
        long days = getTotalDays();
        return "tfc.enum.day." + DAY_NAMES[(int) (days % 7)];
    }

    /**
     * Resets the calendar to the provided values
     */
    private void reset(long calendarOffset, int daysInMonth, boolean doCalendarCycle)
    {
        this.calendarOffset = calendarOffset;
        this.daysInMonth = daysInMonth;
        this.doCalendarCycle = doCalendarCycle;
        reset();
    }

    /**
     * Resets calculated values based on the current calendar settings
     */
    private void reset()
    {
        // Re-calculate values for calendar
        ticksInMonth = 8 * TICKS_IN_DAY;
        ticksInYear = 8 * 12 * TICKS_IN_DAY;
    }

    /**
     * Calculate total years from a calendar time
     */
    private long getTotalYears(long calendarTime)
    {
        // Years start at 1000, and begin at Jan, but month is indexed starting at March
        return 1000 + (calendarTime + 2 * ticksInMonth) / ticksInYear;
    }

    /**
     * Calculates the current month from a calendar time
     */
    private Month getMonthOfYear(long calendarTime)
    {
        return Month.getById((int) ((calendarTime / ticksInMonth) % 12));
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
        return (int) ((6 + (calendarTime / TICKS_IN_HOUR)) % HOURS_IN_DAY);
    }

    /**
     * Calculates the minute of the hour from a calendar time (i.e. 00 - 59)
     */
    private int getMinuteOfHour(long calendarTime)
    {
        return (int) ((calendarTime % TICKS_IN_HOUR) / TICKS_IN_MINUTE);
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
                CalendarTFC.INSTANCE.setTotalTime(event.world.getTotalWorldTime());
            }
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void onClientTick(TickEvent.ClientTickEvent event)
        {
            // On LOGICAL CLIENT, this will be overwritten by onWorldTick
            // On DEDICATED CLIENT, this will be the sole time-tracking for each player
            if (event.phase == TickEvent.Phase.START && !Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().player != null)
            {
                CalendarTFC.INSTANCE.setTotalTime(Minecraft.getMinecraft().world.getTotalWorldTime());
            }
        }

        @SubscribeEvent
        public static void onGameRuleChange(GameRuleChangeEvent event)
        {
            // This is only called on server, so it needs to sync to client
            GameRules rules = event.getRules();
            if ("doDaylightCycle".equals(event.getRuleName()))
            {
                CalendarWorldData.update(event.getServer().getEntityWorld(), CalendarTFC.INSTANCE.calendarOffset, CalendarTFC.INSTANCE.daysInMonth, rules.getBoolean("doDaylightCycle"));
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
            long newCalendarTime = (CalendarTFC.INSTANCE.getTotalDays() + 1) * TICKS_IN_DAY;
            CalendarTFC.INSTANCE.setCalendarTime(event.getEntityPlayer().getEntityWorld(), newCalendarTime);
        }

        @SubscribeEvent
        public static void onWorldLoad(WorldEvent.Load event)
        {
            // Calendar Sync / Initialization
            final World world = event.getWorld();
            if (world.provider.getDimension() == 0 && !world.isRemote)
            {
                CalendarTFC.CalendarWorldData.update(event.getWorld());
            }
        }
    }

    @ParametersAreNonnullByDefault
    public static class CalendarWorldData extends WorldSavedData
    {
        private static final String NAME = MOD_ID + ":calendar";

        /**
         * Sends an update packet to a player on log in
         *
         * @param player the server player to send to
         */
        public static void update(EntityPlayerMP player)
        {
            TerraFirmaCraft.getNetwork().sendTo(new PacketCalendarUpdate(CalendarTFC.INSTANCE.calendarOffset, CalendarTFC.INSTANCE.daysInMonth, CalendarTFC.INSTANCE.doCalendarCycle), player);
        }

        /**
         * Reads the calendar from saved world data, then updates clients
         * Called on {@link CalendarTFC.EventHandler#onWorldLoad}
         *
         * @param world the world
         */
        public static void update(World world)
        {
            // Update calendar from saved world data
            CalendarWorldData data = get(world);
            CalendarTFC.INSTANCE.calendarOffset = data.calendarOffset;
            CalendarTFC.INSTANCE.daysInMonth = data.daysInMonth;
            CalendarTFC.INSTANCE.doCalendarCycle = data.doCalendarCycle;
            CalendarTFC.INSTANCE.reset(data.calendarOffset, data.daysInMonth, data.doCalendarCycle);

            // Sync to clients
            if (!world.isRemote)
            {
                TerraFirmaCraft.getNetwork().sendToAll(new PacketCalendarUpdate(data.calendarOffset, data.daysInMonth, data.doCalendarCycle));
            }
        }

        /**
         * Updates the world data and the calendar with the provided values
         *
         * @param world the world
         */
        public static void update(World world, long calendarOffset, int daysInMonth, boolean doCalendarCycle)
        {
            // Get and update world data
            CalendarWorldData data = get(world);
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
