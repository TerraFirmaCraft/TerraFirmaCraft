/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.capability.heat;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.util.Helpers;

/**
 * It is reccomended that if you extend ItemHeatHandler rather than implement this directly.
 * If you do extend this, look at ItemHeatHandler to observe how heat decays over time.
 */
public interface IItemHeat extends INBTSerializable<NBTTagCompound>
{
    /**
     * Gets the current temperature. Should call CapabilityItemHeat.adjustTemp() internally
     *
     * @return the temperature. Between 0 - 1600
     */
    float getTemperature();

    /**
     * Sets the temperature. Used for anything that modifies the temperature.
     * @param temperature the temperature to set. Between 0 - 1600
     */
    void setTemperature(float temperature);

    /**
     * If the object can melt / transform, return if it is transformed
     * This can mean many different things depending on the object
     *
     * @return is the object transformed.
     */
    default boolean isMolten()
    {
        return false;
    }

    /**
     * Adds the heat info tooltip when hovering over.
     * When overriding this to show additional information, fall back to IItemHeat.super.addHeatInfo()
     * @param stack The stack to add information to
     * @param text The list of tooltips
     */
    @SideOnly(Side.CLIENT)
    default void addHeatInfo(ItemStack stack, List<String> text)
    {
        float temperature = getTemperature();
        Heat heat = Arrays.stream(Heat.values())
            .filter(x -> x.min < temperature && temperature <= x.max)
            .findFirst()
            .orElse(null);
        if (heat == null)
            return;

        String desc = I18n.format(Helpers.getEnumName(heat));
        for (int i = 1; i <= 4; i++)
        {
            if (temperature <= heat.min + ((float) i * 0.2f) * (heat.max - heat.min))
                continue;
            desc += "\u2605";
        }
        text.add(heat.format + desc);
    }

}
