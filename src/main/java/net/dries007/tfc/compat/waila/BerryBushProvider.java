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
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.blocks.agriculture.BlockBerryBush;
import net.dries007.tfc.util.calendar.Month;

@WailaPlugin
public class BerryBushProvider implements IWailaDataProvider, IWailaPlugin
{
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getBlock() instanceof BlockBerryBush)
        {
            currentTooltip.add(new TextComponentTranslation("waila.tfc.agriculture.harvesting_months").getFormattedText());
            BlockBerryBush b = (BlockBerryBush) accessor.getBlock();
            for (Month month : Month.values())
            {
                if (b.bush.isHarvestMonth(month))
                {
                    currentTooltip.add(TerraFirmaCraft.getProxy().getMonthName(month, true));
                }
            }
        }
        return currentTooltip;
    }

    @Override
    public void register(IWailaRegistrar registrar)
    {
        registrar.registerBodyProvider(this, BlockBerryBush.class);
    }
}
