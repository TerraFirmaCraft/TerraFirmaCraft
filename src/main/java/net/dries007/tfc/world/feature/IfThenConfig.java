/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;


import java.util.stream.Stream;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record IfThenConfig(Holder<PlacedFeature> ifFeature, Holder<PlacedFeature> thenFeature) implements FeatureConfiguration
{
    public static final Codec<IfThenConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        PlacedFeature.CODEC.fieldOf("if").forGetter(IfThenConfig::ifFeature),
        PlacedFeature.CODEC.fieldOf("then").forGetter(IfThenConfig::thenFeature)
    ).apply(instance, IfThenConfig::new));

    public Stream<ConfiguredFeature<?, ?>> getFeatures()
    {
        return Stream.concat(this.ifFeature.value().getFeatures(), this.ifFeature.value().getFeatures());
    }
}
