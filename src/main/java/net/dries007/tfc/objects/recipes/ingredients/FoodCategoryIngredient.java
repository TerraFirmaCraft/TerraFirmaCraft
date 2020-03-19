/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes.ingredients;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.agriculture.Food;

@SuppressWarnings("unused")
public class FoodCategoryIngredient extends Ingredient
{
    private static ItemStack[] getMatchingStacks(Food.Category[] acceptedCategories, boolean allowedEmpty)
    {
        List<ItemStack> stacks = Arrays.stream(acceptedCategories).flatMap(name -> OreDictionary.getOres(OreDictionaryHelper.toString("category", name)).stream()).collect(Collectors.toList());
        if (allowedEmpty)
        {
            stacks.add(ItemStack.EMPTY);
        }
        return stacks.toArray(new ItemStack[0]);
    }

    private Food.Category[] acceptedCategories;
    private boolean allowedEmpty;

    public FoodCategoryIngredient(Food.Category[] acceptedCategories, boolean allowedEmpty)
    {
        super(getMatchingStacks(acceptedCategories, allowedEmpty));
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    public static class Factory implements IIngredientFactory
    {
        @Nonnull
        @Override
        public Ingredient parse(JsonContext context, JsonObject json)
        {
            JsonArray categoryNames = JsonUtils.getJsonArray(json, "categories");
            Food.Category[] categories = new Food.Category[categoryNames.size()];
            for (int i = 0; i < categoryNames.size(); i++)
            {
                categories[i] = Food.Category.valueOf(categoryNames.get(i).getAsString().toUpperCase());
            }
            boolean allowedEmpty = JsonUtils.getBoolean(json, "allowed_empty", false);
            return new FoodCategoryIngredient(categories, allowedEmpty);
        }
    }
}
