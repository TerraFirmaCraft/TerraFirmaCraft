/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.heat;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.config.TFCConfig;

/**
 * Immutable, view-only supertype of {@link IHeat}, which is provided for convenience for methods which
 * don't need to interact with custom implementations, thus requiring a {@link HeatView}.
 */
public interface IHeatView
{
    /**
     * A flag which is saved to the heat capacity of the item. indicating that the temperature is static, and not affected by
     * time-based temperature decay. This will also prevent tooltips from being drawn on the item
     */
    float FLAG_STATIC_TEMPERATURE = -1f;

    /**
     * Gets the current temperature. Should call {@link HeatCapability#adjustTemp(float, float, long)} internally
     *
     * @return the temperature.
     */
    float getTemperature();

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
     * @return The temperature that this item can be worked at.
     */
    float getWorkingTemperature();

    /**
     * @return The temperature that this item can be welded at.
     */
    float getWeldingTemperature();

    /**
     * @return {@code true} if this item is hot enough to work.
     */
    default boolean canWork()
    {
        return getTemperature() >= getWorkingTemperature();
    }

    /**
     * @return {@code true} if this item is hot enough to weld.
     */
    default boolean canWeld()
    {
        return getTemperature() >= getWeldingTemperature();
    }


    /**
     * Adds the heat info tooltip when hovering over.
     */
    default void addTooltipInfo(ItemStack stack, Consumer<Component> text)
    {
        // First, avoid showing any tooltip in the event that we set a static temperature
        if (getHeatCapacity() == FLAG_STATIC_TEMPERATURE)
        {
            return;
        }

        final float temperature = getTemperature();
        final MutableComponent tooltip = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(temperature);
        if (tooltip != null)
        {
            // Only add " - can work" and " - can weld" if both temperatures are set
            final float weldingTemperature = getWeldingTemperature(), forgingTemperature = getWorkingTemperature();
            if (weldingTemperature > 0 && weldingTemperature <= temperature)
            {
                tooltip.append(Component.translatable("tfc.tooltip.welding"));
            }
            else if (forgingTemperature > 0 && forgingTemperature <= temperature)
            {
                tooltip.append(Component.translatable("tfc.tooltip.forging"));
            }

            // 'DANGER' tooltip is displayed for things that may be lost - defined by an empty item output
            final HeatingRecipe recipe = HeatingRecipe.getRecipe(stack);
            if (recipe != null && temperature > 0.9 * recipe.getTemperature() && recipe.assembleItem(stack).isEmpty())
            {
                tooltip.append(Component.translatable("tfc.tooltip.danger"));
            }

            text.accept(tooltip);
        }
    }
}
