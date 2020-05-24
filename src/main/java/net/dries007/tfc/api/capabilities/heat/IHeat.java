/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capabilities.heat;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

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
     * Gets the temperature at which this item can be worked in forging
     *
     * @return temperature at which this item is able to be worked
     */
    default float getForgingTemperature()
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
     * When overriding this to show additional information, fall back to IItemHeat.super.addHeatInfo()
     *
     * @param stack The stack to add information to
     * @param text  The list of tooltips
     */
    @OnlyIn(Dist.CLIENT)
    default void addHeatInfo(ItemStack stack, List<ITextComponent> text)
    {
        float temperature = getTemperature();
        ITextComponent tooltip = Heat.getTooltip(temperature);
        if (tooltip != null)
        {
            // Only add " - can work" and " - can weld" if both temperatures are set
            if (getWeldingTemperature() > 0 && getWeldingTemperature() <= temperature)
            {
                tooltip.appendSibling(new TranslationTextComponent(MOD_ID + ".tooltip.welding"));
            }
            else if (getForgingTemperature() > 0 && getForgingTemperature() <= temperature)
            {
                tooltip.appendSibling(new TranslationTextComponent(MOD_ID + ".tooltip.forging"));
            }
            text.add(tooltip);
        }
    }
}
