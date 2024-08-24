/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.mixin.accessor.BiomeAccessor;

/**
 * A default climate model, for dimensions that are entirely biome determined (i.e. vanilla).
 */
public enum BiomeBasedClimateModel implements ClimateModel
{
    INSTANCE;

    public static final StreamCodec<ByteBuf, BiomeBasedClimateModel> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public ClimateModelType<?> type()
    {
        return ClimateModels.BIOME_BASED.get();
    }

    @Override
    public float getTemperature(LevelReader level, BlockPos pos)

    public float getAverageTemperature(LevelReader level, BlockPos pos)
    {
        return Climate.fromVanilla(((BiomeAccessor) (Object) level.getBiome(pos).value()).invoke$getTemperature(pos));
    }

    @Override
    public float getRainfall(LevelReader level, BlockPos pos)
    {
        return level.getBiome(pos).value().getPrecipitationAt(pos) != Biome.Precipitation.NONE ? 300f : 0f;
    }

    @Override
    public float getRainVariance(LevelReader level, BlockPos pos)
    {
        return 0;
    }

    @Override
    public float getBaseGroundwater(LevelReader level, BlockPos pos)
    {
        return 0;
    }

    @Override
    public float getGroundwater(LevelReader level, BlockPos pos)
    {
        return getRainfall(level, pos);
    }


    @Override
    public float getMonthlyRainfall(LevelReader level, BlockPos pos, float fractionOfYear)
    {
        return getRainfall(level, pos);
    }

    @Override
    public float getMonthlyGroundwater(LevelReader level, BlockPos pos, float fractionOfYear)
    {
        return getGroundwater(level, pos);
    }
}
