/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.function.BiFunction;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.Helpers;

/**
 * A recipe that delegates to an internal recipe, held in the "recipe" field.
 * The internal recipe must obviously be compatible but we have no way of assuring that, so we rely on users to not screw this up
 * This is more powerful than creating a recipe type for every combination of modifier (e.g. damage inputs, apply food, etc.)
 */
public abstract class DelegatingRecipe<C extends IInventory> implements IDelegatingRecipe<C>
{
    public static final ResourceLocation DELEGATE = Helpers.identifier("delegate");

    private final ResourceLocation id;
    private final IRecipe<C> recipe;

    protected DelegatingRecipe(ResourceLocation id, IRecipe<C> recipe)
    {
        this.id = id;
        this.recipe = recipe;
    }

    @Override
    public IRecipe<C> getInternal()
    {
        return recipe;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    protected static class Serializer<C extends IInventory, R extends DelegatingRecipe<C>> extends RecipeSerializer<R>
    {
        private final BiFunction<ResourceLocation, IRecipe<C>, R> factory;

        protected Serializer(BiFunction<ResourceLocation, IRecipe<C>, R> factory)
        {
            this.factory = factory;
        }

        @Override
        @SuppressWarnings("unchecked")
        public R read(ResourceLocation recipeId, JsonObject json)
        {
            IRecipe<?> internal = RecipeManager.deserializeRecipe(DELEGATE, JSONUtils.getJsonObject(json, "recipe"));
            return factory.apply(recipeId, (IRecipe<C>) internal);
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public R read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            IRecipe<?> internal = SUpdateRecipesPacket.func_218772_c(buffer);//from network
            return factory.apply(recipeId, (IRecipe<C>) internal);
        }

        @Override
        public void write(PacketBuffer buffer, R recipe)
        {
            SUpdateRecipesPacket.func_218771_a(recipe.getInternal(), buffer);//to network
        }
    }
}
