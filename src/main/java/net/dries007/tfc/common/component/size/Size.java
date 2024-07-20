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

public enum Size implements StringRepresentable
{
    TINY, // Fits in anything
    VERY_SMALL, // Fits in anything
    SMALL, // Fits in small vessels
    NORMAL, // Fits in large vessels
    LARGE, // Fits in chests, Pit kilns can hold four.
    VERY_LARGE, // Pit kilns can only hold one.
    HUGE; // Pit kilns can only hold one. Counts towards overburdened when also very heavy.

    public static final Codec<Size> CODEC = StringRepresentable.fromValues(Size::values);
    public static final StreamCodec<ByteBuf, Size> STREAM_CODEC = StreamCodecs.forEnum(Size::values);

    public final String name;

    Size()
    {
        this.name = name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public boolean isSmallerThan(Size other)
    {
        return this.ordinal() < other.ordinal();
    }

    public boolean isEqualOrSmallerThan(Size other)
    {
        return this.ordinal() <= other.ordinal();
    }
}
