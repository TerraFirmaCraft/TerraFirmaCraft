/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import net.dries007.tfc.util.calendar.Calendars;

public class HeatHandler implements IHeat
{
    private final float forgingTemp; // Temperature at which this item can be worked in forging
    private final float weldingTemp; // Temperature at which this item can be welded


    // This is almost "constant". Some implementations will want to change these based on other factors. (See molds or small vessels)
    protected float heatCapacity; // How fast temperature rises and drops

    // These are the values from last point of update. They are updated when read from NBT, or when the temperature is set manually.
    // Note that if temperature is == 0, lastUpdateTick should set itself to -1 to keep their capabilities compatible - i.e. stackable
    protected float temperature;
    protected long lastUpdateTick;

    /**
     * Default ItemHeatHandler implementation
     *
     * @param heatCapacity The heat capacity
     */
    public HeatHandler(float heatCapacity, float forgingTemp, float weldingTemp)
    {
        this.heatCapacity = heatCapacity;
        this.forgingTemp = forgingTemp;
        this.weldingTemp = weldingTemp;
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
        return HeatCapability.adjustTemp(temperature, getHeatCapacity(), Calendars.get().getTicks() - lastUpdateTick);
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
        this.lastUpdateTick = Calendars.get().getTicks();
    }

    @Override
    public float getHeatCapacity()
    {
        return heatCapacity;
    }

    @Override
    public float getWorkingTemperature()
    {
        return forgingTemp;
    }

    @Override
    public float getWeldingTemperature()
    {
        return weldingTemp;
    }

    /**
     * Sets the current heat capacity, for implementations that might change this based on an internal state
     */
    public void setHeatCapacity(float heatCapacity)
    {
        if (getHeatCapacity() != heatCapacity)
        {
            // Note: in order not to perform a sudden jump in temperature, we need to reset any latent temperature delta.
            setTemperature(getTemperature());
            this.heatCapacity = heatCapacity;
        }
    }
}