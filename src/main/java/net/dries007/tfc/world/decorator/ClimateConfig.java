/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.Locale;
import java.util.Random;

import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;

public class ClimateConfig implements DecoratorConfiguration
{
    public static final Codec<ClimateConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.optionalFieldOf("min_temperature", -Float.MAX_VALUE).forGetter(c -> c.minTemp),
        Codec.FLOAT.optionalFieldOf("max_temperature", Float.MAX_VALUE).forGetter(c -> c.maxTemp),
        TemperatureType.CODEC.optionalFieldOf("temperature_type", TemperatureType.AVERAGE).forGetter(c -> c.tempType),
        Codec.FLOAT.optionalFieldOf("min_rainfall", -Float.MAX_VALUE).forGetter(c -> c.minRainfall),
        Codec.FLOAT.optionalFieldOf("max_rainfall", Float.MAX_VALUE).forGetter(c -> c.maxRainfall),
        ForestType.CODEC.optionalFieldOf("min_forest", ForestType.NONE).forGetter(c -> c.minForest),
        ForestType.CODEC.optionalFieldOf("max_forest", ForestType.OLD_GROWTH).forGetter(c -> c.maxForest),
        Codec.BOOL.optionalFieldOf("fuzzy", false).forGetter(c -> c.fuzzy)
    ).apply(instance, ClimateConfig::new));

    private final float minTemp;
    private final float maxTemp;
    private final TemperatureType tempType;
    private final float targetTemp;
    private final float minRainfall;
    private final float maxRainfall;
    private final float targetRainfall;
    private final ForestType minForest;
    private final ForestType maxForest;
    private final boolean fuzzy;

    public ClimateConfig(float minTemp, float maxTemp, TemperatureType tempType, float minRainfall, float maxRainfall, ForestType minForest, ForestType maxForest, boolean fuzzy)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.tempType = tempType;
        this.targetTemp = (minTemp + maxTemp) / 2f;
        this.minRainfall = minRainfall;
        this.maxRainfall = maxRainfall;
        this.targetRainfall = (minRainfall + maxRainfall) / 2f;
        this.minForest = minForest;
        this.maxForest = maxForest;
        this.fuzzy = fuzzy;
    }

    public boolean isValid(ChunkData data, BlockPos pos, Random random)
    {
        final float temperature = getTemperature(data, pos);
        final float rainfall = data.getRainfall(pos);
        final ForestType forestType = data.getForestType();

        if (minTemp <= temperature && temperature <= maxTemp && minRainfall <= rainfall && rainfall <= maxRainfall && minForest.ordinal() <= forestType.ordinal() && forestType.ordinal() <= maxForest.ordinal())
        {
            if (fuzzy)
            {
                float normTempDelta = Math.abs(temperature - targetTemp) / (maxTemp - minTemp);
                float normRainfallDelta = Math.abs(rainfall - targetRainfall) / (maxRainfall - minRainfall);
                return random.nextFloat() * random.nextFloat() > Math.max(normTempDelta, normRainfallDelta);
            }
            return true;
        }
        return false;
    }

    private float getTemperature(ChunkData data, BlockPos pos)
    {
        return switch (tempType)
            {
                case AVERAGE -> data.getAverageTemp(pos);
                case MONTHLY -> Climate.calculateMonthlyAverageTemperature(pos.getZ(), pos.getY(), data.getAverageTemp(pos), Calendars.SERVER.getCalendarMonthOfYear().getTemperatureModifier());
                case ACTUAL -> Climate.calculateTemperature(pos, data.getAverageTemp(pos), Calendars.SERVER);
            };
    }

    public enum TemperatureType implements StringRepresentable
    {
        AVERAGE,
        MONTHLY,
        ACTUAL;

        public static final Codec<TemperatureType> CODEC = StringRepresentable.fromEnum(TemperatureType::values, name -> TemperatureType.valueOf(name.toUpperCase()));

        private final String serializedName;

        TemperatureType()
        {
            serializedName = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }
}
