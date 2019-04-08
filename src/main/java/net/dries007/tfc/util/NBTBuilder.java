/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.nbt.NBTTagCompound;

public class NBTBuilder
{
    private final NBTTagCompound nbt;

    public NBTBuilder()
    {
        this(new NBTTagCompound());
    }

    public NBTBuilder(NBTTagCompound nbt)
    {
        this.nbt = nbt;
    }

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
