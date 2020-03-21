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
    BANANA(FRUIT, 4, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 3.75f),
    BLACKBERRY(FRUIT, 4, 1f, 0f, 0f, 0f, 0.5f, 0f, 0f, 4f),
    BLUEBERRY(FRUIT, 4, 1f, 0f, 0f, 0f, 0.5f, 0f, 0f, 4.25f),
    BUNCH_BERRY(FRUIT, 4, 1f, 0f, 0f, 0f, 0.5f, 0f, 0f, 4.25f),
    CHERRY(FRUIT, 4, 0f, 0f, 0f, 0f, 0.75f, 0f, 0f, 4f),
    CLOUD_BERRY(FRUIT, 4, 1f, 0f, 0f, 0f, 0.5f, 0f, 0f, 4.5f),
    CRANBERRY(FRUIT, 4, 0f, 0f, 0f, 0f, 0.75f, 0f, 0f, 4.25f),
    ELDERBERRY(FRUIT, 4, 0f, 0f, 0f, 0f, 0.5f, 0f, 0.25f, 4.25f),
    GOOSEBERRY(FRUIT, 4, 1f, 0f, 0f, 0f, 0.5f, 0f, 0f, 4.25f),
    GREEN_APPLE(FRUIT, 4, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 3.25f, "apple"),
    LEMON(FRUIT, 4, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 3.5f),
    OLIVE(FRUIT, 4, 0f, 0f, 0f, 0.5f, 0.5f, 0f, 0f, 3.25f),
    ORANGE(FRUIT, 4, 0f, 5f, 0f, 0f, 1.5f, 0f, 0f, 3.75f),
    PEACH(FRUIT, 4, 0f, 5f, 0f, 0f, 1f, 0f, 0f, 4f),
    PLUM(FRUIT, 4, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 4f),
    RASPBERRY(FRUIT, 4, 1f, 0f, 0f, 0f, 0.5f, 0f, 0f, 4.5f),
    RED_APPLE(FRUIT, 4, 0f, 0f, 0f, 0f, 1.5f, 0f, 0f, 3.75f, "apple"),
    SNOW_BERRY(FRUIT, 4, 0f, 0f, 0f, 0f, 0.75f, 0f, 0f, 4.5f),
    STRAWBERRY(FRUIT, 4, 0f, 5f, 0f, 0f, 1f, 0f, 0f, 4.5f),
    WINTERGREEN_BERRY(FRUIT, 4, 0f, 0f, 0f, 0f, 0.75f, 0f, 0f, 4.5f),
    BARLEY(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, "barley"),
    BARLEY_GRAIN(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.4f, "grain_barley", "grain"),
    BARLEY_FLOUR(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, "flour_barley", "flour"),
    BARLEY_DOUGH(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, 1f, 200f),
    BARLEY_BREAD(BREAD, 4, 0f, 0f, 1.5f, 0f, 0f, 0f, 0f, 0.8f, 1f, 480f),
    MAIZE(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, "maize", "grain"),
    CORNBREAD(BREAD, 4, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 0.8f, 1f, 480f),
    CORNMEAL_FLOUR(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, "flour_cornmeal", "flour"),
    CORNMEAL_DOUGH(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, 1f, 200f),
    OAT(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, "oat"),
    OAT_GRAIN(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.4f, "grain_oat", "grain"),
    OAT_FLOUR(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, "flour_oat", "flour"),
    OAT_DOUGH(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, 1f, 200f),
    OAT_BREAD(BREAD, 4, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 0.8f, 1f, 480f),
    RICE(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, "rice"),
    RICE_GRAIN(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.4f, "grain_rice", "grain"),
    RICE_FLOUR(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, "flour_rice", "flour"),
    RICE_DOUGH(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, 1f, 200f),
    RICE_BREAD(BREAD, 4, 0f, 0f, 1.5f, 0f, 0f, 0f, 0f, 0.8f, 1f, 480f),
    RYE(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, "rye"),
    RYE_GRAIN(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.4f, "grain_rye", "grain"),
    RYE_FLOUR(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, "flour_rye", "flour"),
    RYE_DOUGH(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, 1f, 200f),
    RYE_BREAD(BREAD, 4, 0f, 0f, 1.5f, 0f, 0f, 0f, 0f, 0.8f, 1f, 480f),
    WHEAT(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, "wheat"),
    WHEAT_GRAIN(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.4f, "grain_wheat", "grain"),
    WHEAT_FLOUR(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, "flour_wheat", "flour"),
    WHEAT_DOUGH(GRAIN, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.8f, 1f, 200f),
    WHEAT_BREAD(BREAD, 4, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 0.8f, 1f, 480f),
    BEET(VEGETABLE, 4, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 2.5f),
    CABBAGE(VEGETABLE, 4, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 2.5f),
    CARROT(VEGETABLE, 4, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 2.5f, "carrot"),
    GARLIC(VEGETABLE, 4, 1f, 0f, 0f, 2f, 0f, 0f, 0f, 2.5f),
    GREEN_BEAN(VEGETABLE, 4, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 3.5f),
    GREEN_BELL_PEPPER(VEGETABLE, 4, 2f, 0f, 0f, 1f, 0f, 0f, 0f, 2.5f),
    ONION(VEGETABLE, 4, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 2.5f),
    POTATO(VEGETABLE, 4, 1f, 0f, 0f, 1.5f, 0f, 0f, 0f, 3f),
    RED_BELL_PEPPER(VEGETABLE, 4, 2f, 0f, 0f, 1f, 0f, 0f, 0f, 2.5f),
    SEAWEED(VEGETABLE, 4, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 2.5f),
    SOYBEAN(VEGETABLE, 4, 1f, 0f, 0f, 2f, 0f, 0f, 0f, 2.5f),
    SQUASH(VEGETABLE, 4, 0f, 0f, 0f, 1.5f, 0f, 0f, 0f, 2.5f),
    TOMATO(VEGETABLE, 4, 0f, 0f, 0f, 1.5f, 0f, 0f, 0f, 3.5f),
    YELLOW_BELL_PEPPER(VEGETABLE, 4, 2f, 0f, 0f, 1f, 0f, 0f, 0f, 2.5f),
    CHEESE(DAIRY, 4, 1f, 0f, 0f, 0f, 0f, 0f, 4f, 2f),
    COOKED_EGG(DAIRY, 4, 1f, 0f, 0f, 0f, 0f, 0f, 3f, 1f),
    BEEF(MEAT, 4, 2f, 0f, 0f, 0f, 0f, 2f, 0f, 2f, 1f, 200f),
    PORK(MEAT, 4, 2f, 0f, 0f, 0f, 0f, 2f, 0f, 2f, 1f, 200f),
    CHICKEN(MEAT, 4, 2f, 0f, 0f, 0f, 0f, 2f, 0f, 3f, 1f, 200f),
    MUTTON(MEAT, 4, 2f, 0f, 0f, 0f, 0f, 2f, 0f, 3f, 1f, 200f),
    FISH(MEAT, 4, 1f, 0f, 0f, 0f, 0f, 1.5f, 0f, 3f, 1f, 200f),
    BEAR(MEAT, 4, 1f, 0f, 0f, 0f, 0f, 2f, 0f, 2f, 1f, 200f),
    CALAMARI(MEAT, 4, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 3f, 1f, 200f),
    HORSE_MEAT(MEAT, 4, 1f, 0f, 0f, 0f, 0f, 2f, 0f, 2f, 1f, 200f),
    PHEASANT(MEAT, 4, 1f, 0f, 0f, 0f, 0f, 2f, 0f, 3f, 1f, 200f),
    VENISON(MEAT, 4, 1f, 0f, 0f, 0f, 0f, 1.5f, 0f, 2f, 1f, 200f),
    WOLF(MEAT, 4, 1f, 0f, 0f, 0f, 0f, 0.5f, 0f, 3f, 1f, 200f),
    RABBIT(MEAT, 4, 1f, 0f, 0f, 0f, 0f, 0.5f, 0f, 3f, 1f, 200f),
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
    BARLEY_BREAD_SANDWICH(MEAL, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "sandwich"),
    CORNBREAD_SANDWICH(MEAL, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "sandwich"),
    OAT_BREAD_SANDWICH(MEAL, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "sandwich"),
    RICE_BREAD_SANDWICH(MEAL, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "sandwich"),
    RYE_BREAD_SANDWICH(MEAL, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "sandwich"),
    WHEAT_BREAD_SANDWICH(MEAL, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "sandwich"),
    SOUP_GRAIN(MEAL, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "soup"),
    SOUP_FRUIT(MEAL, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "soup"),
    SOUP_VEGETABLE(MEAL, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "soup"),
    SOUP_MEAT(MEAL, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "soup"),
    SOUP_DAIRY(MEAL, 4, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 3.5f, "soup");

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
