package net.dries007.tfc.api.capability.forge;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

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
}
