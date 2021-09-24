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
    private final float valueNW, valueNE, valueSW, valueSE;

    public LerpFloatLayer(FriendlyByteBuf buffer)
    {
        valueNW = buffer.readFloat();
        valueNE = buffer.readFloat();
        valueSW = buffer.readFloat();
        valueSE = buffer.readFloat();
    }

    public LerpFloatLayer(CompoundTag nbt)
    {
        valueNW = nbt.getFloat("nw");
        valueNE = nbt.getFloat("ne");
        valueSW = nbt.getFloat("sw");
        valueSE = nbt.getFloat("se");
    }

    public LerpFloatLayer(float valueNW, float valueNE, float valueSW, float valueSE)
    {
        this.valueNW = valueNW;
        this.valueNE = valueNE;
        this.valueSW = valueSW;
        this.valueSE = valueSE;
    }

    /**
     * Gets the floating point value approximated at the point within the grid.
     *
     * @param tNS A distance in the N-S direction. 0 = Full north, 1 = Full south.
     * @param tEW A distance in the E-W direction. 0 = Full east, 1 = Full west.
     */
    public float getValue(float tNS, float tEW)
    {
        return Helpers.lerp4(valueNE, valueNW, valueSE, valueSW, tNS, tEW);
    }

    public CompoundTag write()
    {
        final CompoundTag nbt = new CompoundTag();
        nbt.putFloat("nw", valueNW);
        nbt.putFloat("ne", valueNE);
        nbt.putFloat("sw", valueSW);
        nbt.putFloat("se", valueSE);
        return nbt;
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeFloat(valueNW);
        buffer.writeFloat(valueNE);
        buffer.writeFloat(valueSW);
        buffer.writeFloat(valueSE);
    }
}