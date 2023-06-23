/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import net.dries007.tfc.util.JsonHelpers;

/**
 * An immutable collection of data about a certain piece of food.
 *
 * @param hunger Hunger amount. In TFC, it is almost always 4.
 * @param saturation Saturation, only provided by some basic foods and meal bonuses.
 * @param water Water, provided by some foods.
 * @param decayModifier Decay modifier - higher = shorter decay.
 */
public record FoodData(int hunger, float water, float saturation, float grain, float fruit, float vegetables, float protein, float dairy, float decayModifier)
{
    public static final FoodData EMPTY = new FoodData(0, 0, 0, 0, 0, 0, 0, 0, 0);

    public static FoodData create(int hunger, float water, float saturation, float[] nutrients, float decayModifier)
    {
        return new FoodData(hunger, water, saturation, nutrients[0], nutrients[1], nutrients[2], nutrients[3], nutrients[4], decayModifier);
    }

    public static FoodData decode(FriendlyByteBuf buffer)
    {
        final int hunger = buffer.readVarInt();
        final float saturation = buffer.readFloat();
        final float water = buffer.readFloat();
        final float decayModifier = buffer.readFloat();

        final float[] nutrition = new float[Nutrient.TOTAL];
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            nutrition[nutrient.ordinal()] = buffer.readFloat();
        }

        return FoodData.create(hunger, water, saturation, nutrition, decayModifier);
    }

    public static FoodData read(JsonObject json)
    {
        final int hunger = JsonHelpers.getAsInt(json, "hunger", 4);
        final float saturation = JsonHelpers.getAsFloat(json, "saturation", 0);
        final float water = JsonHelpers.getAsFloat(json, "water", 0);
        final float decayModifier = JsonHelpers.getAsFloat(json, "decay_modifier", 1);

        final float[] nutrition = new float[Nutrient.TOTAL];
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            nutrition[nutrient.ordinal()] = JsonHelpers.getAsFloat(json, nutrient.getSerializedName(), 0);
        }

        return FoodData.create(hunger, water, saturation, nutrition, decayModifier);
    }

    public static FoodData read(CompoundTag nbt)
    {
        return new FoodData(
            nbt.getInt("food"),
            nbt.getFloat("water"),
            nbt.getFloat("sat"),
            nbt.getFloat("grain"),
            nbt.getFloat("fruit"),
            nbt.getFloat("veg"),
            nbt.getFloat("meat"),
            nbt.getFloat("dairy"),
            nbt.getFloat("decay")
        );
    }

    public float nutrient(Nutrient nutrient)
    {
        return switch (nutrient)
            {
                case GRAIN -> grain;
                case FRUIT -> fruit;
                case VEGETABLES -> vegetables;
                case PROTEIN -> protein;
                case DAIRY -> dairy;
            };
    }

    public float[] nutrients()
    {
        return new float[] {grain, fruit, vegetables, protein, dairy};
    }

    public CompoundTag write()
    {
        final CompoundTag nbt = new CompoundTag();
        nbt.putInt("food", hunger);
        nbt.putFloat("sat", saturation);
        nbt.putFloat("water", water);
        nbt.putFloat("decay", decayModifier);
        nbt.putFloat("grain", grain);
        nbt.putFloat("veg", vegetables);
        nbt.putFloat("fruit", fruit);
        nbt.putFloat("meat", protein);
        nbt.putFloat("dairy", dairy);
        return nbt;
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(hunger);
        buffer.writeFloat(saturation);
        buffer.writeFloat(water);
        buffer.writeFloat(decayModifier);

        for (Nutrient nutrient : Nutrient.VALUES)
        {
            buffer.writeFloat(nutrient(nutrient));
        }
    }
}
