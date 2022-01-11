/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.mixin.accessor.BiomeAccessor;
import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * A default climate model, for dimensions that are entirely biome determined (i.e. vanilla).
 */
public class BiomeBasedClimateModel implements TimeInvariantClimateModel
{
    @Override
    public float getTemperature(LevelReader level, BlockPos pos)
    {
        return Climate.toActualTemperature(((BiomeAccessor) (Object) level.getBiome(pos)).invoke$getTemperature(pos));
    }

    @Override
    public float getRainfall(LevelReader level, BlockPos pos)
    {
        return Mth.clamp(level.getBiome(pos).getDownfall(), 0, 1) * ClimateModel.MAXIMUM_RAINFALL;
    }

    @Override
    public Biome.Precipitation getPrecipitation(LevelReader level, BlockPos pos)
    {
        return level.getBiome(pos).getPrecipitation();
    }

    @Override
    public float getFogginess(LevelReader level, BlockPos pos, long calendarTime)
    {
        return 0;
    }
}
