/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.agriculture.Food;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

public class FoodHandler implements IFood, ICapabilitySerializable<NBTTagCompound>
{
    private static final long ROTTEN_DATE = Long.MIN_VALUE;
    private static final long NEVER_DECAY_DATE = Long.MAX_VALUE;
    private static final long UNKNOWN_CREATION_DATE = 0;

    private static boolean markStacksNonDecaying = true;

    public static void setNonDecaying(boolean markStacksNonDecaying)
    {
        FoodHandler.markStacksNonDecaying = markStacksNonDecaying;
    }

    private final List<FoodTrait> foodTraits;
    private final float[] nutrients;
    private final float decayModifier;
    private final float water;
    private final float calories;

    private long creationDate;
    private boolean isNonDecaying; // This is intentionally not serialized, as we don't want it to preserve over `ItemStack.copy()` operations

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
        this.foodTraits = new ArrayList<>(2);
        this.nutrients = new float[Nutrient.TOTAL];
        this.decayModifier = decayModifier;
        this.water = water;
        this.calories = calories;
        this.isNonDecaying = FoodHandler.markStacksNonDecaying;
        System.arraycopy(nutrients, 0, this.nutrients, 0, nutrients.length);

        deserializeNBT(nbt);
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
        if (isNonDecaying)
        {
            return UNKNOWN_CREATION_DATE;
        }
        if (calculateRottenDate(creationDate) < CalendarTFC.PLAYER_TIME.getTicks())
        {
            this.creationDate = ROTTEN_DATE;
        }
        return creationDate;
    }

    @Override
    public void setCreationDate(long creationDate)
    {
        this.creationDate = creationDate;
    }

    @Override
    public long getRottenDate()
    {
        if (isNonDecaying)
        {
            return NEVER_DECAY_DATE;
        }
        if (creationDate == ROTTEN_DATE)
        {
            return ROTTEN_DATE;
        }
        long rottenDate = calculateRottenDate(creationDate);
        if (rottenDate < CalendarTFC.PLAYER_TIME.getTicks())
        {
            return ROTTEN_DATE;
        }
        return rottenDate;
    }

    @Override
    public float getWater()
    {
        return water;
    }

    @Override
    public float getCalories()
    {
        return calories;
    }

    @Override
    public float getDecayDateModifier()
    {
        // Decay modifiers are higher = shorter
        float mod = decayModifier * (float) ConfigTFC.GENERAL.foodDecayModifier;
        for (FoodTrait trait : foodTraits)
        {
            mod *= trait.getDecayModifier();
        }
        // The modifier returned is used to calculate time, so higher = longer
        return mod == 0 ? Float.POSITIVE_INFINITY : 1 / mod;
    }

    @Override
    public void setNonDecaying()
    {
        isNonDecaying = true;
    }

    @Nonnull
    @Override
    public List<FoodTrait> getTraits()
    {
        return foodTraits;
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
        // Traits are sorted so they match when trying to stack them
        NBTTagList traitList = new NBTTagList();
        for (FoodTrait trait : foodTraits)
        {
            traitList.appendTag(new NBTTagString(trait.getName()));
        }
        nbt.setTag("traits", traitList);
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        foodTraits.clear();
        if (nbt != null && nbt.hasKey("creationDate"))
        {
            creationDate = nbt.getLong("creationDate");
            if (creationDate == 0)
            {
                // Stop defaulting to zero, in cases where the item stack is cloned or copied from one that was initialized at load (and thus was before the calendar was initialized)
                creationDate = (int) (CalendarTFC.PLAYER_TIME.getTotalHours() / ConfigTFC.GENERAL.foodDecayStackTime) * ICalendar.TICKS_IN_HOUR * ConfigTFC.GENERAL.foodDecayStackTime;
            }
            NBTTagList traitList = nbt.getTagList("traits", 8 /* String */);
            for (int i = 0; i < traitList.tagCount(); i++)
            {
                foodTraits.add(FoodTrait.getTraits().get(traitList.getStringTagAt(i)));
            }
        }
        else
        {
            // Don't default to zero
            // Food decay initially is synced with the hour. This allows items grabbed within a minute to stack
            creationDate = CalendarTFC.PLAYER_TIME.getTotalHours() * ICalendar.TICKS_IN_HOUR;
        }
    }

    private long calculateRottenDate(long creationDateIn)
    {
        float decayMod = getDecayDateModifier();
        if (decayMod == Float.POSITIVE_INFINITY)
        {
            // Infinite decay modifier
            return Long.MAX_VALUE;
        }
        return creationDateIn + (long) (decayMod * CapabilityFood.DEFAULT_ROT_TICKS);
    }
}
