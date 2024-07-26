/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.placement.ClimatePlacement;

public record ForestConfig(HolderSet<ConfiguredFeature<?, ?>> entries) implements FeatureConfiguration
{
    public static final Codec<ForestConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ExtraCodecs.nonEmptyHolderSet(ConfiguredFeature.LIST_CODEC).fieldOf("entries").forGetter(c -> c.entries)
    ).apply(instance, ForestConfig::new));

    public record Entry(ClimatePlacement climate, Optional<BlockState> bushLog, Optional<BlockState> bushLeaves, Optional<BlockState> fallenLog, Optional<BlockState> fallenLeaves, Optional<IWeighted<BlockState>> groundcover, Holder<ConfiguredFeature<?, ?>> treeFeature, Holder<ConfiguredFeature<?, ?>> deadFeature, Optional<Holder<ConfiguredFeature<?, ?>>> oldGrowthFeature, Optional<Holder<ConfiguredFeature<?, ?>>> krummholz, int oldGrowthChance, int spoilerOldGrowthChance, int fallenChance, int deadChance, boolean floating) implements FeatureConfiguration
    {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> {
            Codec<IWeighted<BlockState>> codec = Codecs.weightedCodec(Codecs.BLOCK_STATE, "block");
            return instance.group(
                ClimatePlacement.CODEC.fieldOf("climate").forGetter(c -> c.climate),
                Codecs.BLOCK_STATE.optionalFieldOf("bush_log").forGetter(c -> c.bushLog),
                Codecs.BLOCK_STATE.optionalFieldOf("bush_leaves").forGetter(c -> c.bushLeaves),
                Codecs.BLOCK_STATE.optionalFieldOf("fallen_log").forGetter(c -> c.fallenLog),
                Codecs.BLOCK_STATE.optionalFieldOf("fallen_leaves").forGetter(c -> c.fallenLeaves),
                codec.optionalFieldOf("groundcover").forGetter(c -> c.groundcover),
                ConfiguredFeature.CODEC.fieldOf("normal_tree").forGetter(c -> c.treeFeature),
                ConfiguredFeature.CODEC.fieldOf("dead_tree").forGetter(c -> c.deadFeature),
                ConfiguredFeature.CODEC.optionalFieldOf("old_growth_tree").forGetter(c -> c.oldGrowthFeature),
                ConfiguredFeature.CODEC.optionalFieldOf("krummholz").forGetter(c -> c.oldGrowthFeature),
                Codec.INT.optionalFieldOf("old_growth_chance", 6).forGetter(c -> c.oldGrowthChance),
                Codec.INT.optionalFieldOf("spoiler_old_growth_chance", 200).forGetter(c -> c.spoilerOldGrowthChance),
                Codec.INT.optionalFieldOf("fallen_tree_chance", 14).forGetter(c -> c.fallenChance),
                Codec.INT.optionalFieldOf("dead_chance", 75).forGetter(c -> c.deadChance),
                Codec.BOOL.optionalFieldOf("floating", false).forGetter(c -> c.floating)
            ).apply(instance, Entry::new);
        });

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

}