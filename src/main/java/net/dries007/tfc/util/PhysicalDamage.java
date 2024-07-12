/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PhysicalDamage(
    float piercing,
    float slashing,
    float crushing
) implements PhysicalDamageType.Multiplier
{
    public static final MapCodec<PhysicalDamage> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Codec.FLOAT.optionalFieldOf("piercing", 0f).forGetter(c -> c.piercing),
        Codec.FLOAT.optionalFieldOf("slashing", 0f).forGetter(c -> c.slashing),
        Codec.FLOAT.optionalFieldOf("crushing", 0f).forGetter(c -> c.crushing)
    ).apply(i, PhysicalDamage::new));

    public static final StreamCodec<ByteBuf, PhysicalDamage> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, c -> c.piercing,
        ByteBufCodecs.FLOAT, c -> c.slashing,
        ByteBufCodecs.FLOAT, c -> c.crushing,
        PhysicalDamage::new
    );
}
