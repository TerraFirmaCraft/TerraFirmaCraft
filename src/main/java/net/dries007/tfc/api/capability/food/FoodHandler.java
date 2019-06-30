/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.util.agriculture.Food;
import net.dries007.tfc.world.classic.CalendarTFC;

public class FoodHandler implements IFood, ICapabilitySerializable<NBTTagCompound>
{
    private float[] nutrients;
    private long creationDate;
    private float decayModifier;
    private float water;
    private float calories;

    public FoodHandler()
    {
        this(null, new float[] {0f, 0f, 0f, 0f, 0f}, 0.5f, 0f, 1f);
    }

    public FoodHandler(@Nullable NBTTagCompound nbt, @Nonnull Food food)
    {
        this(nbt, food.getNutrients(), food.getCalories(), food.getWater(), food.getDecayModifier());
    }

    public FoodHandler(@Nullable NBTTagCompound nbt, float[] nutrients, float calories, float water, float decayModifier)
    {
        this.nutrients = new float[Nutrient.TOTAL];
        this.decayModifier = decayModifier;
        this.water = water;
        this.calories = calories;
        System.arraycopy(nutrients, 0, this.nutrients, 0, nutrients.length);

        deserializeNBT(nbt);
    }

    @Override
    public long getRottenDate()
    {
        return creationDate + (long) (decayModifier * CapabilityFood.DEFAULT_ROT_TICKS);
    }

    @Override
    public float getWater()
    {
        return water;
    }

    @Override
    public float getNutrient(ItemStack stack, Nutrient nutrient)
    {
        if (isRotten())
        {
            return 0;
        }
        return nutrients[nutrient.ordinal()];
    }

    @Override
    public long getCreationDate()
    {
        if (isRotten())
        {
            // All rotten food is equally rotten
            this.creationDate = Long.MIN_VALUE;
        }
        return creationDate;
    }

    @Override
    public void setCreationDate(long creationDate)
    {
        this.creationDate = creationDate;
    }

    @Override
    public float getCalories()
    {
        return calories;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFood.CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFood.CAPABILITY ? (T) this : null;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("creationDate", getCreationDate());
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null && nbt.hasKey("creationDate"))
        {
            creationDate = nbt.getLong("creationDate");
        }
        else
        {
            // Don't default to zero
            // Food decay initially is synced with the hour. This allows items grabbed within a minute to stack
            creationDate = CalendarTFC.getTotalHours() * CalendarTFC.TICKS_IN_HOUR;
        }
    }
}
