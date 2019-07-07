/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.GameRuleChangeEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class CalendarEventHandler
{
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        // Does not get called on DEDICATED CLIENT
        if (event.phase == TickEvent.Phase.START)
        {
            CalendarTFC.INSTANCE.setTotalTime(event.world.getTotalWorldTime());
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        // On LOGICAL CLIENT, this will be overwritten by onWorldTick
        // On DEDICATED CLIENT, this will be the sole time-tracking for each player
        if (event.phase == TickEvent.Phase.START && !Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().player != null)
        {
            CalendarTFC.INSTANCE.setTotalTime(Minecraft.getMinecraft().world.getTotalWorldTime());
        }
    }

    @SubscribeEvent
    public static void onGameRuleChange(GameRuleChangeEvent event)
    {
        // This is only called on server, so it needs to sync to client
        GameRules rules = event.getRules();
        if ("doDaylightCycle".equals(event.getRuleName()))
        {
            CalendarTFC.update(event.getServer().getEntityWorld(), CalendarTFC.INSTANCE.getCalendarOffset(), CalendarTFC.INSTANCE.getDaysInMonth(), rules.getBoolean("doDaylightCycle"));
        }
    }

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
     * This allows beds to function correctly with TFC's calendar
     *
     * @param event {@link PlayerWakeUpEvent}
     */
    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event)
    {
        // Set the calendar time to time=0. This will implicitly call CalendarTFC#update
        long newCalendarTime = (CalendarTFC.INSTANCE.getTotalDays() + 1) * CalendarTFC.TICKS_IN_DAY;
        CalendarTFC.INSTANCE.setCalendarTime(event.getEntityPlayer().getEntityWorld(), newCalendarTime);
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        // Calendar Sync / Initialization
        final World world = event.getWorld();
        if (world.provider.getDimension() == 0 && !world.isRemote)
        {
            CalendarTFC.INSTANCE.update(event.getWorld());
        }
    }
}
