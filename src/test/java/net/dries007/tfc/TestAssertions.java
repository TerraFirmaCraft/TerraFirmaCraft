/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonElement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidStack;

import org.junit.jupiter.api.Assertions;


public final class TestAssertions
{
    public static void assertEquals(FluidStack expected, FluidStack actual)
    {
        Assertions.assertEquals(wrap(expected), wrap(actual));
    }

    public static void assertEquals(ItemStack expected, ItemStack actual)
    {
        Assertions.assertEquals(wrap(expected), wrap(actual));
    }

    public static void assertEquals(Ingredient expected, Ingredient actual)
    {
        Assertions.assertEquals(wrap(expected), wrap(actual));
    }

    public static void assertEquals(Recipe<?> expected, Recipe<?> actual)
    {
        Assertions.assertEquals(wrap(expected), wrap(actual));
    }


    private static Type wrap(FluidStack stack)
    {
        return new Named<>(stack, "%d mB of %s".formatted(stack.getAmount(), stack.getFluid().getRegistryName()));
    }

    private static Type wrap(ItemStack stack)
    {
        record TItemStack(Item item, int count, CompoundTag tag) {}
        return new Named<>(new TItemStack(stack.getItem(), stack.getCount(), stack.getTag()), stack.toString());
    }

    private static Type wrap(Ingredient ingredient)
    {
        record TIngredient(Class<?> clazz, IIngredientSerializer<? extends Ingredient> serializer, JsonElement json, List<Type> stacks) implements Type {}
        return new TIngredient(ingredient.getClass(), ingredient.getSerializer(), ingredient.toJson(), wrap(ingredient.getItems(), TestAssertions::wrap));
    }

    private static Type wrap(Recipe<?> recipe)
    {
        record TRecipe(Class<?> clazz, ResourceLocation id, String group, Type result, List<Type> ingredients) implements Type {}
        return new TRecipe(recipe.getClass(), recipe.getId(), recipe.getGroup(), wrap(recipe.getResultItem()), wrap(recipe.getIngredients(), TestAssertions::wrap));
    }

    private static <T> List<Type> wrap(T[] array, Function<T, Type> wrap)
    {
        return Arrays.stream(array).map(wrap).toList();
    }

    private static <T> List<Type> wrap(List<T> list, Function<T, Type> wrap)
    {
        return list.stream().map(wrap).toList();
    }

    interface Type {}

    record Named<T>(T t, String name) implements Type
    {
        @Override
        public String toString()
        {
            return name;
        }
    }
}
