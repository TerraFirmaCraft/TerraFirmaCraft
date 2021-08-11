/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record VolcanoConfig(boolean center, float distance) implements DecoratorConfiguration
{
    public static final Codec<VolcanoConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("center", false).forGetter(c -> c.center),
        Codec.floatRange(0, 1).optionalFieldOf("distance", 0f).forGetter(c -> c.distance)
    ).apply(instance, VolcanoConfig::new));
}
