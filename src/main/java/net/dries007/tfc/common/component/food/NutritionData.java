/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.food;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.ToDoubleFunction;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.player.PlayerInfo;
import net.dries007.tfc.config.TFCConfig;

/**
 * A wrapper class for nutrition stats for a player
 * This acts as a FIFO queue for the last N foods eaten.
 * It has capability to manage the total hunger and averages over that value
 * <p>
 * This only executes logic on server side, on client side it simply sets the lastAverageNutrients
 */
public class NutritionData implements INutritionData
{
    private final LinkedList<FoodData> records;
    private final float defaultNutritionValue, defaultDairyNutritionValue;
    private final float[] nutrients;
    private float averageNutrients;
    private int hungerWindow;
    private int hunger;

    public NutritionData(float defaultNutritionValue, float defaultDairyNutritionValue)
    {
        this.records = new LinkedList<>();
        this.defaultNutritionValue = defaultNutritionValue;
        this.defaultDairyNutritionValue = defaultDairyNutritionValue;
        this.nutrients = new float[5];
        this.hungerWindow = 0;
        this.hunger = PlayerInfo.MAX_HUNGER;

        calculateNutrition();
    }

    /**
     * @return The average of the nutrition values of the player
     */
    @Override
    public float getAverageNutrition()
    {
        return averageNutrients;
    }

    /**
     * @return The nutrient value, in [0, 1]
     */
    @Override
    public float getNutrient(Nutrient nutrient)
    {
        return nutrients[nutrient.ordinal()];
    }

    /**
     * @return An array of all nutrient values, each in [0, 1]
     */
    @Override
    public float[] getNutrients()
    {
        return nutrients;
    }

    /**
     * Set the current {@code hunger} value of the player, in {@code [0, PlayerInfo.MAX_HUNGER]}.
     * This may update the nutrition of the player.
     */
    @Override
    public void setHungerAndUpdate(int hunger)
    {
        setHunger(hunger);
        calculateNutrition();
    }

    /**
     * Set the current {@code hunger} value of the player, in {@code [0, PlayerInfo.MAX_HUNGER]}.
     * This <strong>must not</strong> update the nutrition of the player.
     */
    @Override
    public void setHunger(int hunger)
    {
        this.hunger = hunger;
    }

    /**
     * Sets data from a packet, received on client side. Only contains the array of nutrient values of the player, since only those are needed for the client
     */
    @Override
    public void onClientUpdate(float[] nutrients)
    {
        System.arraycopy(nutrients, 0, this.nutrients, 0, this.nutrients.length);
        updateAverageNutrients(); // Only need to update the average
    }

    /**
     * Applies nutrients of the food data to the player.
     * <p>
     * If the last meal you ate had hunger, and this one didn't have hunger, we will apply the meal
     * Use case: Milk drinking. We add milk as a meal if and only if you just ate something
     *
     * @param data The {@link FoodData} of the eaten food
     */
    public void addNutrients(FoodData data)
    {
        if (data.hunger() > 0 || records.isEmpty() || records.getFirst().hunger() > 0)
        {
            records.addFirst(data);
            calculateNutrition();
        }
    }

    /**
     * Redirects to {@link NutritionData#addNutrients(FoodData data)}
     */
    @Override
    public void addNutrients(FoodData data, int currentHunger)
    {
        addNutrients(data);
    }

    /**
     * @return The relevant data for computing nutrition values written to an NBT Tag
     */
    @Override
    public Tag writeToNbt()
    {
        return FoodData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, records).getOrThrow();
    }

    /**
     * Reads relevant data for computing nutrition values from an NBT Tag
     */
    @Override
    public void readFromNbt(@Nullable Tag nbt)
    {
        records.clear();
        FoodData.LIST_CODEC.decode(NbtOps.INSTANCE, nbt).ifSuccess(e -> records.addAll(e.getFirst()));
        calculateNutrition();
    }

    /**
     * Calculates the current nutrition based on the record of last eaten meals and the current hunger of the player
     */
    private void calculateNutrition()
    {
        // Reset
        Arrays.fill(this.nutrients, 0);

        // Consider any hunger that isn't currently satisfied (i.e. < 20) to be a zero-nutrient gap in the current window. This has the effect
        // of pushing nutrient decay forward, into the time when hunger decays, and making food consumption always push total nutrients positively
        //
        // This does make it almost impossible to stay at "peak nutrition", so we re-weight the average nutrients, so values above 0.95 are
        // effectively 1.0, as far as health is concerned.
        int runningHungerTotal = Math.max(PlayerInfo.MAX_HUNGER - hunger, 0);

        // Reload from config
        hungerWindow = TFCConfig.SERVER.nutritionRotationHungerWindow.get();
        for (int i = 0; i < records.size(); i++)
        {
            FoodData record = records.get(i);
            int nextHunger = record.hunger() + runningHungerTotal;
            if (nextHunger <= this.hungerWindow)
            {
                // Add weighted nutrition, keep moving
                updateAllNutrients(nutrients, j -> nutrients[j.ordinal()] + record.nutrient(j) * Math.max(record.hunger(), 1));
                runningHungerTotal = nextHunger;
            }
            else
            {
                // Calculate overshoot, weight appropriately, and exit
                float actualHunger = hungerWindow - runningHungerTotal;
                updateAllNutrients(nutrients, j -> nutrients[j.ordinal()] + record.nutrient(j) * actualHunger);

                // Remove any excess elements, this has the side effect of exiting the loop
                while (records.size() > i + 1)
                {
                    records.remove(i + 1);
                }
            }
        }

        // Average over hunger window, using default value if beyond the hunger window
        updateAllNutrients(nutrients, j -> nutrients[j.ordinal()] / hungerWindow);
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
        updateAllNutrients(nutrients, j -> Math.min(1, nutrients[j.ordinal()])); // Cap all nutrient averages at 1
        updateAverageNutrients(); // Also calculate overall average
    }

    /**
     * Recalculates the average nutrients of the player
     */
    private void updateAverageNutrients()
    {
        averageNutrients = 0;
        for (float nutrient : nutrients)
        {
            averageNutrients += nutrient;
        }
        averageNutrients /= Nutrient.TOTAL;
    }

    /**
     * Applies an operator on each nutrient value, currently used for summing up the nutrients and capping them to [0, 1]
     * @param array The nutrient array of the player
     * @param operator An operator that maps each {@link Nutrient} to a double
     */
    private void updateAllNutrients(float[] array, ToDoubleFunction<Nutrient> operator)
    {
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            array[nutrient.ordinal()] = (float) operator.applyAsDouble(nutrient);
        }
    }
}