/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

/**
 * Recipe that will never show up in a crafting grid. Used to override and disable any kind of recipe.
 */
public class NoopRecipe<C extends Container> implements Recipe<C>
{
    public static final NoopRecipe<?> INSTANCE = new NoopRecipe<>();

    @Override
    public boolean matches(C container, Level level)
    {
        return false;
    }

    @Override
    public ItemStack assemble(C container)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int a, int b)
    {
        return false;
    }

    @Override
    public ItemStack getResultItem()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return new ResourceLocation("minecraft", "empty");
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.NOOP.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.NOOP.get();
    }

    @Override
    public boolean isSpecial()
    {
        return true;
    }

    public static class Serializer extends RecipeSerializerImpl<NoopRecipe<?>>
    {
        @Override
        public NoopRecipe<?> fromJson(ResourceLocation id, JsonObject json)
        {
            return INSTANCE;
        }

        @Nullable
        @Override
        public NoopRecipe<?> fromNetwork(ResourceLocation id, FriendlyByteBuf buf)
        {
            return INSTANCE;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, NoopRecipe<?> recipe) { }
    }
}
