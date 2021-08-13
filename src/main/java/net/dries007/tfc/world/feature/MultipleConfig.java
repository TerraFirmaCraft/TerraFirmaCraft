/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MultipleConfig implements IFeatureConfig
{
    public static final Codec<MultipleConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ConfiguredFeature.CODEC.listOf().fieldOf("features").forGetter(c -> c.features)
    ).apply(instance, MultipleConfig::new));

    public final List<Supplier<ConfiguredFeature<?, ?>>> features;

    public MultipleConfig(List<Supplier<ConfiguredFeature<?, ?>>> features)
    {
        this.features = features;
    }
}
