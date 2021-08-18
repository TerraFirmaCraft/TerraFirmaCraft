/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.Helpers;

/**
 * Top level class for item-based 'definition' objects that are defined in JSON
 *
 * @see net.dries007.tfc.util.data.DataManager
 */
public class ItemDefinition
{
    protected final ResourceLocation id;
    protected final Ingredient ingredient;

    protected ItemDefinition(ResourceLocation id, JsonObject json)
    {
        this(id, Ingredient.fromJson(Helpers.getJsonAsAny(json, "ingredient")));
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
