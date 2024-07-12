/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;


import net.dries007.tfc.common.capabilities.heat.IHeat;

public interface DelegateHeatHandler extends IHeat
{
    /**
     * Return the underlying heat implementation of this delegate. This will be used to invoke <strong>all non-default methods</strong> from {@link IHeat}
     * on the implementor of {@link DelegateHeatHandler}. Any calls to default methods on {@link IHeat} will call the default implementation, and then be
     * bounced through to the original, underlying implementation.
     * <p>
     * Consumers should not call this method, rather, they should directly use {@code this} as and {@link IHeat}.
     *
     * @return An {@link IHeat} implementation.
     */
    IHeat getHeatHandler();

    @Override
    default float getTemperature()
    {
        return getHeatHandler().getTemperature();
    }

    @Override
    default void setTemperature(float temperature)
    {
        getHeatHandler().setTemperature(temperature);
    }

    @Override
    default float getHeatCapacity()
    {
        return getHeatHandler().getHeatCapacity();
    }

    @Override
    default float getWorkingTemperature()
    {
        return getHeatHandler().getWorkingTemperature();
    }

    @Override
    default float getWeldingTemperature()
    {
        return getHeatHandler().getWeldingTemperature();
    }
}
