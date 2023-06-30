/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import net.dries007.tfc.util.Helpers;

/**
 * This is a simple linearly interpolated float grid.
 * It records the value at the corner, and interpolates the values between on demand.
 */
public class LerpFloatLayer
{
    private final float value00, value01, value10, value11; // valueXZ

    public LerpFloatLayer(FriendlyByteBuf buffer)
    {
        value00 = buffer.readFloat();
        value01 = buffer.readFloat();
        value10 = buffer.readFloat();
        value11 = buffer.readFloat();
    }

    public LerpFloatLayer(CompoundTag nbt)
    {
        value00 = nbt.getFloat("00");
        value01 = nbt.getFloat("01");
        value10 = nbt.getFloat("10");
        value11 = nbt.getFloat("11");
    }

    public LerpFloatLayer(float value00, float value01, float value10, float value11)
    {
        this.value00 = value00;
        this.value01 = value01;
        this.value10 = value10;
        this.value11 = value11;
    }

    /**
     * Gets the floating point value approximated at the point within the grid.
     *
     * @param deltaX A distance in the X direction.
     * @param deltaZ A distance in the Z direction.
     */
    public float getValue(float deltaX, float deltaZ)
    {
        return Helpers.lerp4(value00, value01, value10, value11, deltaX, deltaZ);
    }

    public CompoundTag write()
    {
        final CompoundTag nbt = new CompoundTag();
        nbt.putFloat("00", value00);
        nbt.putFloat("01", value01);
        nbt.putFloat("10", value10);
        nbt.putFloat("11", value11);
        return nbt;
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeFloat(value00);
        buffer.writeFloat(value01);
        buffer.writeFloat(value10);
        buffer.writeFloat(value11);
    }
}