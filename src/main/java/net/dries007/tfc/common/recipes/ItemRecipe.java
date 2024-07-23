/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Collection;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

/**
 * Handling for simple Ingredient -> ItemStack recipes
 */
public abstract class ItemRecipe implements INoopInputRecipe, IRecipePredicate<ItemStack>
{
    protected final Ingredient ingredient;
    protected final ItemStackProvider result;

    protected ItemRecipe(Ingredient ingredient, ItemStackProvider result)
    {
        this.ingredient = ingredient;
        this.result = result;
    }

    /**
     * @return {@code true} if the recipe matches the {@code input}
     */
    @Override
    public boolean matches(ItemStack input)
    {
        return ingredient.test(input);
    }

    /**
     * @return The output of this recipe with the provided {@code input}
     */
    public ItemStack assemble(ItemStack input)
    {
        return result.getSingleStack(input);
    }

    @Override
    public ItemStack getResultItem(@Nullable HolderLookup.Provider registries)
    {
        return result.getEmptyStack();
    }

    public Collection<Item> getValidItems()
    {
        return RecipeHelpers.itemKeys(ingredient);
    }

    public final Ingredient getIngredient()
    {
        return ingredient;
    }

    public final ItemStackProvider getResult()
    {
        return result;
    }

    @Override
    public boolean isSpecial()
    {
        return result.dependsOnInput();
    }
}
