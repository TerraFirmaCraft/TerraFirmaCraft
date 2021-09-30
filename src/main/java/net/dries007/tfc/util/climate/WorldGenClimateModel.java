/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Climate model methods which are safe to call both externally, and during world generation.
 */
public interface WorldGenClimateModel extends ClimateModel
{
    @Override
    default float getTemperature(LevelReader level, BlockPos pos, long calendarTicks, int daysInMonth)
    {
        return getTemperature(level, pos, ChunkData.get(level, pos), calendarTicks, daysInMonth);
    }

    /**
     * A specialization of {@link #getTemperature(LevelReader, BlockPos, long, int)} which takes an explicit chunk data, used during world generation when {@link ChunkData#get(LevelReader, BlockPos)} would be invalid.
     *
     * @return A temperature, in degrees Celsius. Typically in the range [-40, 40]
     */
    float getTemperature(LevelReader level, BlockPos pos, ChunkData data, long calendarTicks, int daysInMonth);
}
