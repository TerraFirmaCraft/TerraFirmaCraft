/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capabilities.heat;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IHeat extends ICapabilitySerializable<CompoundNBT>
{
    /**
     * Gets the current temperature. Should call {@link CapabilityHeat#adjustTemp(float, float, long)} internally
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
    @OnlyIn(Dist.CLIENT)
    default void addHeatInfo(ItemStack stack, List<ITextComponent> text)
    {
        ITextComponent tooltip = Heat.getTooltip(getTemperature());
        if (tooltip != null)
        {
            text.add(tooltip);
        }
        // todo handle forging tooltips, eg: (" - can work", " - can weld", " - danger")
        // idea: since forging capability is now applied to all items, consider checking if there is a welding/anvil recipe to do this)
    }
}
