/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

/**
 * A climate model where the underlying model is time invariant.
 */
public interface TimeInvariantClimateModel extends ClimateModel
{
    float getTemperature(LevelReader level, BlockPos pos);

    @Override
    default float getTemperature(LevelReader level, BlockPos pos, long calendarTicks, int daysInMonth)
    {
        return getTemperature(level, pos);
    }

    @Override
    default float getAverageTemperature(LevelReader level, BlockPos pos)
    {
        return getTemperature(level, pos);
    }

}
