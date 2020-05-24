/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.calendar;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class CalendarEventHandler
{
    public static final Logger LOGGER = LogManager.getLogger();
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
        if (event.phase == TickEvent.Phase.END)
        {
            CalendarTFC.INSTANCE.onServerTick();
        }
    }

    @SubscribeEvent
    public static void onOverworldTick(TickEvent.WorldTickEvent event)
    {
        World world = event.world;
        if (event.phase == TickEvent.Phase.END && world instanceof ServerWorld && event.world.getDimension().getType() == DimensionType.OVERWORLD)
        {
            CalendarTFC.INSTANCE.onOverworldTick((ServerWorld) world);
        }
    }

    /** todo
     * Disables the vanilla /time command as we replace it with one that takes into account the calendar
     *
     * @param event {@link CommandEvent}
     */
    /*
    @SubscribeEvent
    public static void onCommandFire(CommandEvent event)
    {
        if ("time".equals(event.getCommand().getName()))
        {
            event.setCanceled(true);
            event.getSender().sendMessage(new TextComponentTranslation(MOD_ID + ".command.time.disabled"));
        }
    }
    */

    /**
     * This allows beds to function correctly with TFCs calendar
     *
     * @param event {@link PlayerWakeUpEvent}
     */
    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event)
    {
        if (!event.getEntity().getEntityWorld().isRemote() && !event.updateWorld())
        {
            long currentWorldTime = event.getEntity().getEntityWorld().getGameTime();
            if (CalendarTFC.CALENDAR_TIME.getWorldTime() != currentWorldTime)
            {
                long jump = CalendarTFC.INSTANCE.setTimeFromWorldTime(currentWorldTime);
                /* todo
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
                CalendarTFC.INSTANCE.setPlayersLoggedOn(playerCount > 0);
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
                int players = server.getPlayerList().getPlayers().size();
                CalendarTFC.INSTANCE.setPlayersLoggedOn(players > 0);
            }
        }
    }

    /** todo - probably gonna need to change this to listen to the command and not the gamerule change
     * Detects when a user manually changes `doDaylightCycle`, and updates the calendar accordingly
     *
     * @param event {@link GameRuleChangeEvent}
     */
    /*
    @SubscribeEvent
    public static void onGameRuleChange(GameRuleChangeEvent event)
    {
        if ("doDaylightCycle".equals(event.getRuleName()))
        {
            // This is only called on server, so it needs to sync to client
            CalendarTFC.INSTANCE.setDoDaylightCycle();
        }
    }
    */
}
