/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import java.time.LocalDate;
import java.time.Year;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.accessor.GameRulesAccessor;
import net.dries007.tfc.mixin.accessor.GameRulesTypeAccessor;
import net.dries007.tfc.network.CalendarUpdatePacket;
import net.dries007.tfc.util.ReentrantListener;
import net.dries007.tfc.util.advancements.TFCAdvancements;

public final class ServerCalendar extends Calendar
{
    public static final int SYNC_INTERVAL = 20; // Number of ticks between sync attempts. This mimics vanilla's time sync
    public static final int TIME_DESYNC_THRESHOLD = 5;

    @SuppressWarnings("Convert2MethodRef") // Creates a class load dependent NPE
    private static final ReentrantListener DO_DAYLIGHT_CYCLE = new ReentrantListener(() -> Calendars.SERVER.setDoDaylightCycle());

    public static void overrideDoDaylightCycleCallback()
    {
        final GameRulesTypeAccessor type = (GameRulesTypeAccessor) GameRulesAccessor.accessor$getGameRuleTypes().get(GameRules.RULE_DAYLIGHT);
        type.accessor$setCallback(type.accessor$getCallback().andThen((server, t) -> DO_DAYLIGHT_CYCLE.onListenerUpdate()));
    }

    private int syncCounter;

    /**
     * Sets the current player time and calendar time from a calendar timestamp
     *
     * @param calendarTimeToSetTo a calendar ticks time stamp
     */
    public void setTimeFromCalendarTime(long calendarTimeToSetTo)
    {
        // Calculate the time jump
        long timeJump = calendarTimeToSetTo - calendarTicks;

        calendarTicks = calendarTimeToSetTo;
        playerTicks += timeJump;

        // Update the actual world times
        for (ServerLevel world : getServer().getAllLevels())
        {
            long currentDayTime = world.getDayTime();
            world.setDayTime(currentDayTime + timeJump);
        }

        sendUpdatePacket();
    }

    /**
     * Sets the current player time and calendar time from an overworld day time timestamp, e.g. sleeping will set the time to morning.
     *
     * @param worldTimeToSetTo a world time, obtained from {@link ServerLevel#getDayTime()}. Must be in [0, ICalendar.TICKS_IN_DAY]
     * @return the number of ticks skipped (in world time)
     */
    public long setTimeFromDayTime(long worldTimeToSetTo)
    {
        // Calculate the offset to jump to
        long worldTimeJump = (worldTimeToSetTo % ICalendar.TICKS_IN_DAY) - getCalendarDayTime();
        if (worldTimeJump < 0)
        {
            worldTimeJump += ICalendar.TICKS_IN_DAY;
        }

        calendarTicks += worldTimeJump;
        playerTicks += worldTimeJump;

        return worldTimeJump;
    }

    public void setMonthLength(int newMonthLength)
    {
        // Recalculate the new calendar time
        // Preserve the current month, time of day, and position within the month
        long baseMonths = getTotalCalendarMonths();
        long baseDayTime = calendarTicks - (getTotalCalendarDays() * ICalendar.TICKS_IN_DAY);
        // Minus one here because `getDayOfMonth` returns the player visible one (which adds one)
        float monthPercent = (float) (getCalendarDayOfMonth() - 1) / daysInMonth;
        int newDayOfMonth = (int) (monthPercent * newMonthLength);

        this.daysInMonth = newMonthLength;
        this.calendarTicks = (baseMonths * daysInMonth + newDayOfMonth) * ICalendar.TICKS_IN_DAY + baseDayTime;

        sendUpdatePacket();
    }

    public void setPlayersLoggedOn(final boolean arePlayersLoggedOn)
    {
        final boolean alwaysRunAsIfPlayersAreLoggedIn = !TFCConfig.SERVER.enableTimeStopWhenServerEmpty.get();

        this.arePlayersLoggedOn = arePlayersLoggedOn || alwaysRunAsIfPlayersAreLoggedIn;
        if (this.arePlayersLoggedOn)
        {
            setDoDaylightCycleWithNoCallback(doDaylightCycle);
            LOGGER.info(alwaysRunAsIfPlayersAreLoggedIn ?
                "Calendar = true, Daylight = {} due to enableTimeStopWhenServerEmpty = false" :
                "Calendar = true, Daylight = {} due to players logged in", doDaylightCycle);
        }
        else
        {
            setDoDaylightCycleWithNoCallback(false);
            LOGGER.info("Calendar = false, Daylight = false ({}) due to no players logged in", doDaylightCycle);
        }

        sendUpdatePacket();
    }

    public void setDoDaylightCycle()
    {
        GameRules rules = getServer().getGameRules();
        doDaylightCycle = rules.getBoolean(GameRules.RULE_DAYLIGHT);
        if (!arePlayersLoggedOn)
        {
            setDoDaylightCycleWithNoCallback(false);
            LOGGER.info("Calendar = false, Daylight = false ({}) due to no players logged in (updated the value for when players log back in)", doDaylightCycle);
        }

        sendUpdatePacket();
    }

    /**
     * Initializes the calendar with the current minecraft server instance, reloading all values from world saved data
     */
    void onServerStart(MinecraftServer server)
    {
        final boolean alwaysRunAsIfPlayersAreLoggedIn = !TFCConfig.SERVER.enableTimeStopWhenServerEmpty.get();
        if (!alwaysRunAsIfPlayersAreLoggedIn)
        {
            setDoDaylightCycleWithNoCallback(false);
        }

        resetTo(CalendarWorldData.get(server.overworld()).getCalendar());
        sendUpdatePacket();

        // In a game test server environment, we want the calendar to progress, in order for some tests to work properly
        // So, just force it to simulate players logged on
        if (server instanceof GameTestServer)
        {
            setPlayersLoggedOn(true);
        }
    }

    void onServerStop()
    {
        resetToDefault();
    }

    /**
     * Called on server ticks, syncs to client
     */
    void onServerTick()
    {
        if (arePlayersLoggedOn)
        {
            playerTicks++;
        }
        syncCounter++;
        if (syncCounter >= SYNC_INTERVAL)
        {
            sendUpdatePacket();
            syncCounter = 0;
        }
    }

    /**
     * Called on each overworld tick, increments and syncs calendar time
     */
    void onOverworldTick(ServerLevel level)
    {
        if (doDaylightCycle && arePlayersLoggedOn)
        {
            calendarTicks++;
        }

        final long deltaWorldTime = (level.getDayTime() % ICalendar.TICKS_IN_DAY) - getCalendarDayTime();
        if (deltaWorldTime > TIME_DESYNC_THRESHOLD || deltaWorldTime < -TIME_DESYNC_THRESHOLD)
        {
            // We can recount the number of players online, or if we force this on due to a config change
            // If there are players online, we read the value of the doDaylightCycle game rule directly
            // If there are no players online, we force the game rule off, but don't modify our cached / saved value,
            // as we can only assume that is accurately loaded.

            arePlayersLoggedOn = getServer().getPlayerList().getPlayerCount() > 0 || !TFCConfig.SERVER.enableTimeStopWhenServerEmpty.get();

            if (arePlayersLoggedOn)
            {
                doDaylightCycle = getServer().getGameRules().getBoolean(GameRules.RULE_DAYLIGHT);
            }
            else
            {
                // Don't modify doDaylightCycle, as we assume that was accurate, since we can't guess because TFC will have changed the real value
                // However, force the rule off in case it got flipped somehow without us noticing it.
                setDoDaylightCycleWithNoCallback(false);
            }

            if (deltaWorldTime < 0)
            {
                level.setDayTime(level.getDayTime() - deltaWorldTime); // Calendar is ahead, so jump world time
            }
            else
            {
                calendarTicks += deltaWorldTime; // World time is ahead, so jump calendar
            }

            LOGGER.warn("Calendar is out of sync - trying to fix: Calendar = {}, Daylight = {} ({}), Sync = {}", arePlayersLoggedOn, getServer().getGameRules().getBoolean(GameRules.RULE_DAYLIGHT), doDaylightCycle, deltaWorldTime);

            sendUpdatePacket();
        }
        if (level.getGameTime() % 200 == 0)
        {
            checkIfInTheFuture(level);
        }
    }

    void checkIfInTheFuture(ServerLevel level)
    {
        final LocalDate date = LocalDate.now();
        final LocalDate calendarDate = LocalDate.of(
            Mth.clamp((int) getTotalCalendarYears(), Year.MIN_VALUE, Year.MAX_VALUE),
            getCalendarMonthOfYear().ordinal() + 1,
            Mth.clamp(getCalendarDayOfMonth(), 1, 28)
        );
        if (date.isBefore(calendarDate))
        {
            level.getServer().getPlayerList().getPlayers().forEach(TFCAdvancements.PRESENT_DAY::trigger);
        }
    }

    void sendUpdatePacket()
    {
        PacketDistributor.sendToAllPlayers(new CalendarUpdatePacket(this));
    }

    @SuppressWarnings("DataFlowIssue")
    private MinecraftServer getServer()
    {
        return ServerLifecycleHooks.getCurrentServer();
    }

    private void setDoDaylightCycleWithNoCallback(final boolean value)
    {
        final MinecraftServer server = getServer();
        DO_DAYLIGHT_CYCLE.runWithoutTriggeringCallbacks(() -> server.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(value, server));
    }
}