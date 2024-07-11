/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.util.GsonHelper;
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
        return Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).toList();
    }

    public static Collection<Fluid> fluidKeys(FluidIngredient ingredient)
    {
        return Arrays.stream(ingredient.getStacks()).map(FluidStack::getFluid).toList();
    }

    public static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight)
    {
        final NonNullList<Ingredient> recipeItems = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
        final Set<String> keySet = Sets.newHashSet(keys.keySet());
        keySet.remove(" ");

        for (int row = 0; row < pattern.length; ++row)
        {
            for (int column = 0; column < pattern[row].length(); ++column)
            {
                final String key = pattern[row].substring(column, column + 1);
                final Ingredient ingredient = keys.get(key);
                if (ingredient == null)
                {
                    throw new JsonSyntaxException("Pattern references symbol '" + key + "' but it's not defined in the key");
                }

                keySet.remove(key);
                recipeItems.set(dissolveRowColumn(row, column, patternWidth), ingredient);
            }
        }

        if (!keySet.isEmpty())
        {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keySet);
        }
        else
        {
            return recipeItems;
        }
    }

    public static String[] shrink(String... pattern)
    {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < pattern.length; ++i1)
        {
            String s = pattern[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0)
            {
                if (k == i1)
                {
                    ++k;
                }

                ++l;
            }
            else
            {
                l = 0;
            }
        }

        if (pattern.length == l)
        {
            return new String[0];
        }
        else
        {
            final String[] newPattern = new String[pattern.length - l - k];
            for (int k1 = 0; k1 < newPattern.length; ++k1)
            {
                newPattern[k1] = pattern[k1 + k].substring(i, j + 1);
            }
            return newPattern;
        }
    }

    public static String[] patternFromJson(JsonArray array)
    {
        return patternFromJson(array, 3, 3);
    }

    public static String[] patternFromJson(JsonArray array, int maxHeight, int maxWidth)
    {
        String[] pattern = new String[array.size()];
        if (pattern.length > maxHeight)
        {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + maxHeight + " is maximum");
        }
        else if (pattern.length == 0)
        {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        }
        else
        {
            for (int i = 0; i < pattern.length; ++i)
            {
                final String row = GsonHelper.convertToString(array.get(i), "pattern[" + i + "]");
                if (row.length() > maxWidth)
                {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + maxWidth + " is maximum");
                }

                if (i > 0 && pattern[0].length() != row.length())
                {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }
                pattern[i] = row;
            }
            return pattern;
        }
    }

    /**
     * Returns a key json object as a Java HashMap.
     */
    public static Map<String, Ingredient> keyFromJson(JsonObject json)
    {
        final Map<String, Ingredient> keys = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : json.entrySet())
        {
            if (entry.getKey().length() != 1)
            {
                throw new JsonSyntaxException("Invalid key entry: '" + (String) entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals(entry.getKey()))
            {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            keys.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
        }
        keys.put(" ", Ingredient.EMPTY);
        return keys;
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

    public static NonNullList<Ingredient> itemsFromJson(JsonArray array)
    {
        final NonNullList<Ingredient> list = NonNullList.create();
        for (int i = 0; i < array.size(); ++i)
        {
            list.add(Ingredient.fromJson(array.get(i)));
        }
        return list;
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

    @SuppressWarnings("StatementWithEmptyBody")
    private static int firstNonSpace(String line)
    {
        int i;
        for (i = 0; i < line.length() && line.charAt(i) == ' '; ++i) ;
        return i;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private static int lastNonSpace(String line)
    {
        int i;
        for (i = line.length() - 1; i >= 0 && line.charAt(i) == ' '; --i) ;
        return i;
    }
}
