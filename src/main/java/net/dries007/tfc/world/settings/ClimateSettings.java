package net.dries007.tfc.world.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ClimateSettings(float frozenColdCutoff, float coldNormalCutoff, float normalLukewarmCutoff, float lukewarmWarmCutoff, float aridDryCutoff, float dryNormalCutoff, float normalDampCutoff, float dampWetCutoff)
{
    public static final MapCodec<ClimateSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.FLOAT.optionalFieldOf("frozen_cold_cutoff", -17.25f).forGetter(c -> c.frozenColdCutoff),
        Codec.FLOAT.optionalFieldOf("cold_normal_cutoff", -3.75f).forGetter(c -> c.coldNormalCutoff),
        Codec.FLOAT.optionalFieldOf("normal_lukewarm_cutoff", 9.75f).forGetter(c -> c.normalLukewarmCutoff),
        Codec.FLOAT.optionalFieldOf("lukewarm_warm_cutoff", 23.25f).forGetter(c -> c.lukewarmWarmCutoff),
        Codec.FLOAT.optionalFieldOf("arid_dry_cutoff", 125f).forGetter(c -> c.aridDryCutoff),
        Codec.FLOAT.optionalFieldOf("dry_normal_cutoff", 200f).forGetter(c -> c.dryNormalCutoff),
        Codec.FLOAT.optionalFieldOf("normal_damp_cutoff", 300f).forGetter(c -> c.normalDampCutoff),
        Codec.FLOAT.optionalFieldOf("damp_wet_cutoff", 375f).forGetter(c -> c.dampWetCutoff)
    ).apply(instance, ClimateSettings::new));

    public static ClimateSettings getDefault()
    {
        return new ClimateSettings(-17.25f, -3.75f, 9.75f, 23.25f, 125, 200, 300, 375);
    }
}
