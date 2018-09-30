/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.heat;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.dries007.tfc.ConfigTFC;

public class CapabilityItemHeat
{
    @CapabilityInject(IItemHeat.class)
    public static Capability<IItemHeat> ITEM_HEAT_CAPABILITY = null;

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IItemHeat.class, new ItemHeatStorage(), ItemHeatHandler::new);
    }

    /**
     * Call this from within IItemHeat#getTemperature();
     */
    public static float adjustTemp(float temp, float heatCapacity, long ticksSinceUpdate)
    {
        final float newTemp = temp - heatCapacity * (float) ticksSinceUpdate * (float) ConfigTFC.GENERAL.temperatureModifier;
        return newTemp < 0 ? 0 : newTemp;
    }

    /**
     * Use this to increase the heat on an item stack
     */
    public static void addTemp(ItemStack stack, float modifier)
    {
        final IItemHeat cap = stack.getCapability(ITEM_HEAT_CAPABILITY, null);
        if (cap != null)
        {
            addTemp(cap, modifier);
            stack.setTagCompound(cap.serializeNBT());
        }
    }

    /**
     * Use this to increase the heat on an IItemHeat instance.
     * Note: to save the change you will need to still call stack.setTagCompound(cap.serializeNBT());
     *
     * @param modifier the modifier for how much this will heat up: 0 - 1 slows down cooling, 1 = no heating or cooling, > 1 heats, 2 heats at the same rate it cools
     */
    public static void addTemp(IItemHeat instance, float modifier)
    {
        final float temp = instance.getTemperature() + modifier * instance.getHeatCapacity() * (float) ConfigTFC.GENERAL.temperatureModifier;
        instance.setTemperature(temp > 1600f ? 1600f : temp);
    }

    public static class ItemHeatStorage implements Capability.IStorage<IItemHeat>
    {
        @Nonnull
        @Override
        public NBTBase writeNBT(Capability<IItemHeat> capability, IItemHeat instance, EnumFacing side)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setFloat("heat", instance.getTemperature());
            return nbt;
        }

        @Override
        public void readNBT(Capability<IItemHeat> capability, IItemHeat instance, EnumFacing side, NBTBase base)
        {
            if (base == null)
            {
                instance.setTemperature(0);
                return;
            }
            NBTTagCompound nbt = (NBTTagCompound) base;
            instance.setTemperature(nbt.getFloat("heat"));
        }
    }

}
