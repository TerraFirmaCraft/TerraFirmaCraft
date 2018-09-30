/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.heat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.world.classic.CalenderTFC;

/**
 * This is an implementation of ItemHeat that automatically cools down over time
 * Prefer extending or using this than implementing IItemHeat directly
 * Exceptions if you want to extend another capability object (see SmallVessel) but you should still implement this functionality somewhere
 */
public class ItemHeatHandler implements ICapabilitySerializable<NBTTagCompound>, IItemHeat
{
    // These are "constants". Some implementations will want to change these based on other factors. (See ItemMold)
    protected float heatCapacity;
    protected float meltingPoint;

    // These are the values from last point of update. They are updated when read from NBT, or when the temperature is set manually.
    protected float temperature;
    protected long lastUpdateTick;

    /**
     * Default ItemHeatHandler implementation
     *
     * @param nbt          The NBT of the itemstack. (Provided in Item#initCapabilities())
     * @param heatCapacity The heat capacity
     * @param meltingPoint The melting point
     */
    public ItemHeatHandler(@Nullable NBTTagCompound nbt, float heatCapacity, float meltingPoint)
    {
        this.heatCapacity = heatCapacity;
        this.meltingPoint = meltingPoint;

        if (nbt != null)
            deserializeNBT(nbt);
    }

    public ItemHeatHandler() { } // This is here so you can do a custom implementation

    @Override
    public float getTemperature()
    {
        // This gets the outwards facing temperature. It will differ from the internal temperature value or the value saved to NBT
        // Note: if checking the temperature internally, DO NOT use temperature, use this instead, as temperature does not represent the current temperature
        return CapabilityItemHeat.adjustTemp(temperature, heatCapacity, CalenderTFC.getTotalTime() - lastUpdateTick);
    }

    @Override
    public void setTemperature(float temperature)
    {
        // Update the temperature, and save the timestamp of when it was updated
        this.temperature = temperature;
        this.lastUpdateTick = CalenderTFC.getTotalTime();
    }

    @Override
    public float getHeatCapacity()
    {
        return heatCapacity;
    }

    @Override
    public float getMeltingPoint()
    {
        return meltingPoint;
    }

    // Override for that 0.00001% efficiency
    @Override
    public boolean isMolten()
    {
        return getTemperature() >= meltingPoint;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHeat.ITEM_HEAT_CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return hasCapability(capability, facing) ? (T) this : null;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setFloat("heat", getTemperature());
        nbt.setLong("ticks", CalenderTFC.getTotalTime());
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            temperature = nbt.getFloat("heat");
            lastUpdateTick = nbt.getLong("ticks");
        }
    }
}
