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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

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

    public static void init()
    {
        final IEventBus bus = NeoForge.EVENT_BUS;

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

    /**
     * This allows beds to function correctly with TFCs calendar
     *
     * @param event {@link PlayerWakeUpEvent}
     */
    public static void onPlayerWakeUp(PlayerWakeUpEvent event)
    {
        if (!event.getEntity().getCommandSenderWorld().isClientSide() && !event.updateLevel())
        {
            long currentDayTime = event.getEntity().getCommandSenderWorld().getDayTime();
            if (Calendars.SERVER.getCalendarDayTime() != currentDayTime)
            {
                // Consume food/water on all online players accordingly
                final long jump = Calendars.SERVER.setTimeFromDayTime(currentDayTime);
                final float exhaustion = jump * PlayerInfo.PASSIVE_EXHAUSTION_PER_TICK * TFCConfig.SERVER.passiveExhaustionModifier.get().floatValue();
                for (Player player : event.getEntity().level().players())
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