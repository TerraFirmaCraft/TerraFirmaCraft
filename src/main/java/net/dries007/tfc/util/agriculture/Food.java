/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.agriculture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.util.OreDictionaryHelper;

import static net.dries007.tfc.util.agriculture.Food.Category.*;

public enum Food
{
    BANANA(FRUIT, 4, 0.2f, 0f, 0f, 0f, 1f, 0f, 0f, 2f),
    BLACKBERRY(FRUIT, 4, 0.2f, 5f, 0f, 0f, 0.75f, 0f, 0f, 4.9f),
    BLUEBERRY(FRUIT, 4, 0.2f, 5f, 0f, 0f, 0.75f, 0f, 0f, 4.9f),
    BUNCH_BERRY(FRUIT, 4, 0.5f, 5f, 0f, 0f, 0.75f, 0f, 0f, 4.9f),
    CHERRY(FRUIT, 4, 0.2f, 5f, 0f, 0f, 1f, 0f, 0f, 4f),
    CLOUD_BERRY(FRUIT, 4, 0.5f, 5f, 0f, 0f, 0.75f, 0f, 0f, 4.9f),
    CRANBERRY(FRUIT, 4, 0.2f, 5f, 0f, 0f, 1f, 0f, 0f, 1.8f),
    ELDERBERRY(FRUIT, 4, 0.2f, 5f, 0f, 0f, 1f, 0f, 0f, 4.9f),
    GOOSEBERRY(FRUIT, 4, 0.5f, 5f, 0f, 0f, 0.75f, 0f, 0f, 4.9f),
    GREEN_APPLE(FRUIT, 4, 0.5f, 0f, 0f, 0f, 1f, 0f, 0f, 2.5f, "apple"),
    LEMON(FRUIT, 4, 0.2f, 5f, 0f, 0f, 0.75f, 0f, 0f, 2f),
    OLIVE(FRUIT, 4, 0.2f, 0f, 0f, 0f, 1f, 0f, 0f, 1.6f),
    ORANGE(FRUIT, 4, 0.5f, 10f, 0f, 0f, 0.5f, 0f, 0f, 2.2f),
    PEACH(FRUIT, 4, 0.5f, 10f, 0f, 0f, 0.5f, 0f, 0f, 2.8f),
    PLUM(FRUIT, 4, 0.5f, 5f, 0f, 0f, 0.75f, 0f, 0f, 2.8f),
    RASPBERRY(FRUIT, 4, 0.5f, 5f, 0f, 0f, 0.75f, 0f, 0f, 4.9f),
    RED_APPLE(FRUIT, 4, 0.5f, 0f, 0f, 0f, 1f, 0f, 0f, 1.7f, "apple"),
    SNOW_BERRY(FRUIT, 4, 0.2f, 5f, 0f, 0f, 1f, 0f, 0f, 4.9f),
    STRAWBERRY(FRUIT, 4, 0.5f, 10f, 0f, 0f, 0.5f, 0f, 0f, 4.9f),
    WINTERGREEN_BERRY(FRUIT, 4, 0.2f, 5f, 0f, 0f, 1f, 0f, 0f, 4.9f),
    BARLEY(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 2f, "barley"),
    BARLEY_GRAIN(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.25f, "grain_barley", "grain"),
    BARLEY_FLOUR(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.5f, "flour_barley", "flour"),
    BARLEY_DOUGH(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3f, 1f, 200f),
    BARLEY_BREAD(BREAD, 4, 1f, 0f, 1.5f, 0f, 0f, 0f, 0f, 1f, 1f, 480f),
    MAIZE(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 2f, "maize"),
    MAIZE_GRAIN(GRAIN, 4, 0.5f, 0f, 0f, 0f, 0f, 0f, 0f, 0.25f, "grain_maize", "grain"),
    CORNBREAD(BREAD, 4, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 480f),
    CORNMEAL_FLOUR(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.5f, "flour_cornmeal", "flour"),
    CORNMEAL_DOUGH(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3f, 1f, 200f),
    OAT(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 2f, "oat"),
    OAT_GRAIN(GRAIN, 4, 0.5f, 0f, 0f, 0f, 0f, 0f, 0f, 0.25f, "grain_oat", "grain"),
    OAT_FLOUR(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.5f, "flour_oat", "flour"),
    OAT_DOUGH(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3f, 1f, 200f),
    OAT_BREAD(BREAD, 4, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 480f),
    RICE(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 2f, "rice"),
    RICE_GRAIN(GRAIN, 4, 0.5f, 0f, 0f, 0f, 0f, 0f, 0f, 0.25f, "grain_rice", "grain"),
    RICE_FLOUR(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.5f, "flour_rice", "flour"),
    RICE_DOUGH(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3f, 1f, 200f),
    RICE_BREAD(BREAD, 4, 1f, 0f, 1.5f, 0f, 0f, 0f, 0f, 1f, 1f, 480f),
    RYE(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 2f, "rye"),
    RYE_GRAIN(GRAIN, 4, 0.5f, 0f, 0f, 0f, 0f, 0f, 0f, 0.25f, "grain_rye", "grain"),
    RYE_FLOUR(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.5f, "flour_rye", "flour"),
    RYE_DOUGH(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3f, 1f, 200f),
    RYE_BREAD(BREAD, 4, 1f, 0f, 1.5f, 0f, 0f, 0f, 0f, 1f, 1f, 480f),
    WHEAT(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 2f, "wheat"),
    WHEAT_GRAIN(GRAIN, 4, 0.5f, 0f, 0f, 0f, 0f, 0f, 0f, 0.25f, "grain_wheat", "grain"),
    WHEAT_FLOUR(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.5f, "flour_wheat", "flour"),
    WHEAT_DOUGH(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3f, 1f, 200f),
    WHEAT_BREAD(BREAD, 4, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 480f),
    BEET(VEGETABLE, 4, 2f, 0f, 0f, 1f, 0f, 0f, 0f, 0.7f),
    CABBAGE(VEGETABLE, 4, 0.5f, 0f, 0f, 1f, 0f, 0f, 0f, 1.2f),
    CARROT(VEGETABLE, 4, 2f, 0f, 0f, 1f, 0f, 0f, 0f, 0.7f, "carrot"),
    GARLIC(VEGETABLE, 4, 0.5f, 0f, 0f, 2f, 0f, 0f, 0f, 0.4f),
    GREEN_BEAN(VEGETABLE, 4, 0.5f, 0f, 0f, 1f, 0f, 0f, 0f, 3.5f),
    GREEN_BELL_PEPPER(VEGETABLE, 4, 0.5f, 0f, 0f, 1f, 0f, 0f, 0f, 2.7f),
    ONION(VEGETABLE, 4, 0.5f, 0f, 0f, 1f, 0f, 0f, 0f, 0.5f),
    POTATO(VEGETABLE, 4, 2f, 0f, 0f, 1.5f, 0f, 0f, 0f, 0.666f),
    RED_BELL_PEPPER(VEGETABLE, 4, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 2.5f),
    SEAWEED(VEGETABLE, 4, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 2.5f),
    SOYBEAN(VEGETABLE, 4, 2f, 0f, 0f, 0.5f, 0f, 1f, 0f, 2.5f),
    SQUASH(VEGETABLE, 4, 1f, 0f, 0f, 1.5f, 0f, 0f, 0f, 1.67f),
    TOMATO(VEGETABLE, 4, 0.5f, 5f, 0f, 1.5f, 0f, 0f, 0f, 3.5f),
    YELLOW_BELL_PEPPER(VEGETABLE, 4, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 2.5f),
    CHEESE(DAIRY, 4, 2f, 0f, 0f, 0f, 0f, 0f, 3f, 0.3f),
    COOKED_EGG(OTHER, 4, 0.5f, 0f, 0f, 0f, 0f, 0.75f, 0.25f, 4f),
    SUGARCANE(GRAIN, 4, 0f, 0f, 0.5f, 0f, 0f, 0f, 0f, 1.6f),
    BEEF(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 2f, 0f, 2f, 1f, 200f),
    PORK(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 1.5f, 0f, 2f, 1f, 200f),
    CHICKEN(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 1.5f, 0f, 3f, 1f, 200f),
    MUTTON(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 1.5f, 0f, 3f, 1f, 200f),
    FISH(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 3f, 1f, 200f),
    BEAR(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 1.5f, 0f, 2f, 1f, 200f),
    CALAMARI(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 0.5f, 0f, 3f, 1f, 200f),
    HORSE_MEAT(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 1.5f, 0f, 2f, 1f, 200f),
    PHEASANT(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 1.5f, 0f, 3f, 1f, 200f),
    VENISON(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 2f, 1f, 200f),
    WOLF(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 0.5f, 0f, 3f, 1f, 200f),
    RABBIT(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 0.5f, 0f, 3f, 1f, 200f),
    MONGOOSE(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 0.5f, 0f, 3f, 1f, 200f),
    GRAN_FELINE(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 0.5f, 0f, 3f, 1f, 200f),
    CAMELIDAE(MEAT, 4, 0f, 0f, 0f, 0f, 0f, 0.5f, 0f, 3f, 1f, 200f),
    COOKED_BEEF(COOKED_MEAT, 4, 2f, 0f, 0f, 0f, 0f, 2.5f, 0f, 1.5f),
    COOKED_PORK(COOKED_MEAT, 4, 2f, 0f, 0f, 0f, 0f, 2.5f, 0f, 1.5f),
    COOKED_CHICKEN(COOKED_MEAT, 4, 2f, 0f, 0f, 0f, 0f, 2.5f, 0f, 2.25f),
    COOKED_MUTTON(COOKED_MEAT, 4, 2f, 0f, 0f, 0f, 0f, 2.5f, 0f, 2.25f),
    COOKED_FISH(COOKED_MEAT, 4, 1f, 0f, 0f, 0f, 0f, 2f, 0f, 2.25f),
    COOKED_BEAR(COOKED_MEAT, 4, 1f, 0f, 0f, 0f, 0f, 2.5f, 0f, 1.5f),
    COOKED_CALAMARI(COOKED_MEAT, 4, 1f, 0f, 0f, 0f, 0f, 1.5f, 0f, 2.25f),
    COOKED_HORSE_MEAT(COOKED_MEAT, 4, 2f, 0f, 0f, 0f, 0f, 2.5f, 0f, 1.5f),
    COOKED_PHEASANT(COOKED_MEAT, 4, 1f, 0f, 0f, 0f, 0f, 2.5f, 0f, 2.25f),
    COOKED_VENISON(COOKED_MEAT, 4, 1f, 0f, 0f, 0f, 0f, 2f, 0f, 1.5f),
    COOKED_WOLF(COOKED_MEAT, 4, 1f, 0f, 0f, 0f, 0f, 1.5f, 0f, 2.25f),
    COOKED_RABBIT(COOKED_MEAT, 4, 1f, 0f, 0f, 0f, 0f, 1.5f, 0f, 2.25f),
    COOKED_MONGOOSE(COOKED_MEAT, 4, 1f, 0f, 0f, 0f, 0f, 1.5f, 0f, 2.25f),
    COOKED_GRAN_FELINE(COOKED_MEAT, 4, 2f, 0f, 0f, 0f, 0f, 2.5f, 0f, 2.25f),
    COOKED_CAMELIDAE(COOKED_MEAT, 4, 2f, 0f, 0f, 0f, 0f, 2.5f, 0f, 2.25f),
    BARLEY_BREAD_SANDWICH(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 4.5f, "sandwich"),
    CORNBREAD_SANDWICH(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 4.5f, "sandwich"),
    OAT_BREAD_SANDWICH(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 4.5f, "sandwich"),
    RICE_BREAD_SANDWICH(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 4.5f, "sandwich"),
    RYE_BREAD_SANDWICH(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 4.5f, "sandwich"),
    WHEAT_BREAD_SANDWICH(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 4.5f, "sandwich"),
    SOUP_GRAIN(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "soup"),
    SOUP_FRUIT(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "soup"),
    SOUP_VEGETABLE(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "soup"),
    SOUP_MEAT(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "soup"),
    SOUP_DAIRY(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "soup"),
    SALAD_GRAIN(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 5f, "salad"),
    SALAD_FRUIT(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 5f, "salad"),
    SALAD_VEGETABLE(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 5f, "salad"),
    SALAD_MEAT(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 5f, "salad"),
    SALAD_DAIRY(MEAL, 4, 3f, 0f, 0f, 0f, 0f, 0f, 0f, 5f, "salad");

    private final Category category;
    private final FoodData foodData;

    private final boolean heatable;
    private final float heatCapacity;
    private final float cookingTemp;

    private final String[] oreDictNames;

    Food(@Nonnull Category category, int hunger, float saturation, float water, float grain, float veg, float fruit, float meat, float dairy, float decayModifier, String... oreNames)
    {
        this(category, hunger, saturation, water, grain, veg, fruit, meat, dairy, decayModifier, 0, -1, oreNames);
    }

    Food(@Nonnull Category category, int hunger, float saturation, float water, float grain, float veg, float fruit, float meat, float dairy, float decayModifier, float heatCapacity, float cookingTemp, String... oreNames)
    {
        this.category = category;
        this.foodData = new FoodData(hunger, water, saturation, grain, fruit, veg, meat, dairy, decayModifier);

        this.heatable = cookingTemp >= 0;
        this.heatCapacity = heatCapacity;
        this.cookingTemp = cookingTemp;

        this.oreDictNames = oreNames == null || oreNames.length == 0 ? null : oreNames;
    }

    @Nonnull
    public Category getCategory()
    {
        return category;
    }

    @Nonnull
    public FoodData getData()
    {
        return foodData;
    }

    public boolean isHeatable()
    {
        return heatable;
    }

    public float getHeatCapacity()
    {
        return heatCapacity;
    }

    public float getCookingTemp()
    {
        return cookingTemp;
    }

    @Nullable
    public String[] getOreDictNames()
    {
        return oreDictNames;
    }

    public enum Category
    {
        FRUIT,
        GRAIN,
        BREAD,
        VEGETABLE,
        MEAT,
        COOKED_MEAT,
        DAIRY,
        MEAL,
        OTHER; // Provided for addons / other mods

        public static boolean doesStackMatchCategories(ItemStack stack, Category... categories)
        {
            for (Category cat : categories)
            {
                if (OreDictionaryHelper.doesStackMatchOre(stack, OreDictionaryHelper.toString("category_" + cat.name())))
                {
                    return true;
                }
            }
            return false;
        }
    }
}
