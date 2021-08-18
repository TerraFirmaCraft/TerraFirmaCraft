/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public record TFCCarvingMaskConfig(VerticalAnchor minY, VerticalAnchor maxY, float probability, GenerationStep.Carving step) implements DecoratorConfiguration
{
    public static final Codec<TFCCarvingMaskConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        VerticalAnchor.CODEC.optionalFieldOf("min_y", VerticalAnchor.bottom()).forGetter(c -> c.minY),
        VerticalAnchor.CODEC.optionalFieldOf("max_y", VerticalAnchor.top()).forGetter(c -> c.maxY),
        Codecs.UNIT_FLOAT.fieldOf("probability").forGetter(c -> c.probability),
        GenerationStep.Carving.CODEC.fieldOf("step").forGetter(c -> c.step)
    ).apply(instance, TFCCarvingMaskConfig::new));
}
