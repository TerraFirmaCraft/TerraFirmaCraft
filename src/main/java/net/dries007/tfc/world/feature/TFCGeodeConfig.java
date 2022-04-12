/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public record TFCGeodeConfig(BlockState outer, BlockState middle, SimpleWeightedRandomList<BlockState> inner) implements FeatureConfiguration
{
    public static final Codec<TFCGeodeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.BLOCK_STATE.fieldOf("outer").forGetter(c -> c.outer),
        Codecs.BLOCK_STATE.fieldOf("middle").forGetter(c -> c.middle),
        SimpleWeightedRandomList.wrappedCodec(Codecs.BLOCK_STATE).fieldOf("inner").forGetter(c -> c.inner)
    ).apply(instance, TFCGeodeConfig::new));
}
