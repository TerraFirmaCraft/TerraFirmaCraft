/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.heat.IHeat;

public interface DelegateHeatHandler extends IHeat
{
    IHeat getHeatHandler();

    @Override
    default float getTemperature()
    {
        return getHeatHandler().getTemperature();
    }

    @Override
    default float getTemperature(boolean isClientSide)
    {
        return getHeatHandler().getTemperature(isClientSide);
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

    @Override
    default void addTooltipInfo(ItemStack stack, List<Component> text)
    {
        getHeatHandler().addTooltipInfo(stack, text);
    }
}
