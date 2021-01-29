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
        Codecs.POSITIVE_INT.optionalFieldOf("radius", 3).forGetter(PipeVeinConfig::getRadius)
    ).apply(instance, PipeVeinConfig::new));

    private final int radius;

    private PipeVeinConfig(VeinConfig other, int radius)
    {
        super(other);

        this.radius = radius;
    }

    public int getRadius()
    {
        return radius;
    }

    @Override
    public int getChunkRadius()
    {
        return 1 + (radius >> 4);
    }
}
