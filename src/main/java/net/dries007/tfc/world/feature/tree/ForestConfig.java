/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public record ForestConfig(List<Entry> entries) implements FeatureConfiguration
{
    public static final Codec<ForestConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Entry.CODEC.listOf().fieldOf("entries").forGetter(c -> c.entries)
    ).apply(instance, ForestConfig::new));

    public record Entry(float minRainfall, float maxRainfall, float minAverageTemp, float maxAverageTemp, BlockState log, BlockState leaves, BlockState twig, BlockState fallenLeaves, Supplier<ConfiguredFeature<?, ?>> treeFeature, Optional<Supplier<ConfiguredFeature<?, ?>>> oldGrowthFeature)
    {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("min_rain").forGetter(c -> c.minRainfall),
            Codec.FLOAT.fieldOf("max_rain").forGetter(c -> c.maxRainfall),
            Codec.FLOAT.fieldOf("min_temp").forGetter(c -> c.minAverageTemp),
            Codec.FLOAT.fieldOf("max_temp").forGetter(c -> c.maxAverageTemp),
            Codecs.BLOCK_STATE.fieldOf("log").forGetter(c -> c.log),
            Codecs.BLOCK_STATE.fieldOf("leaves").forGetter(c -> c.leaves),
            Codecs.BLOCK_STATE.fieldOf("twig").forGetter(c -> c.twig),
            Codecs.BLOCK_STATE.fieldOf("fallen_leaves").forGetter(c -> c.fallenLeaves),
            ConfiguredFeature.CODEC.fieldOf("normal_tree").forGetter(c -> c.treeFeature),
            ConfiguredFeature.CODEC.optionalFieldOf("old_growth_tree").forGetter(c -> c.oldGrowthFeature)
        ).apply(instance, Entry::new));

        public boolean isValid(float temperature, float rainfall)
        {
            return rainfall >= minRainfall && rainfall <= maxRainfall && temperature >= minAverageTemp && temperature <= maxAverageTemp;
        }

        public float distanceFromMean(float temperature, float rainfall)
        {
            return (rainfall + temperature - getAverageTemp() - getAverageRain()) / 2;
        }

        public float getAverageTemp()
        {
            return (maxAverageTemp - minAverageTemp) / 2;
        }

        public float getAverageRain()
        {
            return (maxRainfall - minRainfall) / 2;
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