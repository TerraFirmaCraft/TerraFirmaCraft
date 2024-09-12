/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.overworld;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Star(float zenith, float azimuth, int color, float apparentMagnitude)
{
    public static final Codec<List<Star>> CODEC = RecordCodecBuilder.<Star>create(i -> i.group(
        Codec.FLOAT.fieldOf("z").forGetter(c -> c.zenith),
        Codec.FLOAT.fieldOf("a").forGetter(c -> c.azimuth),
        Codec.INT.fieldOf("c").forGetter(c -> c.color),
        Codec.FLOAT.fieldOf("m").forGetter(c -> c.apparentMagnitude)
    ).apply(i, Star::new)).listOf();
}
