/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.capability.heat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;

import net.dries007.tfc.Constants;

public class CapabilityItemHeat
{

    public static final CapabilityItemHeat INSTANCE = new CapabilityItemHeat();
    private static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "item_heat");

    @CapabilityInject(IItemHeat.class)
    public static Capability<IItemHeat> ITEM_HEAT_CAPABILITY = null;

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IItemHeat.class, new ItemHeatStorage(), ItemHeat::new);
    }

    @Nullable
    public static IItemHeat getIItemHeat(@Nonnull ItemStack stack)
    {
        return stack.getCapability(ITEM_HEAT_CAPABILITY, null);
    }

    public ICapabilityProvider getCapability(ItemStack stack, NBTTagCompound nbt, float heatCapacity, float meltingPoint)
    {
        return new ItemHeat(heatCapacity, meltingPoint);
    }

    public static class ItemHeat implements ICapabilitySerializable<NBTTagCompound>, IItemHeat
    {
        private final float heatCapacity;
        private final float meltingPoint;
        private float temperature;

        public ItemHeat(@Nullable NBTTagCompound nbt, float heatCapacity, float meltingPoint)
        {
            this.heatCapacity = heatCapacity;
            this.meltingPoint = meltingPoint;

            if (nbt != null)
                deserializeNBT(nbt);
        }

        public ItemHeat(float heatCapacity, float meltingPoint)
        {
            this(null, heatCapacity, meltingPoint);
        }

        public ItemHeat()
        {
            this(null, 1, 1000);
        }

        @Override
        public float getTemperature()
        {
            return temperature;
        }

        @Override
        public float getMeltingPoint()
        {
            return meltingPoint;
        }

        @Override
        public float getHeatCapacity()
        {
            return heatCapacity;
        }

        @Override
        public void addTemperature(float temperature)
        {
            this.temperature += temperature;
        }

        @Override
        public void setTemperature(float temperature)
        {
            this.temperature = temperature;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return true;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return CapabilityItemHeat.ITEM_HEAT_CAPABILITY.cast(this);
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            return (NBTTagCompound) ITEM_HEAT_CAPABILITY.writeNBT(this, null);
        }

        @Override
        public void deserializeNBT(@Nullable NBTTagCompound nbt)
        {
            ITEM_HEAT_CAPABILITY.readNBT(this, null, nbt);
        }
    }

    public static class ItemHeatStorage implements Capability.IStorage<IItemHeat>
    {
        @Nonnull
        @Override
        public NBTBase writeNBT(Capability<IItemHeat> capability, IItemHeat instance, EnumFacing side)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setFloat("temperature", instance.getTemperature());
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
            instance.setTemperature(nbt.getFloat("temperature"));
        }
    }

}
