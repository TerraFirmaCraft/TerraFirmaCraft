/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ForestConfig implements IFeatureConfig
{
    public static final Codec<ForestConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Entry.CODEC.listOf().fieldOf("entries").forGetter(c -> c.entries)
    ).apply(instance, ForestConfig::new));

    private final List<Entry> entries;

    public ForestConfig(List<Entry> entries)
    {
        this.entries = entries;
    }

    public List<Entry> getEntries()
    {
        return entries;
    }

    public static class Entry
    {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("min_rain").forGetter(c -> c.minRainfall),
            Codec.FLOAT.fieldOf("max_rain").forGetter(c -> c.maxRainfall),
            Codec.FLOAT.fieldOf("min_temp").forGetter(c -> c.minAverageTemp),
            Codec.FLOAT.fieldOf("max_temp").forGetter(c -> c.maxAverageTemp),
            ConfiguredFeature.CODEC.fieldOf("tree_feature").forGetter(c -> c.treeFeature),
            ConfiguredFeature.CODEC.optionalFieldOf("old_growth_feature").forGetter(c -> c.oldGrowthFeature)
        ).apply(instance, Entry::new));

        private final float minRainfall;
        private final float maxRainfall;
        private final float minAverageTemp;
        private final float maxAverageTemp;
        private final Supplier<ConfiguredFeature<?, ?>> treeFeature;
        private final Optional<Supplier<ConfiguredFeature<?, ?>>> oldGrowthFeature;

        public Entry(float minRainfall, float maxRainfall, float minAverageTemp, float maxAverageTemp, Supplier<ConfiguredFeature<?, ?>> treeFeature, Optional<Supplier<ConfiguredFeature<?, ?>>> oldGrowthFeature)
        {
            this.minRainfall = minRainfall;
            this.maxRainfall = maxRainfall;
            this.minAverageTemp = minAverageTemp;
            this.maxAverageTemp = maxAverageTemp;
            this.treeFeature = treeFeature;
            this.oldGrowthFeature = oldGrowthFeature;
        }

        public boolean isValid(float rainfall, float temperature)
        {
            return rainfall > minRainfall && rainfall < maxRainfall && temperature > minAverageTemp && temperature < maxAverageTemp;
        }

        public ConfiguredFeature<?, ?> getFeature()
        {
            return treeFeature.get();
        }

        public ConfiguredFeature<?, ?> getOldGrowthFeature()
        {
            return oldGrowthFeature.orElse(treeFeature).get();
        }
    }
}