/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

/**
 * A capability that is exposed by blocks that consume heat. The underlying functionality of the block and how it interacts with heat
 * is unspecified.
 */
public interface IHeatConsumer
{
    /**
     * Gets the current temperature.
     *
     * @return the temperature.
     */
    float getTemperature();

    /**
     * Set the temperature of the consumer. This is to be understood as providing some abstract "amount" of heat to the block at the
     * specified temperature. Note there are few assumptions that can be made about using this method:
     * <ul>
     *     <li>The temperature may only be modified if {@code temperature} is higher than the internal temperature</li>
     *     <li>The temperature may not be modified immediately, or at all, and may not reflect in {@link #getTemperature()} directly</li>
     * </ul>
     *
     * @param temperature the temperature to set.
     */
    void setTemperature(float temperature);
}
