/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade.provider;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public enum CharcoalForgeProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlockEntity() instanceof CharcoalForgeBlockEntity forge)
        {
            tooltip.add(TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(forge.getTemperature()));
            tooltip.add(Helpers.translatable("tfc.jade.air_ticks", forge.getAirTicks()));
        }
    }
}
