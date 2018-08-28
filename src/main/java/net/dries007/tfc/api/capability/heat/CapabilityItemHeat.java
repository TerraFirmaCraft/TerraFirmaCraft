/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.capability.heat;

import javax.annotation.Nonnull;

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

    public static float adjustTemp(float temp, float heatCapacity, long ticksSinceUpdate)
    {
        float newTemp = temp - heatCapacity * (float) ticksSinceUpdate * (float) ConfigTFC.GENERAL.temperatureModifier;
        return newTemp < 0 ? 0 : newTemp;
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
