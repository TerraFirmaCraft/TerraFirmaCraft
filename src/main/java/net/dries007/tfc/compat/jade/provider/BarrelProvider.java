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
import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public enum BarrelProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlock() instanceof BarrelBlock && access.getBlockEntity() instanceof BarrelBlockEntity barrel)
        {
            if (access.getBlockState().getValue(BarrelBlock.SEALED))
            {
                BarrelRecipe recipe = barrel.getRecipe();
                if (recipe != null)
                {
                    tooltip.add(recipe.getTranslationComponent());
                    tooltip.add(Helpers.translatable("tfc.jade.sealed_date").append(ICalendar.getTimeAndDate(Calendars.CLIENT.ticksToCalendarTicks(barrel.getSealedTick()), Calendars.get(access.getLevel()).getCalendarDaysInMonth())));
                }
            }
        }
    }
}
