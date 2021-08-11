/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

import com.mojang.serialization.Codec;

public enum ForestType implements StringRepresentable
{
    NONE,
    SPARSE,
    EDGE,
    NORMAL,
    OLD_GROWTH;

    public static final Codec<ForestType> CODEC = StringRepresentable.fromEnum(ForestType::values, ForestType::byName);

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