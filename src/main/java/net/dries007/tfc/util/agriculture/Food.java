/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.agriculture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.dries007.tfc.util.agriculture.Food.Category.*;

public enum Food
{
    BANANA(FRUIT, 0.4f, 5f, 1f, 0f, 0f, 0.5f, 0f, 3.75f),
    BLACKBERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    BLUEBERRY(FRUIT, 0.4f, 5f, 0.5f, 0f, 0f, 0.5f, 0f, 4.25f),
    BUNCH_BERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4.25f),
    CHERRY(FRUIT, 0.4f, 5f, 0.5f, 0f, 0f, 0.5f, 0f, 4f),
    CLOUD_BERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0.5f, 0.5f, 0f, 4.5f),
    CRANBERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4.25f),
    ELDERBERRY(FRUIT, 0.4f, 5f, 0.5f, 0f, 0f, 0.5f, 0.5f, 4.25f),
    GOOSEBERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4.25f),
    GREEN_APPLE(FRUIT, 0.4f, 5f, 0.5f, 0f, 0f, 0.5f, 0f, 3.25f, "apple"),
    LEMON(FRUIT, 0.2f, 5f, 0f, 0f, 0f, 1f, 0f, 3.5f),
    OLIVE(FRUIT, 0.6f, 5f, 0f, 1f, 0f, 0.5f, 0f, 3.25f),
    ORANGE(FRUIT, 0.4f, 7f, 0f, 0f, 0f, 1f, 0f, 3.75f),
    PEACH(FRUIT, 0.4f, 8f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    PLUM(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    RASPBERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4.5f),
    RED_APPLE(FRUIT, 0.4f, 5f, 0.5f, 0f, 0f, 0.5f, 0f, 3.75f, "apple"),
    SNOW_BERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4.5f),
    STRAWBERRY(FRUIT, 0.4f, 8f, 0f, 0f, 0f, 1f, 0f, 4.5f),
    WINTERGREEN_BERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4.5f),
    BARLEY(GRAIN, 0f, 0f, 0.2f, 0f, 0f, 0f, 0f, 0.8f, "barley"),
    BARLEY_GRAIN(GRAIN, 0f, 0f, 0.2f, 0f, 0f, 0f, 0f, 0.4f, "grain_barley", "grain"),
    BARLEY_FLOUR(GRAIN, 0f, 0f, 0.2f, 0f, 0f, 0f, 0f, 0.8f, "flour_barley", "flour"),
    BARLEY_DOUGH(GRAIN, 0f, 0f, 0.4f, 0f, 0f, 0f, 0f, 0.8f, 1f, 200f),
    BARLEY_BREAD(GRAIN, 0.6f, 0f, 2f, 0.5f, 0f, 0f, 0f, 0.8f, 1f, 480f),
    MAIZE(GRAIN, 0f, 0f, 0.2f, 0.1f, 0f, 0f, 0f, 0.8f, "maize", "grain"),
    CORNBREAD(GRAIN, 0.6f, 0f, 2f, 1f, 0.5f, 0f, 0f, 0.8f, 1f, 480f),
    CORNMEAL_FLOUR(GRAIN, 0f, 0f, 0.2f, 0.1f, 0f, 0f, 0f, 0.8f, "flour_cornmeal", "flour"),
    CORNMEAL_DOUGH(GRAIN, 0f, 0f, 0.2f, 0.1f, 0f, 0f, 0f, 0.8f, 1f, 200f),
    OAT(GRAIN, 0f, 0f, 0.2f, 0.1f, 0.1f, 0f, 0f, 0.8f, "oat"),
    OAT_GRAIN(GRAIN, 0f, 0f, 0.2f, 0.1f, 0.1f, 0f, 0f, 0.4f, "grain_oat", "grain"),
    OAT_FLOUR(GRAIN, 0f, 0f, 0.2f, 0.1f, 0.1f, 0f, 0f, 0.8f, "flour_oat", "flour"),
    OAT_DOUGH(GRAIN, 0f, 0f, 2f, 1f, 1f, 0f, 0f, 0.8f, 1f, 200f),
    OAT_BREAD(GRAIN, 0.6f, 0f, 0.2f, 0.1f, 0.1f, 0f, 0f, 0.8f, 1f, 480f),
    RICE(GRAIN, 0f, 0f, 0.1f, 0f, 0f, 0f, 0f, 0.8f, "rice"),
    RICE_GRAIN(GRAIN, 0f, 0f, 0.1f, 0f, 0f, 0f, 0f, 0.4f, "grain_rice", "grain"),
    RICE_FLOUR(GRAIN, 0f, 0f, 0.1f, 0f, 0f, 0f, 0f, 0.8f, "flour_rice", "flour"),
    RICE_DOUGH(GRAIN, 0f, 0f, 0.1f, 0f, 0f, 0f, 0f, 0.8f, 1f, 200f),
    RICE_BREAD(GRAIN, 0.4f, 0f, 1.5f, 0.5f, 0f, 0f, 0f, 0.8f, 1f, 480f),
    RYE(GRAIN, 0f, 0f, 0.1f, 0f, 0f, 0f, 0f, 0.8f, "rye"),
    RYE_GRAIN(GRAIN, 0f, 0f, 0.1f, 0f, 0f, 0f, 0f, 0.4f, "grain_rye", "grain"),
    RYE_FLOUR(GRAIN, 0f, 0f, 0.1f, 0f, 0f, 0f, 0f, 0.8f, "flour_rye", "flour"),
    RYE_DOUGH(GRAIN, 0f, 0f, 0.1f, 0f, 0f, 0f, 0f, 0.8f, 1f, 200f),
    RYE_BREAD(GRAIN, 0.6f, 0f, 1.5f, 0.5f, 0f, 0f, 0f, 0.8f, 1f, 480f),
    WHEAT(GRAIN, 0f, 0f, 0.2f, 0f, 0f, 0f, 0f, 0.8f, "wheat"),
    WHEAT_GRAIN(GRAIN, 0f, 0f, 0.2f, 0f, 0f, 0f, 0f, 0.4f, "grain_wheat", "grain"),
    WHEAT_FLOUR(GRAIN, 0f, 0f, 0.2f, 0f, 0f, 0f, 0f, 0.8f, "flour_wheat", "flour"),
    WHEAT_DOUGH(GRAIN, 0f, 0f, 0.2f, 0f, 0f, 0f, 0f, 0.8f, 1f, 200f),
    WHEAT_BREAD(GRAIN, 0.6f, 0f, 2f, 0.5f, 0.5f, 0f, 0f, 0.8f, 1f, 480f),
    BEET(VEGETABLE, 0.4f, 3f, 0f, 0f, 0f, 1f, 0f, 2.5f),
    CABBAGE(VEGETABLE, 0.4f, 5f, 0f, 0f, 0f, 1f, 0f, 2.5f),
    CARROT(VEGETABLE, 0.4f, 3f, 0f, 0f, 0f, 1f, 0f, 2.5f, "carrot"),
    GARLIC(VEGETABLE, 0.6f, 0f, 1f, 0f, 1f, 1f, 0f, 2.5f),
    GREEN_BEAN(VEGETABLE, 0.4f, 3f, 0f, 0f, 0f, 1f, 0f, 3.5f),
    GREEN_BELL_PEPPER(VEGETABLE, 0.4f, 3f, 0f, 0f, 0f, 1f, 0f, 2.5f),
    ONION(VEGETABLE, 0.4f, 3f, 0f, 0f, 0f, 1f, 0f, 2.5f),
    POTATO(VEGETABLE, 0.6f, 3f, 1f, 0f, 0.5f, 1f, 0f, 3f),
    RED_BELL_PEPPER(VEGETABLE, 0.4f, 3f, 0f, 0f, 0f, 1f, 0f, 2.5f),
    SEAWEED(VEGETABLE, 0.4f, 3f, 0f, 0f, 0f, 1f, 0.5f, 2.5f),
    SOYBEAN(VEGETABLE, 0.6f, 3f, 0f, 1.5f, 2f, 0.5f, 0.5f, 2.5f),
    SQUASH(VEGETABLE, 0.4f, 5f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    TOMATO(VEGETABLE, 0.4f, 6f, 0f, 0f, 0.5f, 1f, 0f, 3.5f),
    YELLOW_BELL_PEPPER(VEGETABLE, 0.4f, 3f, 0f, 0f, 0f, 1f, 0f, 2.5f),
    CHEESE(DAIRY, 0.8f, 3f, 0f, 2.5f, 2f, 0f, 2f, 2f),
    COOKED_EGG(DAIRY, 0.6f, 0f, 2f, 2f, 1.5f, 0f, 1f, 1f),
    SUGARCANE(GRAIN, 0.2f, 3f, 0f, 0f, 0f, 0f, 0f, 1f),
    BEEF(MEAT, 0.2f, 1f, 0f, 2f, 2.5f, 0f, 0f, 2f, 1f, 200f),
    PORK(MEAT, 0.2f, 1f, 0f, 2f, 2.5f, 0f, 0f, 2f, 1f, 200f),
    CHICKEN(MEAT, 0.2f, 1f, 0f, 0.5f, 2.5f, 0f, 0f, 3f, 1f, 200f),
    MUTTON(MEAT, 0.2f, 1f, 0f, 1.5f, 2.5f, 0f, 0f, 3f, 1f, 200f),
    FISH(MEAT, 0.2f, 1f, 0f, 0f, 2f, 0f, 0f, 3f, 1f, 200f),
    BEAR(MEAT, 0.2f, 1f, 0f, 2f, 2.5f, 0.5f, 0f, 2f, 1f, 200f),
    CALAMARI(MEAT, 0.2f, 1f, 0f, 0.5f, 1.5f, 0f, 0f, 3f, 1f, 200f),
    HORSE_MEAT(MEAT, 0.2f, 1f, 0f, 1f, 2.5f, 0f, 0f, 2f, 1f, 200f),
    PHEASANT(MEAT, 0.2f, 1f, 0f, 2f, 2.5f, 0f, 0f, 3f, 1f, 200f),
    VENISON(MEAT, 0.2f, 1f, 0f, 0.5f, 2f, 0f, 0f, 2f, 1f, 200f),
    WOLF(MEAT, 0.2f, 1f, 0f, 0.5f, 1f, 0f, 0f, 3f, 1f, 200f),
    RABBIT(MEAT, 0.2f, 1f, 0f, 0.5f, 1f, 0f, 0f, 3f, 1f, 200f),
    COOKED_BEEF(MEAT, 0.8f, 2f, 0f, 2f, 2.5f, 0f, 0f, 1.5f),
    COOKED_PORK(MEAT, 0.8f, 2f, 0f, 2f, 2.5f, 0f, 0f, 1.5f),
    COOKED_CHICKEN(MEAT, 0.6f, 2f, 0f, 0.5f, 2.5f, 0f, 0f, 2.25f),
    COOKED_MUTTON(MEAT, 0.8f, 2f, 0f, 1.5f, 2.5f, 0f, 0f, 2.25f),
    COOKED_FISH(MEAT, 0.6f, 2f, 0f, 0f, 2f, 0f, 0f, 2.25f),
    COOKED_BEAR(MEAT, 0.8f, 2f, 0f, 2f, 2.5f, 0.5f, 0f, 1.5f),
    COOKED_CALAMARI(MEAT, 0.4f, 2f, 0f, 0.5f, 1.5f, 0f, 0f, 2.25f),
    COOKED_HORSE_MEAT(MEAT, 0.8f, 2f, 0f, 1f, 2.5f, 0f, 0f, 1.5f),
    COOKED_PHEASANT(MEAT, 0.8f, 2f, 0f, 2f, 2.5f, 0f, 0f, 2.25f),
    COOKED_VENISON(MEAT, 0.6f, 2f, 0f, 0.5f, 2f, 0f, 0f, 1.5f),
    COOKED_WOLF(MEAT, 0.6f, 2f, 0f, 0.5f, 1.5f, 0f, 0f, 2.25f),
    COOKED_RABBIT(MEAT, 0.6f, 2f, 0f, 0.5f, 1.5f, 0f, 0f, 2.25f);

    private final Category category;
    private final float calories;
    private final float water;
    private final float carbohydrates;
    private final float fat;
    private final float protein;
    private final float vitamins;
    private final float minerals;
    private final float decayModifier;

    private final boolean heatable;
    private final float heatCapacity;
    private final float cookingTemp;

    private final String[] oreDictNames;

    Food(@Nonnull Category category, float calories, float water, float carbohydrates, float fat, float protein, float vitamins, float minerals, float decayModifier, String... oreNames)
    {
        this(category, calories, water, carbohydrates, fat, protein, vitamins, minerals, decayModifier, 0, -1, oreNames);
    }

    Food(@Nonnull Category category, float calories, float water, float carbohydrates, float fat, float protein, float vitamins, float minerals, float decayModifier, float heatCapacity, float cookingTemp, String... oreNames)
    {
        this.category = category;
        this.calories = calories;
        this.water = water;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.protein = protein;
        this.vitamins = vitamins;
        this.minerals = minerals;
        this.decayModifier = decayModifier;

        this.heatable = cookingTemp >= 0;
        this.heatCapacity = heatCapacity;
        this.cookingTemp = cookingTemp;

        this.oreDictNames = oreNames == null || oreNames.length == 0 ? null : oreNames;
    }

    public float getCalories()
    {
        return calories;
    }

    @Nonnull
    public Category getCategory()
    {
        return category;
    }

    public float getDecayModifier()
    {
        return decayModifier;
    }

    public float getWater()
    {
        return water;
    }

    public float[] getNutrients()
    {
        return new float[] {carbohydrates, fat, protein, vitamins, minerals};
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
        VEGETABLE,
        MEAT,
        DAIRY
    }
}
