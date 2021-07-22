/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class HotSpringConfig implements IFeatureConfig
{
    public static final Codec<HotSpringConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.LENIENT_BLOCKSTATE.optionalFieldOf("wall_state").forGetter(c -> c.wallState),
        Codecs.LENIENT_BLOCKSTATE.fieldOf("fluid_state").forGetter(c -> c.fluidState),
        Codec.intRange(1, 16).optionalFieldOf("radius", 14).forGetter(c -> c.radius),
        FissureConfig.Decoration.CODEC.optionalFieldOf("decoration").forGetter(c -> c.decoration)
    ).apply(instance, HotSpringConfig::new));

    public final Optional<BlockState> wallState;
    public final BlockState fluidState;
    public final int radius;
    public final Optional<FissureConfig.Decoration> decoration;

    public HotSpringConfig(Optional<BlockState> wallState, BlockState fluidState, int radius, Optional<FissureConfig.Decoration> decoration)
    {
        this.wallState = wallState;
        this.fluidState = fluidState;
        this.radius = radius;
        this.decoration = decoration;
    }
}
