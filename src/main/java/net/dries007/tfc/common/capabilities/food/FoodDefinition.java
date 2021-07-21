package net.dries007.tfc.common.capabilities.food;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.common.ItemDefinition;

public class FoodDefinition extends ItemDefinition
{
    private final FoodData data;

    public FoodDefinition(ResourceLocation id, JsonObject json)
    {
        super(id, json);

        final int hunger = JSONUtils.getAsInt(json, "hunger", 4);
        final float saturation = JSONUtils.getAsFloat(json, "saturation", 0);
        final float water = JSONUtils.getAsFloat(json, "water", 0);
        final float decayModifier = JSONUtils.getAsFloat(json, "decay_modifier", 1);

        final float[] nutrition = new float[Nutrient.TOTAL];
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            nutrition[nutrient.ordinal()] = JSONUtils.getAsFloat(json, nutrient.getSerializedName(), 0);
        }

        this.data = new FoodData(hunger, water, saturation, nutrition, decayModifier);
    }

    public FoodData getData()
    {
        return data;
    }
}
