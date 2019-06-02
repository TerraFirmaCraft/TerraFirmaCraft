/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.nuturient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.util.agriculture.Nutrient;

public class PlayerNutrientsHandler implements IPlayerNutrients, ICapabilitySerializable<NBTTagCompound>
{
    private final float[] nutrients;

    public PlayerNutrientsHandler()
    {
        this(null);
    }

    public PlayerNutrientsHandler(@Nullable NBTTagCompound nbt)
    {
        this.nutrients = new float[Nutrient.TOTAL];

        deserializeNBT(nbt);
    }

    @Override
    public float getNutrient(Nutrient nutrient)
    {
        return 0;
    }

    @Override
    public void addNutrient(Nutrient nutrient, float amount)
    {

    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityNutrients.CAPABILITY_PLAYER_NUTRIENTS;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityNutrients.CAPABILITY_PLAYER_NUTRIENTS ? (T) this : null;
    }

    @Override
    @Nonnull
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        for (Nutrient nutrient : Nutrient.values())
        {
            nbt.setFloat(nutrient.name().toLowerCase(), this.nutrients[nutrient.ordinal()]);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            for (Nutrient nutrient : Nutrient.values())
            {
                this.nutrients[nutrient.ordinal()] = nbt.getFloat(nutrient.name().toLowerCase());
            }
        }
    }
}
