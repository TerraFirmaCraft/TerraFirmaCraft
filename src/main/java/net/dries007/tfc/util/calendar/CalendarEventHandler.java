/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Event handler for calendar related ticking
 *
 * @see ServerCalendar
 */
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CalendarEventHandler
{
    public static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
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
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            Calendars.SERVER.onServerTick();
        }
    }

    @SubscribeEvent
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
    @SubscribeEvent
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
    @SubscribeEvent
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
    @SubscribeEvent
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