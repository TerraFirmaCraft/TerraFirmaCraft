package net.dries007.tfc.objects.items.metal;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.forge.ForgeableHandler;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.objects.items.ItemTFC;

public class ItemBloom extends ItemTFC implements IMetalObject
{
    public ItemBloom()
    {
        setMaxDamage(0);
        setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.LARGE;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY;
    }

    @Nullable
    @Override
    public Metal getMetal(ItemStack stack)
    {
        return Metal.UNKNOWN;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return stack.hasTagCompound() ? stack.getTagCompound().getInteger("count") : 100;
    }

    public static void setSmeltAmount(ItemStack stack, int value)
    {
        if(!stack.hasTagCompound())
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("count", value);
            stack.setTagCompound(nbt);
        }else{
            stack.getTagCompound().setInteger("count", value);
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new ForgeableHandler(nbt, Metal.WROUGHT_IRON.getSpecificHeat(), Metal.WROUGHT_IRON.getMeltTemp());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(I18n.format("tfc.tooltip.units", getSmeltAmount(stack)));
    }
}