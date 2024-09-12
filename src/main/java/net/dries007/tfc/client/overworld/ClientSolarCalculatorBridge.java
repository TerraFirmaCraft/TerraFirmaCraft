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
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;

/**
 * Bridge methods from common code to client code that needs to calculate day time, and also to encapsulate passing in
 * the correct parameters for a client-sided context.
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

    public static int getMoonPhase()
    {
        return SolarCalculator.getMoonPhase(Calendars.CLIENT.getCalendarTicks(), getMoonOrbitTicks());
    }

    public static SkyPos getMoonPosition(Level level, BlockPos pos)
    {
        return SolarCalculator.getMoonPosition(
            pos.getZ(),
            Climate.get(level).hemisphereScale(),
            Calendars.CLIENT.getCalendarTicks(),
            getMoonOrbitTicks());
    }

    public static int getMoonOrbitTicks()
    {
        return (int) (16.13 * ICalendar.TICKS_IN_DAY); // This is not quite a nice clean multiple, to introduce some additional variation into the moon's orbit
    }

    public static SkyPos getStarPosition(Level level, BlockPos pos)
    {
        return SolarCalculator.getStarPosition(
            pos.getZ(),
            Climate.get(level).hemisphereScale(),
            Calendars.CLIENT.getCalendarFractionOfDay(),
            Calendars.CLIENT.getCalendarFractionOfYear());
    }
}
