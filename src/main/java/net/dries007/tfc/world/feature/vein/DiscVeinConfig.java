/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class DiscVeinConfig extends VeinConfig
{
    public static final Codec<DiscVeinConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        VeinConfig.MAP_CODEC.forGetter(c -> c),
        Codecs.POSITIVE_INT.optionalFieldOf("height", 2).forGetter(DiscVeinConfig::getHeight)
    ).apply(instance, DiscVeinConfig::new));

    private final int height;

    public DiscVeinConfig(VeinConfig parent, int height)
    {
        super(parent);

        this.height = height;
    }

    public int getHeight()
    {
        return height;
    }
}
