/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Map;
import java.util.Optional;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;

public record HotSpringConfig(Optional<BlockState> wallState, BlockState fluidState, int radius, Optional<FissureConfig.Decoration> decoration, boolean allowUnderwater, Optional<Map<Block, IWeighted<BlockState>>> replacesOnFluidContact) implements FeatureConfiguration
{
    public static final Codec<HotSpringConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.BLOCK_STATE.optionalFieldOf("wall_state").forGetter(c -> c.wallState),
        Codecs.BLOCK_STATE.fieldOf("fluid_state").forGetter(c -> c.fluidState),
        Codec.intRange(1, 16).optionalFieldOf("radius", 14).forGetter(c -> c.radius),
        FissureConfig.Decoration.CODEC.optionalFieldOf("decoration").forGetter(c -> c.decoration),
        Codec.BOOL.optionalFieldOf("allow_underwater", false).forGetter(c -> c.allowUnderwater),
        Codecs.BLOCK_TO_WEIGHTED_BLOCKSTATE.optionalFieldOf("replaces_on_fluid_contact").forGetter(c -> c.replacesOnFluidContact)
    ).apply(instance, HotSpringConfig::new));

}
