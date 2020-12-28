/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

import com.mojang.serialization.Codec;

public enum ForestType implements IStringSerializable
{
    NONE,
    SPARSE,
    NORMAL,
    OLD_GROWTH;

    public static final Codec<ForestType> CODEC = IStringSerializable.fromEnum(ForestType::values, ForestType::byName);

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