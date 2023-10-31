/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.dries007.tfc.world.Codecs;

public record PipeVeinConfig(VeinConfig config, int height, int radius, int minSkew, int maxSkew, int minSlant, int maxSlant, float sign) implements IVeinConfig
{
    public static final Codec<PipeVeinConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        VeinConfig.CODEC.forGetter(c -> c.config),
        Codec.INT.fieldOf("height").forGetter(c -> c.radius),
        Codec.INT.fieldOf("radius").forGetter(c -> c.radius),
        Codec.INT.fieldOf("min_skew").forGetter(c -> c.minSkew),
        Codec.INT.fieldOf("max_skew").forGetter(c -> c.maxSkew),
        Codec.INT.fieldOf("min_slant").forGetter(c -> c.minSlant),
        Codec.INT.fieldOf("max_slant").forGetter(c -> c.maxSlant),
        Codecs.UNIT_FLOAT.fieldOf("sign").forGetter(c -> c.sign)
    ).apply(instance, PipeVeinConfig::new));

    @Override
    public int chunkRadius()
    {
        return 1 + (radius >> 4);
    }

    @Override
    public int verticalRadius()
    {
        return height;
    }
}
