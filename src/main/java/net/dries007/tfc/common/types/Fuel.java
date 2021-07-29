/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.Helpers;

public class Fuel
{
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final int duration;
    private final float temperature;

    public Fuel(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.ingredient = Ingredient.fromJson(Helpers.getJsonAsAny(json, "ingredient"));
        this.duration = JSONUtils.getAsInt(json, "duration");
        this.temperature = JSONUtils.getAsFloat(json, "temperature");
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public boolean isValid(ItemStack stack)
    {
        return ingredient.test(stack);
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(this.ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }

    public int getDuration()
    {
        return duration;
    }

    public float getTemperature()
    {
        return temperature;
    }
}
