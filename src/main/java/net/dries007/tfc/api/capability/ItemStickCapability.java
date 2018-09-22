/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.capability;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class ItemStickCapability extends ItemHeatHandler implements IItemSize
{
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "stick");
    private static final float MELTING_POINT = 80f;
    private static final float HEAT_CAPACITY = 1f;

    public ItemStickCapability(@Nullable NBTTagCompound nbt)
    {
        //todo: check values
        super(nbt, HEAT_CAPACITY, MELTING_POINT);
        if (nbt != null)
            deserializeNBT(nbt);
    }

    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.SMALL;
    }

    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.LIGHT;
    }

    @Override
    public float getHeatCapacity()
    {
        return HEAT_CAPACITY;
    }

    @Override
    public float getMeltingPoint()
    {
        return MELTING_POINT;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHeat.ITEM_HEAT_CAPABILITY || capability == CapabilityItemSize.ITEM_SIZE_CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return hasCapability(capability, facing) ? (T) this : null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addHeatInfo(ItemStack stack, List<String> text, boolean clearStackNBT)
    {
        float temperature = getTemperature();
        if (temperature > MELTING_POINT * 0.9f)
            text.add(I18n.format("tfc.enum.heat.torch.lit"));
        else if (temperature > 1f)
            text.add(I18n.format("tfc.enum.heat.torch.catchingFire"));

        if (clearStackNBT && temperature <= 0 && stack.hasTagCompound())
            stack.setTagCompound(null);
    }
}
