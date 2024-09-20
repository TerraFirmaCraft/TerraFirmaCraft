/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import net.dries007.tfc.world.Codecs;

public record TFCBambooConfig(float probability, BlockState state) implements FeatureConfiguration
{
    public static final Codec<TFCBambooConfig> CODEC = RecordCodecBuilder.create((instance) ->
        instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(c -> c.probability),
            Codecs.BLOCK_STATE.fieldOf("state").forGetter(c -> c.state)
        ).apply(instance, TFCBambooConfig::new));
}
