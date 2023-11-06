/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Map;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.placement.ClimatePlacement;

public record ForestConfig(HolderSet<ConfiguredFeature<?, ?>> entries, Map<ForestType, Type> typeMap, boolean useWeirdness) implements FeatureConfiguration
{
    public static final Codec<ForestConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ExtraCodecs.nonEmptyHolderSet(ConfiguredFeature.LIST_CODEC).fieldOf("entries").forGetter(c -> c.entries),
        Codec.unboundedMap(ForestType.CODEC, Type.CODEC).fieldOf("types").forGetter(c -> c.typeMap),
        Codecs.optionalFieldOf(Codec.BOOL, "use_weirdness", true).forGetter(c -> c.useWeirdness)
    ).apply(instance, ForestConfig::new));

    public record Entry(ClimatePlacement climate, Optional<BlockState> bushLog, Optional<BlockState> bushLeaves, Optional<BlockState> fallenLog, Optional<BlockState> fallenLeaves, Optional<IWeighted<BlockState>> groundcover, Holder<ConfiguredFeature<?, ?>> treeFeature, Holder<ConfiguredFeature<?, ?>> deadFeature, Optional<Holder<ConfiguredFeature<?, ?>>> oldGrowthFeature, Optional<Holder<ConfiguredFeature<?, ?>>> krummholz, int oldGrowthChance, int spoilerOldGrowthChance, int fallenChance, int deadChance, boolean floating) implements FeatureConfiguration
    {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ClimatePlacement.PLACEMENT_CODEC.fieldOf("climate").forGetter(c -> c.climate),
            Codecs.optionalFieldOf(Codecs.BLOCK_STATE, "bush_log").forGetter(c -> c.bushLog),
            Codecs.optionalFieldOf(Codecs.BLOCK_STATE, "bush_leaves").forGetter(c -> c.bushLeaves),
            Codecs.optionalFieldOf(Codecs.BLOCK_STATE, "fallen_log").forGetter(c -> c.fallenLog),
            Codecs.optionalFieldOf(Codecs.BLOCK_STATE, "fallen_leaves").forGetter(c -> c.fallenLeaves),
            Codecs.optionalFieldOf(Codecs.weightedCodec(Codecs.BLOCK_STATE, "block"), "groundcover").forGetter(c -> c.groundcover),
            ConfiguredFeature.CODEC.fieldOf("normal_tree").forGetter(c -> c.treeFeature),
            ConfiguredFeature.CODEC.fieldOf("dead_tree").forGetter(c -> c.deadFeature),
            Codecs.optionalFieldOf(ConfiguredFeature.CODEC, "old_growth_tree").forGetter(c -> c.oldGrowthFeature),
            Codecs.optionalFieldOf(ConfiguredFeature.CODEC, "krummholz").forGetter(c -> c.oldGrowthFeature),
            Codecs.optionalFieldOf(Codec.INT, "old_growth_chance", 6).forGetter(c -> c.oldGrowthChance),
            Codecs.optionalFieldOf(Codec.INT, "spoiler_old_growth_chance", 200).forGetter(c -> c.spoilerOldGrowthChance),
            Codecs.optionalFieldOf(Codec.INT, "fallen_tree_chance", 14).forGetter(c -> c.fallenChance),
            Codecs.optionalFieldOf(Codec.INT, "dead_chance", 75).forGetter(c -> c.deadChance),
            Codecs.optionalFieldOf(Codec.BOOL, "floating", false).forGetter(c -> c.floating)
        ).apply(instance, Entry::new));

        public boolean isValid(float temperature, float rainfall)
        {
            return rainfall >= climate.getMinRainfall() && rainfall <= climate.getMaxRainfall() && temperature >= climate.getMinTemp() && temperature <= climate.getMaxTemp();
        }

        public float distanceFromMean(float temperature, float rainfall)
        {
            return (rainfall + temperature - getAverageTemp() - getAverageRain()) / 2;
        }

        public float getAverageTemp()
        {
            return (climate.getMaxTemp() - climate.getMinTemp()) / 2;
        }

        public float getAverageRain()
        {
            return (climate.getMaxRainfall() - climate.getMinRainfall()) / 2;
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

    public record Type(IntProvider treeCount, IntProvider groundcoverCount, float perChunkChance, Optional<IntProvider> bushCount, boolean hasSpoilers, boolean allowOldGrowth, IntProvider leafPileCount)
    {
        public static final Codec<Type> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.optionalFieldOf(IntProvider.CODEC, "tree_count", UniformInt.of(0, 0)).forGetter(c -> c.treeCount),
            Codecs.optionalFieldOf(IntProvider.CODEC, "groundcover_count", UniformInt.of(0, 0)).forGetter(c -> c.groundcoverCount),
            Codecs.optionalFieldOf(Codec.FLOAT, "per_chunk_chance", 1f).forGetter(c -> c.perChunkChance),
            Codecs.optionalFieldOf(IntProvider.CODEC, "bush_count").forGetter(c -> c.bushCount),
            Codecs.optionalFieldOf(Codec.BOOL, "has_spoiler_old_growth", false).forGetter(c -> c.hasSpoilers),
            Codecs.optionalFieldOf(Codec.BOOL, "allows_old_growth", false).forGetter(c -> c.allowOldGrowth),
            Codecs.optionalFieldOf(IntProvider.CODEC, "leaf_pile_count", UniformInt.of(0, 0)).forGetter(c -> c.leafPileCount)
        ).apply(instance, Type::new));

        public int sampleBushCount(RandomSource random, Optional<IntProvider> count, int treeCount, float density)
        {
            return count.map(sampler -> sampler.sample(random)).orElse((int) (treeCount * density));
        }
    }
}