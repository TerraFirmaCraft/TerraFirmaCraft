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

public interface IItemHeat extends INBTSerializable<NBTTagCompound>
{

    float getTemperature();

    void setTemperature(float temperature);

    default void updateTemperature(float enviromentTemperature, long ticksSinceLastUpdate)
    {
        setTemperature(CapabilityItemHeat.getTempChange(getTemperature(), enviromentTemperature, ticksSinceLastUpdate));
    }

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
