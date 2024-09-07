/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.overworld;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;

/**
 * Bridge methods from common code to client code that needs to calculate day time
 */
public final class ClientSolarCalculatorBridge
{
    public static long getDayTime(LevelAccessor maybeLevel)
    {
        if (maybeLevel instanceof Level level && level.dimension() == Level.OVERWORLD)
        {
            final Player player = ClientHelpers.getPlayer();
            if (player != null)
            {
                return SolarCalculator.getSunBasedDayTime(
                    player.blockPosition().getZ(),
                    Climate.get(level).hemisphereScale(),
                    Calendars.CLIENT.getCalendarFractionOfYear(),
                    Calendars.CLIENT.getCalendarFractionOfDay());
            }
        }
        return maybeLevel.getLevelData().getDayTime(); // Fallback
    }

    public static SkyPos getSunPosition(Level level, BlockPos pos)
    {
        return SolarCalculator.getSunPosition(
            pos.getZ(),
            Climate.get(level).hemisphereScale(),
            Calendars.CLIENT.getCalendarFractionOfYear(),
            Calendars.CLIENT.getCalendarFractionOfDay());
    }
}
