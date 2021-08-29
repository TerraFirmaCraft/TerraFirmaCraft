/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ItemDefinition
{
    protected final ResourceLocation id;
    protected final Ingredient ingredient;

    protected ItemDefinition(ResourceLocation id, JsonObject json)
    {
        this(id, Ingredient.fromJson(JsonHelpers.get(json, "ingredient")));
    }

    protected ItemDefinition(ResourceLocation id, Ingredient ingredient)
    {
        this.id = id;
        this.ingredient = ingredient;
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public boolean matches(ItemStack stack)
    {
        return ingredient.test(stack);
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }
}
