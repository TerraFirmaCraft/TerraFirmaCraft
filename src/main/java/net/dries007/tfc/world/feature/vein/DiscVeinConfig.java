/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.dries007.tfc.world.Codecs;

public record DiscVeinConfig(VeinConfig config, int size, int height) implements IVeinConfig
{
    public static final Codec<DiscVeinConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        VeinConfig.CODEC.forGetter(c -> c.config),
        Codecs.POSITIVE_INT.fieldOf("size").forGetter(c -> c.size),
        Codecs.POSITIVE_INT.fieldOf("height").forGetter(c -> c.height)
    ).apply(instance, DiscVeinConfig::new));

    @Override
    public int chunkRadius()
    {
        return 1 + (size >> 4);
    }

    @Override
    public int verticalRadius()
    {
        return size;
    }
}
