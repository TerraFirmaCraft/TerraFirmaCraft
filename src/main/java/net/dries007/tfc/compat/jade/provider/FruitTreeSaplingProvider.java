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
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.plant.fruit.FruitTreeSaplingBlock;
import net.dries007.tfc.compat.jade.JadeIntegration;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public enum FruitTreeSaplingProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlockEntity() instanceof TickCounterBlockEntity counter && access.getBlock() instanceof FruitTreeSaplingBlock sapling)
        {
            final String growth = Helpers.formatPercentage(Math.min(0.99f, (float) counter.getTicksSinceUpdate() / (sapling.getTreeGrowthDays() * ICalendar.TICKS_IN_DAY)));
            tooltip.add(Helpers.translatable("tfc.jade.growth").append(growth));
            JadeIntegration.loadHoeOverlay(sapling, tooltip, access);
        }
    }
}
