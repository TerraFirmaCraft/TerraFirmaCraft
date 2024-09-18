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

    /**
     * We don't use this game rule - it makes tracking time complicated, and forces us to rely on the NeoForge implementation of
     * time advancing. Instead, we listen to this command and force this always to false. We take over day time tracking ourselves.
     */
    private static final ReentrantListener DO_DAYLIGHT_CYCLE = new ReentrantListener(ServerCalendar::overrideDoDaylightCycleToFalse);

    @SuppressWarnings("DataFlowIssue")
    private static void overrideDoDaylightCycleToFalse()
    {
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        server.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
    }

    public static void overrideDoDaylightCycleCallback()
    {
        final GameRulesTypeAccessor type = (GameRulesTypeAccessor) GameRulesAccessor.accessor$getGameRuleTypes().get(GameRules.RULE_DAYLIGHT);
        type.accessor$setCallback(type.accessor$getCallback().andThen((server, t) -> DO_DAYLIGHT_CYCLE.onListenerUpdate()));
    }

    private int syncCounter;

    /**
     * Skips the calendar forward by a number of calendar ticks, and also increments the number of player ticks by the amount that
     * would've passed in this duration. An exception occurs, if the current calendar tick rate is zero, no player ticks are incremented.
     * @param calendarTicks The calendar ticks to skip forward by.
     */
    public void skipForwardBy(long calendarTicks)
    {
        this.calendarTicks += calendarTicks;
        this.playerTicks += calendarTickRate == 0 ? 0 : calendarTicks / calendarTickRate;
        updateDayTime(getServer().overworld());
        sendUpdatePacket();
    }

    public void setMonthLength(int newMonthLength)
    {
        // Recalculate the new calendar time
        // Preserve the current month, time of day, and position within the month

        final long baseMonths = calendarTicks / getCalendarTicksInMonth();
        final double baseFractionOfMonth = getCalendarFractionOfMonth();
        final long baseDayTime = calendarTicks % CALENDAR_TICKS_IN_DAY;

        this.daysInMonth = newMonthLength;
        this.calendarTicks = baseMonths * daysInMonth * CALENDAR_TICKS_IN_DAY // Advance to the new month
            + ((long) (baseFractionOfMonth * daysInMonth)) * CALENDAR_TICKS_IN_HOUR // At a representative day of month
            + baseDayTime; // With the same day time

        updateDayTime(getServer().overworld());
        sendUpdatePacket();
    }

    public void setCalendarTickRate(float calendarTickRate)
    {
        this.calendarTickRate = calendarTickRate;
        sendUpdatePacket();
    }

    public void setPlayersLoggedOn(final boolean arePlayersLoggedOn)
    {
        final boolean alwaysRunAsIfPlayersAreLoggedIn = !TFCConfig.SERVER.enableTimeStopWhenServerEmpty.get();
        this.arePlayersLoggedOn = arePlayersLoggedOn || alwaysRunAsIfPlayersAreLoggedIn;
        sendUpdatePacket();
    }

    /**
     * Initializes the calendar with the current minecraft server instance, reloading all values from world saved data
     */
    void onServerStart(MinecraftServer server)
    {
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
        if (arePlayersLoggedOn)
        {
            advanceCalendarTick();
            if ((calendarTicks & 0x100) == 0) checkIfInTheFuture(level);
        }
        updateDayTime(level);
    }

    void checkIfInTheFuture(ServerLevel level)
    {
        final LocalDate date = LocalDate.now();
        final LocalDate calendarDate = LocalDate.of(
            Mth.clamp((int) getCalendarYear(), Year.MIN_VALUE, Year.MAX_VALUE),
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

    void updateDayTime(ServerLevel level)
    {
        // Just force day time (in vanilla) to be set to the same as our calendar, plus an offset so that vanilla can calculate % 24_000
        // values correctly. In this case, that means advancing so 0 calendar time (midnight) = 18_000 (vanilla midnight)
        level.setDayTime(calendarTicks + 18_000L);
    }

    @SuppressWarnings("DataFlowIssue")
    private MinecraftServer getServer()
    {
        return ServerLifecycleHooks.getCurrentServer();
    }
}