/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
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
 * It is recommended that if you extend ItemHeatHandler rather than implement this directly.
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
     * Gets the Heat capacity. (A measure of how fast this items heats up or cools down)
     * Implementation is left up to the heating object. (See TEFirePit for example)
     *
     * @return a value between 0 and 1
     */
    float getHeatCapacity();

    /**
     * Gets the melting point of the item.
     * Depending on the item, this may not mean anything.
     *
     * @return a temperature between 0 - 1600 that is the melting point
     */
    float getMeltingPoint();

    /**
     * Sets the temperature. Used for anything that modifies the temperature.
     *
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
        return getTemperature() > getMeltingPoint();
    }

    /**
     * Adds the heat info tooltip when hovering over.
     * When overriding this to show additional information, fall back to IItemHeat.super.addHeatInfo()
     * Note: if your object has multiple capabilities that write to NBT, make sure to fall back to super with clearStackNBT = false
     * Otherwise, if heat ever turns to zero, this will clear the NBT of the stack whenever you hover over it (includes moving it / clicking, etc.)
     *
     * @param stack The stack to add information to
     * @param text  The list of tooltips
     */
    @SideOnly(Side.CLIENT)
    default void addHeatInfo(ItemStack stack, List<String> text, boolean clearStackNBT)
    {
        float temperature = getTemperature();
        Heat heat = Arrays.stream(Heat.values())
            .filter(x -> x.min < temperature && temperature <= x.max)
            .findFirst()
            .orElse(null);
        if (heat != null)
        {
            StringBuilder b = new StringBuilder();
            b.append(I18n.format(Helpers.getEnumName(heat)));
            if (heat != Heat.BRILLIANT_WHITE)
            {
                for (int i = 1; i <= 4; i++)
                {
                    if (temperature <= heat.min + ((float) i * 0.2f) * (heat.max - heat.min))
                        continue;
                    b.append("\u2605");
                }
            }
            text.add(heat.format + b.toString());
        }
        else if (clearStackNBT)
        {
            // If the heat = 0, update the stack. Only when the flag is set, to avoid overwriting stacks with other data
            if (stack.hasTagCompound())
                stack.setTagCompound(null);

        }
    }

}
