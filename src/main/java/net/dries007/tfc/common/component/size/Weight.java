/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.size;

import java.util.Locale;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import net.dries007.tfc.network.StreamCodecs;

public enum Weight implements StringRepresentable
{
    VERY_LIGHT(64),
    LIGHT(32),
    MEDIUM(16),
    HEAVY(4),
    VERY_HEAVY(1);

    public static final Codec<Weight> CODEC = StringRepresentable.fromValues(Weight::values);
    public static final StreamCodec<ByteBuf, Weight> STREAM_CODEC = StreamCodecs.forEnum(Weight::values);

    public final int stackSize;
    public final String name;

    Weight(int stackSize)
    {
        this.name = name().toLowerCase(Locale.ROOT);
        this.stackSize = stackSize;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public boolean isSmallerThan(Weight other)
    {
        return this.stackSize > other.stackSize;
    }
}
