/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.heat;

public interface HeatContainer extends IHeat
{
    IHeat heatContainer();

    @Override
    default float getTemperature()
    {
        return heatContainer().getTemperature();
    }

    @Override
    default void setTemperature(float temperature)
    {
        heatContainer().setTemperature(temperature);
    }

    @Override
    default float getHeatCapacity()
    {
        return heatContainer().getHeatCapacity();
    }

    @Override
    default void setHeatCapacity(float value)
    {
        heatContainer().setHeatCapacity(value);
    }

    @Override
    default float getWorkingTemperature()
    {
        return heatContainer().getWorkingTemperature();
    }

    @Override
    default float getWeldingTemperature()
    {
        return heatContainer().getWeldingTemperature();
    }
}
