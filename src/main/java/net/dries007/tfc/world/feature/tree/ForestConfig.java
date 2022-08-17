/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.chunkdata.ForestType;

public record ForestConfig(HolderSet<ConfiguredFeature<?, ?>> entries, Map<ForestType, Type> typeMap, boolean useWeirdness) implements FeatureConfiguration
{
    public static final Codec<ForestConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ExtraCodecs.nonEmptyHolderSet(ConfiguredFeature.LIST_CODEC).fieldOf("entries").forGetter(c -> c.entries),
        Codec.unboundedMap(ForestType.CODEC, Type.CODEC).fieldOf("types").forGetter(c -> c.typeMap),
        Codec.BOOL.fieldOf("use_weirdness").orElse(true).forGetter(c -> c.useWeirdness)
    ).apply(instance, ForestConfig::new));

    public record Entry(float minRainfall, float maxRainfall, float minAverageTemp, float maxAverageTemp, Optional<BlockState> bushLog, Optional<BlockState> bushLeaves, Optional<BlockState> fallenLog, Optional<IWeighted<BlockState>> groundcover, Holder<ConfiguredFeature<?, ?>> treeFeature, Holder<ConfiguredFeature<?, ?>> deadFeature, Optional<Holder<ConfiguredFeature<?, ?>>> oldGrowthFeature, int oldGrowthChance, int spoilerOldGrowthChance, int fallenChance, int deadChance) implements FeatureConfiguration
    {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("min_rain").forGetter(c -> c.minRainfall),
            Codec.FLOAT.fieldOf("max_rain").forGetter(c -> c.maxRainfall),
            Codec.FLOAT.fieldOf("min_temp").forGetter(c -> c.minAverageTemp),
            Codec.FLOAT.fieldOf("max_temp").forGetter(c -> c.maxAverageTemp),
            Codecs.BLOCK_STATE.optionalFieldOf("bush_log").forGetter(c -> c.bushLog),
            Codecs.BLOCK_STATE.optionalFieldOf("bush_leaves").forGetter(c -> c.bushLeaves),
            Codecs.BLOCK_STATE.optionalFieldOf("fallen_log").forGetter(c -> c.fallenLog),
            Codecs.weightedCodec(Codecs.BLOCK_STATE, "block").optionalFieldOf("groundcover").forGetter(c -> c.groundcover),
            ConfiguredFeature.CODEC.fieldOf("normal_tree").forGetter(c -> c.treeFeature),
            ConfiguredFeature.CODEC.fieldOf("dead_tree").forGetter(c -> c.deadFeature),
            ConfiguredFeature.CODEC.optionalFieldOf("old_growth_tree").forGetter(c -> c.oldGrowthFeature),
            Codec.INT.fieldOf("old_growth_chance").orElse(6).forGetter(c -> c.oldGrowthChance),
            Codec.INT.fieldOf("spoiler_old_growth_chance").orElse(200).forGetter(c -> c.spoilerOldGrowthChance),
            Codec.INT.fieldOf("fallen_tree_chance").orElse(14).forGetter(c -> c.fallenChance),
            Codec.INT.fieldOf("dead_chance").orElse(75).forGetter(c -> c.deadChance)
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
            return treeFeature.value();
        }

        public ConfiguredFeature<?, ?> getDeadFeature()
        {
            return deadFeature.value();
        }


        public ConfiguredFeature<?, ?> getOldGrowthFeature()
        {
            return oldGrowthFeature.orElse(treeFeature).value();
        }
    }

    public record Type(IntProvider treeCount, IntProvider groundcoverCount, float perChunkChance, Optional<IntProvider> bushCount, boolean hasSpoilers, boolean allowOldGrowth)
    {
        public static final Codec<Type> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IntProvider.CODEC.fieldOf("tree_count").orElse(UniformInt.of(0, 0)).forGetter(c -> c.treeCount),
            IntProvider.CODEC.fieldOf("groundcover_count").orElse(UniformInt.of(0, 0)).forGetter(c -> c.groundcoverCount),
            Codec.FLOAT.fieldOf("per_chunk_chance").orElse(1f).forGetter(c -> c.perChunkChance),
            IntProvider.CODEC.optionalFieldOf("bush_count").forGetter(c -> c.bushCount),
            Codec.BOOL.fieldOf("has_spoiler_old_growth").orElse(false).forGetter(c -> c.hasSpoilers),
            Codec.BOOL.fieldOf("allows_old_growth").orElse(false).forGetter(c -> c.allowOldGrowth)
        ).apply(instance, Type::new));

        public int sampleBushCount(Random random, Optional<IntProvider> count, int treeCount, float density)
        {
            return count.map(sampler -> sampler.sample(random)).orElse((int) (treeCount * density));
        }
    }
}