/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

/**
 * Event handler for calendar related ticking
 *
 * @see ServerCalendar
 */
public class CalendarEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(CalendarEventHandler::onServerStart);
        bus.addListener(CalendarEventHandler::onServerTick);
        bus.addListener(CalendarEventHandler::onOverworldTick);
        bus.addListener(CalendarEventHandler::onPlayerWakeUp);
        bus.addListener(CalendarEventHandler::onPlayerLoggedOut);
        bus.addListener(CalendarEventHandler::onPlayerLoggedIn);
    }

    public static void onServerStart(FMLServerStartingEvent event)
    {
        Calendars.SERVER.onServerStart(event.getServer());
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
        World world = event.world;
        // todo: verify this is the correct overworld dimension check
        if (event.phase == TickEvent.Phase.END && world instanceof ServerWorld && event.world.dimension() == World.OVERWORLD)
        {
            Calendars.SERVER.onOverworldTick((ServerWorld) world);
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
                long jump = Calendars.SERVER.setTimeFromDayTime(currentDayTime);
                /* todo: requires food overrides
                // Consume food/water on all online players accordingly (EXHAUSTION_MULTIPLIER is here to de-compensate)
                event.getEntity().getEntityWorld().getPlayers()
                    .forEach(player -> player.addExhaustion(FoodStatsTFC.PASSIVE_EXHAUSTION * jump / FoodStatsTFC.EXHAUSTION_MULTIPLIER * (float) ConfigTFC.GENERAL.foodPassiveExhaustionMultiplier));
                */
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
        if (event.getPlayer() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            // Check total players and reset player / calendar time ticking
            MinecraftServer server = player.getServer();
            if (server != null)
            {
                LOGGER.info("Player Logged Out - Checking for Calendar Updates.");
                List<ServerPlayerEntity> players = server.getPlayerList().getPlayers();
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
        if (event.getPlayer() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
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