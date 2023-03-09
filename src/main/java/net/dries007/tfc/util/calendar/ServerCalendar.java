/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoField;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.accessor.GameRulesAccessor;
import net.dries007.tfc.mixin.accessor.GameRulesTypeAccessor;
import net.dries007.tfc.network.CalendarUpdatePacket;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.util.ReentrantRunnable;
import net.dries007.tfc.util.advancements.TFCAdvancements;

public class ServerCalendar extends Calendar
{
    public static final int SYNC_INTERVAL = 20; // Number of ticks between sync attempts. This mimics vanilla's time sync
    public static final int TIME_DESYNC_THRESHOLD = 5;

    @SuppressWarnings("Convert2MethodRef") // Creates a class load dependent NPE
    private static final ReentrantRunnable DO_DAYLIGHT_CYCLE = new ReentrantRunnable(() -> Calendars.SERVER.setDoDaylightCycle());

    public static void overrideDoDaylightCycleCallback()
    {
        final GameRulesTypeAccessor type = (GameRulesTypeAccessor) GameRulesAccessor.accessor$getGameRuleTypes().get(GameRules.RULE_DAYLIGHT);
        type.accessor$setCallback(type.accessor$getCallback().andThen((server, t) -> DO_DAYLIGHT_CYCLE.run()));
    }

    private int syncCounter;

    /**
     * This runs a sequence of code, but first will set the calendar and player time by an offset
     * Useful if we need to run code that technically needs to happen at a different calendar time
     * The offsets are removed once the transaction is complete
     *
     * @param transactionPlayerTimeOffset   the offset to be added to the player time
     * @param transactionCalendarTimeOffset the offset to be added to the calendar time
     *
     * @deprecated Use {@link #transaction()}
     */
    @Deprecated(forRemoval = true)
    public void runTransaction(long transactionPlayerTimeOffset, long transactionCalendarTimeOffset, Runnable action)
    {
        try (CalendarTransaction tr = transaction())
        {
            tr.add(transactionPlayerTimeOffset, transactionCalendarTimeOffset);
            action.run();
        }
    }

    /**
     * Opens a calendar transaction, which allows you to safely manipulate time to perform a sequence of actions, without possibility of distributing the state of the global calendar.
     * @return A new {@link CalendarTransaction}
     * @see CalendarTransaction
     */
    public CalendarTransaction transaction()
    {
        return new Transaction();
    }

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

    public void setPlayersLoggedOn(boolean arePlayersLoggedOn)
    {
        GameRules rules = getServer().overworld().getGameRules();
        this.arePlayersLoggedOn = arePlayersLoggedOn;
        if (arePlayersLoggedOn)
        {
            DO_DAYLIGHT_CYCLE.runBlocking(() -> rules.getRule(GameRules.RULE_DAYLIGHT).set(doDaylightCycle, getServer()));
            LOGGER.info("Reverted doDaylightCycle to {} as players are logged in.", doDaylightCycle);
        }
        else
        {
            DO_DAYLIGHT_CYCLE.runBlocking(() -> rules.getRule(GameRules.RULE_DAYLIGHT).set(false, getServer()));
            LOGGER.info("Forced doDaylightCycle to false as no players are logged in. Will revert to {} as soon as a player logs in.", doDaylightCycle);
        }

        sendUpdatePacket();
    }

    public void setDoDaylightCycle()
    {
        if (TFCConfig.SERVER.enableTimeStopWhenServerEmpty.get())
        {
            GameRules rules = getServer().getGameRules();
            doDaylightCycle = rules.getBoolean(GameRules.RULE_DAYLIGHT);
            if (!arePlayersLoggedOn)
            {
                DO_DAYLIGHT_CYCLE.runBlocking(() -> rules.getRule(GameRules.RULE_DAYLIGHT).set(false, getServer()));
                LOGGER.info("Forced doDaylightCycle to false as no players are logged in. Will revert to {} as soon as a player logs in.", doDaylightCycle);
            }

            sendUpdatePacket();
        }
    }

    /**
     * Initializes the calendar with the current minecraft server instance, reloading all values from world saved data
     */
    void onServerStart(MinecraftServer server)
    {
        GameRules rules = server.overworld().getGameRules();
        DO_DAYLIGHT_CYCLE.runBlocking(() -> rules.getRule(GameRules.RULE_DAYLIGHT).set(false, server));

        resetTo(CalendarWorldData.get(server.overworld()).getCalendar());
        sendUpdatePacket();
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
        long deltaWorldTime = (level.getDayTime() % ICalendar.TICKS_IN_DAY) - getCalendarDayTime();
        if (deltaWorldTime > TIME_DESYNC_THRESHOLD || deltaWorldTime < -TIME_DESYNC_THRESHOLD)
        {
            LOGGER.warn("World time and Calendar Time are out of sync! Trying to fix...");
            LOGGER.debug("Calendar Time = {} ({}), Player Time = {}, World Time = {}, doDaylightCycle = {}, ArePlayersLoggedOn = {}", calendarTicks, getCalendarDayTime(), playerTicks, level.getDayTime() % ICalendar.TICKS_IN_DAY, doDaylightCycle, arePlayersLoggedOn);

            // Check if tracking values are wrong
            boolean checkArePlayersLoggedOn = getServer().getPlayerList().getPlayerCount() > 0;
            if (arePlayersLoggedOn != checkArePlayersLoggedOn)
            {
                // Whoops, somehow we missed this.
                LOGGER.info("Setting ArePlayersLoggedOn = {}", checkArePlayersLoggedOn);
                setPlayersLoggedOn(checkArePlayersLoggedOn);
            }
            if (arePlayersLoggedOn && doDaylightCycle != getServer().getGameRules().getBoolean(GameRules.RULE_DAYLIGHT))
            {
                // Do daylight cycle should match
                LOGGER.info("Setting DoDaylightCycle = {}", getServer().getGameRules().getBoolean(GameRules.RULE_DAYLIGHT));
                setDoDaylightCycle();
            }
            if (deltaWorldTime < 0)
            {
                // Calendar is ahead, so jump world time
                level.setDayTime(level.getDayTime() - deltaWorldTime);
                LOGGER.info("Calendar is ahead by {} ticks, jumping world time to catch up", -deltaWorldTime);
            }
            else
            {
                // World time is ahead, so jump calendar
                calendarTicks += deltaWorldTime;
                LOGGER.info("Calendar is behind by {} ticks, jumping calendar time to catch up", deltaWorldTime);
            }
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
        PacketHandler.send(PacketDistributor.ALL.noArg(), new CalendarUpdatePacket(this));
    }

    private MinecraftServer getServer()
    {
        return ServerLifecycleHooks.getCurrentServer();
    }

    private class Transaction implements CalendarTransaction
    {
        private final long originalPlayerTicks = playerTicks, originalCalendarTicks = calendarTicks;

        @Override
        public void add(long playerTicks, long calendarTicks)
        {
            ServerCalendar.this.playerTicks += playerTicks;
            ServerCalendar.this.calendarTicks += calendarTicks;
        }

        @Override
        public void close()
        {
            ServerCalendar.this.playerTicks = originalPlayerTicks;
            ServerCalendar.this.calendarTicks = originalCalendarTicks;
        }
    }
}