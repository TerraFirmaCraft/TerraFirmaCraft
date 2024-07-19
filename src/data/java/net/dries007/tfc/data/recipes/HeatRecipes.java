package net.dries007.tfc.data.recipes;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.ingredients.AndIngredient;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.dries007.tfc.common.recipes.outputs.ChanceModifier;
import net.dries007.tfc.common.recipes.outputs.CopyFoodModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;

public interface HeatRecipes extends Recipes
{
    float POTTERY = 1399f;

    default void heatRecipes()
    {
        add("from_stick", Ingredient.of(Tags.Items.RODS_WOODEN), ItemStackProvider.of(Items.TORCH, 2), 60);
        add("from_bundle", Ingredient.of(TFCItems.STICK_BUNCH), ItemStackProvider.of(Items.TORCH, 18), 60);
        add(Ingredient.of(TFCItems.KAOLIN_CLAY), ItemStackProvider.of(new ItemStack(TFCItems.POWDERS.get(Powder.KAOLINITE)), new ChanceModifier(0.2f)), 500);

        add(Items.CLAY, Items.TERRACOTTA, POTTERY);
        add(TFCItems.UNFIRED_BRICK, Items.BRICK, POTTERY);
        add(TFCItems.UNFIRED_FLOWER_POT, Items.FLOWER_POT, POTTERY);
        add(TFCItems.UNFIRED_JUG, TFCItems.JUG, POTTERY);
        add(TFCItems.UNFIRED_PAN, TFCItems.EMPTY_PAN, POTTERY);
        add(TFCItems.UNFIRED_CRUCIBLE, TFCBlocks.CRUCIBLE, POTTERY);
        add(TFCItems.UNFIRED_BLOWPIPE, TFCItems.CERAMIC_BLOWPIPE, POTTERY);
        add(TFCItems.UNFIRED_BOWL, TFCBlocks.CERAMIC_BOWL, POTTERY);
        add(TFCItems.UNFIRED_FIRE_BRICK, TFCItems.FIRE_BRICK, POTTERY);
        add(TFCItems.UNFIRED_POT, TFCItems.POT, POTTERY);
        add(TFCItems.UNFIRED_SPINDLE_HEAD, TFCItems.SPINDLE_HEAD, POTTERY);
        add(TFCItems.UNFIRED_BELL_MOLD, TFCItems.BELL_MOLD, POTTERY);
        add(TFCItems.UNFIRED_FIRE_INGOT_MOLD, TFCItems.FIRE_INGOT_MOLD, POTTERY);
        add(TFCItems.UNFIRED_VESSEL, TFCItems.VESSEL, POTTERY);
        add(TFCItems.UNFIRED_LARGE_VESSEL, TFCBlocks.LARGE_VESSEL, POTTERY);

        TFCItems.MOLDS.forEach((type, item) -> add(TFCItems.UNFIRED_MOLDS.get(type), item, POTTERY));
        TFCItems.GLAZED_VESSELS.forEach((color, item) -> add(TFCItems.UNFIRED_GLAZED_VESSELS.get(color), item, POTTERY));
        TFCBlocks.GLAZED_LARGE_VESSELS.forEach((color, item) -> add(TFCItems.UNFIRED_GLAZED_LARGE_VESSELS.get(color), item, POTTERY));

        for (DyeColor color : DyeColor.values())
            add(
                itemOf(ResourceLocation.withDefaultNamespace(color.getSerializedName() + "_terracotta")),
                itemOf(ResourceLocation.withDefaultNamespace(color.getSerializedName() + "_glazed_terracotta")),
                POTTERY);

        addFood(Food.BARLEY_DOUGH, Food.BARLEY_BREAD);
        addFood(Food.MAIZE_DOUGH, Food.MAIZE_BREAD);
        addFood(Food.OAT_DOUGH, Food.OAT_BREAD);
        addFood(Food.RICE_DOUGH, Food.RICE_BREAD);
        addFood(Food.RYE_DOUGH, Food.RYE_BREAD);
        addFood(Food.WHEAT_DOUGH, Food.WHEAT_BREAD);

        addFood(Food.BEEF, Food.COOKED_BEEF);
        addFood(Food.PORK, Food.COOKED_PORK);
        addFood(Food.CHICKEN, Food.COOKED_CHICKEN);
        addFood(Food.QUAIL, Food.COOKED_QUAIL);
        addFood(Food.MUTTON, Food.COOKED_MUTTON);
        addFood(Food.BEAR, Food.COOKED_BEAR);
        addFood(Food.HORSE_MEAT, Food.COOKED_HORSE_MEAT);
        addFood(Food.PHEASANT, Food.COOKED_PHEASANT);
        addFood(Food.TURKEY, Food.COOKED_TURKEY);
        addFood(Food.PEAFOWL, Food.COOKED_PEAFOWL);
        addFood(Food.GROUSE, Food.COOKED_GROUSE);
        addFood(Food.VENISON, Food.COOKED_VENISON);
        addFood(Food.WOLF, Food.COOKED_WOLF);
        addFood(Food.RABBIT, Food.COOKED_RABBIT);
        addFood(Food.HYENA, Food.COOKED_HYENA);
        addFood(Food.DUCK, Food.COOKED_DUCK);
        addFood(Food.CHEVON, Food.COOKED_CHEVON);
        addFood(Food.GRAN_FELINE, Food.COOKED_GRAN_FELINE);
        addFood(Food.CAMELIDAE, Food.COOKED_CAMELIDAE);
        addFood(Food.COD, Food.COOKED_COD);
        addFood(Food.TROPICAL_FISH, Food.COOKED_TROPICAL_FISH);
        addFood(Food.TURTLE, Food.COOKED_TURTLE);
        addFood(Food.CALAMARI, Food.COOKED_CALAMARI);
        addFood(Food.SHELLFISH, Food.COOKED_SHELLFISH);
        addFood(Food.BLUEGILL, Food.COOKED_BLUEGILL);
        addFood(Food.CRAPPIE, Food.COOKED_CRAPPIE);
        addFood(Food.LAKE_TROUT, Food.COOKED_LAKE_TROUT);
        addFood(Food.LARGEMOUTH_BASS, Food.COOKED_LARGEMOUTH_BASS);
        addFood(Food.RAINBOW_TROUT, Food.COOKED_RAINBOW_TROUT);
        addFood(Food.SALMON, Food.COOKED_SALMON);
        addFood(Food.SMALLMOUTH_BASS, Food.COOKED_SMALLMOUTH_BASS);
        addFood(Food.FROG_LEGS, Food.COOKED_FROG_LEGS);
        addFood(Food.FOX, Food.COOKED_FOX);

        addFood(Food.POTATO, Food.BAKED_POTATO);
        addFood(Food.FRESH_SEAWEED, Food.DRIED_SEAWEED);
        add(TFCBlocks.PLANTS.get(Plant.GIANT_KELP_FLOWER), TFCItems.FOOD.get(Food.DRIED_KELP), 200);
        add("from_seaweed", notRotten(TFCItems.FOOD.get(Food.DRIED_SEAWEED)), ItemStackProvider.of(TFCItems.POWDERS.get(Powder.SODA_ASH), 3), 500);
        add("_from_kelp", notRotten(TFCItems.FOOD.get(Food.DRIED_KELP)), ItemStackProvider.of(TFCItems.POWDERS.get(Powder.SODA_ASH), 3), 500);
        add(notRotten(Items.EGG), ItemStackProvider.of(new ItemStack(TFCItems.FOOD.get(Food.COOKED_EGG)), CopyFoodModifier.INSTANCE), 200);
        add("melt", Ingredient.of(TFCItems.BLOWPIPE_WITH_GLASS), ItemStackProvider.of(TFCItems.BLOWPIPE), 1500);
        add("melt", Ingredient.of(TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS), ItemStackProvider.of(TFCItems.CERAMIC_BLOWPIPE), 1500);
        add(TFCItems.POWDERS.get(Powder.FLUX), TFCItems.POWDERS.get(Powder.LIME), 840);

        burnFood("bread", Ingredient.of(TFCTags.Items.BREAD), 700);
        burnFood("meat", Ingredient.of(TFCTags.Items.MEATS), 900);

        addOres(Ore.NATIVE_COPPER, Metal.COPPER);
        addOres(Ore.NATIVE_GOLD, Metal.GOLD);
        addOres(Ore.HEMATITE, Metal.CAST_IRON);
        addOres(Ore.NATIVE_SILVER, Metal.SILVER);
        addOres(Ore.CASSITERITE, Metal.TIN);
        addOres(Ore.BISMUTHINITE, Metal.BISMUTH);
        addOres(Ore.GARNIERITE, Metal.NICKEL);
        addOres(Ore.MALACHITE, Metal.COPPER);
        addOres(Ore.MAGNETITE, Metal.CAST_IRON);
        addOres(Ore.LIMONITE, Metal.CAST_IRON);
        addOres(Ore.SPHALERITE, Metal.ZINC);
        addOres(Ore.TETRAHEDRITE, Metal.COPPER);

        pivot(TFCBlocks.METALS, Metal.BlockType.BLOCK).forEach((metal, block) -> add(
            Ingredient.of(storageBlockTagOf(Registries.ITEM, metal)),
            new FluidStack(fluidOf(metal), units(Metal.BlockType.BLOCK)),
            temperatureOf(metal)));
        TFCItems.METAL_ITEMS.forEach((metal, items) -> items.forEach((type, item) -> add(nameOf(item), new HeatingRecipe(
            ingredientOf(metal, type),
            ItemStackProvider.empty(),
            new FluidStack(meltFluidFor(metal), units(type)),
            temperatureOf(metal), new ItemStack(item).isDamageableItem()))));

        addMetal(TFCItems.RAW_IRON_BLOOM, Metal.CAST_IRON, 100);
        addMetal(TFCItems.REFINED_IRON_BLOOM, Metal.CAST_IRON, 100);
        addMetal(TFCItems.WROUGHT_IRON_GRILL, Metal.CAST_IRON, 100);
        addMetal(Items.IRON_DOOR, Metal.CAST_IRON, 100);
        addMetal(TFCBlocks.BRONZE_BELL, Metal.BRONZE, 100);
        addMetal(TFCBlocks.BRASS_BELL, Metal.BRASS, 100);
        addMetal(Items.BELL, Metal.GOLD, 100);
        addMetal(TFCItems.JACKS, Metal.BRASS, 100);
        addMetal(TFCItems.GEM_SAW, Metal.BRASS, 50);
        addMetal(TFCItems.JAR_LID, Metal.TIN, 50);
    }

    private Fluid meltFluidFor(Metal metal)
    {
        return fluidOf(switch (metal)
        {
            case WROUGHT_IRON -> Metal.CAST_IRON;
            case HIGH_CARBON_STEEL -> Metal.PIG_IRON;
            case HIGH_CARBON_BLACK_STEEL -> Metal.WEAK_STEEL;
            case HIGH_CARBON_BLUE_STEEL -> Metal.WEAK_BLUE_STEEL;
            case HIGH_CARBON_RED_STEEL -> Metal.WEAK_RED_STEEL;
            default -> metal;
        });
    }

    private Ingredient notRotten(ItemLike input)
    {
        return AndIngredient.of(Ingredient.of(input), NotRottenIngredient.INSTANCE);
    }

    private void addOres(Ore ore, Metal metal)
    {
        final float temperature = temperatureOf(metal);

        add(Ingredient.of(TFCBlocks.SMALL_ORES.get(ore)), new FluidStack(fluidOf(metal), 10), temperature);
        add(Ingredient.of(TFCItems.GRADED_ORES.get(ore).get(Ore.Grade.POOR)), new FluidStack(fluidOf(metal), 15), temperature);
        add(Ingredient.of(TFCItems.GRADED_ORES.get(ore).get(Ore.Grade.NORMAL)), new FluidStack(fluidOf(metal), 25), temperature);
        add(Ingredient.of(TFCItems.GRADED_ORES.get(ore).get(Ore.Grade.RICH)), new FluidStack(fluidOf(metal), 35), temperature);
    }

    private void addMetal(ItemLike input, Metal output, int amount)
    {
        add(Ingredient.of(input), new FluidStack(fluidOf(output), amount), temperatureOf(output));
    }

    private void addFood(Food input, Food output)
    {
        add(notRotten(TFCItems.FOOD.get(input)), ItemStackProvider.of(new ItemStack(TFCItems.FOOD.get(output)), CopyFoodModifier.INSTANCE), 200);
    }

    private void burnFood(String name, Ingredient input, float temperature)
    {
        add("burn_" + name, new HeatingRecipe(input, ItemStackProvider.empty(), FluidStack.EMPTY, temperature, false));
    }

    private void add(ItemLike input, ItemLike output, float temperature)
    {
        add(Ingredient.of(input), ItemStackProvider.of(output), temperature);
    }

    private void add(Ingredient input, FluidStack output, float temperature)
    {
        add(nameOf(input), new HeatingRecipe(input, ItemStackProvider.empty(), output, temperature, false));
    }

    private void add(String suffix, Ingredient input, ItemStackProvider output, float temperature)
    {
        add(nameOf(output.getEmptyStack().getItem()) + "_" + suffix, new HeatingRecipe(input, output, FluidStack.EMPTY, temperature, false));
    }

    private void add(Ingredient input, ItemStackProvider output, float temperature)
    {
        add(new HeatingRecipe(input, output, FluidStack.EMPTY, temperature, false));
    }
}
