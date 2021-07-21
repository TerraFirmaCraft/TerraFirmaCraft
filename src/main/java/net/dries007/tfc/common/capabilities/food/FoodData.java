package net.dries007.tfc.common.capabilities.food;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class FoodData implements INBTSerializable<CompoundNBT>
{
    private final float[] nutrients; // Nutritional values
    private int hunger; // Hunger. In TFC (for now) this is almost always 4
    private float saturation; // Saturation, only provided by some basic foods and meal bonuses
    private float water; // Water, provided by some foods
    private float decayModifier; // Decay modifier - higher = shorter decay
    private boolean buffed; // if this data instance has been buffed externally.

    public FoodData(int hunger, float water, float saturation, float grain, float fruit, float veg, float protein, float dairy, float decayModifier)
    {
        this(hunger, water, saturation, new float[] {grain, fruit, veg, protein, dairy}, decayModifier);
    }

    public FoodData(int hunger, float water, float saturation, float[] nutrients, float decayModifier)
    {
        this.hunger = hunger;
        this.water = water;
        this.saturation = saturation;
        this.nutrients = nutrients.clone();
        this.decayModifier = decayModifier;
    }

    public FoodData(CompoundNBT nbt)
    {
        this.nutrients = new float[5];
        deserializeNBT(nbt);
    }

    public float[] getNutrients()
    {
        return nutrients;
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

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("food", hunger);
        nbt.putFloat("sat", saturation);
        nbt.putFloat("water", water);
        nbt.putFloat("decay", decayModifier);
        nbt.putFloat("grain", nutrients[Nutrient.GRAIN.ordinal()]);
        nbt.putFloat("veg", nutrients[Nutrient.VEGETABLES.ordinal()]);
        nbt.putFloat("fruit", nutrients[Nutrient.FRUIT.ordinal()]);
        nbt.putFloat("meat", nutrients[Nutrient.PROTEIN.ordinal()]);
        nbt.putFloat("dairy", nutrients[Nutrient.DAIRY.ordinal()]);
        nbt.putBoolean("buffed", buffed);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        hunger = nbt.getInt("food");
        saturation = nbt.getFloat("sat");
        water = nbt.getFloat("water");
        decayModifier = nbt.getFloat("decay");
        nutrients[Nutrient.GRAIN.ordinal()] = nbt.getFloat("grain");
        nutrients[Nutrient.VEGETABLES.ordinal()] = nbt.getFloat("veg");
        nutrients[Nutrient.FRUIT.ordinal()] = nbt.getFloat("fruit");
        nutrients[Nutrient.PROTEIN.ordinal()] = nbt.getFloat("meat");
        nutrients[Nutrient.DAIRY.ordinal()] = nbt.getFloat("dairy");
        buffed = nbt.getBoolean("buffed");
    }

    public FoodData copy()
    {
        return new FoodData(hunger, water, saturation, nutrients, decayModifier);
    }

    public void applyBuff(FoodData buff)
    {
        if (!buffed)
        {
            buffed = true;
            for (Nutrient nutrient : Nutrient.VALUES)
            {
                nutrients[nutrient.ordinal()] += buff.nutrients[nutrient.ordinal()];
            }
        }
    }
}
