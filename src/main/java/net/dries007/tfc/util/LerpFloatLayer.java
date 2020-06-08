/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.world.noise.NoiseUtil;

/**
 * This is a simple linearly interpolated float grid.
 * It records the value at the corner, and interpolates the values between on demand.
 */
public class LerpFloatLayer implements INBTSerializable<CompoundNBT>
{
    private float valueNW, valueNE, valueSW, valueSE;

    public LerpFloatLayer(PacketBuffer buffer)
    {
        deserialize(buffer);
    }

    public LerpFloatLayer(float defaultValue)
    {
        init(defaultValue, defaultValue, defaultValue, defaultValue);
    }

    public void init(float valueNW, float valueNE, float valueSW, float valueSE)
    {
        this.valueNW = valueNW;
        this.valueNE = valueNE;
        this.valueSW = valueSW;
        this.valueSE = valueSE;
    }

    public float getValue(float tNS, float tEW)
    {
        return NoiseUtil.lerpGrid(valueNW, valueNE, valueSW, valueSE, tNS, tEW);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putFloat("nw", valueNW);
        nbt.putFloat("ne", valueNE);
        nbt.putFloat("sw", valueSW);
        nbt.putFloat("se", valueSE);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        valueNW = nbt.getFloat("nw");
        valueNE = nbt.getFloat("ne");
        valueSW = nbt.getFloat("sw");
        valueSE = nbt.getFloat("se");
    }

    public void serialize(PacketBuffer buffer)
    {
        buffer.writeFloat(valueNW);
        buffer.writeFloat(valueNE);
        buffer.writeFloat(valueSW);
        buffer.writeFloat(valueSE);
    }

    public void deserialize(PacketBuffer buffer)
    {
        valueNW = buffer.readFloat();
        valueNE = buffer.readFloat();
        valueSW = buffer.readFloat();
        valueSE = buffer.readFloat();
    }
}
