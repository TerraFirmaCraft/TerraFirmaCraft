/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import mcp.mobius.waila.api.*;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.objects.blocks.devices.BlockPitKiln;
import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.calendar.CalendarTFC;

@WailaPlugin
public class PitKilnProvider implements IWailaDataProvider, IWailaPlugin
{
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getTileEntity() instanceof TEPitKiln)
        {
            TEPitKiln te = (TEPitKiln) accessor.getTileEntity();
            boolean isLit = te.isLit();

            if (isLit)
            {
                long remainingTicks = ConfigTFC.GENERAL.pitKilnTime - (CalendarTFC.PLAYER_TIME.getTicks() - te.getLitTick());
                long remainingMinutes = Math.round(remainingTicks / 1200.0f);
                currentTooltip.add(new TextComponentTranslation("waila.tfc.remaining", remainingMinutes).getFormattedText());
            }
            else
            {
                int straw = te.getStrawCount();
                int logs = te.getLogCount();
                if (straw == 8 && logs == 8)
                {
                    currentTooltip.add(new TextComponentTranslation("waila.tfc.unlit").getFormattedText());
                }
                else
                {
                    if (straw < 8)
                    {
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.straw", 8 - straw).getFormattedText());
                    }
                    if (logs < 8)
                    {
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.logs", 8 - logs).getFormattedText());
                    }
                }
            }
        }
        return currentTooltip;
    }

    @Override
    public void register(IWailaRegistrar registrar)
    {
        registrar.registerBodyProvider(this, BlockPitKiln.class);
    }
}
