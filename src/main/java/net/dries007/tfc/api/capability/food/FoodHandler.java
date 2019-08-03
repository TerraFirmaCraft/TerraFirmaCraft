/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.agriculture.Food;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

public class FoodHandler implements IFood, ICapabilitySerializable<NBTTagCompound>
{
    private final List<IFoodTrait> foodTraits;
    private final float[] nutrients;
    private final float decayModifier;
    private final float water;
    private final float calories;

    private long creationDate;

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
        this.foodTraits = new ArrayList<>();
        this.nutrients = new float[Nutrient.TOTAL];
        this.decayModifier = decayModifier;
        this.water = water;
        this.calories = calories;
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
    public long getRottenDate()
    {
        // This avoids overflow which breaks when calculateDecayModifier() returns infinity, which happens if decay modifier = 0
        float decayMod = calculateDecayModifier();
        return decayMod == Float.POSITIVE_INFINITY ? Long.MAX_VALUE : creationDate + (long) (decayMod * CapabilityFood.DEFAULT_ROT_TICKS);
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

    @Nonnull
    @Override
    public List<IFoodTrait> getTraits()
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
        if (!foodTraits.isEmpty())
        {
            nbt.setString("traits", foodTraits.stream().map(IFoodTrait::getName).sorted().collect(Collectors.joining(",")));
        }
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
                creationDate = CalendarTFC.PLAYER_TIME.getTotalHours() * ICalendar.TICKS_IN_HOUR;
            }

            // Read the traits and apply each one (if they exist)
            if (nbt.hasKey("traits"))
            {
                String serializedFoodTraits = nbt.getString("traits");
                for (String traitName : serializedFoodTraits.split(","))
                {
                    foodTraits.add(CapabilityFood.getTraits().get(traitName));
                }
            }
        }
        else
        {
            // Don't default to zero
            // Food decay initially is synced with the hour. This allows items grabbed within a minute to stack
            creationDate = CalendarTFC.PLAYER_TIME.getTotalHours() * ICalendar.TICKS_IN_HOUR;
        }
    }

    private float calculateDecayModifier()
    {
        // Decay modifiers are higher = shorter
        float mod = decayModifier * (float) ConfigTFC.GENERAL.foodDecayModifier;
        for (IFoodTrait trait : foodTraits)
        {
            mod *= trait.getDecayModifier();
        }
        // The modifier returned is used to calculate time, so higher = longer
        return mod == 0 ? Float.POSITIVE_INFINITY : 1 / mod;
    }
}
