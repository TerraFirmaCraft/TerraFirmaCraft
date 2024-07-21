package net.dries007.tfc.data.recipes;

import java.util.Arrays;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.QuernRecipe;
import net.dries007.tfc.common.recipes.ingredients.AndIngredient;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.dries007.tfc.common.recipes.outputs.CopyFoodModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public interface QuernRecipes extends Recipes
{
    default void quernRecipes()
    {
        add(notRotten(TFCItems.FOOD.get(Food.OLIVE)), TFCItems.OLIVE_PASTE, 2);
        add(Ingredient.of(
            TFCItems.FOOD.get(Food.SHELLFISH),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.MOLLUSK),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.CLAM),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.MUSSEL),
            TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.SEA_URCHIN),
            Items.TURTLE_SCUTE,
            Items.ARMADILLO_SCUTE,
            TFCBlocks.ROCK_BLOCKS.get(Rock.LIMESTONE).get(Rock.BlockType.LOOSE),
            TFCBlocks.ROCK_BLOCKS.get(Rock.LIMESTONE).get(Rock.BlockType.MOSSY_LOOSE),
            TFCBlocks.ROCK_BLOCKS.get(Rock.DOLOMITE).get(Rock.BlockType.LOOSE),
            TFCBlocks.ROCK_BLOCKS.get(Rock.DOLOMITE).get(Rock.BlockType.MOSSY_LOOSE),
            TFCBlocks.ROCK_BLOCKS.get(Rock.CHALK).get(Rock.BlockType.LOOSE),
            TFCBlocks.ROCK_BLOCKS.get(Rock.CHALK).get(Rock.BlockType.MOSSY_LOOSE),
            TFCBlocks.ROCK_BLOCKS.get(Rock.MARBLE).get(Rock.BlockType.LOOSE),
            TFCBlocks.ROCK_BLOCKS.get(Rock.MARBLE).get(Rock.BlockType.MOSSY_LOOSE)
        ), TFCItems.POWDERS.get(Powder.FLUX), 2);
        add("from_borax", TFCItems.ORES.get(Ore.BORAX), TFCItems.POWDERS.get(Powder.FLUX), 6);
        add(Ingredient.of(
            TFCItems.ORES.get(Ore.CINNABAR),
            TFCItems.ORES.get(Ore.CRYOLITE)
        ), Items.REDSTONE, 8);
        add(Items.BONE, Items.BONE_MEAL, 3);
        add(Items.CHARCOAL, TFCItems.POWDERS.get(Powder.CHARCOAL), 4);
        add(TFCBlocks.ROCK_BLOCKS.get(Rock.LIMESTONE).get(Rock.BlockType.RAW), TFCItems.ORES.get(Ore.GYPSUM), 1);
        addGrain(Food.BARLEY_GRAIN, Food.BARLEY_FLOUR);
        addGrain(Food.MAIZE_GRAIN, Food.MAIZE_FLOUR);
        addGrain(Food.OAT_GRAIN, Food.OAT_FLOUR);
        addGrain(Food.RICE_GRAIN, Food.RICE_FLOUR);
        addGrain(Food.RYE_GRAIN, Food.RYE_FLOUR);
        addGrain(Food.WHEAT_GRAIN, Food.WHEAT_FLOUR);
        add(TFCItems.ORES.get(Ore.HALITE), TFCItems.POWDERS.get(Powder.SALT), 4);
        add(TFCItems.ORES.get(Ore.SYLVITE), TFCItems.ORE_POWDERS.get(Ore.SYLVITE), 4);
        add(TFCItems.ORES.get(Ore.SULFUR), TFCItems.ORE_POWDERS.get(Ore.SULFUR), 4);
        add(TFCItems.ORES.get(Ore.SALTPETER), TFCItems.ORE_POWDERS.get(Ore.SALTPETER), 4);
        add(TFCItems.ORES.get(Ore.GRAPHITE), TFCItems.ORE_POWDERS.get(Ore.GRAPHITE), 4);
        TFCItems.GRADED_ORES.forEach((ore, items) -> {
            add("small", TFCBlocks.SMALL_ORES.get(ore), TFCItems.ORE_POWDERS.get(ore), 2);
            add("poor", items.get(Ore.Grade.POOR), TFCItems.ORE_POWDERS.get(ore), 3);
            add("normal", items.get(Ore.Grade.NORMAL), TFCItems.ORE_POWDERS.get(ore), 5);
            add("rich", items.get(Ore.Grade.RICH), TFCItems.ORE_POWDERS.get(ore), 7);
        });
        TFCItems.GEMS.forEach((gem, item) -> add(Ingredient.of(item, TFCItems.ORES.get(gem)), TFCItems.ORE_POWDERS.get(gem), 4));
        addDye(Items.WHITE_DYE, Plant.HOUSTONIA, Plant.OXEYE_DAISY, Plant.PRIMROSE, Plant.SNAPDRAGON_WHITE, Plant.TRILLIUM, Plant.SPANISH_MOSS, Plant.TULIP_WHITE, Plant.WATER_LILY, Plant.LILY_OF_THE_VALLEY);
        addDye(Items.ORANGE_DYE, Plant.BUTTERFLY_MILKWEED, Plant.CANNA, Plant.NASTURTIUM, Plant.STRELITZIA, Plant.TULIP_ORANGE, Plant.WATER_CANNA, Plant.MARIGOLD);
        addDye(Items.MAGENTA_DYE, Plant.ATHYRIUM_FERN, Plant.MORNING_GLORY, Plant.PULSATILLA, Plant.LILAC, Plant.SILVER_SPURFLOWER);
        addDye(Items.LIGHT_BLUE_DYE, Plant.LABRADOR_TEA, Plant.SAPPHIRE_TOWER);
        addDye(Items.YELLOW_DYE, Plant.CALENDULA, Plant.DANDELION, Plant.MEADS_MILKWEED, Plant.GOLDENROD, Plant.SNAPDRAGON_YELLOW, Plant.DESERT_FLAME);
        addDye(Items.LIME_DYE, Plant.MOSS);
        addDye(Items.PINK_DYE, Plant.FOXGLOVE, Plant.SACRED_DATURA, Plant.TULIP_PINK, Plant.SNAPDRAGON_PINK, Plant.HIBISCUS, Plant.LOTUS, Plant.MAIDEN_PINK);
        addDye(Items.LIGHT_GRAY_DYE, Plant.YUCCA);
        addDye(Items.PURPLE_DYE, Plant.ALLIUM, Plant.BLACK_ORCHID, Plant.PEROVSKIA, Plant.BLUE_GINGER, Plant.PICKERELWEED, Plant.HEATHER);
        addDye(Items.BLUE_DYE, Plant.BLUE_ORCHID, Plant.GRAPE_HYACINTH);
        addDye(Items.BROWN_DYE, Plant.FIELD_HORSETAIL, Plant.SARGASSUM);
        addDye(Items.GREEN_DYE, Plant.BARREL_CACTUS, Plant.REINDEER_LICHEN);
        addDye(Items.RED_DYE, Plant.GUZMANIA, Plant.POPPY, Plant.ROSE, Plant.SNAPDRAGON_RED, Plant.TROPICAL_MILKWEED, Plant.TULIP_RED, Plant.VRIESEA, Plant.ANTHURIUM, Plant.BLOOD_LILY, Plant.HELICONIA, Plant.KANGAROO_PAW);
    }

    private Ingredient notRotten(ItemLike input)
    {
        return AndIngredient.of(Ingredient.of(input), NotRottenIngredient.INSTANCE);
    }

    private void addGrain(Food grain, Food flour)
    {
        add(notRotten(TFCItems.FOOD.get(grain)), ItemStackProvider.of(new ItemStack(TFCItems.FOOD.get(flour)), CopyFoodModifier.INSTANCE));
    }

    private void addDye(ItemLike item, Plant... plants)
    {
        add(Ingredient.of(Arrays.stream(plants).map(TFCBlocks.PLANTS::get).toArray(ItemLike[]::new)), item, 1);
    }

    private void add(ItemLike input, ItemLike output, int count)
    {
        add(Ingredient.of(input), output, count);
    }

    private void add(String suffix, ItemLike input, ItemLike output, int count)
    {
        add(suffix, Ingredient.of(input), output, count);
    }

    private void add(Ingredient input, ItemLike output, int count)
    {
        add(input, ItemStackProvider.of(output, count));
    }

    private void add(String suffix, Ingredient input, ItemLike output, int count)
    {
        add(nameOf(output) + "_" + suffix, new QuernRecipe(input, ItemStackProvider.of(output, count)));
    }

    private void add(Ingredient input, ItemStackProvider output)
    {
        add(new QuernRecipe(input, output));
    }
}
