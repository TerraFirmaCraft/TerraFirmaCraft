/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.providers;

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

public class PitKilnProvider implements IWailaBlock
{
    @Nonnull
    @Override
    public List<String> getBodyTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull List<String> currentTooltip, @Nonnull NBTTagCompound nbt)
    {
        TEPitKiln te = Helpers.getTE(world, pos, TEPitKiln.class);
        if (te != null)
        {
            boolean isLit = te.isLit();

            if (isLit)
            {
                long remainingTicks = ConfigTFC.GENERAL.pitKilnTime - (CalendarTFC.PLAYER_TIME.getTicks() - te.getLitTick());
                long remainingMinutes = Math.round(remainingTicks / 1200.0f);
                currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.remaining", remainingMinutes).getFormattedText());
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
    public List<Class<?>> getBodyClassList()
    {
        return Collections.singletonList(TEPitKiln.class);
    }

    @Nonnull
    @Override
    public List<Class<?>> getNBTClassList()
    {
        return Collections.singletonList(TEPitKiln.class);
    }
}
