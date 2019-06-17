/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.nutrient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.agriculture.Nutrient;
import net.dries007.tfc.world.classic.CalendarTFC;

import static net.dries007.tfc.api.capability.nutrient.CapabilityFood.MAX_PLAYER_NUTRIENTS;
import static net.dries007.tfc.api.capability.nutrient.CapabilityFood.MIN_PLAYER_NUTRIENTS;

public class PlayerNutrientsHandler implements IPlayerNutrients, ICapabilitySerializable<NBTTagCompound>
{
    private final float[] nutrients;
    private long lastUpdateTick;

    public PlayerNutrientsHandler()
    {
        this(null);
    }

    public PlayerNutrientsHandler(@Nullable NBTTagCompound nbt)
    {
        nutrients = new float[Nutrient.TOTAL];
        for (int i = 0; i < nutrients.length; i++)
        {
            nutrients[i] = 0.8f * MAX_PLAYER_NUTRIENTS;
        }

        deserializeNBT(nbt);
    }

    @Override
    public float getNutrient(Nutrient nutrient)
    {
        updateNutrients();
        return nutrients[nutrient.ordinal()];
    }

    @Override
    public float[] getNutrients()
    {
        updateNutrients();
        return nutrients;
    }

    @Override
    public void setNutrients(float[] nutrients)
    {
        System.arraycopy(nutrients, 0, this.nutrients, 0, this.nutrients.length);
    }

    @Override
    public void setNutrient(Nutrient nutrient, float amount)
    {
        if (amount < MIN_PLAYER_NUTRIENTS)
        {
            nutrients[nutrient.ordinal()] = MIN_PLAYER_NUTRIENTS;
        }
        else if (amount > MAX_PLAYER_NUTRIENTS)
        {
            nutrients[nutrient.ordinal()] = MAX_PLAYER_NUTRIENTS;
        }
        else
        {
            nutrients[nutrient.ordinal()] = amount;
        }
    }

    @Override
    public void addNutrient(Nutrient nutrient, float amount)
    {
        updateNutrients();
        float newAmount = nutrients[nutrient.ordinal()] + amount;
        setNutrient(nutrient, newAmount);
    }

    public void updateNutrientsFastForward()
    {
        lastUpdateTick = CalendarTFC.getCalendarTime();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFood.CAPABILITY_PLAYER_NUTRIENTS;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFood.CAPABILITY_PLAYER_NUTRIENTS ? (T) this : null;
    }

    @Override
    @Nonnull
    public NBTTagCompound serializeNBT()
    {
        updateNutrients();

        NBTTagCompound nbt = new NBTTagCompound();
        for (Nutrient nutrient : Nutrient.values())
        {
            nbt.setFloat(nutrient.name().toLowerCase(), this.nutrients[nutrient.ordinal()]);
        }
        nbt.setLong("lastUpdateTick", lastUpdateTick);
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            for (Nutrient nutrient : Nutrient.values())
            {
                nutrients[nutrient.ordinal()] = nbt.getFloat(nutrient.name().toLowerCase());
            }
            lastUpdateTick = nbt.getLong("lastUpdateTick");
        }
    }

    private void updateNutrients()
    {
        int ticksPassed = (int) (CalendarTFC.getCalendarTime() - lastUpdateTick);
        for (Nutrient nutrient : Nutrient.values())
        {
            setNutrient(nutrient, nutrients[nutrient.ordinal()] - (float) (ConfigTFC.GENERAL.playerNutritionDecayModifier * nutrient.getDecayModifier() * ticksPassed));
        }
        lastUpdateTick = CalendarTFC.getCalendarTime();
    }
}
