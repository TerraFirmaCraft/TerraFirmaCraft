/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import java.util.Arrays;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurableMetal;
import net.dries007.tfc.api.recipes.*;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipeMeasurable;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipeSplitting;
import net.dries007.tfc.api.recipes.barrel.*;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.recipes.heat.HeatRecipeMetalMelting;
import net.dries007.tfc.api.recipes.heat.HeatRecipeSimple;
import net.dries007.tfc.api.recipes.heat.HeatRecipeVessel;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeStone;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.api.recipes.quern.QuernRecipe;
import net.dries007.tfc.api.recipes.quern.QuernRecipeRandomGem;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.Gem;
import net.dries007.tfc.objects.Powder;
import net.dries007.tfc.objects.blocks.BlockDecorativeStone;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.inventory.ingredient.IngredientFluidItem;
import net.dries007.tfc.objects.inventory.ingredient.IngredientItemFood;
import net.dries007.tfc.objects.items.ItemAnimalHide;
import net.dries007.tfc.objects.items.ItemPowder;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemMold;
import net.dries007.tfc.objects.items.ceramics.ItemUnfiredMold;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.metal.ItemMetalArmor;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.objects.items.rock.ItemRockToolHead;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.agriculture.Food;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.forge.ForgeRule;
import net.dries007.tfc.util.fuel.FuelManager;
import net.dries007.tfc.util.skills.SmithingSkill;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.api.types.Metal.ItemType.*;
import static net.dries007.tfc.objects.fluids.FluidsTFC.*;
import static net.dries007.tfc.types.DefaultMetals.*;
import static net.dries007.tfc.util.forge.ForgeRule.*;
import static net.dries007.tfc.util.skills.SmithingSkill.Type.*;

/**
 * In 1.14+, every line in here needs to be a json file. Yay, but also ugh.
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MOD_ID)
public final class DefaultRecipes
{
    @SubscribeEvent
    public static void onRegisterBarrelRecipeEvent(RegistryEvent.Register<BarrelRecipe> event)
    {
        event.getRegistry().registerAll(
            // Hide Processing (all three conversions)
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 300), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.SCRAPED, ItemAnimalHide.HideSize.SMALL)), null, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.PREPARED, ItemAnimalHide.HideSize.SMALL)), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("small_prepared_hide"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 400), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.SCRAPED, ItemAnimalHide.HideSize.MEDIUM)), null, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.PREPARED, ItemAnimalHide.HideSize.MEDIUM)), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("medium_prepared_hide"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.SCRAPED, ItemAnimalHide.HideSize.LARGE)), null, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.PREPARED, ItemAnimalHide.HideSize.LARGE)), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("large_prepared_hide"),
            new BarrelRecipe(IIngredient.of(LIMEWATER.get(), 300), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.RAW, ItemAnimalHide.HideSize.SMALL)), null, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.SOAKED, ItemAnimalHide.HideSize.SMALL)), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("small_soaked_hide"),
            new BarrelRecipe(IIngredient.of(LIMEWATER.get(), 400), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.RAW, ItemAnimalHide.HideSize.MEDIUM)), null, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.SOAKED, ItemAnimalHide.HideSize.MEDIUM)), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("medium_soaked_hide"),
            new BarrelRecipe(IIngredient.of(LIMEWATER.get(), 500), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.RAW, ItemAnimalHide.HideSize.LARGE)), null, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.SOAKED, ItemAnimalHide.HideSize.LARGE)), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("large_soaked_hide"),
            new BarrelRecipe(IIngredient.of(TANNIN.get(), 300), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.PREPARED, ItemAnimalHide.HideSize.SMALL)), null, new ItemStack(Items.LEATHER), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("leather_small_hide"),
            new BarrelRecipe(IIngredient.of(TANNIN.get(), 400), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.PREPARED, ItemAnimalHide.HideSize.MEDIUM)), null, new ItemStack(Items.LEATHER, 2), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("leather_medium_hide"),
            new BarrelRecipe(IIngredient.of(TANNIN.get(), 500), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.PREPARED, ItemAnimalHide.HideSize.LARGE)), null, new ItemStack(Items.LEATHER, 3), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("leather_large_hide"),
            // Misc
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 1000), IIngredient.of("logWoodTannin"), new FluidStack(TANNIN.get(), 10000), ItemStack.EMPTY, 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("tannin"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 200), IIngredient.of(ItemsTFC.JUTE), null, new ItemStack(ItemsTFC.JUTE_FIBER), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("jute_fiber"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 600), new IngredientItemFood(IIngredient.of(ItemFoodTFC.get(Food.SUGARCANE), 5)), null, new ItemStack(Items.SUGAR), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("sugar"),
            new BarrelRecipe(IIngredient.of(LIMEWATER.get(), 500), IIngredient.of(new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage())), null, new ItemStack(ItemsTFC.GLUE), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("glue"),
            // Alcohol - Classic created 1000mb with 4oz, which would be 8 items per full barrel at 5 oz/item. Instead we now require 20 items, so conversion is 2 oz/item here
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemFoodTFC.get(Food.BARLEY_FLOUR)), new FluidStack(FluidsTFC.BEER.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("beer"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of("apple"), new FluidStack(FluidsTFC.CIDER.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("cider"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(Items.SUGAR), new FluidStack(FluidsTFC.RUM.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("rum"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemFoodTFC.get(Food.RICE_FLOUR)), new FluidStack(FluidsTFC.SAKE.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("sake"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemFoodTFC.get(Food.POTATO)), new FluidStack(FluidsTFC.VODKA.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("vodka"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemFoodTFC.get(Food.WHEAT_FLOUR)), new FluidStack(FluidsTFC.WHISKEY.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("whiskey"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemFoodTFC.get(Food.CORNMEAL_FLOUR)), new FluidStack(FluidsTFC.CORN_WHISKEY.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("corn_whiskey"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemFoodTFC.get(Food.RYE_FLOUR)), new FluidStack(FluidsTFC.RYE_WHISKEY.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("rye_whiskey"),
            // Vinegar - Classic created 1000mb with 10 oz, which would be 20 items per full barrel at 5 oz/item. Instead we now require 40 items, so conversion is 2.5 oz/item.
            new BarrelRecipe(IIngredient.of(250, FluidsTFC.BEER.get(), FluidsTFC.CIDER.get(), FluidsTFC.RUM.get(), FluidsTFC.SAKE.get(), FluidsTFC.VODKA.get(), FluidsTFC.WHISKEY.get(), FluidsTFC.CORN_WHISKEY.get(), FluidsTFC.RYE_WHISKEY.get()), new IngredientItemFood(IIngredient.of("categoryFruit")), new FluidStack(FluidsTFC.VINEGAR.get(), 250), ItemStack.EMPTY, 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("vinegar"),
            // Food preservation
            BarrelRecipeFoodTraits.pickling(new IngredientItemFood(IIngredient.of("categoryFruit"))).setRegistryName("pickling_fruit"),
            BarrelRecipeFoodTraits.pickling(new IngredientItemFood(IIngredient.of("categoryVegetable"))).setRegistryName("pickling_vegetable"),
            BarrelRecipeFoodTraits.pickling(new IngredientItemFood(IIngredient.of("categoryMeat"))).setRegistryName("pickling_meat"),
            BarrelRecipeFoodTraits.brining(new IngredientItemFood(IIngredient.of("categoryFruit"))).setRegistryName("brining_fruit"),
            BarrelRecipeFoodTraits.brining(new IngredientItemFood(IIngredient.of("categoryVegetable"))).setRegistryName("brining_vegetable"),
            BarrelRecipeFoodTraits.brining(new IngredientItemFood(IIngredient.of("categoryMeat"))).setRegistryName("brining_meat"),
            BarrelRecipeFoodPreservation.vinegar(new IngredientItemFood(IIngredient.of("categoryFruit"))).setRegistryName("vinegar_fruit"),
            BarrelRecipeFoodPreservation.vinegar(new IngredientItemFood(IIngredient.of("categoryVegetable"))).setRegistryName("vinegar_vegetable"),
            BarrelRecipeFoodPreservation.vinegar(new IngredientItemFood(IIngredient.of("categoryMeat"))).setRegistryName("vinegar_meat"),

            new BarrelRecipe(IIngredient.of(LIMEWATER.get(), 100), IIngredient.of("sand"), null, new ItemStack(ItemsTFC.MORTAR, 16), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("mortar"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 125), IIngredient.of("dustSalt"), new FluidStack(SALT_WATER.get(), 125), ItemStack.EMPTY, 0).setRegistryName("fresh_to_salt_water"),
            new BarrelRecipe(IIngredient.of(HOT_WATER.get(), 125), IIngredient.of(new ItemStack(ItemsTFC.WOOD_ASH)), new FluidStack(LYE.get(), 125), ItemStack.EMPTY, 0).setRegistryName("lye"),
            new BarrelRecipe(IIngredient.of(MILK_VINEGAR.get(), 1), IIngredient.of(ItemStack.EMPTY), new FluidStack(CURDLED_MILK.get(), 1), ItemStack.EMPTY, 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("curdled_milk"),
            // based on eating 5 oz in classic, and 1 item in TNG, the full barrel recipe generated 160 oz of cheese, now 32 items. Therefore 625mb creates 2 cheese.
            new BarrelRecipe(IIngredient.of(CURDLED_MILK.get(), 625), IIngredient.of(ItemStack.EMPTY), null, new ItemStack(ItemFoodTFC.get(Food.CHEESE), 2), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("cheese"),

            // Instant recipes: set the duration to 0
            new BarrelRecipeFluidMixing(IIngredient.of(SALT_WATER.get(), 9), new IngredientFluidItem(VINEGAR.get(), 1), new FluidStack(BRINE.get(), 10), 0).setRegistryName("brine"),
            // this ratio works for 9b + 1b = 10b (full barrel) of brine/milk_vinegar, but leaves odd ninths of fluid around for other mixtures.
            new BarrelRecipeFluidMixing(IIngredient.of(MILK.get(), 9), new IngredientFluidItem(VINEGAR.get(), 1), new FluidStack(MILK_VINEGAR.get(), 10), 0).setRegistryName("milk_vinegar"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of("dustFlux"), new FluidStack(LIMEWATER.get(), 500), ItemStack.EMPTY, 0).setRegistryName("limewater"),
            new BarrelRecipe(IIngredient.of(LIMEWATER.get(), 100), IIngredient.of("gemGypsum"), null, new ItemStack(BlocksTFC.ALABASTER_RAW_PLAIN), ICalendar.TICKS_IN_HOUR).setRegistryName("plain_alabaster"),

            //olive oil production
            new BarrelRecipe(IIngredient.of(HOT_WATER.get(), 125), IIngredient.of(ItemsTFC.OLIVE_PASTE), new FluidStack(OLIVE_OIL_WATER.get(), 125), ItemStack.EMPTY, 2 * ICalendar.TICKS_IN_HOUR).setRegistryName("olive_water"),
            // Balance note: Classic gave 250mb for 160oz of olives ~= 32 items. We give 800 mb for that, so 3.2x more. Hopefully will help with lamp usage
            new BarrelRecipe(IIngredient.of(OLIVE_OIL_WATER.get(), 125), IIngredient.of(ItemsTFC.JUTE_NET), new FluidStack(OLIVE_OIL.get(), 25), new ItemStack(ItemsTFC.DIRTY_JUTE_NET), 0).setRegistryName("olive_oil"),
            // Balance: switch to fresh water. Hot water use that way is broken
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 125), IIngredient.of(ItemsTFC.DIRTY_JUTE_NET), null, new ItemStack(ItemsTFC.JUTE_NET), ICalendar.TICKS_IN_HOUR).setRegistryName("clean_net"),
            // Temperature recipes
            new BarrelRecipeTemperature(IIngredient.of(FRESH_WATER.get(), 1), 50).setRegistryName("fresh_water_cooling"),
            new BarrelRecipeTemperature(IIngredient.of(SALT_WATER.get(), 1), 50).setRegistryName("salt_water_cooling")
        );

        for (Food food : new Food[] {Food.SALAD_DAIRY, Food.SALAD_FRUIT, Food.SALAD_GRAIN, Food.SALAD_MEAT, Food.SALAD_VEGETABLE, Food.SOUP_DAIRY, Food.SOUP_FRUIT, Food.SOUP_GRAIN, Food.SOUP_MEAT, Food.SOUP_VEGETABLE})
        {
            event.getRegistry().register(new BarrelRecipeDynamicBowlFood(IIngredient.of(FRESH_WATER.get(), 200), IIngredient.of(ItemFoodTFC.get(food)), 0).setRegistryName(food.name().toLowerCase() + "_cleaning"));
        }

        //The many many many recipes that is dye. This assumes that the standard meta values for colored objects are followed.
        for (EnumDyeColor dyeColor : EnumDyeColor.values())
        {
            String dyeName = dyeColor == EnumDyeColor.SILVER ? "light_gray" : dyeColor.getName();
            int dyeMeta = dyeColor.getMetadata();
            event.getRegistry().registerAll(
                // Dye fluid
                new BarrelRecipe(IIngredient.of(HOT_WATER.get(), 1000), IIngredient.of(OreDictionaryHelper.toString("dye_" + dyeName)), new FluidStack(FluidsTFC.getFluidFromDye(dyeColor).get(), 1000), ItemStack.EMPTY, ICalendar.TICKS_IN_HOUR).setRegistryName(dyeName),
                // Vanilla dye-able items
                new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(dyeColor).get(), 125), IIngredient.of("woolWhite"), null, new ItemStack(Blocks.WOOL, 1, dyeMeta), ICalendar.TICKS_IN_HOUR).setRegistryName("wool_" + dyeName),
                new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(dyeColor).get(), 25), IIngredient.of(new ItemStack(Blocks.CARPET, 1, 0)), null, new ItemStack(Blocks.CARPET, 1, dyeMeta), ICalendar.TICKS_IN_HOUR).setRegistryName("carpet_" + dyeName),
                new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(dyeColor).get(), 125), IIngredient.of(new ItemStack(Items.BED, 1, 0)), null, new ItemStack(Items.BED, 1, dyeMeta), ICalendar.TICKS_IN_HOUR).setRegistryName("bed_" + dyeName),
                new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(dyeColor).get(), 125), IIngredient.of(Blocks.HARDENED_CLAY), null, new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, dyeMeta), ICalendar.TICKS_IN_HOUR).setRegistryName("terracotta_" + dyeName),
                new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(dyeColor).get(), 125), IIngredient.of(Blocks.GLASS), null, new ItemStack(Blocks.STAINED_GLASS, 1, dyeMeta), ICalendar.TICKS_IN_HOUR).setRegistryName("glass_" + dyeName),
                new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(dyeColor).get(), 125), IIngredient.of(Blocks.GLASS_PANE), null, new ItemStack(Blocks.STAINED_GLASS_PANE, 1, dyeMeta), ICalendar.TICKS_IN_HOUR).setRegistryName("glass_pane_" + dyeName),
                // Concrete (vanilla + aggregate)
                new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(dyeColor).get(), 125), IIngredient.of(new ItemStack(Blocks.CONCRETE_POWDER, 1, 0)), null, new ItemStack(Blocks.CONCRETE_POWDER, 1, dyeMeta), ICalendar.TICKS_IN_HOUR).setRegistryName("concrete_" + dyeName),
                new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(dyeColor).get(), 125), IIngredient.of(BlocksTFC.AGGREGATE), null, new ItemStack(Blocks.CONCRETE_POWDER, 1, dyeMeta), ICalendar.TICKS_IN_HOUR).setRegistryName("aggregate_" + dyeName),
                // Alabaster
                new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(dyeColor).get(), 125), IIngredient.of(BlocksTFC.ALABASTER_BRICKS_PLAIN), null, new ItemStack(BlockDecorativeStone.ALABASTER_BRICKS.get(dyeColor)), ICalendar.TICKS_IN_HOUR).setRegistryName("alabaster_bricks_" + dyeColor.getName()),
                new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(dyeColor).get(), 125), IIngredient.of(BlocksTFC.ALABASTER_RAW_PLAIN), null, new ItemStack(BlockDecorativeStone.ALABASTER_RAW.get(dyeColor)), ICalendar.TICKS_IN_HOUR).setRegistryName("alabaster_raw_" + dyeColor.getName()),
                new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(dyeColor).get(), 125), IIngredient.of(BlocksTFC.ALABASTER_POLISHED_PLAIN), null, new ItemStack(BlockDecorativeStone.ALABASTER_POLISHED.get(dyeColor)), ICalendar.TICKS_IN_HOUR).setRegistryName("alabaster_polished_" + dyeColor.getName())
            );
        }
        // Un-dyeing Recipes
        event.getRegistry().registerAll(
            // Vanilla dye-able items
            new BarrelRecipe(IIngredient.of(FluidsTFC.LYE.get(), 125), IIngredient.of("wool"), null, new ItemStack(Blocks.WOOL, 1, 0), ICalendar.TICKS_IN_HOUR).setRegistryName("wool_undo"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.LYE.get(), 25), IIngredient.of("carpet"), null, new ItemStack(Blocks.CARPET, 1, 0), ICalendar.TICKS_IN_HOUR).setRegistryName("carpet_undo"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.LYE.get(), 125), IIngredient.of("bed"), null, new ItemStack(Items.BED, 1, 0), ICalendar.TICKS_IN_HOUR).setRegistryName("bed_undo"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.LYE.get(), 125), IIngredient.of("terracotta"), null, new ItemStack(Blocks.HARDENED_CLAY), ICalendar.TICKS_IN_HOUR).setRegistryName("terracotta_undo"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.LYE.get(), 125), IIngredient.of("blockGlass"), null, new ItemStack(Blocks.GLASS), ICalendar.TICKS_IN_HOUR).setRegistryName("glass_undo"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.LYE.get(), 125), IIngredient.of("paneGlass"), null, new ItemStack(Blocks.GLASS_PANE), ICalendar.TICKS_IN_HOUR).setRegistryName("glass_pane_undo"),
            // Concrete
            new BarrelRecipe(IIngredient.of(FluidsTFC.LYE.get(), 125), IIngredient.of("powderConcrete"), null, new ItemStack(BlocksTFC.AGGREGATE), ICalendar.TICKS_IN_HOUR).setRegistryName("concrete_undo"),
            // Alabaster
            new BarrelRecipe(IIngredient.of(FluidsTFC.LYE.get(), 125), IIngredient.of("alabasterBricks"), null, new ItemStack(BlocksTFC.ALABASTER_BRICKS_PLAIN), ICalendar.TICKS_IN_HOUR).setRegistryName("alabaster_bricks_undo"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.LYE.get(), 125), IIngredient.of("alabasterRaw"), null, new ItemStack(BlocksTFC.ALABASTER_RAW_PLAIN), ICalendar.TICKS_IN_HOUR).setRegistryName("alabaster_raw_undo"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.LYE.get(), 125), IIngredient.of("alabasterPolished"), null, new ItemStack(BlocksTFC.ALABASTER_POLISHED_PLAIN), ICalendar.TICKS_IN_HOUR).setRegistryName("alabaster_polished_undo")
        );
        // Dye combinations.
        event.getRegistry().registerAll(
            //Orange
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.RED).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.YELLOW).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.ORANGE).get(), 2), 0).setRegistryName("orange_dye_red_yellow_liquid"),
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.YELLOW).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.RED).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.ORANGE).get(), 2), 0).setRegistryName("orange_dye_yellow_red_liquid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.RED).get(), 1000), IIngredient.of("dyeYellow"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.ORANGE).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("orange_dye_red_yellow_solid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.YELLOW).get(), 1000), IIngredient.of("dyeRed"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.ORANGE).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("orange_dye_yellow_red_solid"),
            //Light Blue
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.BLUE).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.LIGHT_BLUE).get(), 2), 0).setRegistryName("light_blue_dye_blue_white_liquid"),
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.BLUE).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.LIGHT_BLUE).get(), 2), 0).setRegistryName("light_blue_dye_white_blue_liquid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.BLUE).get(), 1000), IIngredient.of("dyeWhite"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.LIGHT_BLUE).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("light_blue_dye_blue_white_solid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1000), IIngredient.of("dyeBlue"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.LIGHT_BLUE).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("light_blue_dye_white_blue_solid"),
            //Magenta
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.PURPLE).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.PINK).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.MAGENTA).get(), 2), 0).setRegistryName("magenta_dye_purple_pink_liquid"),
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.PINK).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.PURPLE).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.MAGENTA).get(), 2), 0).setRegistryName("magenta_dye_pink_purple_liquid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.PURPLE).get(), 1000), IIngredient.of("dyePink"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.MAGENTA).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("magenta_dye_purple_pink_solid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.PINK).get(), 1000), IIngredient.of("dyePurple"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.MAGENTA).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("magenta_dye_pink_purple_solid"),
            //Pink
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.RED).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.PINK).get(), 2), 0).setRegistryName("pink_dye_red_white_liquid"),
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.RED).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.PINK).get(), 2), 0).setRegistryName("pink_dye_white_red_liquid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.RED).get(), 1000), IIngredient.of("dyeWhite"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.PINK).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("pink_dye_red_white_solid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1000), IIngredient.of("dyeRed"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.PINK).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("pink_dye_white_red_solid"),
            //Light Gray
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.GRAY).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.SILVER).get(), 2), 0).setRegistryName("light_gray_dye_white_gray_liquid"),
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 2), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.BLACK).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.SILVER).get(), 3), 0).setRegistryName("light_gray_dye_white_black_liquid"),
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.GRAY).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.SILVER).get(), 2), 0).setRegistryName("light_gray_dye_gray_white_liquid"),
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.BLACK).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 2), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.SILVER).get(), 3), 0).setRegistryName("light_gray_dye_black_white_liquid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1000), IIngredient.of("dyeGray"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.SILVER).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("light_gray_dye_white_gray_solid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 2000), IIngredient.of("dyeBlack"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.SILVER).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("light_gray_dye_white_black_solid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.GRAY).get(), 1000), IIngredient.of("dyeWhite"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.SILVER).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("light_gray_dye_gray_white_solid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.BLACK).get(), 500), IIngredient.of("dyeWhite"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.SILVER).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("light_gray_dye_black_white_solid"),
            //Lime
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.GREEN).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.LIME).get(), 2), 0).setRegistryName("lime_dye_green_white_liquid"),
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.GREEN).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.LIME).get(), 2), 0).setRegistryName("lime_dye_white_green_liquid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.GREEN).get(), 1000), IIngredient.of("dyeWhite"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.LIME).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("lime_dye_green_white_solid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1000), IIngredient.of("dyeGreen"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.LIME).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("lime_dye_white_green_solid"),
            //Cyan
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.GREEN).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.BLUE).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.CYAN).get(), 2), 0).setRegistryName("cyan_dye_green_blue_liquid"),
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.BLUE).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.GREEN).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.CYAN).get(), 2), 0).setRegistryName("cyan_dye_blue_green_liquid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.GREEN).get(), 1000), IIngredient.of("dyeBlue"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.CYAN).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("cyan_dye_green_blue_solid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.BLUE).get(), 1000), IIngredient.of("dyeGreen"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.CYAN).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("cyan_dye_blue_green_solid"),
            //Purple
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.RED).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.BLUE).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.PURPLE).get(), 2), 0).setRegistryName("purple_dye_red_blue_liquid"),
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.BLUE).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.RED).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.PURPLE).get(), 2), 0).setRegistryName("purple_dye_blue_red_liquid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.RED).get(), 1000), IIngredient.of("dyeBlue"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.PURPLE).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("purple_dye_red_blue_solid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.BLUE).get(), 1000), IIngredient.of("dyeRed"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.PURPLE).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("purple_dye_blue_red_solid"),
            //Gray
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.BLACK).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.GRAY).get(), 2), 0).setRegistryName("gray_dye_black_white_liquid"),
            new BarrelRecipeFluidMixing(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1), new IngredientFluidItem(FluidsTFC.getFluidFromDye(EnumDyeColor.BLACK).get(), 1), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.GRAY).get(), 2), 0).setRegistryName("gray_dye_white_black_liquid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.BLACK).get(), 1000), IIngredient.of("dyeWhite"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.GRAY).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("gray_dye_black_white_solid"),
            new BarrelRecipe(IIngredient.of(FluidsTFC.getFluidFromDye(EnumDyeColor.WHITE).get(), 1000), IIngredient.of("dyeBlack"), new FluidStack(FluidsTFC.getFluidFromDye(EnumDyeColor.GRAY).get(), 1000), ItemStack.EMPTY, 0).setRegistryName("gray_dye_white_black_solid")
        );
    }

    @SubscribeEvent
    public static void onRegisterKnappingRecipeEvent(RegistryEvent.Register<KnappingRecipe> event)
    {
        /* STONE TOOL HEADS */

        for (Rock.ToolType type : Rock.ToolType.values())
        {
            // This covers all stone -> single tool head recipes
            KnappingRecipe r = new KnappingRecipeStone(KnappingType.STONE, rockIn -> new ItemStack(ItemRockToolHead.get(rockIn.getRockCategory(), type)), type.getPattern());
            event.getRegistry().register(r.setRegistryName(type.name().toLowerCase() + "_head"));
        }
        // these recipes cover all cases where multiple stone items can be made
        // recipes are already mirror checked
        event.getRegistry().registerAll(
            new KnappingRecipeStone(KnappingType.STONE, rockIn -> new ItemStack(ItemRockToolHead.get(rockIn.getRockCategory(), Rock.ToolType.KNIFE), 2), "X  X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("knife_head_1"),
            new KnappingRecipeStone(KnappingType.STONE, rockIn -> new ItemStack(ItemRockToolHead.get(rockIn.getRockCategory(), Rock.ToolType.KNIFE), 2), "X   X", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("knife_head_2"),
            new KnappingRecipeStone(KnappingType.STONE, rockIn -> new ItemStack(ItemRockToolHead.get(rockIn.getRockCategory(), Rock.ToolType.KNIFE), 2), " X X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("knife_head_3"),
            new KnappingRecipeStone(KnappingType.STONE, rockIn -> new ItemStack(ItemRockToolHead.get(rockIn.getRockCategory(), Rock.ToolType.HOE), 2), "XXXXX", "XX   ", "     ", "XXXXX", "XX   ").setRegistryName("hoe_head_1"),
            new KnappingRecipeStone(KnappingType.STONE, rockIn -> new ItemStack(ItemRockToolHead.get(rockIn.getRockCategory(), Rock.ToolType.HOE), 2), "XXXXX", "XX   ", "     ", "XXXXX", "   XX").setRegistryName("hoe_head_2")
        );

        /* CLAY ITEMS */

        for (Metal.ItemType type : Metal.ItemType.values())
        {
            if (type.hasMold(null))
            {
                int amount = type == INGOT ? 2 : 1;
                event.getRegistry().register(new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(ItemUnfiredMold.get(type), amount), type.getPattern()).setRegistryName(type.name().toLowerCase() + "_mold"));
            }
        }

        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(ItemsTFC.UNFIRED_VESSEL), " XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX ").setRegistryName("clay_small_vessel"),
            new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(ItemsTFC.UNFIRED_JUG), " X   ", "XXXX ", "XXX X", "XXXX ", "XXX  ").setRegistryName("clay_jug"),
            new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(ItemsTFC.UNFIRED_POT), "X   X", "X   X", "X   X", "XXXXX", " XXX ").setRegistryName("clay_pot"),
            new KnappingRecipeSimple(KnappingType.CLAY, false, new ItemStack(ItemsTFC.UNFIRED_BOWL, 2), "X   X", " XXX ").setRegistryName(MOD_ID, "clay_bowl"),
            new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(ItemsTFC.UNFIRED_BOWL, 4), "X   X", " XXX ", "     ", "X   X", " XXX ").setRegistryName("clay_bowl_2"),
            new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(ItemsTFC.UNFIRED_LARGE_VESSEL), "X   X", "X   X", "X   X", "X   X", "XXXXX").setRegistryName("clay_large_vessel"),
            new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(ItemsTFC.UNFIRED_BRICK, 3), "XXXXX", "     ", "XXXXX", "     ", "XXXXX").setRegistryName("clay_brick"),
            new KnappingRecipeSimple(KnappingType.CLAY, true, new ItemStack(ItemsTFC.UNFIRED_FLOWER_POT, 2), " X X ", " XXX ", "     ", " X X ", " XXX ").setRegistryName("clay_flower_pot")
        );

        /* LEATHER ITEMS */

        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(Items.LEATHER_HELMET), "XXXXX", "X   X", "X   X", "     ", "     ").setRegistryName("leather_helmet"),
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(Items.LEATHER_CHESTPLATE), "X   X", "XXXXX", "XXXXX", "XXXXX", "XXXXX").setRegistryName("leather_chestplate"),
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(Items.LEATHER_LEGGINGS), "XXXXX", "XXXXX", "XX XX", "XX XX", "XX XX").setRegistryName("leather_leggings"),
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(Items.LEATHER_BOOTS), "XX   ", "XX   ", "XX   ", "XXXX ", "XXXXX").setRegistryName("leather_boots"),
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(Items.SADDLE), "  X  ", "XXXXX", "XXXXX", "XXXXX", "  X  ").setRegistryName("leather_saddle"),
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(ItemsTFC.QUIVER), " XXXX", "X XXX", "X XXX", "X XXX", " XXXX").setRegistryName("leather_quiver")
        );

        /* FIRE CLAY ITEMS */

        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingType.FIRE_CLAY, true, new ItemStack(ItemsTFC.UNFIRED_CRUCIBLE), "X   X", "X   X", "X   X", "X   X", "XXXXX").setRegistryName("fire_clay_crucible"),
            new KnappingRecipeSimple(KnappingType.FIRE_CLAY, true, new ItemStack(ItemsTFC.UNFIRED_FIRE_BRICK, 3), "XXXXX", "     ", "XXXXX", "     ", "XXXXX").setRegistryName("fire_clay_brick")
        );

    }

    @SubscribeEvent
    public static void onRegisterBloomeryRecipeEvent(RegistryEvent.Register<BloomeryRecipe> event)
    {
        event.getRegistry().registerAll(
            new BloomeryRecipe(Metal.WROUGHT_IRON, FuelManager::isItemBloomeryFuel)
        );
    }

    @SubscribeEvent
    public static void onRegisterBlastFurnaceRecipeEvent(RegistryEvent.Register<BlastFurnaceRecipe> event)
    {
        event.getRegistry().registerAll(
            new BlastFurnaceRecipe(Metal.PIG_IRON, Metal.WROUGHT_IRON, IIngredient.of("dustFlux"))
        );
    }

    @SubscribeEvent
    public static void onRegisterHeatRecipeEvent(RegistryEvent.Register<HeatRecipe> event)
    {
        IForgeRegistry<HeatRecipe> r = event.getRegistry();

        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            //noinspection ConstantConditions
            r.register(new HeatRecipeMetalMelting(metal).setRegistryName(metal.getRegistryName().getPath() + "_melting"));
        }

        // Pottery Items with metadata
        for (EnumDyeColor dye : EnumDyeColor.values())
        {
            r.register(
                new HeatRecipeSimple(IIngredient.of(new ItemStack(ItemsTFC.UNFIRED_VESSEL_GLAZED, 1, dye.getMetadata())), new ItemStack(ItemsTFC.FIRED_VESSEL_GLAZED, 1, dye.getMetadata()), 1599f, Metal.Tier.TIER_I).setRegistryName("unfired_vessel_glazed_" + dye.getName())
            );
        }

        // Molds
        for (Metal.ItemType type : Metal.ItemType.values())
        {
            ItemUnfiredMold unfiredMold = ItemUnfiredMold.get(type);
            ItemMold firedMold = ItemMold.get(type);
            if (unfiredMold != null && firedMold != null)
            {
                r.register(new HeatRecipeSimple(IIngredient.of(unfiredMold), new ItemStack(firedMold), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_mold_" + type.name().toLowerCase()));
            }
        }

        // Standard / Simple recipes
        r.registerAll(
            // Pottery
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.UNFIRED_FIRE_BRICK), new ItemStack(ItemsTFC.FIRED_FIRE_BRICK), 1599f, Metal.Tier.TIER_I).setRegistryName("unfired_fire_brick"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.UNFIRED_VESSEL), new ItemStack(ItemsTFC.FIRED_VESSEL), 1599f, Metal.Tier.TIER_I).setRegistryName("unfired_vessel"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.UNFIRED_JUG), new ItemStack(ItemsTFC.FIRED_JUG), 1599f, Metal.Tier.TIER_I).setRegistryName("unfired_jug"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.UNFIRED_POT), new ItemStack(ItemsTFC.FIRED_POT), 1599f, Metal.Tier.TIER_I).setRegistryName("unfired_pot"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.UNFIRED_BOWL), new ItemStack(ItemsTFC.FIRED_BOWL), 1599f, Metal.Tier.TIER_I).setRegistryName("unfired_bowl"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.UNFIRED_SPINDLE), new ItemStack(ItemsTFC.FIRED_SPINDLE), 1599f, Metal.Tier.TIER_I).setRegistryName("unfired_spindle"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.UNFIRED_LARGE_VESSEL), new ItemStack(BlocksTFC.FIRED_LARGE_VESSEL), 1599f, Metal.Tier.TIER_I).setRegistryName("unfired_large_vessel"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.UNFIRED_CRUCIBLE), new ItemStack(BlocksTFC.CRUCIBLE), 1599f, Metal.Tier.TIER_I).setRegistryName("unfired_crucible"),

            // Fired Pottery - doesn't burn up
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.FIRED_FIRE_BRICK), new ItemStack(ItemsTFC.FIRED_FIRE_BRICK), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_fire_brick"),
            new HeatRecipeVessel(IIngredient.of(ItemsTFC.FIRED_VESSEL), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_vessel"),
            new HeatRecipeVessel(IIngredient.of(ItemsTFC.FIRED_VESSEL_GLAZED), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_vessel_glazed_all"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.FIRED_JUG), new ItemStack(ItemsTFC.FIRED_JUG), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_jug"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.FIRED_POT), new ItemStack(ItemsTFC.FIRED_POT), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_pot"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.FIRED_BOWL), new ItemStack(ItemsTFC.FIRED_BOWL), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_bowl"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.FIRED_SPINDLE), new ItemStack(ItemsTFC.FIRED_SPINDLE), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_spindle"),
            new HeatRecipeSimple(IIngredient.of(BlocksTFC.FIRED_LARGE_VESSEL), new ItemStack(BlocksTFC.FIRED_LARGE_VESSEL), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_large_vessel"),
            new HeatRecipeSimple(IIngredient.of(BlocksTFC.CRUCIBLE), new ItemStack(BlocksTFC.CRUCIBLE), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_crucible"),

            // Misc
            new HeatRecipeSimple(IIngredient.of("stickWood"), new ItemStack(Blocks.TORCH, 2), 40).setRegistryName("torch"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.STICK_BUNCH), new ItemStack(Blocks.TORCH, 18), 60).setRegistryName("torch_stick_bunch"),
            new HeatRecipeSimple(IIngredient.of("sand"), new ItemStack(Blocks.GLASS), 600).setRegistryName("glass"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.GLASS_SHARD), new ItemStack(Blocks.GLASS), 600).setRegistryName("glass_shard"),
            new HeatRecipeSimple(IIngredient.of("blockClay"), new ItemStack(Blocks.HARDENED_CLAY), 600).setRegistryName("terracotta"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.UNFIRED_BRICK), new ItemStack(Items.BRICK), 1500).setRegistryName("unfired_brick"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.UNFIRED_FLOWER_POT), new ItemStack(Items.FLOWER_POT), 1500).setRegistryName("unfired_flower_pot"),

            // Bread
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.BARLEY_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.BARLEY_BREAD)), 200, 480).setRegistryName("barley_bread"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.CORNMEAL_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.CORNBREAD)), 200, 480).setRegistryName("cornbread"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.OAT_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.OAT_BREAD)), 200, 480).setRegistryName("oat_bread"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.RICE_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.RICE_BREAD)), 200, 480).setRegistryName("rice_bread"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.RYE_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.RYE_BREAD)), 200, 480).setRegistryName("rye_bread"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.WHEAT_DOUGH)), new ItemStack(ItemFoodTFC.get(Food.WHEAT_BREAD)), 200, 480).setRegistryName("wheat_bread"),

            // Meat
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.BEEF)), new ItemStack(ItemFoodTFC.get(Food.COOKED_BEEF)), 200, 480).setRegistryName("cooked_beef"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.PORK)), new ItemStack(ItemFoodTFC.get(Food.COOKED_PORK)), 200, 480).setRegistryName("cooked_pork"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.CHICKEN)), new ItemStack(ItemFoodTFC.get(Food.COOKED_CHICKEN)), 200, 480).setRegistryName("cooked_chicken"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.MUTTON)), new ItemStack(ItemFoodTFC.get(Food.COOKED_MUTTON)), 200, 480).setRegistryName("cooked_mutton"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.FISH)), new ItemStack(ItemFoodTFC.get(Food.COOKED_FISH)), 200, 480).setRegistryName("cooked_fish"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.BEAR)), new ItemStack(ItemFoodTFC.get(Food.COOKED_BEAR)), 200, 480).setRegistryName("cooked_bear"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.CALAMARI)), new ItemStack(ItemFoodTFC.get(Food.COOKED_CALAMARI)), 200, 480).setRegistryName("cooked_calamari"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.HORSE_MEAT)), new ItemStack(ItemFoodTFC.get(Food.COOKED_HORSE_MEAT)), 200, 480).setRegistryName("cooked_horse_meat"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.PHEASANT)), new ItemStack(ItemFoodTFC.get(Food.COOKED_PHEASANT)), 200, 480).setRegistryName("cooked_pheasant"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.VENISON)), new ItemStack(ItemFoodTFC.get(Food.COOKED_VENISON)), 200, 480).setRegistryName("cooked_venison"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.RABBIT)), new ItemStack(ItemFoodTFC.get(Food.COOKED_RABBIT)), 200, 480).setRegistryName("cooked_rabbit"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.WOLF)), new ItemStack(ItemFoodTFC.get(Food.COOKED_WOLF)), 200, 480).setRegistryName("cooked_wolf"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.CAMELIDAE)), new ItemStack(ItemFoodTFC.get(Food.COOKED_CAMELIDAE)), 200, 480).setRegistryName("cooked_camelidae"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.MONGOOSE)), new ItemStack(ItemFoodTFC.get(Food.COOKED_MONGOOSE)), 200, 480).setRegistryName("cooked_mongoose"),
            new HeatRecipeSimple(IIngredient.of(ItemFoodTFC.get(Food.GRAN_FELINE)), new ItemStack(ItemFoodTFC.get(Food.COOKED_GRAN_FELINE)), 200, 480).setRegistryName("cooked_gran_feline"),

            // Egg
            new HeatRecipeSimple(IIngredient.of(Items.EGG), new ItemStack(ItemFoodTFC.get(Food.COOKED_EGG)), 200, 480).setRegistryName("cooked_egg"),

            // Bread
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.BARLEY_BREAD)), 480).setRegistryName("burned_barley_bread"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.CORNBREAD)), 480).setRegistryName("burned_cornbread"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.OAT_BREAD)), 480).setRegistryName("burned_oat_bread"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.RICE_BREAD)), 480).setRegistryName("burned_rice_bread"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.RYE_BREAD)), 480).setRegistryName("burned_rye_bread"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.WHEAT_BREAD)), 480).setRegistryName("burned_wheat_bread"),

            // Meat
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_BEEF)), 480).setRegistryName("burned_beef"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_PORK)), 480).setRegistryName("burned_pork"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_CHICKEN)), 480).setRegistryName("burned_chicken"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_MUTTON)), 480).setRegistryName("burned_mutton"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_FISH)), 480).setRegistryName("burned_fish"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_BEAR)), 480).setRegistryName("burned_bear"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_CALAMARI)), 480).setRegistryName("burned_calamari"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_HORSE_MEAT)), 480).setRegistryName("burned_horse_meat"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_PHEASANT)), 480).setRegistryName("burned_pheasant"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_RABBIT)), 480).setRegistryName("burned_rabbit"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_WOLF)), 480).setRegistryName("burned_wolf"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_VENISON)), 480).setRegistryName("burned_venison"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_CAMELIDAE)), 480).setRegistryName("burned_camelidae"),
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_MONGOOSE)), 480).setRegistryName("burned_mongoose"),

            // Egg
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_EGG)), 480).setRegistryName("burned_egg"),

            // Glazed terracotta, because minecraft decided *this* one should not use metadata.
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.WHITE.getMetadata())), new ItemStack(Blocks.WHITE_GLAZED_TERRACOTTA), 1200).setRegistryName("white_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.ORANGE.getMetadata())), new ItemStack(Blocks.ORANGE_GLAZED_TERRACOTTA), 1200).setRegistryName("orange_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.MAGENTA.getMetadata())), new ItemStack(Blocks.MAGENTA_GLAZED_TERRACOTTA), 1200).setRegistryName("magenta_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.LIGHT_BLUE.getMetadata())), new ItemStack(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA), 1200).setRegistryName("light_blue_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.YELLOW.getMetadata())), new ItemStack(Blocks.YELLOW_GLAZED_TERRACOTTA), 1200).setRegistryName("yellow_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.LIME.getMetadata())), new ItemStack(Blocks.LIME_GLAZED_TERRACOTTA), 1200).setRegistryName("lime_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.PINK.getMetadata())), new ItemStack(Blocks.PINK_GLAZED_TERRACOTTA), 1200).setRegistryName("pink_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.GRAY.getMetadata())), new ItemStack(Blocks.GRAY_GLAZED_TERRACOTTA), 1200).setRegistryName("gray_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.SILVER.getMetadata())), new ItemStack(Blocks.SILVER_GLAZED_TERRACOTTA), 1200).setRegistryName("silver_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.CYAN.getMetadata())), new ItemStack(Blocks.CYAN_GLAZED_TERRACOTTA), 1200).setRegistryName("cyan_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.PURPLE.getMetadata())), new ItemStack(Blocks.PURPLE_GLAZED_TERRACOTTA), 1200).setRegistryName("purple_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.BLUE.getMetadata())), new ItemStack(Blocks.BLUE_GLAZED_TERRACOTTA), 1200).setRegistryName("blue_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.BROWN.getMetadata())), new ItemStack(Blocks.BROWN_GLAZED_TERRACOTTA), 1200).setRegistryName("brown_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.GREEN.getMetadata())), new ItemStack(Blocks.GREEN_GLAZED_TERRACOTTA), 1200).setRegistryName("green_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.RED.getMetadata())), new ItemStack(Blocks.RED_GLAZED_TERRACOTTA), 1200).setRegistryName("red_glazed_terracotta"),
            new HeatRecipeSimple(IIngredient.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumDyeColor.BLACK.getMetadata())), new ItemStack(Blocks.BLACK_GLAZED_TERRACOTTA), 1200).setRegistryName("black_glazed_terracotta")
        );
    }

    @SubscribeEvent
    public static void onRegisterAnvilRecipeEvent(RegistryEvent.Register<AnvilRecipe> event)
    {
        IForgeRegistry<AnvilRecipe> r = event.getRegistry();

        // Misc
        addAnvil(r, DOUBLE_INGOT, SHEET, false, GENERAL, HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST);
        addAnvil(r, DOUBLE_SHEET, TUYERE, true, GENERAL, BEND_LAST, BEND_SECOND_LAST);
        addAnvil(r, INGOT, LAMP, false, GENERAL, BEND_LAST, BEND_SECOND_LAST, DRAW_THIRD_LAST);
        addAnvil(r, SHEET, TRAPDOOR, false, GENERAL, BEND_LAST, DRAW_SECOND_LAST, DRAW_THIRD_LAST);

        // Tools
        addAnvil(r, INGOT, PICK_HEAD, true, TOOLS, PUNCH_LAST, BEND_NOT_LAST, DRAW_NOT_LAST);
        addAnvil(r, INGOT, SHOVEL_HEAD, true, TOOLS, PUNCH_LAST, HIT_NOT_LAST);
        addAnvil(r, INGOT, AXE_HEAD, true, TOOLS, PUNCH_LAST, HIT_SECOND_LAST, UPSET_THIRD_LAST);
        addAnvil(r, INGOT, HOE_HEAD, true, TOOLS, PUNCH_LAST, HIT_NOT_LAST, BEND_NOT_LAST);
        addAnvil(r, INGOT, HAMMER_HEAD, true, TOOLS, PUNCH_LAST, SHRINK_NOT_LAST);
        addAnvil(r, INGOT, PROPICK_HEAD, true, TOOLS, PUNCH_LAST, DRAW_NOT_LAST, BEND_NOT_LAST);
        addAnvil(r, INGOT, SAW_BLADE, true, TOOLS, HIT_LAST, HIT_SECOND_LAST);
        addAnvil(r, DOUBLE_INGOT, SWORD_BLADE, true, WEAPONS, HIT_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
        addAnvil(r, DOUBLE_INGOT, MACE_HEAD, true, WEAPONS, HIT_LAST, SHRINK_NOT_LAST, BEND_NOT_LAST);
        addAnvil(r, INGOT, SCYTHE_BLADE, true, WEAPONS, HIT_LAST, DRAW_SECOND_LAST, BEND_THIRD_LAST);
        addAnvil(r, INGOT, KNIFE_BLADE, true, WEAPONS, HIT_LAST, DRAW_SECOND_LAST, DRAW_THIRD_LAST);
        addAnvil(r, INGOT, JAVELIN_HEAD, true, WEAPONS, HIT_LAST, HIT_SECOND_LAST, DRAW_THIRD_LAST);
        addAnvil(r, INGOT, CHISEL_HEAD, true, TOOLS, HIT_LAST, HIT_NOT_LAST, DRAW_NOT_LAST);

        // Armor
        addAnvil(r, DOUBLE_SHEET, UNFINISHED_HELMET, true, ARMOR, HIT_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
        addAnvil(r, DOUBLE_SHEET, UNFINISHED_CHESTPLATE, true, ARMOR, HIT_LAST, HIT_SECOND_LAST, UPSET_THIRD_LAST);
        addAnvil(r, DOUBLE_SHEET, UNFINISHED_GREAVES, true, ARMOR, BEND_ANY, DRAW_ANY, HIT_ANY);
        addAnvil(r, SHEET, UNFINISHED_BOOTS, true, ARMOR, BEND_LAST, BEND_SECOND_LAST, SHRINK_THIRD_LAST);

        // Blooms
        r.register(new AnvilRecipeMeasurable(new ResourceLocation(MOD_ID, "refining_bloom"), IIngredient.of(ItemsTFC.UNREFINED_BLOOM), new ItemStack(ItemsTFC.REFINED_BLOOM), Metal.Tier.TIER_II, HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST));
        r.register(new AnvilRecipeSplitting(new ResourceLocation(MOD_ID, "splitting_bloom"), IIngredient.of(ItemsTFC.REFINED_BLOOM), new ItemStack(ItemsTFC.REFINED_BLOOM), 100, Metal.Tier.TIER_II, PUNCH_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MOD_ID, "iron_bloom"), x -> {
            if (x.getItem() == ItemsTFC.REFINED_BLOOM)
            {
                IForgeable cap = x.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                if (cap instanceof IForgeableMeasurableMetal)
                {
                    return ((IForgeableMeasurableMetal) cap).getMetal() == Metal.WROUGHT_IRON && ((IForgeableMeasurableMetal) cap).getMetalAmount() == 100;
                }
            }
            return false;
        }, new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON, INGOT)), Metal.Tier.TIER_II, null, HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST));

        // Shields
        addAnvil(r, DOUBLE_SHEET, SHIELD, true, ARMOR, UPSET_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);

        // Steel Working
        addAnvil(r, PIG_IRON, HIGH_CARBON_STEEL, null);
        addAnvil(r, HIGH_CARBON_STEEL, STEEL, null);
        addAnvil(r, HIGH_CARBON_BLACK_STEEL, BLACK_STEEL, null);
        addAnvil(r, HIGH_CARBON_BLUE_STEEL, BLUE_STEEL, null);
        addAnvil(r, HIGH_CARBON_RED_STEEL, RED_STEEL, null);

        // Misc
        addAnvil(r, "iron_bars", SHEET, WROUGHT_IRON, new ItemStack(Blocks.IRON_BARS, 8), Metal.Tier.TIER_III, GENERAL, UPSET_LAST, PUNCH_SECOND_LAST, PUNCH_THIRD_LAST);
        addAnvil(r, "iron_bars_double", DOUBLE_SHEET, WROUGHT_IRON, new ItemStack(Blocks.IRON_BARS, 16), Metal.Tier.TIER_III, GENERAL, UPSET_LAST, PUNCH_SECOND_LAST, PUNCH_THIRD_LAST);
        addAnvil(r, "iron_door", SHEET, WROUGHT_IRON, new ItemStack(Items.IRON_DOOR), Metal.Tier.TIER_III, GENERAL, HIT_LAST, DRAW_NOT_LAST, PUNCH_NOT_LAST);
        addAnvil(r, "red_steel_bucket", SHEET, RED_STEEL, new ItemStack(ItemMetal.get(Metal.RED_STEEL, BUCKET)), Metal.Tier.TIER_VI, GENERAL, BEND_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
        addAnvil(r, "blue_steel_bucket", SHEET, BLUE_STEEL, new ItemStack(ItemMetal.get(Metal.BLUE_STEEL, BUCKET)), Metal.Tier.TIER_VI, GENERAL, BEND_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
        addAnvil(r, "wrought_iron_grill", DOUBLE_SHEET, WROUGHT_IRON, new ItemStack(ItemsTFC.WROUGHT_IRON_GRILL), Metal.Tier.TIER_III, GENERAL, DRAW_ANY, PUNCH_LAST, PUNCH_NOT_LAST);
        addAnvil(r, "brass_mechanisms", INGOT, BRASS, new ItemStack(ItemsTFC.BRASS_MECHANISMS, 2), Metal.Tier.TIER_II, GENERAL, PUNCH_LAST, HIT_SECOND_LAST, PUNCH_THIRD_LAST);

        // Rods, because they produce 2
        Arrays.asList(WROUGHT_IRON, STEEL, GOLD).forEach(metal -> {
            Metal metalObj = TFCRegistries.METALS.getValue(metal);
            //noinspection ConstantConditions
            addAnvil(r, metal.getPath() + "_rod", INGOT, metal, new ItemStack(ItemMetal.get(metalObj, ROD), 2), metalObj.getTier().previous(), GENERAL, DRAW_LAST, DRAW_NOT_LAST, PUNCH_NOT_LAST);
        });
    }

    @SubscribeEvent
    public static void onRegisterWeldingRecipeEvent(RegistryEvent.Register<WeldingRecipe> event)
    {
        IForgeRegistry<WeldingRecipe> r = event.getRegistry();

        // Basic Parts
        addWelding(r, INGOT, DOUBLE_INGOT, null);
        addWelding(r, SHEET, DOUBLE_SHEET, null);

        // Armor
        addWelding(r, UNFINISHED_HELMET, SHEET, HELMET, true, ARMOR);
        addWelding(r, UNFINISHED_CHESTPLATE, DOUBLE_SHEET, CHESTPLATE, true, ARMOR);
        addWelding(r, UNFINISHED_GREAVES, SHEET, GREAVES, true, ARMOR);
        addWelding(r, UNFINISHED_BOOTS, SHEET, BOOTS, true, ARMOR);

        // Steel Welding
        addWelding(r, WEAK_STEEL, PIG_IRON, HIGH_CARBON_BLACK_STEEL);
        addWelding(r, WEAK_BLUE_STEEL, BLACK_STEEL, HIGH_CARBON_BLUE_STEEL);
        addWelding(r, WEAK_RED_STEEL, BLACK_STEEL, HIGH_CARBON_RED_STEEL);

        // Special Recipes
        addWelding(r, KNIFE_BLADE, KNIFE_BLADE, SHEARS, true, TOOLS);
    }

    @SubscribeEvent
    public static void onRegisterLoomRecipeEvent(RegistryEvent.Register<LoomRecipe> event)
    {
        IForgeRegistry<LoomRecipe> r = event.getRegistry();

        r.registerAll(
            new LoomRecipe(new ResourceLocation(MOD_ID, "burlap_cloth"), IIngredient.of(ItemsTFC.JUTE_FIBER, 12), new ItemStack(ItemsTFC.BURLAP_CLOTH), 12, new ResourceLocation(MOD_ID, "textures/blocks/devices/loom/product/burlap.png")),
            new LoomRecipe(new ResourceLocation(MOD_ID, "wool_cloth"), IIngredient.of(ItemsTFC.WOOL_YARN, 16), new ItemStack(ItemsTFC.WOOL_CLOTH), 16, new ResourceLocation("minecraft", "textures/blocks/wool_colored_white.png")),
            new LoomRecipe(new ResourceLocation(MOD_ID, "silk_cloth"), IIngredient.of(Items.STRING, 24), new ItemStack(ItemsTFC.SILK_CLOTH), 24, new ResourceLocation("minecraft", "textures/blocks/wool_colored_white.png")),

            new LoomRecipe(new ResourceLocation(MOD_ID, "wool_block"), IIngredient.of(ItemsTFC.WOOL_CLOTH, 4), new ItemStack(Blocks.WOOL, 8), 4, new ResourceLocation("minecraft", "textures/blocks/wool_colored_white.png"))
        );
    }

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public static void onRegisterQuernRecipeEvent(RegistryEvent.Register<QuernRecipe> event)
    {
        IForgeRegistry<QuernRecipe> r = event.getRegistry();

        r.registerAll(
            //Grain
            new QuernRecipe(IIngredient.of("grainBarley"), new ItemStack(ItemFoodTFC.get(Food.BARLEY_FLOUR), 1)).setRegistryName("barley"),
            new QuernRecipe(IIngredient.of("grainOat"), new ItemStack(ItemFoodTFC.get(Food.OAT_FLOUR), 1)).setRegistryName("oat"),
            new QuernRecipe(IIngredient.of("grainRice"), new ItemStack(ItemFoodTFC.get(Food.RICE_FLOUR), 1)).setRegistryName("rice"),
            new QuernRecipe(IIngredient.of("grainRye"), new ItemStack(ItemFoodTFC.get(Food.RYE_FLOUR), 1)).setRegistryName("rye"),
            new QuernRecipe(IIngredient.of("grainWheat"), new ItemStack(ItemFoodTFC.get(Food.WHEAT_FLOUR), 1)).setRegistryName("wheat"),
            new QuernRecipe(IIngredient.of("grainMaize"), new ItemStack(ItemFoodTFC.get(Food.CORNMEAL_FLOUR), 1)).setRegistryName("maize"),

            new QuernRecipe(new IngredientItemFood(IIngredient.of(ItemFoodTFC.get(Food.OLIVE))), new ItemStack(ItemsTFC.OLIVE_PASTE, 1)).setRegistryName("olive"),

            //Flux
            new QuernRecipe(IIngredient.of("gemBorax"), new ItemStack(ItemPowder.get(Powder.FLUX), 6)).setRegistryName("borax"),
            new QuernRecipe(IIngredient.of("rockFlux"), new ItemStack(ItemPowder.get(Powder.FLUX), 2)).setRegistryName("flux"),

            //Redstone
            new QuernRecipe(IIngredient.of("gemCinnabar"), new ItemStack(Items.REDSTONE, 8)).setRegistryName("cinnabar"),
            new QuernRecipe(IIngredient.of("gemCryolite"), new ItemStack(Items.REDSTONE, 8)).setRegistryName("cryolite"),

            //Hematite
            new QuernRecipe(IIngredient.of(ItemSmallOre.get(Ore.HEMATITE, 1)), new ItemStack(ItemPowder.get(Powder.HEMATITE), 2)).setRegistryName("hematite_powder_from_small"),
            new QuernRecipe(IIngredient.of(ItemOreTFC.get(Ore.HEMATITE, Ore.Grade.POOR, 1)), new ItemStack(ItemPowder.get(Powder.HEMATITE), 3)).setRegistryName("hematite_powder_from_poor"),
            new QuernRecipe(IIngredient.of(ItemOreTFC.get(Ore.HEMATITE, Ore.Grade.NORMAL, 1)), new ItemStack(ItemPowder.get(Powder.HEMATITE), 5)).setRegistryName("hematite_powder_from_normal"),
            new QuernRecipe(IIngredient.of(ItemOreTFC.get(Ore.HEMATITE, Ore.Grade.RICH, 1)), new ItemStack(ItemPowder.get(Powder.HEMATITE), 7)).setRegistryName("hematite_powder_from_rich"),

            //Limonite
            new QuernRecipe(IIngredient.of(ItemSmallOre.get(Ore.LIMONITE, 1)), new ItemStack(ItemPowder.get(Powder.LIMONITE), 2)).setRegistryName("limonite_powder_from_small"),
            new QuernRecipe(IIngredient.of(ItemOreTFC.get(Ore.LIMONITE, Ore.Grade.POOR, 1)), new ItemStack(ItemPowder.get(Powder.LIMONITE), 3)).setRegistryName("limonite_powder_from_poor"),
            new QuernRecipe(IIngredient.of(ItemOreTFC.get(Ore.LIMONITE, Ore.Grade.NORMAL, 1)), new ItemStack(ItemPowder.get(Powder.LIMONITE), 5)).setRegistryName("limonite_powder_from_normal"),
            new QuernRecipe(IIngredient.of(ItemOreTFC.get(Ore.LIMONITE, Ore.Grade.RICH, 1)), new ItemStack(ItemPowder.get(Powder.LIMONITE), 7)).setRegistryName("limonite_powder_from_rich"),

            //Malachite
            new QuernRecipe(IIngredient.of(ItemSmallOre.get(Ore.MALACHITE, 1)), new ItemStack(ItemPowder.get(Powder.MALACHITE), 2)).setRegistryName("malachite_powder_from_small"),
            new QuernRecipe(IIngredient.of(ItemOreTFC.get(Ore.MALACHITE, Ore.Grade.POOR, 1)), new ItemStack(ItemPowder.get(Powder.MALACHITE), 3)).setRegistryName("malachite_powder_from_poor"),
            new QuernRecipe(IIngredient.of(ItemOreTFC.get(Ore.MALACHITE, Ore.Grade.NORMAL, 1)), new ItemStack(ItemPowder.get(Powder.MALACHITE), 5)).setRegistryName("malachite_powder_from_normal"),
            new QuernRecipe(IIngredient.of(ItemOreTFC.get(Ore.MALACHITE, Ore.Grade.RICH, 1)), new ItemStack(ItemPowder.get(Powder.MALACHITE), 7)).setRegistryName("malachite_powder_from_rich"),

            //Bone meal
            new QuernRecipe(IIngredient.of("bone"), new ItemStack(Items.DYE, 3, EnumDyeColor.WHITE.getDyeDamage())).setRegistryName("bone_meal_from_bone"),
            new QuernRecipe(IIngredient.of(Blocks.BONE_BLOCK), new ItemStack(Items.DYE, 9, EnumDyeColor.WHITE.getDyeDamage())).setRegistryName("bone_meal_from_bone_block"),

            //Dye from plants
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.HOUSTONIA))), new ItemStack(ItemsTFC.DYE_WHITE, 2)).setRegistryName("crushed_houstonia"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.OXEYE_DAISY))), new ItemStack(ItemsTFC.DYE_WHITE, 1)).setRegistryName("crushed_oxeye_daisy"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.PRIMROSE))), new ItemStack(ItemsTFC.DYE_WHITE, 2)).setRegistryName("crushed_primrose"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.SNAPDRAGON_WHITE))), new ItemStack(ItemsTFC.DYE_WHITE, 2)).setRegistryName("crushed_snapdragon_white"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.TRILLIUM))), new ItemStack(ItemsTFC.DYE_WHITE, 1)).setRegistryName("crushed_trillium"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.SPANISH_MOSS))), new ItemStack(ItemsTFC.DYE_WHITE, 2)).setRegistryName("crushed_spanish_moss"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.TULIP_WHITE))), new ItemStack(ItemsTFC.DYE_WHITE, 1)).setRegistryName("crushed_tulip_white"),

            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.BUTTERFLY_MILKWEED))), new ItemStack(Items.DYE, 2, EnumDyeColor.ORANGE.getDyeDamage())).setRegistryName("crushed_butterfly_milkweed"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.CANNA))), new ItemStack(Items.DYE, 2, EnumDyeColor.ORANGE.getDyeDamage())).setRegistryName("crushed_canna"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.NASTURTIUM))), new ItemStack(Items.DYE, 2, EnumDyeColor.ORANGE.getDyeDamage())).setRegistryName("crushed_nasturium"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.STRELITZIA))), new ItemStack(Items.DYE, 1, EnumDyeColor.ORANGE.getDyeDamage())).setRegistryName("crushed_strelitzia"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.TULIP_ORANGE))), new ItemStack(Items.DYE, 1, EnumDyeColor.ORANGE.getDyeDamage())).setRegistryName("crushed_tulip_orange"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.WATER_CANNA))), new ItemStack(Items.DYE, 2, EnumDyeColor.ORANGE.getDyeDamage())).setRegistryName("crushed_water_canna"),

            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.ATHYRIUM_FERN))), new ItemStack(Items.DYE, 2, EnumDyeColor.MAGENTA.getDyeDamage())).setRegistryName("crushed_athyrium"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.MORNING_GLORY))), new ItemStack(Items.DYE, 1, EnumDyeColor.MAGENTA.getDyeDamage())).setRegistryName("crushed_morning_glory"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.PULSATILLA))), new ItemStack(Items.DYE, 1, EnumDyeColor.MAGENTA.getDyeDamage())).setRegistryName("crushed_pulsatilla"),

            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.LABRADOR_TEA))), new ItemStack(Items.DYE, 2, EnumDyeColor.LIGHT_BLUE.getDyeDamage())).setRegistryName("crushed_labrador_tea"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.SAPPHIRE_TOWER))), new ItemStack(Items.DYE, 2, EnumDyeColor.LIGHT_BLUE.getDyeDamage())).setRegistryName("crushed_sapphire_tower"),

            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.CALENDULA))), new ItemStack(Items.DYE, 2, EnumDyeColor.YELLOW.getDyeDamage())).setRegistryName("crushed_marigold"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.DANDELION))), new ItemStack(Items.DYE, 1, EnumDyeColor.YELLOW.getDyeDamage())).setRegistryName("crushed_dandelion"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.MEADS_MILKWEED))), new ItemStack(Items.DYE, 1, EnumDyeColor.YELLOW.getDyeDamage())).setRegistryName("crushed_meads_milkweed"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.GOLDENROD))), new ItemStack(Items.DYE, 2, EnumDyeColor.YELLOW.getDyeDamage())).setRegistryName("crushed_goldenrod"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.SNAPDRAGON_YELLOW))), new ItemStack(Items.DYE, 2, EnumDyeColor.YELLOW.getDyeDamage())).setRegistryName("crushed_snapdragon_yellow"),

            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.MOSS))), new ItemStack(Items.DYE, 2, EnumDyeColor.LIME.getDyeDamage())).setRegistryName("crushed_moss"),

            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.FOXGLOVE))), new ItemStack(Items.DYE, 2, EnumDyeColor.PINK.getDyeDamage())).setRegistryName("crushed_foxglove"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.SACRED_DATURA))), new ItemStack(Items.DYE, 2, EnumDyeColor.PINK.getDyeDamage())).setRegistryName("crushed_sacred_datura"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.TULIP_PINK))), new ItemStack(Items.DYE, 1, EnumDyeColor.PINK.getDyeDamage())).setRegistryName("crushed_tulip_pink"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.SNAPDRAGON_PINK))), new ItemStack(Items.DYE, 2, EnumDyeColor.PINK.getDyeDamage())).setRegistryName("crushed_snapdragon_pink"),

            //No gray :c

            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.YUCCA))), new ItemStack(Items.DYE, 2, EnumDyeColor.SILVER.getDyeDamage())).setRegistryName("crushed_yucca"),

            //No Cyan :c

            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.ALLIUM))), new ItemStack(Items.DYE, 2, EnumDyeColor.PURPLE.getDyeDamage())).setRegistryName("crushed_allium"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.BLACK_ORCHID))), new ItemStack(Items.DYE, 2, EnumDyeColor.PURPLE.getDyeDamage())).setRegistryName("crushed_black_orchid"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.PEROVSKIA))), new ItemStack(Items.DYE, 2, EnumDyeColor.PURPLE.getDyeDamage())).setRegistryName("crushed_perovskia"),

            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.BLUE_ORCHID))), new ItemStack(ItemsTFC.DYE_BLUE, 2)).setRegistryName("crushed_blue_orchid"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.GRAPE_HYACINTH))), new ItemStack(ItemsTFC.DYE_BLUE, 2)).setRegistryName("crushed_grape_hyacinth"),

            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.ROUGH_HORSETAIL))), new ItemStack(ItemsTFC.DYE_BROWN, 2)).setRegistryName("crushed_rough_horsetail"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.SARGASSUM))), new ItemStack(ItemsTFC.DYE_BROWN, 2)).setRegistryName("crushed_sargassum"),

            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.BARREL_CACTUS))), new ItemStack(Items.DYE, 4, EnumDyeColor.GREEN.getDyeDamage())).setRegistryName("crushed_barrel_cactus"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.REINDEER_LICHEN))), new ItemStack(Items.DYE, 4, EnumDyeColor.GREEN.getDyeDamage())).setRegistryName("crushed_reindeer_lichen"),

            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.GUZMANIA))), new ItemStack(Items.DYE, 2, EnumDyeColor.RED.getDyeDamage())).setRegistryName("crushed_guzmania"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.POPPY))), new ItemStack(Items.DYE, 2, EnumDyeColor.RED.getDyeDamage())).setRegistryName("crushed_poppy"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.PORCINI))), new ItemStack(Items.DYE, 2, EnumDyeColor.RED.getDyeDamage())).setRegistryName("crushed_porcini"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.ROSE))), new ItemStack(Items.DYE, 4, EnumDyeColor.RED.getDyeDamage())).setRegistryName("crushed_rose"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.SNAPDRAGON_RED))), new ItemStack(Items.DYE, 2, EnumDyeColor.RED.getDyeDamage())).setRegistryName("crushed_snapdragon_red"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.TROPICAL_MILKWEED))), new ItemStack(Items.DYE, 2, EnumDyeColor.RED.getDyeDamage())).setRegistryName("crushed_tropical_milkweed"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.TULIP_RED))), new ItemStack(Items.DYE, 2, EnumDyeColor.RED.getDyeDamage())).setRegistryName("crushed_tulip_red"),
            new QuernRecipe(IIngredient.of(BlockPlantTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.VRIESEA))), new ItemStack(Items.DYE, 2, EnumDyeColor.RED.getDyeDamage())).setRegistryName("crushed_vriesea"),

            //Misc
            new QuernRecipe(IIngredient.of("gemSylvite"), new ItemStack(ItemPowder.get(Powder.FERTILIZER), 4)).setRegistryName("sylvite"),
            new QuernRecipe(IIngredient.of("gemSulfur"), new ItemStack(ItemPowder.get(Powder.SULFUR), 4)).setRegistryName("sulfur"),
            new QuernRecipe(IIngredient.of("gemSaltpeter"), new ItemStack(ItemPowder.get(Powder.SALTPETER), 4)).setRegistryName("saltpeter"),
            new QuernRecipe(IIngredient.of("charcoal"), new ItemStack(ItemPowder.get(Powder.CHARCOAL), 4)).setRegistryName("charcoal"),
            new QuernRecipe(IIngredient.of("rockRocksalt"), new ItemStack(ItemPowder.get(Powder.SALT), 4)).setRegistryName("rocksalt"),
            new QuernRecipe(IIngredient.of(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 2)).setRegistryName("blaze_powder"),
            new QuernRecipe(IIngredient.of("gemLapis"), new ItemStack(ItemPowder.get(Powder.LAPIS_LAZULI), 4)).setRegistryName("lapis_lazuli"),
            new QuernRecipe(IIngredient.of("gemGraphite"), new ItemStack(ItemPowder.get(Powder.GRAPHITE), 4)).setRegistryName("graphite_powder"),
            new QuernRecipe(IIngredient.of("gemKaolinite"), new ItemStack(ItemPowder.get(Powder.KAOLINITE), 4)).setRegistryName("kaolinite_powder"),
            new QuernRecipeRandomGem(IIngredient.of("gemKimberlite"), Gem.DIAMOND).setRegistryName("diamonds"),
            new QuernRecipe(IIngredient.of(BlockRockVariant.get(Rock.LIMESTONE, Rock.Type.RAW)), new ItemStack(ItemsTFC.GYPSUM)).setRegistryName("gypsum")
        );
    }

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public static void onRegisterChiselRecipeEvent(RegistryEvent.Register<ChiselRecipe> event)
    {
        // Rock smoothing
        for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
        {
            Block rawRock = BlockRockVariant.get(rock, Rock.Type.RAW);
            IBlockState smoothRock = BlockRockVariant.get(rock, Rock.Type.SMOOTH).getDefaultState();
            event.getRegistry().register(new ChiselRecipe(rawRock, smoothRock).setRegistryName("smooth_" + rock.getRegistryName().getPath()));
        }

        // Alabaster smoothing
        for (EnumDyeColor color : EnumDyeColor.values())
        {
            Block rawColoredAlabaster = BlockDecorativeStone.ALABASTER_RAW.get(color);
            IBlockState smoothColoredAlabaster = BlockDecorativeStone.ALABASTER_POLISHED.get(color).getDefaultState();
            event.getRegistry().register(new ChiselRecipe(rawColoredAlabaster, smoothColoredAlabaster).setRegistryName("smooth_" + color.getName() + "_alabaster"));
        }
        // And plain
        event.getRegistry().register(new ChiselRecipe(BlocksTFC.ALABASTER_RAW_PLAIN, BlocksTFC.ALABASTER_POLISHED_PLAIN.getDefaultState()).setRegistryName("smooth_alabaster"));
    }

    private static void addAnvil(IForgeRegistry<AnvilRecipe> registry, Metal.ItemType inputType, Metal.ItemType outputType, boolean onlyToolMetals, @Nullable SmithingSkill.Type skillType, ForgeRule... rules)
    {
        // Helper method for adding all recipes that take ItemType -> ItemType
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            if (onlyToolMetals && !metal.isToolMetal())
                continue;

            // Create a recipe for each metal / item type combination
            IIngredient<ItemStack> ingredient;
            if (!inputType.isToolItem()) //Since tools don't have specific ore tags anymore
            {
                String oreDictEntry;
                if (inputType == Metal.ItemType.DOUBLE_INGOT)
                {
                    //noinspection ConstantConditions
                    oreDictEntry = OreDictionaryHelper.toString("ingot", "double", metal.getRegistryName().getPath());
                }
                else if (inputType == Metal.ItemType.DOUBLE_SHEET)
                {
                    //noinspection ConstantConditions
                    oreDictEntry = OreDictionaryHelper.toString("sheet", "double", metal.getRegistryName().getPath());
                }
                else
                {
                    //noinspection ConstantConditions
                    oreDictEntry = OreDictionaryHelper.toString(inputType, metal.getRegistryName().getPath());
                }
                ingredient = IIngredient.of(oreDictEntry);
            }
            else
            {
                ingredient = IIngredient.of(new ItemStack(ItemMetal.get(metal, inputType)));
            }

            ItemStack output = new ItemStack(ItemMetal.get(metal, outputType));
            if (!output.isEmpty())
            {
                //noinspection ConstantConditions
                registry.register(new AnvilRecipe(new ResourceLocation(MOD_ID, (outputType.name() + "_" + metal.getRegistryName().getPath()).toLowerCase()), ingredient, output, metal.getTier(), skillType, rules));
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void addAnvil(IForgeRegistry<AnvilRecipe> registry, ResourceLocation inputMetalLoc, ResourceLocation outputMetalLoc, @Nullable SmithingSkill.Type skillType)
    {
        // Helper method for adding INGOT -> INGOT with different metal working
        Metal inputMetal = TFCRegistries.METALS.getValue(inputMetalLoc);
        Metal outputMetal = TFCRegistries.METALS.getValue(outputMetalLoc);
        if (inputMetal != null && outputMetal != null)
        {
            ItemStack input = new ItemStack(ItemMetal.get(inputMetal, INGOT));
            ItemStack output = new ItemStack(ItemMetal.get(outputMetal, INGOT));
            if (!input.isEmpty() && !output.isEmpty())
            {
                //noinspection ConstantConditions
                registry.register(new AnvilRecipe(new ResourceLocation(MOD_ID, ("ingot_" + outputMetal.getRegistryName().getPath()).toLowerCase()), IIngredient.of(input), output, inputMetal.getTier(), skillType, HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST));
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void addAnvil(IForgeRegistry<AnvilRecipe> registry, String recipeName, Metal.ItemType inputType, ResourceLocation inputMetalRes, ItemStack output, Metal.Tier tier, @Nullable SmithingSkill.Type skillType, ForgeRule... rules)
    {
        // Helper method for adding METAL -> STACK
        Metal inputMetal = TFCRegistries.METALS.getValue(inputMetalRes);
        if (inputMetal != null && !output.isEmpty())
        {
            ItemStack input = new ItemStack(ItemMetal.get(inputMetal, inputType));
            if (!input.isEmpty() && !output.isEmpty())
            {
                registry.register(new AnvilRecipe(new ResourceLocation(MOD_ID, recipeName), IIngredient.of(input), output, tier, skillType, rules));
            }
        }
    }

    private static void addWelding(IForgeRegistry<WeldingRecipe> registry, Metal.ItemType inputType, Metal.ItemType outputType, SmithingSkill.Type skillType)
    {
        addWelding(registry, inputType, inputType, outputType, false, skillType);
    }

    @SuppressWarnings("ConstantConditions")
    private static void addWelding(IForgeRegistry<WeldingRecipe> registry, Metal.ItemType inputType1, Metal.ItemType inputType2, Metal.ItemType outputType, boolean onlyToolMetals, SmithingSkill.Type skillType)
    {
        // Helper method for adding all recipes that take ItemType -> ItemType
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            if (onlyToolMetals && !metal.isToolMetal())
                continue;

            // Create a recipe for each metal / item type combination
            IIngredient<ItemStack> ingredient1, ingredient2;
            if (!inputType1.isToolItem()) //Since tools don't have specific ore tags anymore
            {
                String oreDictEntry;
                if (inputType1 == Metal.ItemType.DOUBLE_INGOT)
                {
                    oreDictEntry = OreDictionaryHelper.toString("ingot", "double", metal.getRegistryName().getPath());
                }
                else if (inputType1 == Metal.ItemType.DOUBLE_SHEET)
                {
                    oreDictEntry = OreDictionaryHelper.toString("sheet", "double", metal.getRegistryName().getPath());
                }
                else
                {
                    oreDictEntry = OreDictionaryHelper.toString(inputType1, metal.getRegistryName().getPath());
                }
                ingredient1 = IIngredient.of(oreDictEntry);
            }
            else
            {
                ingredient1 = IIngredient.of(new ItemStack(ItemMetal.get(metal, inputType1)));
            }

            if (!inputType2.isToolItem())
            {
                String oreDictEntry;
                if (inputType2 == Metal.ItemType.DOUBLE_INGOT)
                {
                    oreDictEntry = OreDictionaryHelper.toString("ingot", "double", metal.getRegistryName().getPath());
                }
                else if (inputType2 == Metal.ItemType.DOUBLE_SHEET)
                {
                    oreDictEntry = OreDictionaryHelper.toString("sheet", "double", metal.getRegistryName().getPath());
                }
                else
                {
                    oreDictEntry = OreDictionaryHelper.toString(inputType2, metal.getRegistryName().getPath());
                }
                ingredient2 = IIngredient.of(oreDictEntry);
            }
            else
            {
                ingredient2 = IIngredient.of(new ItemStack(ItemMetal.get(metal, inputType2)));
            }

            ItemStack output = new ItemStack(outputType.isArmor() ? ItemMetalArmor.get(metal, outputType) : ItemMetal.get(metal, outputType));
            if (!output.isEmpty())
            {
                // Note: Welding recipes require one less than the tier of the metal
                registry.register(new WeldingRecipe(new ResourceLocation(MOD_ID, (outputType.name() + "_" + metal.getRegistryName().getPath()).toLowerCase()), ingredient1, ingredient2, output, metal.getTier().previous(), skillType));
            }
        }
    }

    private static void addWelding(IForgeRegistry<WeldingRecipe> registry, ResourceLocation input1Loc, ResourceLocation input2Loc, ResourceLocation outputLoc)
    {
        Metal inputMetal1 = TFCRegistries.METALS.getValue(input1Loc);
        Metal inputMetal2 = TFCRegistries.METALS.getValue(input2Loc);
        Metal outputMetal = TFCRegistries.METALS.getValue(outputLoc);
        if (inputMetal1 != null && inputMetal2 != null && outputMetal != null)
        {
            // Create a recipe for each metal / item type combination
            ItemStack input1 = new ItemStack(ItemMetal.get(inputMetal1, INGOT));
            ItemStack input2 = new ItemStack(ItemMetal.get(inputMetal2, INGOT));
            ItemStack output = new ItemStack(ItemMetal.get(outputMetal, INGOT));
            if (!input1.isEmpty() && !input2.isEmpty() && !output.isEmpty())
            {
                // Note: Welding recipes require one less than the tier of the metal
                //noinspection ConstantConditions
                registry.register(new WeldingRecipe(new ResourceLocation(MOD_ID, ("ingot_" + outputMetal.getRegistryName().getPath()).toLowerCase()), IIngredient.of(input1), IIngredient.of(input2), output, outputMetal.getTier().previous(), null));
            }
        }
    }
}