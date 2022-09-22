/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

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
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.ForgeConfig;

import org.jetbrains.annotations.Nullable;

/**
 * Most of these are copied from {@link net.minecraft.world.item.crafting.ShapedRecipe} due to access level concerns
 */
public final class RecipeHelpers
{
    private static final ThreadLocal<CraftingContainer> CRAFTING_CONTAINER = ThreadLocal.withInitial(() -> null);

    public static void setCraftingContainer(@Nullable CraftingContainer container)
    {
        CRAFTING_CONTAINER.set(container);
    }

    /**
     * @return The crafting container of the player currently crafting an recipe which might need it. Provided by {@link AdvancedShapedRecipe} and {@link AdvancedShapelessRecipe}. Used by {@link net.dries007.tfc.common.recipes.outputs.AddBaitToRodModifier}.
     */
    @Nullable
    public static CraftingContainer getCraftingContainer()
    {
        return CRAFTING_CONTAINER.get();
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

    public static int translateMatch(ShapedRecipe recipe, int targetIndex, CraftingContainer inventory)
    {
        return translateMatch(recipe.getIngredients(), inventory, recipe.getWidth(), recipe.getHeight(), targetIndex);
    }

    public static int translateMatch(NonNullList<Ingredient> recipeItems, CraftingContainer inventory, int width, int height, int targetIndex)
    {
        for (int startCol = 0; startCol <= inventory.getWidth() - width; ++startCol)
        {
            for (int startRow = 0; startRow <= inventory.getHeight() - height; ++startRow)
            {
                if (matches(recipeItems, inventory, startCol, startRow, true, width, height))
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
                    return ((width - 1 - (targetIndex % width)) + startCol) + ((targetIndex / width) + startRow) * inventory.getWidth();
                }
                if (matches(recipeItems, inventory, startCol, startRow, false, width, height))
                {
                    // Solving the same set of equations, but in the non-mirrored case
                    //   targetIndex = col + row * width
                    //
                    // => col = targetIndex % width
                    // => row = targetIndex / width
                    // => recipeIndex = ((targetIndex % width) + startCol) + ((targetIndex / width) + startRow) * invWidth
                    return ((targetIndex % width) + startCol) + ((targetIndex / width) + startRow) * inventory.getWidth();
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
            Ingredient ingredient = Ingredient.fromJson(array.get(i));
            if (ForgeConfig.SERVER.skipEmptyShapelessCheck.get() || !ingredient.isEmpty())
            {
                list.add(ingredient);
            }
        }
        return list;
    }

    private static boolean matches(NonNullList<Ingredient> recipeItems, CraftingContainer inventory, int startCol, int startRow, boolean mirrored, int width, int height)
    {
        for (int invCol = 0; invCol < inventory.getWidth(); ++invCol)
        {
            for (int invRow = 0; invRow < inventory.getHeight(); ++invRow)
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
                if (!ingredient.test(inventory.getItem(invCol + invRow * inventory.getWidth())))
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
