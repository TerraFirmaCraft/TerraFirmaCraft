/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
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
        // Calculate time to adjust by
        long newCalendarTime = (CalendarTFC.CALENDAR_TIME.getTotalDays() + 1) * ICalendar.TICKS_IN_DAY + CalendarTFC.WORLD_TIME_OFFSET;
        long sleepTimeJump = newCalendarTime - CalendarTFC.CALENDAR_TIME.getTicks();
        long newPlayerTime = CalendarTFC.PLAYER_TIME.getTicks() + sleepTimeJump;

        // Increment offsets
        CalendarTFC.INSTANCE.setCalendarTime(event.getEntityPlayer().getEntityWorld(), newCalendarTime);
        CalendarTFC.INSTANCE.setPlayerTime(event.getEntityPlayer().getEntityWorld(), newPlayerTime);
    }

}
