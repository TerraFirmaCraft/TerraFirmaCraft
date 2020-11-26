package net.dries007.tfc.world.decorator;

import net.minecraft.world.gen.placement.IPlacementConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class VolcanoConfig implements IPlacementConfig
{
    public static final Codec<VolcanoConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("center", false).forGetter(c -> c.center),
        Codec.floatRange(0, 1).optionalFieldOf("distance", 0f).forGetter(c -> c.distance)
    ).apply(instance, VolcanoConfig::new));

    private final boolean center;
    private final float distance;

    public VolcanoConfig(boolean center, float distance)
    {
        this.center = center;
        this.distance = distance;
    }

    public boolean useCenter()
    {
        return center;
    }

    public float getDistance()
    {
        return distance;
    }
}
