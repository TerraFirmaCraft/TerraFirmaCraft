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

public class FlatEnoughConfig implements DecoratorConfiguration
{
    public static final Codec<FlatEnoughConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.floatRange(0, 1).optionalFieldOf("flatness", 0.5f).forGetter(c -> c.flatness),
        Codecs.POSITIVE_INT.optionalFieldOf("radius", 2).forGetter(c -> c.radius),
        Codecs.POSITIVE_INT.optionalFieldOf("max_depth", 4).forGetter(c -> c.maxDepth)
    ).apply(instance, FlatEnoughConfig::new));

    public final float flatness;
    public final int radius;
    public final int maxDepth;

    public FlatEnoughConfig(float flatness, int radius, int maxDepth)
    {
        this.flatness = flatness;
        this.radius = radius;
        this.maxDepth = maxDepth;
    }
}
