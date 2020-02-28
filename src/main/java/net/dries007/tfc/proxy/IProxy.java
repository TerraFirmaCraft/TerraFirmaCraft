/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.dries007.tfc.util.calendar.Month;

public interface IProxy
{
    @Nonnull
    IThreadListener getThreadListener(MessageContext context);

    @Nullable
    EntityPlayer getPlayer(MessageContext context);

    @Nullable
    World getWorld(MessageContext context);

    // Calendar Translation / Localization Methods

    @Nonnull
    String getMonthName(Month month, boolean useSeasons);

    @Nonnull
    String getDayName(int dayOfMonth, long totalDays);

    @Nonnull
    String getDate(int hour, int minute, String monthName, int day, long years);

    class WrongSideException extends RuntimeException
    {
        WrongSideException(String message)
        {
            super(message);
        }
    }
}
