/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.agriculture;

import javax.annotation.Nonnull;

import static net.dries007.tfc.util.agriculture.Food.Category.*;

public enum Food
{
    BANANA(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    BLACKBERRY(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    BLUEBERRY(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    BUNCH_BERRY(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    CHERRY(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    CLOUD_BERRY(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    CRANBERRY(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    ELDERBERRY(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    GOOSEBERRY(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    GREEN_APPLE(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    LEMON(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    OLIVE(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    ORANGE(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    PEACH(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    PLUM(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    RASPBERRY(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    RED_APPLE(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    SNOW_BERRY(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    STRAWBERRY(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    WINTERGREEN_BERRY(FRUIT, 2, 1.5f, 0f, 0f, 0f, 0.5f, 0f, 4f),
    BARLEY(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    BARLEY_GRAIN(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    BARLEY_FLOUR(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    BARLEY_DOUGH(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    BARLEY_BREAD(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    MAIZE(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    CORNBREAD(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    CORNMEAL_FLOUR(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    CORNMEAL_DOUGH(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    OAT(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    OAT_GRAIN(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    OAT_FLOUR(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    OAT_DOUGH(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    OAT_BREAD(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    RICE(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    RICE_GRAIN(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    RICE_FLOUR(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    RICE_DOUGH(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    RICE_BREAD(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    RYE(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    RYE_GRAIN(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    RYE_FLOUR(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    RYE_DOUGH(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    RYE_BREAD(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    WHEAT(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    WHEAT_GRAIN(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    WHEAT_FLOUR(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    WHEAT_DOUGH(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    WHEAT_BREAD(GRAIN, 3, 0f, 2f, 0.5f, 0f, 0f, 0f, 1f),
    BEET(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    CABBAGE(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    CARROT(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    GARLIC(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    GREEN_BEAN(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    GREEN_BELL_PEPPER(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    ONION(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    POTATO(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    RED_BELL_PEPPER(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    SEAWEED(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    SOYBEAN(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    SQUASH(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    TOMATO(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    YELLOW_BELL_PEPPER(VEGETABLE, 2, 1f, 0f, 0f, 0.5f, 1f, 0f, 2.5f),
    //BEEF(MEAT, 4, 0.5f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    CALAMARI(MEAT, 4, 0.5f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    //FISH(MEAT, 4, 0.5f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    HORSE_MEAT(MEAT, 4, 0.5f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    //MUTTON(MEAT, 4, 0.5f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    //PORK_CHOP(MEAT, 4, 0.5f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    //POULTRY(MEAT, 4, 0.5f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    VENISON(MEAT, 4, 0.5f, 0f, 2f, 2.5f, 0f, 0f, 3f),
    CHEESE(DAIRY, 3, 1f, 0f, 1.5f, 0.5f, 0f, 2f, 2f),
    SUGARCANE(GRAIN, 1, 1f, 0f, 0f, 0f, 0f, 0f, 1f),
    COOKED_EGG(DAIRY, 2, 0f, 1f, 1.5f, 0.5f, 0f, 2f, 1f);

    private final Category category;
    private final int calories;
    private final float water;
    private final float carbohydrates;
    private final float fat;
    private final float protein;
    private final float vitamins;
    private final float minerals;
    private final float decayModifier;

    Food(@Nonnull Category category, int calories, float water, float carbohydrates, float fat, float protein, float vitamins, float minerals, float decayModifier)
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

    public int getCalories()
    {
        return calories;
    }

    public float getSaturation()
    {
        return 0.3f * (carbohydrates + fat + protein);
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
