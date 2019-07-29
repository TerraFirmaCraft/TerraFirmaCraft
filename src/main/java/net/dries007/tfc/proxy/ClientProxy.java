/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.Month;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy implements IProxy
{
    @Nonnull
    @Override
    public IThreadListener getThreadListener(MessageContext context)
    {
        if (context.side.isClient())
        {
            return Minecraft.getMinecraft();
        }
        else
        {
            return context.getServerHandler().player.server;
        }
    }

    @Override
    @Nullable
    public EntityPlayer getPlayer(MessageContext context)
    {
        if (context.side.isClient())
        {
            return Minecraft.getMinecraft().player;
        }
        else
        {
            return context.getServerHandler().player;
        }
    }

    @Override
    @Nullable
    public World getWorld(MessageContext context)
    {
        if (context.side.isClient())
        {
            return Minecraft.getMinecraft().world;
        }
        else
        {
            return context.getServerHandler().player.getEntityWorld();
        }
    }

    @Nonnull
    @Override
    public String getMonthName(Month month, boolean useSeasons)
    {
        return I18n.format(useSeasons ? "tfc.enum.season." + month.name().toLowerCase() : Helpers.getEnumName(month));
    }

    @Nonnull
    @Override
    public String getDayName(int dayOfMonth, long totalDays)
    {
        String date = CalendarTFC.CALENDAR_TIME.getMonthOfYear().name() + dayOfMonth;
        String birthday = CalendarTFC.BIRTHDAYS.get(date);
        if (birthday != null)
        {
            return birthday;
        }
        return I18n.format("tfc.enum.day." + CalendarTFC.DAY_NAMES[(int) (totalDays % 7)]);
    }
}
