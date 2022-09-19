/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

/**
 * Handling for simple Ingredient -> ItemStack recipes
 */
public abstract class SimpleItemRecipe implements ISimpleRecipe<ItemStackInventory>
{
    protected final ResourceLocation id;
    protected final Ingredient ingredient;
    protected final ItemStackProvider result;

    public SimpleItemRecipe(ResourceLocation id, Ingredient ingredient, ItemStackProvider result)
    {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(this.getIngredient().getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }

    @Override
    public boolean matches(ItemStackInventory wrapper, Level level)
    {
        return this.getIngredient().test(wrapper.getStack());
    }

    @Override
    public ItemStack getResultItem()
    {
        return result.getEmptyStack();
    }

    public ItemStackProvider getResult()
    {
        return result;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public boolean isSpecial()
    {
        return result.dependsOnInput();
    }

    @Override
    public ItemStack assemble(ItemStackInventory wrapper)
    {
        return result.getSingleStack(wrapper.getStack());
    }

    public Ingredient getIngredient()
    {
        return ingredient;
    }

    public static class Serializer<R extends SimpleItemRecipe> extends RecipeSerializerImpl<R>
    {
        private final SimpleItemRecipe.Serializer.Factory<R> factory;

        public Serializer(SimpleItemRecipe.Serializer.Factory<R> factory)
        {
            this.factory = factory;
        }

        @Override
        public R fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Ingredient ingredient = Ingredient.fromJson(JsonHelpers.get(json, "ingredient"));
            final ItemStackProvider result = ItemStackProvider.fromJson(GsonHelper.getAsJsonObject(json, "result"));
            return factory.create(recipeId, ingredient, result);
        }

        @Nullable
        @Override
        public R fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final Ingredient ingredient = Ingredient.fromNetwork(buffer);
            final ItemStackProvider result = ItemStackProvider.fromNetwork(buffer);
            return factory.create(recipeId, ingredient, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, R recipe)
        {
            recipe.getIngredient().toNetwork(buffer);
            recipe.getResult().toNetwork(buffer);
        }

        public interface Factory<R extends SimpleItemRecipe>
        {
            R create(ResourceLocation id, Ingredient ingredient, ItemStackProvider result);
        }
    }
}
