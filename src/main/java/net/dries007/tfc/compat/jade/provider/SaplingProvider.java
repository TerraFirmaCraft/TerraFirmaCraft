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
import net.dries007.tfc.common.blocks.wood.TFCSaplingBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public enum SaplingProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlockEntity() instanceof TickCounterBlockEntity counter && access.getBlock() instanceof TFCSaplingBlock sapling)
        {
            final float perc = Math.min(0.99f, (float) counter.getTicksSinceUpdate() / (sapling.getDaysToGrow() * ICalendar.TICKS_IN_DAY));
            final String growth = String.format("%d%%", Math.round(perc));
            tooltip.add(Helpers.translatable("tfc.jade.growth").append(growth));
        }
    }
}
