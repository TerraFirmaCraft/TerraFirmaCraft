/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.heat;

import org.jetbrains.annotations.Nullable;


public interface IHeat extends IHeatView
{
    /**
     * Sets the temperature. Used for anything that modifies the temperature.
     *
     * @param temperature the temperature to set.
     */
    void setTemperature(float temperature);

    /**
     * Effectively, sets the temperature to the maximum of the current temperature and the provided temperature.
     */
    default void setTemperatureIfWarmer(float temperature)
    {
        final float current = getTemperature();
        if (temperature > current)
        {
            setTemperature(temperature);
        }
    }

    /**
     * If the {@code other} handler is not {@code null}, this will set the temperature to the maximum of the current temperature,
     * and the temperature of the other handler.
     *
     * @param other Another heat handler
     */
    default void setTemperatureIfWarmer(@Nullable IHeat other)
    {
        if (other != null)
        {
            setTemperatureIfWarmer(other.getTemperature());
        }
    }

    /**
     * Adjusts the temperature based on adding an external source of heat, with a specific heat capacity. This effectively sets the
     * temperature to a weighted average of the current temperature, and input temperature, weighted by heat capacity.
     * <p>
     * This should be preferred over setting the temperature directly i.e. with {@link #setTemperatureIfWarmer(float)}, or
     * {@link #setTemperature(float)} if a heat transfer is being performed.
     */
    default void addTemperatureFromSourceWithHeatCapacity(float temperature, float heatCapacity)
    {
        final float currentTemperature = getTemperature();
        final float currentHeatCapacity = getHeatCapacity();
        final float totalHeatCapacity = currentHeatCapacity + heatCapacity;

        setTemperature(currentTemperature * currentHeatCapacity / totalHeatCapacity + temperature * heatCapacity / totalHeatCapacity);
    }

    void setHeatCapacity(float value);
}