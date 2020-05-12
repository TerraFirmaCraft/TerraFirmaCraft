/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;
import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

public class PitKilnProvider implements IWailaBlock
{
    @Nonnull
    @Override
    public List<String> getTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        List<String> currentTooltip = new ArrayList<>();
        TEPitKiln te = Helpers.getTE(world, pos, TEPitKiln.class);
        if (te != null)
        {
            boolean isLit = te.isLit();

            if (isLit)
            {
                long remainingTicks = ConfigTFC.Devices.PIT_KILN.ticks - (CalendarTFC.PLAYER_TIME.getTicks() - te.getLitTick());
                switch (ConfigTFC.Client.TOOLTIP.timeTooltipMode)
                {
                    case NONE:
                        break;
                    case TICKS:
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.ticks_remaining", remainingTicks).getFormattedText());
                        break;
                    case MINECRAFT_HOURS:
                        long remainingHours = Math.round(remainingTicks / (float) ICalendar.TICKS_IN_HOUR);
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.hours_remaining", remainingHours).getFormattedText());
                        break;
                    case REAL_MINUTES:
                        long remainingMinutes = Math.round(remainingTicks / 1200.0f);
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.minutes_remaining", remainingMinutes).getFormattedText());
                        break;
                }
            }
            else
            {
                int straw = te.getStrawCount();
                int logs = te.getLogCount();
                if (straw == 8 && logs == 8)
                {
                    currentTooltip.add(new TextComponentTranslation("waila.tfc.pitkiln.unlit").getFormattedText());
                }
                else
                {
                    if (straw < 8)
                    {
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.pitkiln.straw", 8 - straw).getFormattedText());
                    }
                    if (logs < 8)
                    {
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.pitkiln.logs", 8 - logs).getFormattedText());
                    }
                }
            }
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getLookupClass()
    {
        return Collections.singletonList(TEPitKiln.class);
    }
}
