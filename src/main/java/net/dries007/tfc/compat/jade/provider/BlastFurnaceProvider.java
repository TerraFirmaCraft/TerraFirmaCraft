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
import net.dries007.tfc.common.blockentities.BlastFurnaceBlockEntity;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.compat.jade.JadeIntegration;
import net.dries007.tfc.util.Helpers;

public enum BlastFurnaceProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlockEntity() instanceof BlastFurnaceBlockEntity furnace)
        {
            furnace.getCapability(HeatCapability.BLOCK_CAPABILITY).ifPresent(cap -> JadeIntegration.displayHeat(tooltip, cap.getTemperature()));

            tooltip.add(Helpers.translatable("tfc.jade.input_stacks", String.valueOf(furnace.getInputCount())));
            tooltip.add(Helpers.translatable("tfc.jade.catalyst_stacks", String.valueOf(furnace.getCatalystCount())));
            tooltip.add(Helpers.translatable("tfc.jade.fuel_stacks", String.valueOf(furnace.getFuelCount())));
        }
    }
}
