/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MultipleConfig(List<Supplier<ConfiguredFeature<?, ?>>> features) implements FeatureConfiguration
{
    public static final Codec<MultipleConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ConfiguredFeature.CODEC.listOf().fieldOf("features").forGetter(c -> c.features)
    ).apply(instance, MultipleConfig::new));

}
