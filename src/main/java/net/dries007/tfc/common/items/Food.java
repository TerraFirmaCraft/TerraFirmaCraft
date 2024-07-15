/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

public enum Food
{
    // Berries
    BLACKBERRY(false, true, true),
    BLUEBERRY(false, true, true),
    BUNCHBERRY(false, true, true),
    CLOUDBERRY(false, true, true),
    CRANBERRY(false, true, true),
    ELDERBERRY(false, true, true),
    GOOSEBERRY(false, true, true),
    RASPBERRY(false, true, true),
    SNOWBERRY(false, true, true),
    STRAWBERRY(false, true, true),
    WINTERGREEN_BERRY(false, true, true),
    // Fruit
    BANANA(false, false, true),
    CHERRY(false, false, true),
    GREEN_APPLE(false, false, true),
    LEMON(false, false, true),
    OLIVE(false, false, true),
    ORANGE(false, false, true),
    PEACH(false, false, true),
    PLUM(false, false, true),
    RED_APPLE(false, false, true),
    // Misc Fruit
    PUMPKIN_CHUNKS(false, false, true),
    MELON_SLICE(false, false, true),
    // Grains
    BARLEY,
    BARLEY_GRAIN,
    BARLEY_DOUGH,
    BARLEY_FLOUR,
    BARLEY_BREAD,
    BARLEY_BREAD_SANDWICH,
    BARLEY_BREAD_JAM_SANDWICH,
    MAIZE,
    MAIZE_GRAIN,
    MAIZE_DOUGH,
    MAIZE_FLOUR,
    MAIZE_BREAD,
    MAIZE_BREAD_SANDWICH,
    MAIZE_BREAD_JAM_SANDWICH,
    OAT,
    OAT_GRAIN,
    OAT_DOUGH,
    OAT_FLOUR,
    OAT_BREAD,
    OAT_BREAD_SANDWICH,
    OAT_BREAD_JAM_SANDWICH,
    RYE,
    RYE_GRAIN,
    RYE_DOUGH,
    RYE_FLOUR,
    RYE_BREAD,
    RYE_BREAD_SANDWICH,
    RYE_BREAD_JAM_SANDWICH,
    RICE,
    RICE_GRAIN,
    RICE_DOUGH,
    RICE_FLOUR,
    RICE_BREAD,
    RICE_BREAD_SANDWICH,
    RICE_BREAD_JAM_SANDWICH,
    COOKED_RICE,
    WHEAT,
    WHEAT_GRAIN,
    WHEAT_DOUGH,
    WHEAT_FLOUR,
    WHEAT_BREAD,
    WHEAT_BREAD_SANDWICH,
    WHEAT_BREAD_JAM_SANDWICH,
    // Vegetables
    BEET,
    CABBAGE,
    CARROT,
    GARLIC,
    GREEN_BEAN,
    GREEN_BELL_PEPPER,
    ONION,
    POTATO,
    BAKED_POTATO,
    RED_BELL_PEPPER,
    SOYBEAN,
    SUGARCANE,
    SQUASH,
    TOMATO,
    YELLOW_BELL_PEPPER,
    CHEESE,
    COOKED_EGG,
    BOILED_EGG,
    FRESH_SEAWEED,
    DRIED_SEAWEED,
    CATTAIL_ROOT,
    TARO_ROOT,
    DRIED_KELP,
    // Meats
    BEEF(true, false),
    PORK(true, false),
    CHICKEN(true, false),
    QUAIL(true, false),
    MUTTON(true, false),
    BEAR(true, false),
    HORSE_MEAT(true, false),
    PHEASANT(true, false),
    GROUSE(true, false),
    TURKEY(true, false),
    PEAFOWL(true, false),
    VENISON(true, false),
    WOLF(true, false),
    RABBIT(true, false),
    FOX(true, false),
    HYENA(true, false),
    DUCK(true, false),
    CHEVON(true, false),
    GRAN_FELINE(true, false),
    TURTLE(true, false),
    CAMELIDAE(true, false),
    FROG_LEGS(true, false),
    COD(true, false),
    TROPICAL_FISH(true, false),
    CALAMARI(true, false),
    SHELLFISH(true, false),
    BLUEGILL(true, false),
    CRAPPIE(true, false),
    LAKE_TROUT(true, false),
    LARGEMOUTH_BASS(true, false),
    RAINBOW_TROUT(true, false),
    SALMON(true, false),
    SMALLMOUTH_BASS(true, false),
    // Cooked Meats
    COOKED_BEEF(true, false),
    COOKED_PORK(true, false),
    COOKED_CHICKEN(true, false),
    COOKED_QUAIL(true, false),
    COOKED_MUTTON(true, false),
    COOKED_BEAR(true, false),
    COOKED_HORSE_MEAT(true, false),
    COOKED_PHEASANT(true, false),
    COOKED_TURKEY(true, false),
    COOKED_PEAFOWL(true, false),
    COOKED_GROUSE(true, false),
    COOKED_VENISON(true, false),
    COOKED_WOLF(true, false),
    COOKED_RABBIT(true, false),
    COOKED_FOX(true, false),
    COOKED_HYENA(true, false),
    COOKED_DUCK(true, false),
    COOKED_CHEVON(true, false),
    COOKED_CAMELIDAE(true, false),
    COOKED_FROG_LEGS(true, false),
    COOKED_GRAN_FELINE(true, false),
    COOKED_TURTLE(true, false),
    COOKED_COD(true, false),
    COOKED_TROPICAL_FISH(true, false),
    COOKED_CALAMARI(true, false),
    COOKED_SHELLFISH(true, false),
    COOKED_BLUEGILL(true, false),
    COOKED_CRAPPIE(true, false),
    COOKED_LAKE_TROUT(true, false),
    COOKED_LARGEMOUTH_BASS(true, false),
    COOKED_RAINBOW_TROUT(true, false),
    COOKED_SALMON(true, false),
    COOKED_SMALLMOUTH_BASS(true, false),
    ;


    private final boolean meat;
    private final boolean fast;
    private final boolean fruit;

    Food()
    {
        this(false, false, false);
    }

    Food(boolean meat, boolean fast)
    {
        this(meat, fast, false);
    }

    Food(boolean meat, boolean fast, boolean fruit)
    {
        this.meat = meat;
        this.fast = fast;
        this.fruit = fruit;
    }

    public boolean isFruit()
    {
        return fruit;
    }
}
