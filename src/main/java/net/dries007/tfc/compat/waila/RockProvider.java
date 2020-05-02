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
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.util.climate.ClimateTFC;

@WailaPlugin
public class RockProvider implements IWailaDataProvider, IWailaPlugin
{
    @Nonnull
    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (config.getConfig("tfc.displayTemp"))
        {
            int temperature = Math.round(ClimateTFC.getActualTemp(accessor.getWorld(), accessor.getPosition(), 0));
            currentTooltip.add(new TextComponentTranslation("waila.tfc.temperature", temperature).getFormattedText());
        }
        return currentTooltip;
    }

    @Override
    public void register(IWailaRegistrar registrar)
    {
        registrar.registerTailProvider(this, BlockRockVariant.class);
    }
}
