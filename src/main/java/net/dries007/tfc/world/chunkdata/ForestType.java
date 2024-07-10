/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Locale;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum ForestType implements StringRepresentable
{
    NONE,
    SPARSE,
    EDGE,
    NORMAL,
    OLD_GROWTH;

    public static final Codec<ForestType> CODEC = StringRepresentable.fromEnum(ForestType::values);
    public static final StreamCodec<ByteBuf, ForestType> STREAM = ByteBufCodecs.BYTE.map(ForestType::valueOf, c -> (byte) c.ordinal());

    private static final ForestType[] VALUES = values();

    public static ForestType valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : NONE;
    }

    public static ForestType byName(String name)
    {
        return valueOf(name.toUpperCase(Locale.ROOT));
    }

    @Override
    public String getSerializedName()
    {
        return name().toLowerCase(Locale.ROOT);
    }
}