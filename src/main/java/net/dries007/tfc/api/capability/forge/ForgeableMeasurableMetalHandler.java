/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.forge;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;

/**
 * Extension of forgeable heatable handler for blooms
 */
public class ForgeableMeasurableMetalHandler extends ForgeableHeatableHandler implements IForgeableMeasurableMetal
{
    private int metalAmount;
    private Metal metal;

    public ForgeableMeasurableMetalHandler(Metal metal, int metalAmount)
    {
        this.metalAmount = metalAmount;
        this.metal = metal;
        this.heatCapacity = metal.getSpecificHeat();
        this.meltTemp = metal.getMeltTemp();
    }

    public ForgeableMeasurableMetalHandler(@Nonnull NBTTagCompound nbt)
    {
        this.metalAmount = 0;
        this.metal = Metal.UNKNOWN;
        this.heatCapacity = Metal.UNKNOWN.getSpecificHeat();
        this.meltTemp = Metal.UNKNOWN.getMeltTemp();
        deserializeNBT(nbt);
    }

    public int getMetalAmount()
    {
        return metalAmount;
    }

    public void setMetalAmount(int metalAmount)
    {
        this.metalAmount = metalAmount;
    }

    public Metal getMetal()
    {
        return metal;
    }

    public void setMetal(Metal metal)
    {
        this.metal = metal;
    }

    @Override
    @Nonnull
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.setInteger("metalAmount", metalAmount);
        //noinspection ConstantConditions
        nbt.setString("metal", metal.getRegistryName().toString());
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            metalAmount = nbt.getInteger("metalAmount");
            ResourceLocation location = new ResourceLocation(nbt.getString("metal"));
            metal = TFCRegistries.METALS.getValue(location);
            if (metal == null)
            {
                metal = Metal.UNKNOWN;
            }
            this.meltTemp = metal.getMeltTemp();
            this.heatCapacity = metal.getSpecificHeat();
        }
        super.deserializeNBT(nbt);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addHeatInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
    {
        String desc = TextFormatting.WHITE + I18n.format("tfc.tooltip.units", metalAmount);
        text.add(desc);
        super.addHeatInfo(stack, text);
    }
}
