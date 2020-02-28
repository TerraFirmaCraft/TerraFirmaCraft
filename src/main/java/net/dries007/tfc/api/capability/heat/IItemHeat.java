/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.heat;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * It is recommended that if you extend {@link ItemHeatHandler} rather than implement this directly.
 * If you do extend this, look at ItemHeatHandler to observe how heat decays over time.
 */
public interface IItemHeat extends INBTSerializable<NBTTagCompound>
{
    /**
     * Gets the current temperature. Should call {@link CapabilityItemHeat#adjustTemp(float, float, long)} internally
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
     * Gets the Heat capacity. (A measure of how fast this items heats up or cools down)
     * Implementation is left up to the heating object. (See TEFirePit for example)
     *
     * @return the heat capacity. Typically 0 - 1, can be outside this range, must be non-negative
     */
    float getHeatCapacity();

    /**
     * Gets the melting point of the item.
     * Depending on the item, this may not mean anything.
     *
     * @return a temperature at which this item should melt at
     */
    float getMeltTemp();

    /**
     * If the object can melt / transform, return if it is transformed
     * This can mean many different things depending on the object
     *
     * @return is the object transformed.
     */
    default boolean isMolten()
    {
        return getTemperature() > getMeltTemp();
    }

    /**
     * Adds the heat info tooltip when hovering over.
     * When overriding this to show additional information, fall back to IItemHeat.super.addHeatInfo()
     *
     * @param stack The stack to add information to
     * @param text  The list of tooltips
     */
    @SideOnly(Side.CLIENT)
    default void addHeatInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
    {
        String tooltip = Heat.getTooltip(getTemperature());
        if (tooltip != null)
        {
            text.add(tooltip);
        }
    }
}
