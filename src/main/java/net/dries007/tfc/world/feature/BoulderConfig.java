/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.List;
import java.util.Map;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;
import org.jetbrains.annotations.Nullable;

public record BoulderConfig(Map<Block, List<BlockState>> states) implements FeatureConfiguration
{
    public static final Codec<BoulderConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.mapListCodec(Codecs.recordPairCodec(
            Codecs.BLOCK, "rock",
            Codecs.BLOCK_STATE.listOf(), "blocks"
        )).fieldOf("states").forGetter(c -> c.states)
    ).apply(instance, BoulderConfig::new));

    @Nullable
    public List<BlockState> getStates(Block block)
    {
        return states.get(block);
    }
}