/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;

/**
 * Most of these are copied from {@link net.minecraft.world.item.crafting.ShapedRecipe} and {@link net.minecraft.world.item.crafting.ShapelessRecipe} due to access level concerns
 */
public final class RecipeHelpers
{
    private static final ThreadLocal<Iterable<ItemStack>> CRAFTING_INPUT = ThreadLocal.withInitial(() -> Collections::emptyIterator);
    private static final ThreadLocal<Boolean> UNIQUE_INPUT = ThreadLocal.withInitial(() -> false);

    /**
     * Neo does not correctly mark this as nullable
     * @return The crafting player
     */
    @Nullable
    @SuppressWarnings("DataFlowIssue")
    public static Player getCraftingPlayer()
    {
        return CommonHooks.getCraftingPlayer();
    }

    public static void clearCraftingInput()
    {
        CRAFTING_INPUT.set(Collections::emptyIterator);
    }

    public static void setCraftingInput(CraftingInput input)
    {
        CRAFTING_INPUT.set(Helpers.iterate(input));
    }

    public static void setCraftingInput(IItemHandler inventory, int startSlotInclusive, int endSlotExclusive)
    {
        CRAFTING_INPUT.set(Helpers.iterate(inventory, startSlotInclusive, endSlotExclusive));
    }

    /**
     * Crafting inputs are any item stacks provided to the input of a crafting operation. This allows {@link ItemStackProvider}s to reference all inputs,
     * for instance taking the lowest expiry date of all inputs.
     *
     * @return An iterator over item stacks which are provided to the input.
     */
    public static Iterable<ItemStack> getCraftingInput()
    {
        return CRAFTING_INPUT.get();
    }

    /**
     * @return {@code true} if this is the one unique call to a given {@link ItemStackProvider} used as a remainder.
     */
    public static boolean isUniqueInput()
    {
        return UNIQUE_INPUT.get();
    }

    public static NonNullList<ItemStack> getRemainderItemsWithProvider(CraftingInput input, ItemStackProvider provider)
    {
        final NonNullList<ItemStack> results = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < results.size(); i++)
        {
            UNIQUE_INPUT.set(i == 0);

            final ItemStack stack = input.getItem(i);
            final ItemStack outputStack = provider.getSingleStack(stack);

            if (!outputStack.isEmpty())
            {
                results.set(i, outputStack);
            }
        }

        return results;
    }

    /**
     * Tries to access the {@code result} field, by providing a {@code null} registry access. Only use this if you are sure of the
     * concrete type of the recipe, that this will not crash!
     */
    @SuppressWarnings("ConstantConditions")
    public static ItemStack getResultUnsafe(CraftingRecipe recipe)
    {
        return recipe.getResultItem(null);
    }

    public static Collection<Item> itemKeys(Ingredient ingredient)
    {
        return stream(ingredient).toList();
    }

    public static Stream<Item> stream(Ingredient ingredient)
    {
        return Arrays.stream(ingredient.getItems()).map(ItemStack::getItem);
    }

    public static Collection<Fluid> fluidKeys(FluidIngredient ingredient)
    {
        return stream(ingredient).toList();
    }

    public static Stream<Fluid> stream(SizedFluidIngredient ingredient)
    {
        return stream(ingredient.ingredient());
    }

    public static Stream<Fluid> stream(FluidIngredient ingredient)
    {
        return Arrays.stream(ingredient.getStacks()).map(FluidStack::getFluid);
    }

    public static int dissolveRowColumn(int row, int column, int width)
    {
        return column + row * width;
    }

    public static int translateMatch(ShapedRecipe recipe, int targetIndex, CraftingInput inventory)
    {
        return translateMatch(recipe.getIngredients(), inventory, recipe.getWidth(), recipe.getHeight(), targetIndex);
    }

    public static int translateMatch(NonNullList<Ingredient> recipeItems, CraftingInput input, int width, int height, int targetIndex)
    {
        for (int startCol = 0; startCol <= input.width() - width; ++startCol)
        {
            for (int startRow = 0; startRow <= input.height() - height; ++startRow)
            {
                if (matches(recipeItems, input, startCol, startRow, true, width, height))
                {
                    // Solving the following equations to find the index into the match:
                    //   targetIndex = width - col - 1 + row * width
                    //   row = invRow - startRow
                    //   col = invCol - startCol
                    //   recipeIndex = invCol + invRow * invWidth
                    // Known that row < height, col < width
                    //
                    // => targetIndex % width = (-col - 1) % width = width - col - 1
                    // => col = width - 1 - (targetIndex % width)
                    // => targetIndex / width = (width - col - 1) / width + row = row
                    // => row = targetIndex / width
                    // => recipeIndex = ((width - 1 - (targetIndex % width)) + startCol) + ((targetIndex / width) + startRow) * invWidth
                    return ((width - 1 - (targetIndex % width)) + startCol) + ((targetIndex / width) + startRow) * input.getWidth();
                }
                if (matches(recipeItems, input, startCol, startRow, false, width, height))
                {
                    // Solving the same set of equations, but in the non-mirrored case
                    //   targetIndex = col + row * width
                    //
                    // => col = targetIndex % width
                    // => row = targetIndex / width
                    // => recipeIndex = ((targetIndex % width) + startCol) + ((targetIndex / width) + startRow) * invWidth
                    return ((targetIndex % width) + startCol) + ((targetIndex / width) + startRow) * input.getWidth();
                }
            }
        }
        return -1;
    }

    private static boolean matches(NonNullList<Ingredient> recipeItems, CraftingInput input, int startCol, int startRow, boolean mirrored, int width, int height)
    {
        for (int invCol = 0; invCol < input.width(); ++invCol)
        {
            for (int invRow = 0; invRow < input.height(); ++invRow)
            {
                final int col = invCol - startCol, row = invRow - startRow;
                Ingredient ingredient = Ingredient.EMPTY;
                if (col >= 0 && row >= 0 && col < width && row < height)
                {
                    if (mirrored)
                    {
                        ingredient = recipeItems.get(width - col - 1 + row * width);
                    }
                    else
                    {
                        ingredient = recipeItems.get(col + row * width);
                    }
                }
                if (!ingredient.test(input.getItem(invCol + invRow * input.width())))
                {
                    return false;
                }
            }
        }
        return true;
    }
}
