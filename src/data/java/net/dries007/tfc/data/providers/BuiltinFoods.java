package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.FoodDefinition;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.data.Accessors;

import static net.dries007.tfc.common.component.food.FoodData.*;

public class BuiltinFoods extends DataManagerProvider<FoodDefinition> implements Accessors
{
    public BuiltinFoods(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(FoodCapability.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add(Food.BANANA, ofFood(0.2f, 0, 2).fruit(1f));
        add(Food.BLACKBERRY, ofFood(0.2f, 5, 4.9f).fruit(0.75f));
        add(Food.BLUEBERRY, ofFood(0.2f, 5, 4.9f).fruit(0.75f));
        add(Food.BUNCHBERRY, ofFood(0.5f, 5, 4.9f).fruit(0.75f));
        add(Food.CHERRY, ofFood(0.2f, 5, 4).fruit(1f));
        add(Food.CLOUDBERRY, ofFood(0.5f, 5, 4.9f).fruit(0.75f));
        add(Food.CRANBERRY, ofFood(0.2f, 5, 1.8f).fruit(1f));
        add(Food.ELDERBERRY, ofFood(0.2f, 5, 4.9f).fruit(1f));
        add(Food.GOOSEBERRY, ofFood(0.5f, 5, 4.9f).fruit(0.75f));
        add(Food.GREEN_APPLE, ofFood(0.5f, 0, 2.5f).fruit(1f));
        add(Food.LEMON, ofFood(0.2f, 5, 2).fruit(0.75f));
        add(Food.OLIVE, ofFood(0.2f, 0, 1.6f).fruit(1f));
        add(Food.ORANGE, ofFood(0.5f, 10, 2.2f).fruit(0.5f));
        add(Food.PEACH, ofFood(0.5f, 10, 2.8f).fruit(0.5f));
        add(Food.PLUM, ofFood(0.5f, 5, 2.8f).fruit(0.75f));
        add(Food.RASPBERRY, ofFood(0.5f, 5, 4.9f).fruit(0.75f));
        add(Food.RED_APPLE, ofFood(0.5f, 0, 1.7f).fruit(1f));
        add(Food.SNOWBERRY, ofFood(0.2f, 5, 4.9f).fruit(1f));
        add(Food.STRAWBERRY, ofFood(0.5f, 10, 4.9f).fruit(0.5f));
        add(Food.WINTERGREEN_BERRY, ofFood(0.2f, 5, 4.9f).fruit(1f));
        add(Food.BARLEY, of(2));
        add(Food.BARLEY_GRAIN, ofFood(0.5f, 0, 0.25f));
        add(Food.BARLEY_FLOUR, of(0.5f));
        add(Food.BARLEY_DOUGH, of(3));
        add(Food.BARLEY_BREAD, ofFood(1, 0, 1).grain(1.5f));
        add(Food.BARLEY_BREAD_SANDWICH, of(4.5f));
        add(Food.BARLEY_BREAD_JAM_SANDWICH, of(4.5f));
        add(Food.MAIZE, of(2));
        add(Food.MAIZE_GRAIN, ofFood(0.5f, 0, 0.25f));
        add(Food.MAIZE_FLOUR, of(0.5f));
        add(Food.MAIZE_DOUGH, of(3));
        add(Food.MAIZE_BREAD, ofFood(1, 0, 1).grain(1f));
        add(Food.MAIZE_BREAD_SANDWICH, of(4.5f));
        add(Food.MAIZE_BREAD_JAM_SANDWICH, of(4.5f));
        add(Food.OAT, of(2));
        add(Food.OAT_GRAIN, ofFood(0.5f, 0, 0.25f));
        add(Food.OAT_FLOUR, of(0.5f));
        add(Food.OAT_DOUGH, of(3));
        add(Food.OAT_BREAD, ofFood(1, 0, 1).grain(1f));
        add(Food.OAT_BREAD_SANDWICH, of(4.5f));
        add(Food.OAT_BREAD_JAM_SANDWICH, of(4.5f));
        add(Food.RICE, of(2));
        add(Food.RICE_GRAIN, ofFood(0.5f, 0, 0.25f));
        add(Food.RICE_FLOUR, of(0.5f));
        add(Food.RICE_DOUGH, of(3));
        add(Food.RICE_BREAD, ofFood(1, 0, 1).grain(1.5f));
        add(Food.RICE_BREAD_SANDWICH, of(4.5f));
        add(Food.RICE_BREAD_JAM_SANDWICH, of(4.5f));
        add(Food.COOKED_RICE, ofFood(2, 5, 1).grain(1f));
        add(Food.RYE, of(2));
        add(Food.RYE_GRAIN, ofFood(0.5f, 0, 0.25f));
        add(Food.RYE_FLOUR, of(0.5f));
        add(Food.RYE_DOUGH, of(3));
        add(Food.RYE_BREAD, ofFood(1, 0, 1).grain(1.5f));
        add(Food.RYE_BREAD_SANDWICH, of(4.5f));
        add(Food.RYE_BREAD_JAM_SANDWICH, of(4.5f));
        add(Food.WHEAT, of(2));
        add(Food.WHEAT_GRAIN, ofFood(0.5f, 0, 0.25f));
        add(Food.WHEAT_FLOUR, of(0.5f));
        add(Food.WHEAT_DOUGH, of(3));
        add(Food.WHEAT_BREAD, ofFood(1, 0, 1).grain(1f));
        add(Food.WHEAT_BREAD_SANDWICH, of(4.5f));
        add(Food.WHEAT_BREAD_JAM_SANDWICH, of(4.5f));
        add(Food.BEET, ofFood(2, 0, 0.7f).vegetables(1f));
        add(Food.CABBAGE, ofFood(0.5f, 0, 1.2f).vegetables(1f));
        add(Food.CARROT, ofFood(2, 0, 0.7f).vegetables(1f));
        add(Food.GARLIC, ofFood(0.5f, 0, 0.4f).vegetables(2f));
        add(Food.GREEN_BEAN, ofFood(0.5f, 0, 3.5f).vegetables(1f));
        add(Food.GREEN_BELL_PEPPER, ofFood(0.5f, 0, 2.7f).vegetables(0.75f));
        add(Food.ONION, ofFood(0.5f, 0, 0.5f).vegetables(1f));
        add(Food.POTATO, ofFood(0.5f, 0, 0.666f).vegetables(1.0f));
        add(Food.BAKED_POTATO, ofFood(2, 0, 1.0f).vegetables(1.5f));
        add(Food.RED_BELL_PEPPER, ofFood(1, 0, 2.5f).vegetables(1f));
        add(Food.DRIED_SEAWEED, ofFood(2, 1, 0, 2.0f).vegetables(0.5f));
        add(Food.FRESH_SEAWEED, ofFood(2, 1, 0, 2.5f).vegetables(0.25f));
        add(Food.DRIED_KELP, ofFood(2, 1, 0, 2.5f).vegetables(0.5f));
        add(Food.CATTAIL_ROOT, ofFood(2, 1, 0, 2.5f).grain(0.5f));
        add(Food.TARO_ROOT, ofFood(2, 1, 0, 2.5f).grain(0.5f));
        add(Food.SOYBEAN, ofFood(2, 0, 2.5f).vegetables(0.5f).protein(1f));
        add(Food.SQUASH, ofFood(1, 0, 1.67f).vegetables(1.5f));
        add(Food.SUGARCANE, of(0.5f));
        add(Food.TOMATO, ofFood(0.5f, 5, 3.5f).vegetables(1.5f));
        add(Food.YELLOW_BELL_PEPPER, ofFood(1, 0, 2.5f).vegetables(1f));
        add(TFCBlocks.PUMPKIN, of(0.5f));
        add(TFCBlocks.MELON, of(0.5f));
        add(Food.MELON_SLICE, ofFood(0.2f, 5, 2.5f).fruit(0.75f));
        add(Items.PUMPKIN_PIE, ofFood(2, 5, 2.5f).fruit(1.5f).grain(1f));
        add(Food.PUMPKIN_CHUNKS, ofFood(1, 5, 1.5f).fruit(0.75f));
        add(Food.CHEESE, ofFood(2, 0, 0.3f).dairy(3f));
        add(Food.COOKED_EGG, ofFood(0.5f, 0, 4).protein(1.5f).dairy(0.25f));
        add(Food.BOILED_EGG, ofFood(2, 10, 4).protein(1.5f).dairy(0.25f));
        add(Food.BEEF, of(2).protein(2f));
        add(Food.PORK, of(2).protein(1.5f));
        add(Food.CHICKEN, of(3).protein(1.5f));
        add(Food.MUTTON, of(3).protein(1.5f));
        add(Food.BLUEGILL, of(3).protein(0.75f));
        add(Food.RAINBOW_TROUT, of(3).protein(1f));
        add(Food.LAKE_TROUT, of(3).protein(1f));
        add(Food.LARGEMOUTH_BASS, of(3).protein(1f));
        add(Food.SMALLMOUTH_BASS, of(3).protein(1f));
        add(Food.CRAPPIE, of(3).protein(0.75f));
        add(Food.SALMON, of(3).protein(1f));
        add(Food.SHELLFISH, ofFood(2, 0, 0, 2).protein(0.5f));
        add(Food.COD, of(3).protein(1f));
        add(Food.TROPICAL_FISH, of(3).protein(1f));
        add(Food.BEAR, of(2).protein(1.5f));
        add(Food.CALAMARI, of(3).protein(0.5f));
        add(Food.HORSE_MEAT, of(2).protein(1.5f));
        add(Food.FROG_LEGS, of(2).protein(1f));
        add(Food.TURTLE, of(2).protein(1.5f));
        add(Food.PHEASANT, of(3).protein(1.5f));
        add(Food.GROUSE, of(3).protein(1.5f));
        add(Food.TURKEY, of(3).protein(1.5f));
        add(Food.PEAFOWL, of(3).protein(1.5f));
        add(Food.VENISON, of(2).protein(1f));
        add(Food.WOLF, of(3).protein(0.5f));
        add(Food.RABBIT, of(3).protein(0.5f));
        add(Food.FOX, of(3).protein(0.5f));
        add(Food.HYENA, of(3).protein(0.5f));
        add(Food.DUCK, of(3).protein(0.5f));
        add(Food.QUAIL, of(3).protein(0.5f));
        add(Food.CHEVON, of(3).protein(0.5f));
        add(Food.GRAN_FELINE, of(3).protein(0.5f));
        add(Food.CAMELIDAE, of(3).protein(0.5f));
        add(Food.COOKED_BEEF, ofFood(2, 0, 1.5f).protein(2.5f));
        add(Food.COOKED_PORK, ofFood(2, 0, 1.5f).protein(2.5f));
        add(Food.COOKED_CHICKEN, ofFood(2, 0, 2.25f).protein(2.5f));
        add(Food.COOKED_MUTTON, ofFood(2, 0, 2.25f).protein(2.5f));
        add(Food.COOKED_SHELLFISH, ofFood(2, 2, 0, 2.25f).protein(1.5f));
        add(Food.COOKED_COD, ofFood(1, 0, 2.25f).protein(2f));
        add(Food.COOKED_TROPICAL_FISH, ofFood(1, 0, 1.5f).protein(2f));
        add(Food.COOKED_BLUEGILL, ofFood(1, 0, 2.25f).protein(1.5f));
        add(Food.COOKED_RAINBOW_TROUT, ofFood(1, 0, 2.25f).protein(2f));
        add(Food.COOKED_LAKE_TROUT, ofFood(1, 0, 2.25f).protein(2f));
        add(Food.COOKED_LARGEMOUTH_BASS, ofFood(1, 0, 2.25f).protein(2.25f));
        add(Food.COOKED_SMALLMOUTH_BASS, ofFood(1, 0, 2.25f).protein(2f));
        add(Food.COOKED_CRAPPIE, ofFood(1, 0, 2.25f).protein(1.5f));
        add(Food.COOKED_SALMON, ofFood(1, 0, 2.25f).protein(2f));
        add(Food.COOKED_BEAR, ofFood(1, 0, 1.5f).protein(2.5f));
        add(Food.COOKED_CALAMARI, ofFood(1, 0, 2.25f).protein(1.5f));
        add(Food.COOKED_HORSE_MEAT, ofFood(2, 0, 1.5f).protein(2.5f));
        add(Food.COOKED_FROG_LEGS, ofFood(2, 0, 1.5f).protein(2f));
        add(Food.COOKED_TURTLE, of(2).protein(2.5f));
        add(Food.COOKED_PHEASANT, ofFood(1, 0, 2.25f).protein(2f));
        add(Food.COOKED_TURKEY, ofFood(1, 0, 2.25f).protein(2.5f));
        add(Food.COOKED_PEAFOWL, ofFood(1, 0, 2.25f).protein(2.5f));
        add(Food.COOKED_GROUSE, ofFood(1, 0, 2.25f).protein(2f));
        add(Food.COOKED_VENISON, ofFood(1, 0, 1.5f).protein(2f));
        add(Food.COOKED_WOLF, ofFood(1, 0, 2.25f).protein(1.5f));
        add(Food.COOKED_RABBIT, ofFood(1, 0, 2.25f).protein(1.5f));
        add(Food.COOKED_FOX, ofFood(1, 0, 2.25f).protein(1.5f));
        add(Food.COOKED_HYENA, ofFood(1, 0, 2.25f).protein(1.5f));
        add(Food.COOKED_DUCK, ofFood(1, 0, 2.25f).protein(1.5f));
        add(Food.COOKED_QUAIL, ofFood(1, 0, 2.25f).protein(2f));
        add(Food.COOKED_CHEVON, ofFood(1, 0, 2.25f).protein(2f));
        add(Food.COOKED_GRAN_FELINE, ofFood(2, 0, 2.25f).protein(2.5f));
        add(Food.COOKED_CAMELIDAE, ofFood(2, 0, 2.25f).protein(2.5f));
        add(TFCTags.Items.SEALED_PRESERVES, ofFood(0, 0, 0, 0.1f), false);
        add(TFCTags.Items.PRESERVES, ofFood(0, 0, 0, 5).fruit(0.75f), true);
        add(TFCTags.Items.SALADS, of(4.5f), true);
        add(TFCTags.Items.SOUPS, of(4.5f), true);
    }

    private void add(Food item, FoodData food)
    {
        add(TFCItems.FOOD.get(item), food);
    }

    private void add(ItemLike item, FoodData food)
    {
        add(nameOf(item).replace("food/", ""), new FoodDefinition(Ingredient.of(item), food, true));
    }

    private void add(TagKey<Item> tag, FoodData food, boolean edible)
    {
        add(tag.location().getPath().replace("foods/", ""), new FoodDefinition(Ingredient.of(tag), food, edible));
    }
}
