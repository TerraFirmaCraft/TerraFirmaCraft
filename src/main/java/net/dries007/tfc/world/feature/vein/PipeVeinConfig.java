/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class PipeVeinConfig extends VeinConfig
{
    public static final Codec<PipeVeinConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        VeinConfig.MAP_CODEC.forGetter(c -> c),
        Codecs.POSITIVE_INT.optionalFieldOf("radius", 3).forGetter(PipeVeinConfig::getRadius),
        Codecs.POSITIVE_INT.optionalFieldOf("min_skew", 0).forGetter(PipeVeinConfig::getMinSkew),
        Codecs.POSITIVE_INT.optionalFieldOf("max_skew", 0).forGetter(PipeVeinConfig::getMaxSkew),
        Codecs.POSITIVE_INT.optionalFieldOf("min_slant", 0).forGetter(PipeVeinConfig::getMinSlant),
        Codecs.POSITIVE_INT.optionalFieldOf("max_slant", 0).forGetter(PipeVeinConfig::getMaxSlant),
        Codecs.UNIT_FLOAT.optionalFieldOf("sign", 0.5F).forGetter(PipeVeinConfig::getSign)
    ).apply(instance, PipeVeinConfig::new));

    private final int radius;
    private final int minSkew;
    private final int maxSkew;
    private final int minSlant;
    private final int maxSlant;
    private final float sign;

    public PipeVeinConfig(VeinConfig other, int radius, int minSkew, int maxSkew, int minSlant, int maxSlant, float sign)
    {
        super(other);

        this.radius = radius;
        this.minSkew = minSkew;
        this.maxSkew = maxSkew;
        this.minSlant = minSlant;
        this.maxSlant = maxSlant;
        this.sign = sign;
    }

    public int getRadius()
    {
        return radius;
    }

    public int getMinSkew()
    {
        return minSkew;
    }

    public int getMaxSkew()
    {
        return maxSkew;
    }

    public int getMinSlant()
    {
        return minSlant;
    }

    public int getMaxSlant()
    {
        return maxSlant;
    }

    public float getSign()
    {
        return sign;
    }

    @Override
    public int getChunkRadius()
    {
        return 1 + (radius >> 4);
    }
}
