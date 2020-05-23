/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capabilities.heat;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.api.calendar.CalendarTFC;

public class HeatHandler implements IHeat
{
    private final LazyOptional<IHeat> capability = LazyOptional.of(() -> this);
    // These are "constants". Some implementations will want to change these based on other factors. (See ItemMold)
    protected float heatCapacity; // How fast temperature rises and drops
    protected float meltTemp; // Which temperature this item is considered "molten". Some devices (ie: Charcoal Forge) destroys the item when this is reached
    // These are the values from last point of update. They are updated when read from NBT, or when the temperature is set manually.
    // Note that if temperature is == 0, lastUpdateTick should set itself to -1 to keep their capabilities compatible - i.e. stackable
    protected float temperature;
    protected long lastUpdateTick;

    /**
     * Default ItemHeatHandler implementation
     *
     * @param heatCapacity The heat capacity
     */
    public HeatHandler(float heatCapacity, float meltTemp)
    {
        this.heatCapacity = heatCapacity;
        this.meltTemp = meltTemp;
    }

    public HeatHandler()
    {
        this(1, Float.MAX_VALUE);
    }

    /**
     * This gets the outwards facing temperature. It will differ from the internal temperature value or the value saved to NBT
     * Note: if checking the temperature internally, DO NOT use temperature, use this instead, as temperature does not represent the current temperature
     *
     * @return The current temperature
     */
    @Override
    public float getTemperature()
    {
        return CapabilityHeat.adjustTemp(temperature, heatCapacity, CalendarTFC.PLAYER_TIME.getTicks() - lastUpdateTick);
    }

    /**
     * Update the temperature, and save the timestamp of when it was updated
     *
     * @param temperature the temperature to set. Between 0 - 1600
     */
    @Override
    public void setTemperature(float temperature)
    {
        this.temperature = temperature;
        this.lastUpdateTick = CalendarTFC.PLAYER_TIME.getTicks();
    }

    @Override
    public float getHeatCapacity()
    {
        return heatCapacity;
    }

    @Override
    public float getMeltTemp()
    {
        return meltTemp;
    }

    @Override
    public boolean isMolten()
    {
        return getTemperature() >= meltTemp;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return CapabilityHeat.CAPABILITY.orEmpty(cap, capability);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        if (getTemperature() <= 0)
        {
            // Reset temperature to zero
            nbt.putLong("ticks", 0);
            nbt.putFloat("heat", 0);
        }
        else
        {
            // Serialize existing values - this is intentionally lazy (and not using the result of getTemperature())
            // Why? So we don't update the serialization unnecessarily. Important for not sending unnecessary client syncs.
            nbt.putLong("ticks", lastUpdateTick);
            nbt.putFloat("heat", temperature);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        if (nbt != null)
        {
            temperature = nbt.getFloat("heat");
            lastUpdateTick = nbt.getLong("ticks");
        }
    }
}
