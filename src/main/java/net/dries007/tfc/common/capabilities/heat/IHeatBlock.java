/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

/**
 * A simplified version of {@link IHeat} for attaching to blocks (or specifically, their block entities).
 * Since block entities can have tick methods, and can track their own heat without requiring a passive heat solution, this may behave differently w.r.t how {@link #setTemperature(float)} works.
 * Most internal handlers will set some form of 'target temperature', and their actual temperature will adjust over time to the target.
 */
public interface IHeatBlock
{
    /**
     * Gets the current temperature.
     *
     * @return the temperature.
     */
    float getTemperature();

    /**
     * Sets the (target) temperature. Used for anything that modifies the temperature.
     *
     * @param temperature the temperature to set.
     */
    void setTemperature(float temperature);

    /**
     * Effectively, sets the (target) temperature to the maximum of the current temperature and the provided temperature.
     */
    default void setTemperatureIfWarmer(float temperature)
    {
        final float current = getTemperature();
        if (temperature > current)
        {
            setTemperature(temperature);
        }
    }
}
