/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
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

import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Custom heat + size capability for stick items.
 */
public class ItemStickCapability extends ItemHeatHandler implements IItemSize
{
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "stick");
    private static final float MELTING_POINT = 40f;
    private static final float HEAT_CAPACITY = 1f;

    public ItemStickCapability()
    {
        this(null);
    }

    public ItemStickCapability(@Nullable NBTTagCompound nbt)
    {
        super(nbt, HEAT_CAPACITY, MELTING_POINT);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addHeatInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
    {
        float temperature = getTemperature();
        if (temperature > getMeltTemp() * 0.9f)
        {
            text.add(I18n.format("tfc.enum.heat.torch.lit"));
        }
        else if (temperature > 1f)
        {
            text.add(I18n.format("tfc.enum.heat.torch.catching_fire"));
        }
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.SMALL; // Store anywhere
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.VERY_LIGHT; // Stacksize = 64
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemSize.ITEM_SIZE_CAPABILITY || super.hasCapability(capability, facing);
    }
}
