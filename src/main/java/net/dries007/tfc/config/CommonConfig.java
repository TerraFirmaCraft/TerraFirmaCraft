package net.dries007.tfc.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Common Config
 * - not synced, saved per instance
 * - use for things that are only important server side (i.e. world gen), or make less sense to
 */
public class CommonConfig
{
    // World Generation
    public final ForgeConfigSpec.IntValue sandGrassRainfallCutoff;
    public final ForgeConfigSpec.IntValue sandGrassRainfallSpread;
    public final ForgeConfigSpec.IntValue sandGravelTemperatureCutoff;
    public final ForgeConfigSpec.DoubleValue sandGravelTemperatureSpread;
    public final ForgeConfigSpec.BooleanValue flatBedrock;
    public final ForgeConfigSpec.IntValue islandFrequency;
    public final ForgeConfigSpec.IntValue biomeZoomLevel;
    public final ForgeConfigSpec.IntValue rockLayerHeight;
    public final ForgeConfigSpec.IntValue rockLayerSpread;
    public final ForgeConfigSpec.IntValue seaLevel;
    public final ForgeConfigSpec.IntValue worleyCaveHeightFade;
    public final ForgeConfigSpec.DoubleValue worleyCaveBaseNoiseCutoff;
    public final ForgeConfigSpec.DoubleValue worleyCaveWorleyNoiseCutoff;

    CommonConfig(ForgeConfigSpec.Builder builder)
    {
        builder.push("world_generation");

        sandGrassRainfallCutoff = builder.defineInRange("sand_grass_rainfall_cutoff", 125, 0, 500);
        sandGrassRainfallSpread = builder.defineInRange("sand_grass_rainfall_spread", 2, 0, 500);

        sandGravelTemperatureCutoff = builder.defineInRange("sand_gravel_temperature_cutoff", 10, -40, 40);
        sandGravelTemperatureSpread = builder.defineInRange("sand_gravel_temperature_spread", 0.3, 0, 80);

        flatBedrock = builder.define("flat_bedrock", false);

        islandFrequency = builder.defineInRange("island_frequency", 6, 1, 100);
        biomeZoomLevel = builder.defineInRange("biome_zoom_level", 4, 1, 10);

        rockLayerHeight = builder.defineInRange("rock_layer_height", 50, 0, 256);
        rockLayerSpread = builder.defineInRange("rock_layer_spread", 10, 0, 256);

        seaLevel = builder.defineInRange("sea_level", 96, 0, 256);

        worleyCaveHeightFade = builder.defineInRange("worley_cave_height_fade", 80, 0, 256);
        worleyCaveBaseNoiseCutoff = builder.defineInRange("worley_cave_base_noise_cutoff", 0.3, 0, 1);
        worleyCaveWorleyNoiseCutoff = builder.defineInRange("worley_cave_worley_noise_cutoff", 0.38, 0, 1);

        builder.pop();
    }
}
