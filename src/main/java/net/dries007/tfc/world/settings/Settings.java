/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * TerraFirmaCraft world generation settings.
 *
 * @param temperatureScale Distance between temperature 'poles'. Default 20km
 * @param temperatureConstant If {@code temperatureScale} is equal to zero, then the constant between [-1, 1] temperature input value.
 * @param rainfallScale Distance between rainfall 'poles'. Default 20km
 * @param rainfallConstant If {@code rainfallScale} is equal to zero, then the constant between [-1, 1] rainfall input value.
 */
public record Settings(
    boolean flatBedrock,
    int spawnDistance,
    int spawnCenterX,
    int spawnCenterZ,
    int temperatureScale,
    float temperatureConstant,
    int rainfallScale,
    float rainfallConstant,
    RockLayerSettings rockLayerSettings,
    float continentalness,
    float grassDensity,
    boolean finiteContinents)
{
    public static final MapCodec<Settings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.BOOL.fieldOf("flat_bedrock").forGetter(c -> c.flatBedrock),
        Codec.INT.fieldOf("spawn_distance").forGetter(c -> c.spawnDistance),
        Codec.INT.fieldOf("spawn_center_x").forGetter(c -> c.spawnCenterX),
        Codec.INT.fieldOf("spawn_center_z").forGetter(c -> c.spawnCenterZ),
        Codec.INT.fieldOf("temperature_scale").forGetter(c -> c.temperatureScale),
        Codec.FLOAT.optionalFieldOf("temperature_constant", 0f).forGetter(c -> c.temperatureConstant),
        Codec.INT.fieldOf("rainfall_scale").forGetter(c -> c.rainfallScale),
        Codec.FLOAT.optionalFieldOf("rainfall_constant", 0f).forGetter(c -> c.rainfallConstant),
        RockLayerSettings.CODEC.fieldOf("rock_layer_settings").forGetter(c -> c.rockLayerSettings),
        Codec.FLOAT.fieldOf("continentalness").forGetter(c -> c.continentalness),
        Codec.FLOAT.optionalFieldOf("grassDensity", 0.5f).forGetter(c -> c.grassDensity),
        Codec.BOOL.optionalFieldOf("finiteContinents", false).forGetter(c -> c.finiteContinents)
    ).apply(instance, Settings::new));
}
