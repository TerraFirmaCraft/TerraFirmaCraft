/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.function.BiFunction;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;

import net.dries007.tfc.util.Helpers;

/**
 * A recipe that delegates to an internal recipe, held in the "recipe" field.
 * The internal recipe must obviously be compatible but we have no way of assuring that, so we rely on users to not screw this up
 * This is more powerful than creating a recipe type for every combination of modifier (e.g. damage inputs, apply food, etc.)
 *
 * @param <R> The recipe type this must delegate to. Either for requiring shaped, or shapeless recipes
 */
public abstract class DelegateRecipe<R extends Recipe<C>, C extends Container> implements IRecipeDelegate<C>
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

    public static class Serializer<C extends Container> extends RecipeSerializerImpl<DelegateRecipe<?, C>>
    {
        public static <C extends Container> Serializer<C> shapeless(BiFunction<ResourceLocation, Recipe<C>, DelegateRecipe<Recipe<C>, C>> factory)
        {
            return new Serializer<>((id, delegate) -> {
                if (delegate instanceof IShapedRecipe)
                {
                    throw new JsonParseException("Mixing shapeless delegate recipe type with shaped delegate, not allowed!");
                }
                return factory.apply(id, delegate);
            });
        }

        public static <C extends Container> Serializer<C> shaped(BiFunction<ResourceLocation, IShapedRecipe<C>, DelegateRecipe<IShapedRecipe<C>, C>> factory)
        {
            return new Serializer<>((id, delegate) -> {
                if (!(delegate instanceof IShapedRecipe))
                {
                    throw new JsonParseException("Mixing shaped delegate recipe type with shapeless delegate, not allowed!");
                }
                return factory.apply(id, (IShapedRecipe<C>) delegate);
            });
        }

        private final BiFunction<ResourceLocation, Recipe<C>, DelegateRecipe<?, C>> factory;

        protected Serializer(BiFunction<ResourceLocation, Recipe<C>, DelegateRecipe<?, C>> factory)
        {
            this.factory = factory;
        }

        @Override
        @SuppressWarnings("unchecked")
        public DelegateRecipe<?, C> fromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context)
        {
            Recipe<C> internal = (Recipe<C>) RecipeManager.fromJson(DELEGATE, GsonHelper.getAsJsonObject(json, "recipe"), context);
            return factory.apply(recipeId, internal);
        }

        @Override
        public DelegateRecipe<?, C> fromJson(ResourceLocation recipeId, JsonObject json)
        {
            return fromJson(recipeId, json, ICondition.IContext.EMPTY);
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public DelegateRecipe<?, C> fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            Recipe<C> internal = (Recipe<C>) ClientboundUpdateRecipesPacket.fromNetwork(buffer);
            return factory.apply(recipeId, internal);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DelegateRecipe<?, C> recipe)
        {
            ClientboundUpdateRecipesPacket.toNetwork(buffer, recipe.getDelegate());
        }
    }
}
