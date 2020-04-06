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
import net.dries007.tfc.api.capability.forge.ForgeableMeasurableMetalHandler;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurableMetal;
import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class ItemBloom extends ItemTFC implements IMetalItem
{
    private final boolean meltable;

    public ItemBloom(boolean meltable)
    {
        this.meltable = meltable;
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.LARGE; // Stored in chests
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY; // Stacksize = 4
    }

    @Nullable
    @Override
    public Metal getMetal(ItemStack stack)
    {
        IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if (cap instanceof IForgeableMeasurableMetal)
        {
            return ((IForgeableMeasurableMetal) cap).getMetal();
        }
        return Metal.UNKNOWN;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if (cap instanceof IForgeableMeasurableMetal)
        {
            int amount = ((IForgeableMeasurableMetal) cap).getMetalAmount();
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
        IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if (cap instanceof IForgeableMeasurableMetal)
        {
            text.add("");
            text.add(I18n.format("tfc.tooltip.metal", I18n.format(Helpers.getTypeName(((IForgeableMeasurableMetal) cap).getMetal()))));
            text.add(I18n.format("tfc.tooltip.units", ((IForgeableMeasurableMetal) cap).getMetalAmount()));
            text.add(I18n.format(Helpers.getEnumName(((IForgeableMeasurableMetal) cap).getMetal().getTier())));
        }
    }

    @Override
    @Nonnull
    public String getTranslationKey(ItemStack stack)
    {
        //noinspection ConstantConditions
        return super.getTranslationKey(stack) + "." + getMetal(stack).getRegistryName().getPath();
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
                if (cap instanceof IForgeableMeasurableMetal)
                {
                    IForgeableMeasurableMetal handler = (IForgeableMeasurableMetal) cap;
                    handler.setMetal(Metal.WROUGHT_IRON);
                    handler.setMetalAmount(i);
                    items.add(stack);
                }
            }
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        if (nbt == null)
        {
            return new ForgeableMeasurableMetalHandler(Metal.WROUGHT_IRON, 100);
        }
        else
        {
            return new ForgeableMeasurableMetalHandler(nbt);
        }
    }
}