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
