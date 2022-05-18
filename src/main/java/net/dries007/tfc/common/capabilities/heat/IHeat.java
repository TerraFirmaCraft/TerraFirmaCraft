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
     * Gets the Heat capacity. (A measure of how fast this items heats up or cools down)
     * Implementation is left up to the heating object. (See TEFirePit for example)
     *
     * @return the heat capacity. Typically 0 - 1, can be outside this range, must be non-negative
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
                tooltip.append(new TranslatableComponent("tfc.tooltip.welding"));
            }
            else if (forgingTemperature > 0 && forgingTemperature <= temperature)
            {
                tooltip.append(new TranslatableComponent("tfc.tooltip.forging"));
            }

            final ItemStackInventory wrapper = new ItemStackInventory(stack);
            final HeatingRecipe recipe = HeatingRecipe.getRecipe(wrapper);

            if (recipe != null && temperature > 0.9 * recipe.getTemperature())
            {
                tooltip.append(new TranslatableComponent("tfc.tooltip.danger"));
            }

            text.add(tooltip);
        }
    }
}