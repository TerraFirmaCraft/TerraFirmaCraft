package net.dries007.tfc.world.carver;

import net.minecraft.world.gen.carver.ICarverConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorleyCaveConfig implements ICarverConfig
{
    public static final Codec<WorleyCaveConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.intRange(0, 256).optionalFieldOf("height_fade_threshold", 94).forGetter(c -> c.heightFadeThreshold),
        Codec.floatRange(0, 1).optionalFieldOf("carving_threshold", 0.15f).forGetter(c -> c.carvingThreshold)
    ).apply(instance, WorleyCaveConfig::new));

    public final int heightFadeThreshold;
    public final float carvingThreshold;

    public WorleyCaveConfig(int heightFadeThreshold, float carvingThreshold)
    {
        this.heightFadeThreshold = heightFadeThreshold;
        this.carvingThreshold = carvingThreshold;
    }
}
