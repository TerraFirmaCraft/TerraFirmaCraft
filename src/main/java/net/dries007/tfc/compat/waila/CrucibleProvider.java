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
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.devices.BlockCrucible;
import net.dries007.tfc.objects.te.TECrucible;

@WailaPlugin
public class CrucibleProvider implements IWailaDataProvider, IWailaPlugin
{
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getTileEntity() instanceof TECrucible)
        {
            TECrucible crucible = (TECrucible) accessor.getTileEntity();
            if(crucible.getAlloy().getAmount() > 0)
            {
                Metal metal = crucible.getAlloyResult();
                currentTooltip.add(new TextComponentTranslation("waila.tfc.metal.output", crucible.getAlloy().getAmount(), new TextComponentTranslation(metal.getTranslationKey()).getFormattedText()).getFormattedText());
            }
            int temperature = crucible.getField(TECrucible.FIELD_TEMPERATURE);
            String heatTooltip = Heat.getTooltip(temperature);
            if (heatTooltip != null)
            {
                currentTooltip.add(heatTooltip);
            }
        }
        return currentTooltip;
    }

    @Override
    public void register(IWailaRegistrar registrar)
    {
        registrar.registerBodyProvider(this, BlockCrucible.class);
    }
}
