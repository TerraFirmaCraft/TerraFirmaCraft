/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.util.ItemDefinition;
import net.dries007.tfc.util.JsonHelpers;

public class FoodDefinition extends ItemDefinition
{
    private final FoodData data;

    public FoodDefinition(ResourceLocation id, JsonObject json)
    {
        super(id, json);

        final int hunger = JsonHelpers.getAsInt(json, "hunger", 4);
        final float saturation = JsonHelpers.getAsFloat(json, "saturation", 0);
        final float water = JsonHelpers.getAsFloat(json, "water", 0);
        final float decayModifier = JsonHelpers.getAsFloat(json, "decay_modifier", 1);

        final float[] nutrition = new float[Nutrient.TOTAL];
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            nutrition[nutrient.ordinal()] = JsonHelpers.getAsFloat(json, nutrient.getSerializedName(), 0);
        }

        this.data = FoodData.create(hunger, water, saturation, nutrition, decayModifier);
    }

    public FoodDefinition(ResourceLocation id, FriendlyByteBuf buffer)
    {
        super(id, Ingredient.fromNetwork(buffer));

        final int hunger = buffer.readVarInt();
        final float saturation = buffer.readFloat();
        final float water = buffer.readFloat();
        final float decayModifier = buffer.readFloat();

        final float[] nutrition = new float[Nutrient.TOTAL];
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            nutrition[nutrient.ordinal()] = buffer.readFloat();
        }

        this.data = FoodData.create(hunger, water, saturation, nutrition, decayModifier);
    }

    public void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);

        buffer.writeVarInt(data.hunger());
        buffer.writeFloat(data.saturation());
        buffer.writeFloat(data.water());
        buffer.writeFloat(data.decayModifier());

        for (Nutrient nutrient : Nutrient.VALUES)
        {
            buffer.writeFloat(data.nutrient(nutrient));
        }
    }

    public FoodData getData()
    {
        return data;
    }
}
