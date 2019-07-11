package net.dries007.tfc.objects.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.api.capability.egg.CapabilityEgg;
import net.dries007.tfc.api.capability.egg.EggHandler;
import net.dries007.tfc.api.capability.egg.IEgg;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

public class ItemEggTFC extends ItemTFC
{
    public static void setFertilized(ItemStack stack, Entity entity, long hatchDay)
    {
        IEgg cap = stack.getCapability(CapabilityEgg.CAPABILITY, null);
        if (cap != null)
        {
            cap.setFertilized(entity, hatchDay);
        }
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.TINY;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.LIGHT;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new EggHandler(nbt);
    }
}
