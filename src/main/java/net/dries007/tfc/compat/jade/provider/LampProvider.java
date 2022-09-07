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
import net.dries007.tfc.common.blockentities.LampBlockEntity;
import net.dries007.tfc.common.blocks.devices.LampBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.LampFuel;
import net.dries007.tfc.util.calendar.ICalendar;

public enum LampProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlockEntity() instanceof LampBlockEntity lamp && access.getBlock() instanceof LampBlock)
        {
            LampFuel fuel = lamp.getFuel();
            if (fuel != null)
            {
                tooltip.add(Helpers.translatable("tfc.jade.burn_rate", String.valueOf(fuel.getBurnRate())));
                if (access.getBlockState().getValue(LampBlock.LIT))
                {
                    if (fuel.getBurnRate() == -1)
                    {
                        tooltip.add(Helpers.translatable("tfc.jade.burn_forever"));
                    }
                    else
                    {
                        lamp.getCapability(Capabilities.FLUID).ifPresent(cap -> {
                            final int fluid = cap.getFluidInTank(0).getAmount();
                            if (fluid > 0)
                            {
                                // ticks / mB * mB = ticks
                                tooltip.add(Helpers.translatable("tfc.jade.hours_remaining", String.valueOf(Math.round((float) fluid * fuel.getBurnRate() / ICalendar.TICKS_IN_HOUR))));
                            }
                        });
                    }

                }

            }
        }
    }
}
