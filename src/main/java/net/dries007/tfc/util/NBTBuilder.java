package net.dries007.tfc.util;

import net.minecraft.nbt.NBTTagCompound;

public class NBTBuilder
{
    private final NBTTagCompound nbt = new NBTTagCompound();

    public NBTBuilder setString(String key, String value)
    {
        nbt.setString(key, value);
        return this;
    }

    public NBTBuilder setBoolean(String key, boolean value)
    {
        nbt.setBoolean(key, value);
        return this;
    }

    public NBTTagCompound build()
    {
        return nbt;
    }
}
