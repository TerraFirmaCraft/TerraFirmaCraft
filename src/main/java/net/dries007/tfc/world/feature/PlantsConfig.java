/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PlantsConfig implements IFeatureConfig
{
    public static final Codec<PlantsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        PlantsConfig.Entry.CODEC.listOf().fieldOf("entries").forGetter(c -> c.entries),
        Codec.INT.fieldOf("standard").forGetter(c -> c.getPlantsPerChunk(PlantType.STANDARD)),
        Codec.INT.fieldOf("floating").forGetter(c -> c.getPlantsPerChunk(PlantType.FLOATING)),
        Codec.INT.fieldOf("short_grass").forGetter(c -> c.getPlantsPerChunk(PlantType.SHORT_GRASS)),
        Codec.INT.fieldOf("tall_grass").forGetter(c -> c.getPlantsPerChunk(PlantType.TALL_GRASS)),
        Codec.INT.fieldOf("creeping").forGetter(c -> c.getPlantsPerChunk(PlantType.CREEPING)),
        Codec.INT.fieldOf("hanging").forGetter(c -> c.getPlantsPerChunk(PlantType.HANGING)),
        Codec.INT.fieldOf("epiphyte").forGetter(c -> c.getPlantsPerChunk(PlantType.EPIPHYTE))
    ).apply(instance, PlantsConfig::new));

    private final List<PlantsConfig.Entry> entries;
    private final Map<PlantType, Integer> plantsPerChunk;

    public PlantsConfig(List<PlantsConfig.Entry> entries, int standard, int floating, int shortGrass, int tallGrass, int creeping, int hanging, int epiphyte)
    {
        this.entries = entries;
        this.plantsPerChunk = new EnumMap<>(PlantType.class);
        plantsPerChunk.put(PlantType.STANDARD, standard);
        plantsPerChunk.put(PlantType.FLOATING, floating);
        plantsPerChunk.put(PlantType.SHORT_GRASS, shortGrass);
        plantsPerChunk.put(PlantType.TALL_GRASS, tallGrass);
        plantsPerChunk.put(PlantType.CREEPING, creeping);
        plantsPerChunk.put(PlantType.HANGING, hanging);
        plantsPerChunk.put(PlantType.EPIPHYTE, epiphyte);
    }

    public List<PlantsConfig.Entry> getEntries()
    {
        return entries;
    }

    public int getPlantsPerChunk(PlantType plantType)
    {
        return plantsPerChunk.get(plantType);
    }

    /**
     * Plant type, used to determine how many of said type is spawned per chunk
     * This should not be confused with {@link net.dries007.tfc.common.blocks.plant.Plant.PlantType}
     */
    public enum PlantType
    {
        STANDARD, // Flowers
        FLOATING, // Water
        SHORT_GRASS, // Lots of grass
        TALL_GRASS, // Some tall ones
        CREEPING,
        HANGING, // Hang on
        EPIPHYTE // Trees?
    }

    public static class Entry
    {
        public static final Codec<PlantsConfig.Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("min_rain").forGetter(c -> c.minRainfall),
            Codec.FLOAT.fieldOf("max_rain").forGetter(c -> c.maxRainfall),
            Codec.FLOAT.fieldOf("min_temp").forGetter(c -> c.minAverageTemp),
            Codec.FLOAT.fieldOf("max_temp").forGetter(c -> c.maxAverageTemp),
            Codec.STRING.fieldOf("type").forGetter(c -> c.type.name().toLowerCase()),
            Codec.BOOL.fieldOf("clay_indicator").forGetter(c -> c.clay_indicator),
            ConfiguredFeature.CODEC.fieldOf("feature").forGetter(c -> c.feature)
        ).apply(instance, PlantsConfig.Entry::new));

        private final float minRainfall;
        private final float maxRainfall;
        private final float minAverageTemp;
        private final float maxAverageTemp;
        private final PlantType type;
        private final boolean clay_indicator;
        private final Supplier<ConfiguredFeature<?, ?>> feature;

        public Entry(float minRainfall, float maxRainfall, float minAverageTemp, float maxAverageTemp, PlantType type, boolean clay_indicator, Supplier<ConfiguredFeature<?, ?>> feature)
        {
            this.minRainfall = minRainfall;
            this.maxRainfall = maxRainfall;
            this.minAverageTemp = minAverageTemp;
            this.maxAverageTemp = maxAverageTemp;
            this.clay_indicator = clay_indicator;
            this.feature = feature;
            this.type = type;
        }

        public Entry(float minRainfall, float maxRainfall, float minAverageTemp, float maxAverageTemp, String type, boolean clay_indicator, Supplier<ConfiguredFeature<?, ?>> feature)
        {
            this.minRainfall = minRainfall;
            this.maxRainfall = maxRainfall;
            this.minAverageTemp = minAverageTemp;
            this.maxAverageTemp = maxAverageTemp;
            this.clay_indicator = clay_indicator;
            this.feature = feature;
            this.type = PlantType.valueOf(type.toUpperCase());
        }

        public boolean isValid(float rainfall, float temperature)
        {
            return rainfall > minRainfall && rainfall < maxRainfall && temperature > minAverageTemp && temperature < maxAverageTemp;
        }

        public PlantType getType()
        {
            return type;
        }

        public boolean isClayIndicator()
        {
            return clay_indicator;
        }

        public ConfiguredFeature<?, ?> getFeature()
        {
            return feature.get();
        }
    }
}