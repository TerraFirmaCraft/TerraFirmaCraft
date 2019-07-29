/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.forge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Default Implementation of {@link IForgeableMeasurable}
 */
public class ForgeableMeasurableHandler extends ForgeableHandler implements IForgeableMeasurable
{
    protected int metalAmount;

    public ForgeableMeasurableHandler(@Nullable NBTTagCompound nbt, float heatCapacity, float meltTemp, int metalAmount)
    {
        super(nbt, heatCapacity, meltTemp);
        this.metalAmount = metalAmount;
    }

    @Override
    public int getMetalAmount()
    {
        return this.metalAmount;
    }

    @Override
    public void setMetalAmount(int metalAmount)
    {
        this.metalAmount = metalAmount;
    }

    @Override
    @Nonnull
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.setInteger("metalAmount", metalAmount);
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            metalAmount = nbt.getInteger("metalAmount");
        }
        super.deserializeNBT(nbt);
    }
}
