package net.dries007.tfc.objects.items.metal;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
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
        setMaxDamage(1000);
        setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems (CreativeTabs tab, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this, 1, 100));
        items.add(new ItemStack(this, 1, 200));
        items.add(new ItemStack(this, 1, 300));
        items.add(new ItemStack(this, 1, 400));
    }

    @Override
    public boolean showDurabilityBar(ItemStack itemStack)
    {
        return false;
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
        return stack.getItemDamage();
    }

    public static void setSmeltAmount(ItemStack stack, int value)
    {
        stack.setItemDamage(value);
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