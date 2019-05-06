/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
}
