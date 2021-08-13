/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TFCCarvingMaskConfig(int minY, int maxY, float probability, GenerationStep.Carving step) implements DecoratorConfiguration
{
    // todo: change to use vertical anchors
    public static final Codec<TFCCarvingMaskConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("min_y", Integer.MIN_VALUE).forGetter(c -> c.minY),
        Codec.INT.optionalFieldOf("max_y", Integer.MAX_VALUE).forGetter(c -> c.maxY),
        Codec.floatRange(0, 1).fieldOf("probability").forGetter(c -> c.probability),
        GenerationStep.Carving.CODEC.fieldOf("step").forGetter(c -> c.step)
    ).apply(instance, TFCCarvingMaskConfig::new));
}
