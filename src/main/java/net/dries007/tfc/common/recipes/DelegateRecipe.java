/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.crafting.IShapedRecipe;

import net.dries007.tfc.util.Helpers;

/**
 * A recipe that delegates to an internal recipe, held in the "recipe" field.
 * The internal recipe must obviously be compatible but we have no way of assuring that, so we rely on users to not screw this up
 * This is more powerful than creating a recipe type for every combination of modifier (e.g. damage inputs, apply food, etc.)
 *
 * @param <R> The recipe type this must delegate to. Either for requiring shaped, or shapeless recipes
 */
public abstract class DelegateRecipe<R extends IRecipe<C>, C extends IInventory> implements IRecipeDelegate<C>
{
    public static final ResourceLocation DELEGATE = Helpers.identifier("delegate");

    private final ResourceLocation id;
    private final R recipe;

    protected DelegateRecipe(ResourceLocation id, R recipe)
    {
        this.id = id;
        this.recipe = recipe;
    }

    @Override
    public R getDelegate()
    {
        return recipe;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    protected static class Serializer<C extends IInventory> extends RecipeSerializer<DelegateRecipe<?, C>>
    {
        public static <C extends IInventory> Serializer<C> shapeless(BiFunction<ResourceLocation, IRecipe<C>, DelegateRecipe<IRecipe<C>, C>> factory)
        {
            return new Serializer<>((id, delegate) -> {
                if (delegate instanceof IShapedRecipe)
                {
                    throw new JsonParseException("Mixing shapeless delegate recipe type with shaped delegate, not allowed!");
                }
                return factory.apply(id, delegate);
            });
        }

        public static <C extends IInventory> Serializer<C> shaped(BiFunction<ResourceLocation, IShapedRecipe<C>, DelegateRecipe<IShapedRecipe<C>, C>> factory)
        {
            return new Serializer<>((id, delegate) -> {
                if (!(delegate instanceof IShapedRecipe))
                {
                    throw new JsonParseException("Mixing shaped delegate recipe type with shapeless delegate, not allowed!");
                }
                return factory.apply(id, (IShapedRecipe<C>) delegate);
            });
        }

        private final BiFunction<ResourceLocation, IRecipe<C>, DelegateRecipe<?, C>> factory;

        protected Serializer(BiFunction<ResourceLocation, IRecipe<C>, DelegateRecipe<?, C>> factory)
        {
            this.factory = factory;
        }

        @Override
        @SuppressWarnings("unchecked")
        public DelegateRecipe<?, C> fromJson(ResourceLocation recipeId, JsonObject json)
        {
            IRecipe<C> internal = (IRecipe<C>) RecipeManager.fromJson(DELEGATE, JSONUtils.getAsJsonObject(json, "recipe"));
            return factory.apply(recipeId, internal);
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public DelegateRecipe<?, C> fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
        {
            IRecipe<C> internal = (IRecipe<C>) SUpdateRecipesPacket.fromNetwork(buffer);
            return factory.apply(recipeId, internal);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, DelegateRecipe<?, C> recipe)
        {
            SUpdateRecipesPacket.toNetwork(recipe.getDelegate(), buffer);
        }
    }
}
