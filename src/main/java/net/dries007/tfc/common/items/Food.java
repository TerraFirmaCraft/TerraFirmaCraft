/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public enum Food implements StringRepresentable
{
    // Berries
    BLACKBERRY(true),
    BLUEBERRY(true),
    BUNCHBERRY(true),
    CLOUDBERRY(true),
    CRANBERRY(true),
    ELDERBERRY(true),
    GOOSEBERRY(true),
    RASPBERRY(true),
    SNOWBERRY(true),
    STRAWBERRY(true),
    WINTERGREEN_BERRY(true),
    // Fruit
    BANANA(true),
    CHERRY(true),
    GREEN_APPLE(true),
    LEMON(true),
    OLIVE(true),
    ORANGE(true),
    PEACH(true),
    PLUM(true),
    RED_APPLE(true),
    // Misc Fruit
    PUMPKIN_CHUNKS(true),
    MELON_SLICE(true),
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
    // Non-Vegetables
    CHEESE,
    COOKED_EGG,
    BOILED_EGG,
    FRESH_SEAWEED,
    DRIED_SEAWEED,
    CATTAIL_ROOT,
    TARO_ROOT,
    DRIED_KELP,
    // Meats
    BEEF,
    PORK,
    CHICKEN,
    QUAIL,
    MUTTON,
    BEAR,
    HORSE_MEAT,
    PHEASANT,
    GROUSE,
    TURKEY,
    PEAFOWL,
    VENISON,
    WOLF,
    RABBIT,
    FOX,
    HYENA,
    DUCK,
    CHEVON,
    GRAN_FELINE,
    TURTLE,
    CAMELIDAE,
    FROG_LEGS,
    COD,
    TROPICAL_FISH,
    CALAMARI,
    SHELLFISH,
    BLUEGILL,
    CRAPPIE,
    LAKE_TROUT,
    LARGEMOUTH_BASS,
    RAINBOW_TROUT,
    SALMON,
    SMALLMOUTH_BASS,
    // Cooked Meats
    COOKED_BEEF,
    COOKED_PORK,
    COOKED_CHICKEN,
    COOKED_QUAIL,
    COOKED_MUTTON,
    COOKED_BEAR,
    COOKED_HORSE_MEAT,
    COOKED_PHEASANT,
    COOKED_TURKEY,
    COOKED_PEAFOWL,
    COOKED_GROUSE,
    COOKED_VENISON,
    COOKED_WOLF,
    COOKED_RABBIT,
    COOKED_FOX,
    COOKED_HYENA,
    COOKED_DUCK,
    COOKED_CHEVON,
    COOKED_CAMELIDAE,
    COOKED_FROG_LEGS,
    COOKED_GRAN_FELINE,
    COOKED_TURTLE,
    COOKED_COD,
    COOKED_TROPICAL_FISH,
    COOKED_CALAMARI,
    COOKED_SHELLFISH,
    COOKED_BLUEGILL,
    COOKED_CRAPPIE,
    COOKED_LAKE_TROUT,
    COOKED_LARGEMOUTH_BASS,
    COOKED_RAINBOW_TROUT,
    COOKED_SALMON,
    COOKED_SMALLMOUTH_BASS,
    ;
    
    private final boolean fruit;
    private final String serializedName;

    Food()
    {
        this(false);
    }

    Food(boolean fruit)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.fruit = fruit;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public boolean isFruit()
    {
        return fruit;
    }
}
