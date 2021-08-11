/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.size;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import net.dries007.tfc.util.Helpers;

public class ItemSizeDefinition implements IItemSize
{
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final Size size;
    private final Weight weight;

    public ItemSizeDefinition(ResourceLocation id, JsonObject obj)
    {
        this.id = id;
        this.ingredient = CraftingHelper.getIngredient(Objects.requireNonNull(obj.get("ingredient")));
        this.size = Helpers.getEnumFromJson(obj, "size", Size.class, Size.NORMAL);
        this.weight = Helpers.getEnumFromJson(obj, "weight", Weight.class, Weight.MEDIUM);
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

    @Override
    public Size getSize(ItemStack stack)
    {
        return size;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return weight;
    }
}
