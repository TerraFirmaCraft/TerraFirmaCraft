/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.dries007.tfc.api.calendar.Calendar;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * @see net.dries007.tfc.api.calendar.CalendarEventHandler
 */
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientCalendarEventHandler
{
    @SubscribeEvent
    public static void onOverworldTick(TickEvent.ClientTickEvent event)
    {
        World world = Minecraft.getInstance().world;
        if (event.phase == TickEvent.Phase.END && world != null && !Minecraft.getInstance().isGamePaused())
        {
            Calendar.INSTANCE.get().onClientTick();
        }
    }
}
