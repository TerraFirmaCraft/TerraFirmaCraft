/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.config.TFCConfig;

/**
 * This is the capability interface for an instance of a heat applied to an item stack.
 * <p>
 * N.B. Any capability implementing this must also provide {@link INetworkHeat} in order to sync properly.
 */
public interface IHeat extends INetworkHeat
{
    /**
     * Gets the current temperature. Should call {@link HeatCapability#adjustTemp(float, float, long)} internally
     *
     * @return the temperature.
     */
    float getTemperature();

    /**
     * Sets the temperature. Used for anything that modifies the temperature.
     * <p>
     * N.B.: if you override this method, to apply other effects, and you also implement {@link net.dries007.tfc.common.capabilities.DelegateHeatHandler}, you <strong>MUST</strong> also override {@link #setTemperatureIfWarmer(float)} and {@link #addTemperatureFromSourceWithHeatCapacity(float, float)}. Otherwise they will call the default implementation.
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
                tooltip.append(Component.translatable("tfc.tooltip.welding"));
            }
            else if (forgingTemperature > 0 && forgingTemperature <= temperature)
            {
                tooltip.append(Component.translatable("tfc.tooltip.forging"));
            }

            // 'DANGER' tooltip is displayed for things that may be lost - defined by an empty item output
            final ItemStackInventory wrapper = new ItemStackInventory(stack);
            final HeatingRecipe recipe = HeatingRecipe.getRecipe(wrapper);
            if (recipe != null && temperature > 0.9 * recipe.getTemperature() && recipe.assemble(wrapper, null).isEmpty())
            {
                tooltip.append(Component.translatable("tfc.tooltip.danger"));
            }

            text.add(tooltip);
        }
    }
}