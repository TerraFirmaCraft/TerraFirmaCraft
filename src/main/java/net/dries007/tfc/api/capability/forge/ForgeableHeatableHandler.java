/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.forge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.util.forge.ForgeStep;
import net.dries007.tfc.util.forge.ForgeSteps;

public class ForgeableHeatableHandler extends ItemHeatHandler implements IForgeableHeatable
{
    private final ForgeableHandler internalForgeCap;

    public ForgeableHeatableHandler(@Nullable NBTTagCompound nbt, float heatCapacity, float meltTemp)
    {
        this.heatCapacity = heatCapacity;
        this.meltTemp = meltTemp;

        internalForgeCap = new ForgeableHandler(nbt);

        deserializeNBT(nbt);
    }

    public ForgeableHeatableHandler()
    {
        // for custom implementations
        internalForgeCap = new ForgeableHandler();
    }

    @Override
    public int getWork()
    {
        return internalForgeCap.getWork();
    }

    @Override
    public void setWork(int work)
    {
        internalForgeCap.setWork(work);
    }

    @Override
    @Nullable
    public ResourceLocation getRecipeName()
    {
        return internalForgeCap.getRecipeName();
    }

    @Override
    public void setRecipe(@Nullable ResourceLocation recipeName)
    {
        internalForgeCap.setRecipe(recipeName);
    }

    @Override
    @Nonnull
    public ForgeSteps getSteps()
    {
        return internalForgeCap.getSteps();
    }

    @Override
    public void addStep(ForgeStep step)
    {
        internalForgeCap.addStep(step);
    }

    @Override
    public void reset()
    {
        internalForgeCap.reset();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityForgeable.FORGEABLE_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return hasCapability(capability, facing) ? (T) this : null;
    }

    @Override
    @Nonnull
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.setTag("forge", internalForgeCap.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            internalForgeCap.deserializeNBT(nbt.getCompoundTag("forge"));
            super.deserializeNBT(nbt);
        }
    }
}
