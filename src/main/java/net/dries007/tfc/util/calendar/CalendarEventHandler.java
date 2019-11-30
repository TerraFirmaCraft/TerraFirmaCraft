/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.GameRuleChangeEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import static net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class CalendarEventHandler
{
    /**
     * Called from LOGICAL SERVER
     * Responsible for primary time tracking for the calendar
     * Synced to client every tick
     *
     * @param event {@link ServerTickEvent}
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            CalendarTFC.INSTANCE.onTick();
        }
    }

    /**
     * Disables the vanilla /time command as we replace it with one that takes into account the calendar
     * @param event {@link CommandEvent}
     */
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
     * This allows beds to function correctly with TFCs calendar
     *
     * @param event {@link PlayerWakeUpEvent}
     */
    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event)
    {
        // Update calendar to world time = 0
        if (!event.getEntityPlayer().world.isRemote)
        {
            CalendarTFC.INSTANCE.setTimeFromWorldTime(0);
        }
    }

    /**
     * Fired on server only when a player logs out
     *
     * @param event {@link PlayerLoggedOutEvent}
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            // Check total players and reset player / calendar time ticking
            List<EntityPlayer> players = event.player.world.playerEntities;
            int playerCount = players.size();
            // The player logging out doesn't count
            if (players.contains(event.player))
            {
                playerCount--;
            }
            CalendarTFC.INSTANCE.setPlayersLoggedOn(playerCount > 0);
        }
    }

    /**
     * Fired on server only when a player logs in
     *
     * @param event {@link PlayerLoggedInEvent}
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            // Check total players and reset player / calendar time ticking
            int players = event.player.world.playerEntities.size();
            CalendarTFC.INSTANCE.setPlayersLoggedOn(players > 0);
        }
    }

    /**
     * Detects when a user manually changes `doDaylightCycle`, and updates the calendar accordingly
     *
     * @param event {@link GameRuleChangeEvent}
     */
    @SubscribeEvent
    public static void onGameRuleChange(GameRuleChangeEvent event)
    {
        if ("doDaylightCycle".equals(event.getRuleName()))
        {
            // This is only called on server, so it needs to sync to client
            CalendarTFC.INSTANCE.setDoDaylightCycle();
        }
    }
}
