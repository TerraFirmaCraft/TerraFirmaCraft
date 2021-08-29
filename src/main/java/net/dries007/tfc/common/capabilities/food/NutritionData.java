/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.Arrays;
import java.util.LinkedList;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.config.TFCConfig;

/**
 * A wrapper class for nutrition stats for a player
 * This acts as a FIFO queue for the last N foods eaten.
 * It has capability to manage the total hunger and averages over that value
 *
 * This only executes logic on server side, on client side it simply sets the lastAverageNutrients
 */
public class NutritionData implements INBTSerializable<CompoundTag>
{
    private final LinkedList<FoodRecord> records;
    private final float defaultNutritionValue, defaultDairyNutritionValue;
    private final float[] nutrients;
    private float averageNutrients;
    private int hungerWindow;

    public NutritionData(float defaultNutritionValue, float defaultDairyNutritionValue)
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

    public float getAverageNutrition()
    {
        return averageNutrients;
    }

    /**
     * @return The nutrient value, in [0, 1]
     */
    public float getNutrient(Nutrient nutrient)
    {
        return nutrients[nutrient.ordinal()];
    }

    public float[] getNutrients()
    {
        return nutrients;
    }

    /**
     * Sets data from a packet, received on client side. Does not contain the full data only the important information
     */
    public void onClientUpdate(float[] nutrients)
    {
        System.arraycopy(nutrients, 0, this.nutrients, 0, this.nutrients.length);
        updateAverageNutrients(); // Only need to update the average
    }

    public void addNutrients(FoodRecord data)
    {
        records.addFirst(data);
        calculateNutrition();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        ListTag recordsNbt = new ListTag();
        for (FoodRecord data : records)
        {
            recordsNbt.add(data.write());
        }
        nbt.put("records", recordsNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        records.clear();
        ListTag recordsNbt = nbt.getList("records", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < recordsNbt.size(); i++)
        {
            records.add(new FoodRecord(recordsNbt.getCompound(i)));
        }
        calculateNutrition();
    }

    private void calculateNutrition()
    {
        // Reset
        Arrays.fill(this.nutrients, 0);
        int runningHungerTotal = 0;

        // Reload from config
        hungerWindow = TFCConfig.SERVER.nutritionRotationHungerWindow.get();
        for (int i = 0; i < records.size(); i++)
        {
            FoodRecord record = records.get(i);
            int nextHunger = record.getHunger() + runningHungerTotal;
            if (nextHunger < this.hungerWindow)
            {
                // Add weighted nutrition, keep moving
                updateAllNutrients(nutrients, j -> nutrients[j] + record.getNutrient(j) * record.getHunger());
                runningHungerTotal = nextHunger;
            }
            else
            {
                // Calculate overshoot, weight appropriately, and exit
                float actualHunger = hungerWindow - runningHungerTotal;
                updateAllNutrients(nutrients, j -> nutrients[j] + record.getNutrient(j) * actualHunger);

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
            for (Nutrient nutrient : Nutrient.VALUES)
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
        updateAllNutrients(nutrients, j -> Math.min(1, nutrients[j])); // Cap all nutrient averages at 1
        updateAverageNutrients(); // Also calculate overall average
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

    @FunctionalInterface
    interface IntToFloatFunction
    {
        float apply(int i);
    }
}