/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ForestFeatureConfig implements IFeatureConfig
{
    @Nullable
    public static <T> ForestFeatureConfig deserialize(Dynamic<T> config)
    {
        return null;
    }

    private final List<Entry> entries;

    public ForestFeatureConfig(List<Entry> entries)
    {
        this.entries = entries;
    }

    public List<Entry> getEntries()
    {
        return entries;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops)
    {
        return new Dynamic<>(ops, ops.emptyMap());
    }

    public static class Entry
    {
        private final float minRainfall;
        private final float maxRainfall;
        private final float minAverageTemp;
        private final float maxAverageTemp;
        private final ConfiguredFeature<?, ?> treeFeature;
        private final ConfiguredFeature<?, ?> oldGrowthFeature;

        public Entry(float minRainfall, float maxRainfall, float minAverageTemp, float maxAverageTemp, ConfiguredFeature<?, ?> treeFeature, ConfiguredFeature<?, ?> oldGrowthFeature)
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
            return treeFeature;
        }

        public ConfiguredFeature<?, ?> getOldGrowthFeature()
        {
            return oldGrowthFeature;
        }
    }
}
