/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PlantsConfig implements IFeatureConfig
{
    public static final Codec<PlantsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        PlantsConfig.Entry.CODEC.listOf().fieldOf("entries").forGetter(c -> c.entries)
    ).apply(instance, PlantsConfig::new));

    private final List<PlantsConfig.Entry> entries;

    public PlantsConfig(List<PlantsConfig.Entry> entries)
    {
        this.entries = entries;
    }

    public List<PlantsConfig.Entry> getEntries()
    {
        return entries;
    }

    public static class Entry
    {
        public static final Codec<PlantsConfig.Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("min_rain").forGetter(c -> c.minRainfall),
            Codec.FLOAT.fieldOf("max_rain").forGetter(c -> c.maxRainfall),
            Codec.FLOAT.fieldOf("min_temp").forGetter(c -> c.minAverageTemp),
            Codec.FLOAT.fieldOf("max_temp").forGetter(c -> c.maxAverageTemp),
            ConfiguredFeature.CODEC.fieldOf("feature").forGetter(c -> c.feature)
        ).apply(instance, PlantsConfig.Entry::new));

        private final float minRainfall;
        private final float maxRainfall;
        private final float minAverageTemp;
        private final float maxAverageTemp;
        private final Supplier<ConfiguredFeature<?, ?>> feature;

        public Entry(float minRainfall, float maxRainfall, float minAverageTemp, float maxAverageTemp, Supplier<ConfiguredFeature<?, ?>> feature)
        {
            this.minRainfall = minRainfall;
            this.maxRainfall = maxRainfall;
            this.minAverageTemp = minAverageTemp;
            this.maxAverageTemp = maxAverageTemp;
            this.feature = feature;
        }

        public boolean isValid(float rainfall, float temperature)
        {
            return rainfall > minRainfall && rainfall < maxRainfall && temperature > minAverageTemp && temperature < maxAverageTemp;
        }

        public ConfiguredFeature<?, ?> getFeature()
        {
            return feature.get();
        }
    }
}