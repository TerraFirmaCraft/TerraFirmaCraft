/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

/**
 * This is an interface representing a minimal object which can be constructed, tested against a unsealedStack, and has deferred loading and
 * initialization of all possible {@link Item}s that match the ingredient. Obviously, {@link Ingredient} satisfies this requirement,
 * but this interface exists for multiple possible scenarios that don't fully fall under that (i.e. "all items which match any input
 * to this ingredient")
 */
public interface KeyedIngredient extends Predicate<ItemStack>
{
    Collection<Item> keys();

    static KeyedIngredient of(Ingredient ingredient)
    {
        return of(ingredient, () -> RecipeHelpers.itemKeys(ingredient));
    }

    static <I extends RecipeInput, R extends Recipe<I>> KeyedIngredient ofMatchingAnyRecipeInput(Supplier<RecipeType<R>> type, IndirectHashCollection<Item, R> cache, Function<R, Ingredient> key)
    {
        return of(
            stack -> !cache.getAll(stack.getItem()).isEmpty(),
            () -> RecipeHelpers.getRecipes(Helpers.getUnsafeRecipeManager(), type)
                .stream()
                .flatMap(r -> RecipeHelpers.stream(key.apply(r.value())))
                .toList()
        );
    }

    static KeyedIngredient of(Predicate<ItemStack> test, Supplier<Collection<Item>> keys)
    {
        return new KeyedIngredient() {
            @Override
            public Collection<Item> keys()
            {
                return keys.get();
            }

            @Override
            public boolean test(ItemStack stack)
            {
                return test.test(stack);
            }
        };
    }
}
