/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

/**
 * This is the capability interface for an instance of a heat applied to an item stack
 */
public interface IHeat extends INBTSerializable<CompoundTag>
{
    /**
     * Gets the current temperature. Should call {@link HeatCapability#adjustTemp(float, float, long)} internally
     *
     * @return the temperature.
     */
    float getTemperature();

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
     * Adjusts the temperature based on adding an external source of heat, with a specific heat capacity. This effectively sets the temperature to a weighted average of the current temperature, and input temperature, weighted by heat capacity.
     * This should be preferred over
     */
    default void addTemperatureFromSourceWithHeatCapacity(float temperature, float heatCapacity)
    {
        final float currentTemperature = getTemperature();
        final float currentHeatCapacity = getHeatCapacity();
        final float totalHeatCapacity = currentHeatCapacity + heatCapacity;

        setTemperature(currentTemperature * currentHeatCapacity / totalHeatCapacity + temperature * heatCapacity / totalHeatCapacity);
    }

    /**
     * A measure of how fast or slow an item heats up. In the real world, there are two physical quantities:
     * <ol>
     *     <li>Specific Heat Capacity, which is a dimensionless number typically measured in J/(kg°C)</li>
     *     <li>Heat Capacity, which is the product of a Specific Heat Capacity by an object's mass, typically measured in J/°C</li>
     * </ol>
     * Heat capacity is affected by the mass of objects in real life, and is - to an extent - in TFC. However, due to this strange relationship, note that for similar items with the same material but different mass (i.e. ingots and double ingots), the object with a larger mass will have a <strong>smaller</strong> heat capacity, as it should be heating slower.
     *
     * @return The heat capacity. Must be > 0. Higher values indicate the object will heat slower. Units are Energy / °C
     */
    float getHeatCapacity();

    /**
     * Gets the temperature at which this item can be worked in forging
     *
     * @return temperature at which this item is able to be worked
     */
    default float getWorkingTemperature()
    {
        return 0;
    }

    /**
     * Gets the temperature at which this item can be welded in forging
     *
     * @return temperature at which this item is able to be welded
     */
    default float getWeldingTemperature()
    {
        return 0;
    }

    /**
     * Adds the heat info tooltip when hovering over.
     *
     * @param stack The stack to add information to
     * @param text  The list of tooltips
     */
    default void addTooltipInfo(ItemStack stack, List<Component> text)
    {
        final float temperature = getTemperature();
        final MutableComponent tooltip = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(temperature);
        if (tooltip != null)
        {
            // Only add " - can work" and " - can weld" if both temperatures are set
            final float weldingTemperature = getWeldingTemperature(), forgingTemperature = getWorkingTemperature();
            if (weldingTemperature > 0 && weldingTemperature <= temperature)
            {
                tooltip.append(Helpers.translatable("tfc.tooltip.welding"));
            }
            else if (forgingTemperature > 0 && forgingTemperature <= temperature)
            {
                tooltip.append(Helpers.translatable("tfc.tooltip.forging"));
            }

            // 'DANGER' tooltip is displayed for things that may be lost - defined by an empty item output
            final ItemStackInventory wrapper = new ItemStackInventory(stack);
            final HeatingRecipe recipe = HeatingRecipe.getRecipe(wrapper);
            if (recipe != null && temperature > 0.9 * recipe.getTemperature() && recipe.assemble(wrapper).isEmpty())
            {
                tooltip.append(Helpers.translatable("tfc.tooltip.danger"));
            }

            text.add(tooltip);
        }
    }
}