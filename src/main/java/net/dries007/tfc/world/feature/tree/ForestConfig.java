/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

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
            Codecs.LENIENT_BLOCKSTATE.fieldOf("log").forGetter(c -> c.log),
            Codecs.LENIENT_BLOCKSTATE.fieldOf("leaves").forGetter(c -> c.leaves),
            Codecs.LENIENT_BLOCKSTATE.fieldOf("twig").forGetter(c -> c.twig),
            Codecs.LENIENT_BLOCKSTATE.fieldOf("fallen_leaves").forGetter(c -> c.fallen_leaves),
            ConfiguredFeature.field_236264_b_.fieldOf("normal_tree").forGetter(c -> c.treeFeature),
            ConfiguredFeature.field_236264_b_.optionalFieldOf("old_growth_tree").forGetter(c -> c.oldGrowthFeature)
        ).apply(instance, Entry::new));

        private final float minRainfall;
        private final float maxRainfall;
        private final float minAverageTemp;
        private final float maxAverageTemp;
        private final BlockState log;
        private final BlockState leaves;
        private final BlockState twig;
        private final BlockState fallen_leaves;
        private final Supplier<ConfiguredFeature<?, ?>> treeFeature;
        private final Optional<Supplier<ConfiguredFeature<?, ?>>> oldGrowthFeature;

        public Entry(float minRainfall, float maxRainfall, float minAverageTemp, float maxAverageTemp, BlockState log, BlockState leaves, BlockState twig, BlockState fallen_leaves, Supplier<ConfiguredFeature<?, ?>> treeFeature, Optional<Supplier<ConfiguredFeature<?, ?>>> oldGrowthFeature)
        {
            this.minRainfall = minRainfall;
            this.maxRainfall = maxRainfall;
            this.minAverageTemp = minAverageTemp;
            this.maxAverageTemp = maxAverageTemp;
            this.log = log;
            this.leaves = leaves;
            this.twig = twig;
            this.fallen_leaves = fallen_leaves;
            this.treeFeature = treeFeature;
            this.oldGrowthFeature = oldGrowthFeature;
        }

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

        public BlockState getLog()
        {
            return log;
        }

        public BlockState getLeaves()
        {
            return leaves;
        }

        public BlockState getTwig()
        {
            return twig;
        }

        public BlockState getFallenLeaves()
        {
            return fallen_leaves;
        }
    }
}