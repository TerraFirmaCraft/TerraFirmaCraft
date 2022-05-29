/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.config.TFCConfig;
import org.slf4j.Logger;

/**
 * Event handler for calendar related ticking
 *
 * @see ServerCalendar
 */
public class CalendarEventHandler
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(CalendarEventHandler::onServerStart);
        bus.addListener(CalendarEventHandler::onServerStop);
        bus.addListener(CalendarEventHandler::onServerTick);
        bus.addListener(CalendarEventHandler::onOverworldTick);
        bus.addListener(CalendarEventHandler::onPlayerWakeUp);
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

    /**
     * Called from LOGICAL SERVER
     * Responsible for primary time tracking for player time
     * Synced to client every second
     *
     * @param event {@link TickEvent.ServerTickEvent}
     */
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            Calendars.SERVER.onServerTick();
        }
    }

    public static void onOverworldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && event.world instanceof ServerLevel level && level.dimension() == Level.OVERWORLD)
        {
            Calendars.SERVER.onOverworldTick(level);
        }
    }

    /**
     * This allows beds to function correctly with TFCs calendar
     *
     * @param event {@link PlayerWakeUpEvent}
     */
    public static void onPlayerWakeUp(PlayerWakeUpEvent event)
    {
        if (!event.getEntity().getCommandSenderWorld().isClientSide() && !event.updateWorld())
        {
            long currentDayTime = event.getEntity().getCommandSenderWorld().getDayTime();
            if (Calendars.SERVER.getCalendarDayTime() != currentDayTime)
            {
                // Consume food/water on all online players accordingly
                final long jump = Calendars.SERVER.setTimeFromDayTime(currentDayTime);
                final float exhaustion = jump * TFCFoodData.PASSIVE_EXHAUSTION_PER_TICK * TFCConfig.SERVER.passiveExhaustionModifier.get().floatValue();
                for (Player player : event.getEntity().level.players())
                {
                    player.causeFoodExhaustion(exhaustion);
                }
            }
        }
    }

    /**
     * Fired on server only when a player logs out
     *
     * @param event {@link PlayerEvent.PlayerLoggedOutEvent}
     */
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayer player)
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
        if (event.getPlayer() instanceof ServerPlayer player)
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