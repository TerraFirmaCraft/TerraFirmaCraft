/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

/**
 * <a href="https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/d064915183a4a3b803d779576f982279268b1ca3/src/main/java/choonster/testmod3/crafting/recipe/ShapelessCuttingRecipe.java">Source</a>
 */
public class RecipeUtils
{
    public static NonNullList<Ingredient> parseShapeless(JsonContext context, JsonObject json)
    {
        final NonNullList<Ingredient> ingredients = NonNullList.create();
        for (final JsonElement element : JsonUtils.getJsonArray(json, "ingredients"))
            ingredients.add(CraftingHelper.getIngredient(element, context));

        if (ingredients.isEmpty())
            throw new JsonParseException("No ingredients for shapeless recipe");

        return ingredients;
    }

    public static CraftingHelper.ShapedPrimer parsePhaped(JsonContext context, JsonObject json)
    {
        Map<Character, Ingredient> ingMap = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet())
        {
            if (entry.getKey().length() != 1)
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            if (" ".equals(entry.getKey()))
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

            ingMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), context));
        }

        ingMap.put(' ', Ingredient.EMPTY);

        JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");

        if (patternJ.size() == 0)
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");

        String[] pattern = new String[patternJ.size()];
        for (int x = 0; x < pattern.length; ++x)
        {
            String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
            if (x > 0 && pattern[0].length() != line.length())
                throw new JsonSyntaxException("Invalid pattern: each row must  be the same width");
            pattern[x] = line;
        }

        CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
        primer.width = pattern[0].length();
        primer.height = pattern.length;
        primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
        primer.input = NonNullList.withSize(primer.width * primer.height, Ingredient.EMPTY);

        Set<Character> keys = Sets.newHashSet(ingMap.keySet());
        keys.remove(' ');

        int x = 0;
        for (String line : pattern)
        {
            for (char chr : line.toCharArray())
            {
                Ingredient ing = ingMap.get(chr);
                if (ing == null)
                    throw new JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key");
                primer.input.set(x++, ing);
                keys.remove(chr);
            }
        }

        if (!keys.isEmpty())
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);

        return primer;
    }
}
