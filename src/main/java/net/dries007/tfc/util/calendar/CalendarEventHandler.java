/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import java.util.List;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.player.PlayerInfo;
import net.dries007.tfc.config.TFCConfig;

/**
 * Event handler for calendar related ticking
 *
 * @see ServerCalendar
 */
public class CalendarEventHandler
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float SLEEP_TIME = 20f / 24f; // 8 pm
    private static final float WAKE_TIME = 5f / 24f; // 5 am

    public static void init()
    {
        final IEventBus bus = NeoForge.EVENT_BUS;

        bus.addListener(CalendarEventHandler::onServerStart);
        bus.addListener(CalendarEventHandler::onServerStop);
        bus.addListener(CalendarEventHandler::onServerTick);
        bus.addListener(CalendarEventHandler::onOverworldTick);
        bus.addListener(CalendarEventHandler::onPlayerStartSleeping);
        bus.addListener(CalendarEventHandler::onPlayerLoggedOut);
        bus.addListener(CalendarEventHandler::onPlayerLoggedIn);
    }

    public static void onServerStart(ServerStartingEvent event)
    {
        Calendars.SERVER.onServerStart(event.getServer());
    }

    public static void onServerStop(ServerStoppedEvent event)
    {
        Calendars.SERVER.onServerStop();
    }

    public static void onServerTick(ServerTickEvent.Pre event)
    {
        Calendars.SERVER.onServerTick();
    }

    public static void onOverworldTick(LevelTickEvent.Post event)
    {
        if (event.getLevel() instanceof ServerLevel level && level.dimension() == Level.OVERWORLD)
        {
            Calendars.SERVER.onOverworldTick(level);
        }
    }

    public static void onPlayerStartSleeping(CanPlayerSleepEvent event)
    {
        if (event.getVanillaProblem() == BedSleepingProblem.NOT_POSSIBLE_NOW || event.getVanillaProblem() == null)
        {
            // Vanilla denied it due to the time, or allowed it (without checking calendar time). We replace this with our outcome here
            // N.B. We can replace the vanilla tooltip from "Only at night/thunderstorm" to "Only at night" because we will
            // never use the former, and this is easier than alternatives for trying to hook into this logic
            final float time = Calendars.SERVER.getCalendarFractionOfDay();
            event.setProblem((time >= SLEEP_TIME || time <= WAKE_TIME) ? null : BedSleepingProblem.NOT_POSSIBLE_NOW);
        }
    }

    /**
     * Handles sleep related effects. In the vanilla code path for handling time skips post sleeping:
     * <ol>
     *     <li>If the game rule {@code doDaylightCycle} is true, then {@link SleepFinishedTimeEvent} is fired to calculate a new day time,
     *     and the server is updated with said time. Note this won't fire in TFC due to the game rule being forced off, and anyone else
     *     that fires this event manually will have no effect, since {@link Level#getDayTime()} is also forced to be overridden by TFC</li>
     *     <li>Each player that was sleeping is notified via {@link PlayerWakeUpEvent}. This is inconvenient to listen to as the event is
     *     fired in other locations which may not require a time skip.</li>
     *     <li>The weather cycle is reset. This has no effect on TFC worlds because we take control of the weather handling.</li>
     * </ol>
     * In practice, this means we have an injection into where this logic would've happened, so we perform this time skip once, on server,
     * under our control.
     * <h3>Sleeping Mechanics</h3>
     * For now, sleeping possibility is based on vanilla possibility (that is, only during "night" or thunderstorm), and it will advance
     * a set amount of time (random between 6-8 hours). In the future we want to change both of these things to be on a per-player basis, with
     * rewards for regular sleep.
     */
    public static void onPlayersFinishedSleeping(ServerLevel level)
    {
        // Calculate time slept
        final float currentFractionOfDay = Calendars.SERVER.getCalendarFractionOfDay();
        final float wakeUpTime = 5f / 24f; // 5 am, expressed as a fraction of day
        final float targetFraction = wakeUpTime > currentFractionOfDay ? wakeUpTime : 1 + wakeUpTime;
        final int calendarTicksSlept = (int) ((targetFraction - currentFractionOfDay) * ICalendar.CALENDAR_TICKS_IN_DAY);

        // Skip the calendar forward
        Calendars.SERVER.skipForwardBy(calendarTicksSlept);

        // Exhaustion is based on the player ticks, not calendar ticks, so re-scale it appropriately
        final float exhaustion = Calendars.SERVER.getFixedCalendarTicksFromTick(calendarTicksSlept)
            * PlayerInfo.PASSIVE_EXHAUSTION_PER_TICK
            * TFCConfig.SERVER.passiveExhaustionModifier.get().floatValue();

        for (ServerPlayer player : level.players())
        {
            // Consume hunger based on time skipped
            // This doesn't really make sense in either case, but this makes the most sense to do in multiplayer
            player.causeFoodExhaustion(exhaustion);
        }
    }

    /**
     * Fired on server only when a player logs out
     *
     * @param event {@link PlayerEvent.PlayerLoggedOutEvent}
     */
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player)
        {
            // Check total players and reset player / calendar time ticking
            MinecraftServer server = player.getServer();
            if (server != null)
            {
                LOGGER.info("Player Logged Out - Checking for Calendar Updates.");
                List<ServerPlayer> players = server.getPlayerList().getPlayers();
                int playerCount = players.size();
                // The player logging out doesn't count
                if (players.contains(player))
                {
                    playerCount--;
                }
                Calendars.SERVER.setPlayersLoggedOn(playerCount > 0);
            }
        }
    }

    /**
     * Fired on server only when a player logs in
     *
     * @param event {@link PlayerEvent.PlayerLoggedInEvent}
     */
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player)
        {
            // Check total players and reset player / calendar time ticking
            MinecraftServer server = player.getServer();
            if (server != null)
            {
                LOGGER.info("Player Logged In - Checking for Calendar Updates.");
                Calendars.SERVER.setPlayersLoggedOn(server.getPlayerList().getPlayerCount() > 0);
            }
        }
    }
}