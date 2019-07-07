/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.agriculture;

import javax.annotation.Nonnull;

import static net.dries007.tfc.util.agriculture.Food.Category.*;

public enum Food
{
    BANANA(FRUIT, 0.4f, 5f, 1f, 0f, 0f, 0.5f, 0f, 4f),
    BLACKBERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    BLUEBERRY(FRUIT, 0.4f, 5f, 0.5f, 0f, 0f, 0.5f, 0f, 4f),
    BUNCH_BERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    CHERRY(FRUIT, 0.4f, 5f, 0.5f, 0f, 0f, 0.5f, 0f, 4f),
    CLOUD_BERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0.5f, 0.5f, 0f, 4f),
    CRANBERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    ELDERBERRY(FRUIT, 0.4f, 5f, 0.5f, 0f, 0f, 0.5f, 0.5f, 4f),
    GOOSEBERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    GREEN_APPLE(FRUIT, 0.4f, 5f, 0.5f, 0f, 0f, 0.5f, 0f, 4f),
    LEMON(FRUIT, 0.2f, 5f, 0f, 0f, 0f, 1f, 0f, 4f),
    OLIVE(FRUIT, 0.6f, 5f, 0f, 1f, 0f, 0.5f, 0f, 4f),
    ORANGE(FRUIT, 0.4f, 7f, 0f, 0f, 0f, 1f, 0f, 4f),
    PEACH(FRUIT, 0.4f, 8f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    PLUM(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    RASPBERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    RED_APPLE(FRUIT, 0.4f, 5f, 0.5f, 0f, 0f, 0.5f, 0f, 4f),
    SNOW_BERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    STRAWBERRY(FRUIT, 0.4f, 8f, 0f, 0f, 0f, 1f, 0f, 4f),
    WINTERGREEN_BERRY(FRUIT, 0.4f, 5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    BARLEY(GRAIN, 0.6f, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    BARLEY_GRAIN(GRAIN, 0.6f, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    BARLEY_FLOUR(GRAIN, 0.6f, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    BARLEY_DOUGH(GRAIN, 0.6f, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    BARLEY_BREAD(GRAIN, 0.6f, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    MAIZE(GRAIN, 0.6f, 0f, 2f, 1f, 0.5f, 0f, 0f, 1f),
    CORNBREAD(GRAIN, 0.6f, 0f, 2f, 1f, 0.5f, 0f, 0f, 1f),
    CORNMEAL_FLOUR(GRAIN, 0.6f, 0f, 2f, 1f, 0.5f, 0f, 0f, 1f),
    CORNMEAL_DOUGH(GRAIN, 0.6f, 0f, 2f, 1f, 0.5f, 0f, 0f, 1f),
    OAT(GRAIN, 0.6f, 0f, 2f, 1f, 1f, 0f, 0f, 1f),
    OAT_GRAIN(GRAIN, 0.6f, 0f, 2f, 1f, 1f, 0f, 0f, 1f),
    OAT_FLOUR(GRAIN, 0.6f, 0f, 2f, 1f, 1f, 0f, 0f, 1f),
    OAT_DOUGH(GRAIN, 0.6f, 0f, 2f, 1f, 1f, 0f, 0f, 1f),
    OAT_BREAD(GRAIN, 0.6f, 0f, 2f, 1f, 1f, 0f, 0f, 1f),
    RICE(GRAIN, 0.4f, 0f, 1.5f, 0.5f, 0f, 0f, 0f, 1f),
    RICE_GRAIN(GRAIN, 0.4f, 0f, 1.5f, 0.5f, 0f, 0f, 0f, 1f),
    RICE_FLOUR(GRAIN, 0.4f, 0f, 1.5f, 0.5f, 0f, 0f, 0f, 1f),
    RICE_DOUGH(GRAIN, 0.4f, 0f, 1.5f, 0.5f, 0f, 0f, 0f, 1f),
    RICE_BREAD(GRAIN, 0.4f, 0f, 1.5f, 0.5f, 0f, 0f, 0f, 1f),
    RYE(GRAIN, 0.6f, 0f, 1.5f, 0.5f, 0f, 0f, 0f, 1f),
    RYE_GRAIN(GRAIN, 0.6f, 0f, 1.5f, 0.5f, 0f, 0f, 0f, 1f),
    RYE_FLOUR(GRAIN, 0.6f, 0f, 1.5f, 0.5f, 0f, 0f, 0f, 1f),
    RYE_DOUGH(GRAIN, 0.6f, 0f, 1.5f, 0.5f, 0f, 0f, 0f, 1f),
    RYE_BREAD(GRAIN, 0.6f, 0f, 1.5f, 0.5f, 0f, 0f, 0f, 1f),
    WHEAT(GRAIN, 0.6f, 0f, 2f, 0.5f, 0.5f, 0f, 0f, 1f),
    WHEAT_GRAIN(GRAIN, 0.6f, 0f, 2f, 0.5f, 0.5f, 0f, 0f, 1f),
    WHEAT_FLOUR(GRAIN, 0.6f, 0f, 2f, 0.5f, 0.5f, 0f, 0f, 1f),
    WHEAT_DOUGH(GRAIN, 0.6f, 0f, 2f, 0.5f, 0.5f, 0f, 0f, 1f),
    WHEAT_BREAD(GRAIN, 0.6f, 0f, 2f, 0.5f, 0.5f, 0f, 0f, 1f),
    BEET(VEGETABLE, 0.4f, 3f, 0f, 0f, 0f, 1f, 0f, 2.5f),
    CABBAGE(VEGETABLE, 0.4f, 5f, 0f, 0f, 0f, 1f, 0f, 2.5f),
    CARROT(VEGETABLE, 0.4f, 3f, 0f, 0f, 0f, 1f, 0f, 2.5f),
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
    BEEF(MEAT, 0.2f, 1f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    PORK(MEAT, 0.2f, 1f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    CHICKEN(MEAT, 0.2f, 1f, 0f, 0.5f, 2.5f, 0f, 0f, 3f),
    MUTTON(MEAT, 0.2f, 1f, 0f, 1.5f, 2.5f, 0f, 0f, 3f),
    FISH(MEAT, 0.2f, 1f, 0f, 0f, 2f, 0f, 0f, 4f),
    BEAR(MEAT, 0.2f, 1f, 0f, 2f, 2.5f, 0.5f, 0f, 3f),
    CALAMARI(MEAT, 0.2f, 1f, 0f, 0.5f, 1.5f, 0f, 0f, 3f),
    HORSE_MEAT(MEAT, 0.2f, 1f, 0f, 1f, 2.5f, 0f, 0f, 3f),
    PHEASANT(MEAT, 0.2f, 1f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    VENISON(MEAT, 0.2f, 1f, 0f, 0.5f, 2f, 0f, 0f, 3f),
    COOKED_BEEF(MEAT, 0.8f, 2f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    COOKED_PORK(MEAT, 0.8f, 2f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    COOKED_CHICKEN(MEAT, 0.6f, 2f, 0f, 0.5f, 2.5f, 0f, 0f, 3f),
    COOKED_MUTTON(MEAT, 0.8f, 2f, 0f, 1.5f, 2.5f, 0f, 0f, 3f),
    COOKED_FISH(MEAT, 0.6f, 2f, 0f, 0f, 2f, 0f, 0f, 4f),
    COOKED_BEAR(MEAT, 0.8f, 2f, 0f, 2f, 2.5f, 0.5f, 0f, 3f),
    COOKED_CALAMARI(MEAT, 0.4f, 2f, 0f, 0.5f, 1.5f, 0f, 0f, 3f),
    COOKED_HORSE_MEAT(MEAT, 0.8f, 2f, 0f, 1f, 2.5f, 0f, 0f, 3f),
    COOKED_PHEASANT(MEAT, 0.8f, 2f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    COOKED_VENISON(MEAT, 0.6f, 2f, 0f, 0.5f, 2f, 0f, 0f, 3f);

    private final Category category;
    private final float calories;
    private final float water;
    private final float carbohydrates;
    private final float fat;
    private final float protein;
    private final float vitamins;
    private final float minerals;
    private final float decayModifier;

    Food(@Nonnull Category category, float calories, float water, float carbohydrates, float fat, float protein, float vitamins, float minerals, float decayModifier)
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

    public enum Category
    {
        FRUIT,
        GRAIN,
        VEGETABLE,
        MEAT,
        DAIRY
    }
}
