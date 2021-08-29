/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import javax.annotation.concurrent.Immutable;

import net.minecraft.nbt.CompoundTag;

@Immutable
public class FoodRecord
{
    private final float[] nutrients; // Nutritional values
    private final int hunger; // Hunger. In TFC (for now) this is almost always 4
    private final float saturation; // Saturation, only provided by some basic foods and meal bonuses
    private final float water; // Water, provided by some foods
    private final float decayModifier; // Decay modifier - higher = shorter decay

    public FoodRecord(int hunger, float water, float saturation, float grain, float fruit, float veg, float protein, float dairy, float decayModifier)
    {
        this(hunger, water, saturation, new float[] {grain, fruit, veg, protein, dairy}, decayModifier);
    }

    public FoodRecord(int hunger, float water, float saturation, float[] nutrients, float decayModifier)
    {
        this.hunger = hunger;
        this.water = water;
        this.saturation = saturation;
        this.nutrients = nutrients.clone();
        this.decayModifier = decayModifier;
    }

    public FoodRecord(CompoundTag nbt)
    {
        hunger = nbt.getInt("food");
        saturation = nbt.getFloat("sat");
        water = nbt.getFloat("water");
        decayModifier = nbt.getFloat("decay");
        nutrients = new float[5];
        nutrients[Nutrient.GRAIN.ordinal()] = nbt.getFloat("grain");
        nutrients[Nutrient.VEGETABLES.ordinal()] = nbt.getFloat("veg");
        nutrients[Nutrient.FRUIT.ordinal()] = nbt.getFloat("fruit");
        nutrients[Nutrient.PROTEIN.ordinal()] = nbt.getFloat("meat");
        nutrients[Nutrient.DAIRY.ordinal()] = nbt.getFloat("dairy");
    }

    public float getNutrient(Nutrient nutrient)
    {
        return nutrients[nutrient.ordinal()];
    }

    public CompoundTag write()
    {
        final CompoundTag nbt = new CompoundTag();
        nbt.putInt("food", hunger);
        nbt.putFloat("sat", saturation);
        nbt.putFloat("water", water);
        nbt.putFloat("decay", decayModifier);
        nbt.putFloat("grain", nutrients[Nutrient.GRAIN.ordinal()]);
        nbt.putFloat("veg", nutrients[Nutrient.VEGETABLES.ordinal()]);
        nbt.putFloat("fruit", nutrients[Nutrient.FRUIT.ordinal()]);
        nbt.putFloat("meat", nutrients[Nutrient.PROTEIN.ordinal()]);
        nbt.putFloat("dairy", nutrients[Nutrient.DAIRY.ordinal()]);
        return nbt;
    }

    public int getHunger()
    {
        return hunger;
    }

    public float getSaturation()
    {
        return saturation;
    }

    public float getWater()
    {
        return water;
    }

    public float getDecayModifier()
    {
        return decayModifier;
    }

    float getNutrient(int i)
    {
        return nutrients[i];
    }
}
