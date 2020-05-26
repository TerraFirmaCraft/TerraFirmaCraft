/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import java.util.Arrays;
import java.util.LinkedList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.ConfigTFC;

/**
 * A wrapper class for nutrition stats for a player
 * This acts as a FIFO queue for the last N foods eaten.
 * It has capability to manage the total hunger and averages over that value
 *
 * This only executes logic on server side, on client side it simply sets the lastAverageNutrients
 */
public class NutritionStats implements INBTSerializable<NBTTagCompound>
{
    private final LinkedList<FoodData> records;
    private final float defaultNutritionValue, defaultDairyNutritionValue;
    private final float[] nutrients;
    private float averageNutrients;
    private int hungerWindow;

    public NutritionStats(float defaultNutritionValue, float defaultDairyNutritionValue)
    {
        this.records = new LinkedList<>();
        this.defaultNutritionValue = defaultNutritionValue;
        this.defaultDairyNutritionValue = defaultDairyNutritionValue;
        this.nutrients = new float[5];
        this.hungerWindow = 0;

        calculateNutrition();
    }

    public void reset()
    {
        this.records.clear();
        calculateNutrition();
    }

    @Nullable
    public FoodData getMostRecentRecord()
    {
        return records.peekFirst(); // The "most recent" is actually the first in the queue
    }

    public float getAverageNutrition()
    {
        return averageNutrients;
    }

    public float getNutrient(Nutrient nutrient)
    {
        return nutrients[nutrient.ordinal()];
    }

    public float[] getNutrients()
    {
        return nutrients;
    }

    public void onReceivePacket(float[] nutrients)
    {
        System.arraycopy(nutrients, 0, this.nutrients, 0, this.nutrients.length);
        // Only need to update the average
        updateAverageNutrients();
    }

    public void addNutrients(@Nonnull FoodData data)
    {
        records.addFirst(data.copy());
        calculateNutrition();
    }

    /**
     * This adds a small amount of nutrition directly to the last food data consumed.
     * It marks said food data as "buffed", and each food data can only be buffed once.
     * This is used for non-food related nutrition bonuses, for instance drinking milk (which is not a food as it dosen't expire, which is unbalanced)
     */
    public void addBuff(@Nonnull FoodData data)
    {
        FoodData recentFood = getMostRecentRecord();
        if (recentFood != null)
        {
            recentFood.applyBuff(data);
            calculateNutrition();
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList recordsNbt = new NBTTagList();
        for (FoodData data : records)
        {
            recordsNbt.appendTag(data.serializeNBT());
        }
        nbt.setTag("records", recordsNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            records.clear();
            NBTTagList recordsNbt = nbt.getTagList("records", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < recordsNbt.tagCount(); i++)
            {
                records.add(new FoodData(recordsNbt.getCompoundTagAt(i)));
            }
            calculateNutrition();
        }
    }

    private void calculateNutrition()
    {
        // Reset
        Arrays.fill(this.nutrients, 0);
        int runningHungerTotal = 0;
        // Reload from config
        hungerWindow = ConfigTFC.General.PLAYER.nutritionRotationHungerWindow;
        for (int i = 0; i < records.size(); i++)
        {
            FoodData record = records.get(i);
            int nextHunger = record.getHunger() + runningHungerTotal;
            if (nextHunger < this.hungerWindow)
            {
                // Add weighted nutrition, keep moving
                updateAllNutrients(nutrients, j -> nutrients[j] + record.getNutrients()[j] * record.getHunger());
                runningHungerTotal = nextHunger;
            }
            else
            {
                // Calculate overshoot, weight appropriately, and exit
                float actualHunger = hungerWindow - runningHungerTotal;
                updateAllNutrients(nutrients, j -> nutrients[j] + record.getNutrients()[j] * actualHunger);

                // Remove any excess elements, this has the side effect of exiting the loop
                while (records.size() > i + 1)
                {
                    records.remove(i + 1);
                }
            }
        }
        // Average over hunger window, using default value if beyond the hunger window
        updateAllNutrients(nutrients, j -> nutrients[j] / hungerWindow);
        if (runningHungerTotal < hungerWindow)
        {
            float defaultModifier = 1 - (float) runningHungerTotal / hungerWindow;
            for (Nutrient nutrient : Nutrient.values())
            {
                if (nutrient == Nutrient.DAIRY)
                {
                    nutrients[nutrient.ordinal()] += defaultDairyNutritionValue * defaultModifier;
                }
                else
                {
                    nutrients[nutrient.ordinal()] += defaultNutritionValue * defaultModifier;
                }
            }
        }
        // Cap all nutrient averages at 1
        updateAllNutrients(nutrients, j -> Math.min(1, nutrients[j]));
        // Also calculate overall average
        updateAverageNutrients();
    }

    private void updateAverageNutrients()
    {
        averageNutrients = 0;
        for (float nutrient : nutrients)
        {
            averageNutrients += nutrient;
        }
        averageNutrients /= Nutrient.TOTAL;
    }

    private void updateAllNutrients(float[] array, IntToFloatFunction operator)
    {
        // Arrays.setAll doesn't have a float version >:(
        for (int i = 0; i < array.length; i++)
        {
            array[i] = operator.apply(i);
        }
    }

    private interface IntToFloatFunction
    {
        float apply(int i);
    }
}
