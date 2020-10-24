package net.dries007.tfc.world.decorator;

import net.minecraft.world.gen.placement.IPlacementConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class FlatEnoughConfig implements IPlacementConfig
{
    public static final Codec<FlatEnoughConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.floatRange(0, 1).optionalFieldOf("flatness", 0.5f).forGetter(c -> c.flatness),
        Codec.INT.optionalFieldOf("radius", 2).forGetter(c -> c.radius),
        Codec.INT.optionalFieldOf("max_depth", 4).forGetter(c -> c.maxDepth)
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
