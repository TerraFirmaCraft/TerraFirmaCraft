/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.util.ItemDefinition;

public class FoodDefinition extends ItemDefinition
{
    private final FoodData data;

    public FoodDefinition(ResourceLocation id, JsonObject json)
    {
        super(id, json);
        this.data = FoodData.read(json);
    }

    public FoodDefinition(ResourceLocation id, FriendlyByteBuf buffer)
    {
        super(id, Ingredient.fromNetwork(buffer));
        this.data = FoodData.decode(buffer);
    }

    public void encode(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);
        data.encode(buffer);
    }

    public FoodData getData()
    {
        return data;
    }
}
