/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public record NearWaterConfig(int radius) implements DecoratorConfiguration
{
    public static final Codec<NearWaterConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.NONNEGATIVE_INT.optionalFieldOf("radius", 2).forGetter(c -> c.radius)
    ).apply(instance, NearWaterConfig::new));
}
