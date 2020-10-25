package net.dries007.tfc.world.decorator;

import net.minecraft.world.gen.placement.IPlacementConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ClimateConfig implements IPlacementConfig
{
    public static final Codec<ClimateConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.optionalFieldOf("min_temperature", Float.MIN_VALUE).forGetter(c -> c.minTemp),
        Codec.FLOAT.optionalFieldOf("max_temperature", Float.MAX_VALUE).forGetter(c -> c.maxTemp),
        Codec.FLOAT.optionalFieldOf("min_rainfall", Float.MIN_VALUE).forGetter(c -> c.minRainfall),
        Codec.FLOAT.optionalFieldOf("max_rainfall", Float.MAX_VALUE).forGetter(c -> c.maxRainfall)
    ).apply(instance, ClimateConfig::new));

    private final float minTemp;
    private final float maxTemp;
    private final float minRainfall;
    private final float maxRainfall;

    public ClimateConfig(float minTemp, float maxTemp, float minRainfall, float maxRainfall)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRainfall = minRainfall;
        this.maxRainfall = maxRainfall;
    }

    public boolean isValid(float temperature, float rainfall)
    {
        return minTemp <= temperature && temperature <= maxTemp && minRainfall <= rainfall && rainfall <= maxRainfall;
    }
}
