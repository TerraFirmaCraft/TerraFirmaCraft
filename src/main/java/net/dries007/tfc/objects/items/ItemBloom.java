/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.ForgeableMeasurableHandler;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurable;
import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class ItemBloom extends ItemTFC implements IMetalItem
{
    private boolean meltable;

    public ItemBloom(boolean meltable)
    {
        this.meltable = meltable;
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
        return Metal.WROUGHT_IRON;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if (cap instanceof IForgeableMeasurable)
        {
            int amount = ((IForgeableMeasurable) cap).getMetalAmount();
            if (amount > 100) amount = 100;
            return amount;
        }
        return 0;
    }

    @Override
    public boolean canMelt(ItemStack stack)
    {
        return meltable;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addMetalInfo(ItemStack stack, List<String> text)
    {
        Metal metal = getMetal(stack);
        IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if (metal != null && cap instanceof IForgeableMeasurable)
        {
            text.add("");
            text.add(I18n.format("tfc.tooltip.metal", I18n.format(Helpers.getTypeName(metal))));
            text.add(I18n.format("tfc.tooltip.units", ((IForgeableMeasurable) cap).getMetalAmount()));
            text.add(I18n.format(Helpers.getEnumName(metal.getTier())));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (isInCreativeTab(tab))
        {
            for (int i = 100; i <= 400; i += 100)
            {
                ItemStack stack = new ItemStack(this);
                IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                if (cap instanceof IForgeableMeasurable)
                {
                    ((IForgeableMeasurable) cap).setMetalAmount(i);
                    items.add(stack);
                }
            }
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new ForgeableMeasurableHandler(nbt, Metal.WROUGHT_IRON.getSpecificHeat(), Metal.WROUGHT_IRON.getMeltTemp(), 100);
    }
}