package net.dries007.tfc.world.carver;

import net.minecraft.world.gen.carver.ICarverConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorleyCaveConfig implements ICarverConfig
{
    public static final Codec<WorleyCaveConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("height_fade_threshold", 94).forGetter(c -> c.heightFadeThreshold),
        Codec.FLOAT.optionalFieldOf("base_noise_cutoff", 0.3f).forGetter(c -> c.baseNoiseCutoff),
        Codec.FLOAT.optionalFieldOf("worley_noise_cutoff", 0.38f).forGetter(c -> c.worleyNoiseCutoff)
    ).apply(instance, WorleyCaveConfig::new));

    public final int heightFadeThreshold;
    public final float baseNoiseCutoff;
    public final float worleyNoiseCutoff;

    public WorleyCaveConfig(int heightFadeThreshold, float baseNoiseCutoff, float worleyNoiseCutoff)
    {
        this.heightFadeThreshold = heightFadeThreshold;
        this.baseNoiseCutoff = baseNoiseCutoff;
        this.worleyNoiseCutoff = worleyNoiseCutoff;
    }
}
