/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

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

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.recipes.LoomRecipe;
import net.dries007.tfc.api.recipes.WeldingRecipe;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipeMeasurable;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipeSplitting;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipeFoodTraits;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipeTemperature;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.recipes.heat.HeatRecipeMetalMelting;
import net.dries007.tfc.api.recipes.heat.HeatRecipeSimple;
import net.dries007.tfc.api.recipes.heat.HeatRecipeVessel;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeStone;
import net.dries007.tfc.api.recipes.quern.QuernRecipe;
import net.dries007.tfc.api.recipes.quern.QuernRecipeRandomGem;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.Gem;
import net.dries007.tfc.objects.Powder;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.ItemAnimalHide;
import net.dries007.tfc.objects.items.ItemPowder;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemMold;
import net.dries007.tfc.objects.items.ceramics.ItemUnfiredMold;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.metal.ItemMetalArmor;
import net.dries007.tfc.objects.items.rock.ItemRockToolHead;
import net.dries007.tfc.util.agriculture.Food;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.forge.ForgeRule;

import static net.dries007.tfc.api.types.Metal.ItemType.*;
import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.objects.fluids.FluidsTFC.*;
import static net.dries007.tfc.types.DefaultMetals.*;
import static net.dries007.tfc.util.forge.ForgeRule.*;

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
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 600), IIngredient.of(ItemsTFC.SUGARCANE, 5), null, new ItemStack(Items.SUGAR), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("sugar"),
            // Alcohol
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemFoodTFC.get(Food.BARLEY_FLOUR)), new FluidStack(FluidsTFC.BEER.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("beer"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of("apple"), new FluidStack(FluidsTFC.CIDER.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("cider"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(Items.SUGAR), new FluidStack(FluidsTFC.RUM.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("rum"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemFoodTFC.get(Food.RICE_FLOUR)), new FluidStack(FluidsTFC.SAKE.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("sake"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemFoodTFC.get(Food.POTATO)), new FluidStack(FluidsTFC.VODKA.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("vodka"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemFoodTFC.get(Food.WHEAT_FLOUR)), new FluidStack(FluidsTFC.WHISKEY.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("whiskey"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemFoodTFC.get(Food.CORNMEAL_FLOUR)), new FluidStack(FluidsTFC.CORN_WHISKEY.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("corn_whiskey"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of(ItemFoodTFC.get(Food.RYE_FLOUR)), new FluidStack(FluidsTFC.RYE_WHISKEY.get(), 500), ItemStack.EMPTY, 72 * ICalendar.TICKS_IN_HOUR).setRegistryName("rye_whiskey"),
            // Vinegar
            new BarrelRecipe(IIngredient.of(200, FluidsTFC.BEER.get(), FluidsTFC.CIDER.get(), FluidsTFC.RUM.get(), FluidsTFC.SAKE.get(), FluidsTFC.VODKA.get(), FluidsTFC.WHISKEY.get(), FluidsTFC.CORN_WHISKEY.get(), FluidsTFC.RYE_WHISKEY.get()), IIngredient.of("fruit"), new FluidStack(FluidsTFC.VINEGAR.get(), 200), ItemStack.EMPTY, 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("vinegar"),
            // Food preservation
            new BarrelRecipeFoodTraits(IIngredient.of(VINEGAR.get(), 125), IIngredient.of("fruit"), CapabilityFood.PICKLED, 4 * ICalendar.TICKS_IN_HOUR).setRegistryName("pickling_fruit"),
            new BarrelRecipeFoodTraits(IIngredient.of(VINEGAR.get(), 125), IIngredient.of("meat"), CapabilityFood.PICKLED, 4 * ICalendar.TICKS_IN_HOUR).setRegistryName("pickling_meat"),
            new BarrelRecipeFoodTraits(IIngredient.of(VINEGAR.get(), 125), IIngredient.of("vegetable"), CapabilityFood.PICKLED, 4 * ICalendar.TICKS_IN_HOUR).setRegistryName("pickling_vegetable"),
            // todo: brined food
            new BarrelRecipe(IIngredient.of(LIMEWATER.get(), 100), IIngredient.of("sand"), null, new ItemStack(ItemsTFC.MORTAR, 16), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("mortar"),
            // todo: curdled milk -> cheese (use an empty IIngredient for the item)

            // Instant recipes: set the duration to 0
            // todo: brine
            new BarrelRecipe(IIngredient.of(FRESH_WATER.get(), 500), IIngredient.of("dustFlux"), new FluidStack(LIMEWATER.get(), 500), ItemStack.EMPTY, 0).setRegistryName("limewater"),
            // todo: curdled milk (make it a simpler calculation)

            new BarrelRecipeTemperature(IIngredient.of(FRESH_WATER.get(), 1), 50).setRegistryName("fresh_water_cooling")
        );
    }

    @SubscribeEvent
    public static void onRegisterKnappingRecipeEvent(RegistryEvent.Register<KnappingRecipe> event)
    {
        /* STONE TOOL HEADS */

        for (Rock.ToolType type : Rock.ToolType.values())
        {
            // This covers all stone -> single tool head recipes
            KnappingRecipe r = new KnappingRecipeStone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, type)), type.getPattern());
            event.getRegistry().register(r.setRegistryName(type.name().toLowerCase() + "_head"));
        }
        // these recipes cover all cases where multiple stone items can be made
        // recipes are already mirror checked
        event.getRegistry().registerAll(
            new KnappingRecipeStone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.KNIFE), 2), "X  X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("knife_head_1"),
            new KnappingRecipeStone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.KNIFE), 2), "X   X", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("knife_head_2"),
            new KnappingRecipeStone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.KNIFE), 2), " X X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("knife_head_3"),
            new KnappingRecipeStone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.HOE), 2), "XXXXX", "XX   ", "     ", "XXXXX", "XX   ").setRegistryName("hoe_head_1"),
            new KnappingRecipeStone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.HOE), 2), "XXXXX", "XX   ", "     ", "XXXXX", "   XX").setRegistryName("hoe_head_2")
        );

        /* CLAY ITEMS */

        for (Metal.ItemType type : Metal.ItemType.values())
        {
            if (type.hasMold(null))
            {
                int amount = type == INGOT ? 2 : 1;
                event.getRegistry().register(new KnappingRecipeSimple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemUnfiredMold.get(type), amount), type.getPattern()).setRegistryName(type.name().toLowerCase() + "_mold"));
            }
        }

        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.UNFIRED_VESSEL), " XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX ").setRegistryName("clay_small_vessel"),
            new KnappingRecipeSimple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.UNFIRED_JUG), " X   ", "XXXX ", "XXX X", "XXXX ", "XXX  ").setRegistryName("clay_jug"),
            new KnappingRecipeSimple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.UNFIRED_POT), "X   X", "X   X", "X   X", "XXXXX", " XXX ").setRegistryName("clay_pot"),
            new KnappingRecipeSimple(KnappingRecipe.Type.CLAY, false, new ItemStack(ItemsTFC.UNFIRED_BOWL, 2), "X   X", " XXX ").setRegistryName(MOD_ID, "clay_bowl"),
            new KnappingRecipeSimple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.UNFIRED_BOWL, 4), "X   X", " XXX ", "     ", "X   X", " XXX ").setRegistryName("clay_bowl_2"),
            new KnappingRecipeSimple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.UNFIRED_LARGE_VESSEL), "X   X", "X   X", "X   X", "X   X", "XXXXX").setRegistryName("clay_large_vessel")
        );

        /* LEATHER ITEMS */

        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingRecipe.Type.LEATHER, true, new ItemStack(Items.LEATHER_HELMET), "XXXXX", "X   X", "X   X", "     ", "     ").setRegistryName("leather_helmet"),
            new KnappingRecipeSimple(KnappingRecipe.Type.LEATHER, true, new ItemStack(Items.LEATHER_CHESTPLATE), "X   X", "XXXXX", "XXXXX", "XXXXX", "XXXXX").setRegistryName("leather_chestplate"),
            new KnappingRecipeSimple(KnappingRecipe.Type.LEATHER, true, new ItemStack(Items.LEATHER_LEGGINGS), "XXXXX", "XXXXX", "XX XX", "XX XX", "XX XX").setRegistryName("leather_leggings"),
            new KnappingRecipeSimple(KnappingRecipe.Type.LEATHER, true, new ItemStack(Items.LEATHER_BOOTS), "XX   ", "XX   ", "XX   ", "XXXX ", "XXXXX").setRegistryName("leather_boots"),
            new KnappingRecipeSimple(KnappingRecipe.Type.LEATHER, true, new ItemStack(Items.SADDLE), "  X  ", "XXXXX", "XXXXX", "XXXXX", "  X  ").setRegistryName("leather_saddle")
        );

        /* FIRE CLAY ITEMS */

        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingRecipe.Type.FIRE_CLAY, true, new ItemStack(BlocksTFC.CRUCIBLE), "X   X", "X   X", "X   X", "X   X", "XXXXX").setRegistryName("fire_clay_crucible"),
            new KnappingRecipeSimple(KnappingRecipe.Type.FIRE_CLAY, true, new ItemStack(ItemsTFC.UNFIRED_FIRE_BRICK, 2), "XX XX", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("fire_clay_fire_brick")
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

            // Fired Pottery - doesn't burn up
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.FIRED_FIRE_BRICK), new ItemStack(ItemsTFC.FIRED_FIRE_BRICK), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_fire_brick"),
            new HeatRecipeVessel(IIngredient.of(ItemsTFC.FIRED_VESSEL), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_vessel"),
            new HeatRecipeVessel(IIngredient.of(ItemsTFC.FIRED_VESSEL_GLAZED), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_vessel_glazed_all"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.FIRED_JUG), new ItemStack(ItemsTFC.FIRED_JUG), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_jug"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.FIRED_POT), new ItemStack(ItemsTFC.FIRED_POT), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_pot"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.FIRED_BOWL), new ItemStack(ItemsTFC.FIRED_BOWL), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_bowl"),
            new HeatRecipeSimple(IIngredient.of(ItemsTFC.FIRED_SPINDLE), new ItemStack(ItemsTFC.FIRED_SPINDLE), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_spindle"),
            new HeatRecipeSimple(IIngredient.of(BlocksTFC.FIRED_LARGE_VESSEL), new ItemStack(BlocksTFC.FIRED_LARGE_VESSEL), 1599f, Metal.Tier.TIER_I).setRegistryName("fired_large_vessel"),

            // Misc
            new HeatRecipeSimple(IIngredient.of("stickWood"), new ItemStack(BlocksTFC.TORCH, 2), 40).setRegistryName("torch"),
            new HeatRecipeSimple(IIngredient.of("sand"), new ItemStack(Blocks.GLASS), 600).setRegistryName("glass"),

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
            HeatRecipe.destroy(IIngredient.of(ItemFoodTFC.get(Food.COOKED_VENISON)), 480).setRegistryName("burned_venison")
        );
    }

    @SubscribeEvent
    public static void onRegisterAnvilRecipeEvent(RegistryEvent.Register<AnvilRecipe> event)
    {
        IForgeRegistry<AnvilRecipe> r = event.getRegistry();

        // Basic Components
        addAnvil(r, DOUBLE_INGOT, SHEET, false, HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST);

        // Tools
        addAnvil(r, INGOT, PICK_HEAD, true, PUNCH_LAST, BEND_NOT_LAST, DRAW_NOT_LAST);
        addAnvil(r, INGOT, SHOVEL_HEAD, true, PUNCH_LAST, HIT_NOT_LAST);
        addAnvil(r, INGOT, AXE_HEAD, true, PUNCH_LAST, HIT_SECOND_LAST, UPSET_THIRD_LAST);
        addAnvil(r, INGOT, HOE_HEAD, true, PUNCH_LAST, HIT_NOT_LAST, BEND_NOT_LAST);
        addAnvil(r, INGOT, HAMMER_HEAD, true, PUNCH_LAST, SHRINK_NOT_LAST);
        addAnvil(r, INGOT, PROPICK_HEAD, true, PUNCH_LAST, DRAW_NOT_LAST, BEND_NOT_LAST);
        addAnvil(r, INGOT, SAW_BLADE, true, HIT_LAST, HIT_SECOND_LAST);
        addAnvil(r, INGOT, SWORD_BLADE, true, HIT_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
        addAnvil(r, DOUBLE_INGOT, MACE_HEAD, true, HIT_LAST, SHRINK_NOT_LAST, BEND_NOT_LAST);
        addAnvil(r, INGOT, SCYTHE_BLADE, true, HIT_LAST, DRAW_SECOND_LAST, BEND_THIRD_LAST);
        addAnvil(r, INGOT, KNIFE_BLADE, true, HIT_LAST, DRAW_SECOND_LAST, DRAW_THIRD_LAST);
        addAnvil(r, INGOT, JAVELIN_HEAD, true, HIT_LAST, HIT_SECOND_LAST, DRAW_THIRD_LAST);

        // Armor
        addAnvil(r, DOUBLE_SHEET, UNFINISHED_HELMET, true, HIT_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
        addAnvil(r, DOUBLE_SHEET, UNFINISHED_CHESTPLATE, true, HIT_LAST, HIT_SECOND_LAST, UPSET_THIRD_LAST);
        addAnvil(r, DOUBLE_SHEET, UNFINISHED_GREAVES, true, BEND_ANY, DRAW_ANY, HIT_ANY);
        addAnvil(r, SHEET, UNFINISHED_BOOTS, true, BEND_LAST, BEND_SECOND_LAST, SHRINK_THIRD_LAST);

        r.register(new AnvilRecipeMeasurable(new ResourceLocation(MOD_ID, "refining_bloom"), IIngredient.of(ItemsTFC.UNREFINED_BLOOM), new ItemStack(ItemsTFC.REFINED_BLOOM), Metal.Tier.TIER_II, HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST));
        r.register(new AnvilRecipeSplitting(new ResourceLocation(MOD_ID, "splitting_bloom"), IIngredient.of(ItemsTFC.REFINED_BLOOM), new ItemStack(ItemsTFC.REFINED_BLOOM), 100, Metal.Tier.TIER_II, PUNCH_LAST));
        r.register(new AnvilRecipeMeasurable(new ResourceLocation(MOD_ID, "refine_bloom_ingot"), IIngredient.of(ItemsTFC.REFINED_BLOOM), new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON, INGOT)), 100, Metal.Tier.TIER_II, HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST));

        //Shields
        addAnvil(r, DOUBLE_SHEET, SHIELD, true, UPSET_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);

        // Steel Working
        addAnvil(r, PIG_IRON, HIGH_CARBON_STEEL);
        addAnvil(r, HIGH_CARBON_STEEL, STEEL);
        addAnvil(r, HIGH_CARBON_BLACK_STEEL, BLACK_STEEL);
        addAnvil(r, HIGH_CARBON_BLUE_STEEL, BLUE_STEEL);
        addAnvil(r, HIGH_CARBON_RED_STEEL, RED_STEEL);

        //Vanilla iron bars and trap doors
        addAnvil(r, "iron_bars", SHEET, WROUGHT_IRON, new ItemStack(Blocks.IRON_BARS, 8), Metal.Tier.TIER_III, UPSET_LAST, PUNCH_SECOND_LAST, PUNCH_THIRD_LAST);
        addAnvil(r, "iron_bars_double", DOUBLE_SHEET, WROUGHT_IRON, new ItemStack(Blocks.IRON_BARS, 16), Metal.Tier.TIER_III, UPSET_LAST, PUNCH_SECOND_LAST, PUNCH_THIRD_LAST);
        addAnvil(r, "iron_trap_door", DOUBLE_SHEET, WROUGHT_IRON, new ItemStack(Blocks.IRON_TRAPDOOR), Metal.Tier.TIER_III, UPSET_LAST, PUNCH_SECOND_LAST, PUNCH_THIRD_LAST);
        addAnvil(r, "iron_door", SHEET, WROUGHT_IRON, new ItemStack(Items.IRON_DOOR), Metal.Tier.TIER_III, HIT_LAST, DRAW_NOT_LAST, PUNCH_NOT_LAST);
    }

    @SubscribeEvent
    public static void onRegisterWeldingRecipeEvent(RegistryEvent.Register<WeldingRecipe> event)
    {
        IForgeRegistry<WeldingRecipe> r = event.getRegistry();

        // Basic Parts
        addWelding(r, INGOT, DOUBLE_INGOT);
        addWelding(r, SHEET, DOUBLE_SHEET);

        // Armor
        addWelding(r, UNFINISHED_HELMET, SHEET, HELMET, true);
        addWelding(r, UNFINISHED_CHESTPLATE, DOUBLE_SHEET, CHESTPLATE, true);
        addWelding(r, UNFINISHED_GREAVES, SHEET, GREAVES, true);
        addWelding(r, UNFINISHED_BOOTS, SHEET, BOOTS, true);

        // Steel Welding
        addWelding(r, WEAK_STEEL, PIG_IRON, HIGH_CARBON_BLACK_STEEL);
        addWelding(r, WEAK_BLUE_STEEL, BLACK_STEEL, HIGH_CARBON_BLUE_STEEL);
        addWelding(r, WEAK_RED_STEEL, BLACK_STEEL, HIGH_CARBON_RED_STEEL);

        // Special Recipes
        addWelding(r, KNIFE_BLADE, KNIFE_BLADE, SHEARS, true);
    }

    @SubscribeEvent
    public static void onRegisterLoomRecipeEvent(RegistryEvent.Register<LoomRecipe> event)
    {
        IForgeRegistry<LoomRecipe> r = event.getRegistry();

        r.registerAll(
            new LoomRecipe(new ResourceLocation(MOD_ID, "burlap_cloth"), IIngredient.of(ItemsTFC.JUTE_FIBER, 12), new ItemStack(ItemsTFC.BURLAP_CLOTH), 12, new ResourceLocation(MOD_ID, "textures/blocks/devices/loom/product/burlap.png")),
            new LoomRecipe(new ResourceLocation(MOD_ID, "wool_cloth"), IIngredient.of(ItemsTFC.WOOL_YARN, 16), new ItemStack(ItemsTFC.WOOL_CLOTH), 16, new ResourceLocation("minecraft", "textures/blocks/wool_colored_white.png")),
            new LoomRecipe(new ResourceLocation(MOD_ID, "silk_cloth"), IIngredient.of(Items.STRING, 24), new ItemStack(ItemsTFC.SILK_CLOTH), 24, new ResourceLocation("minecraft", "textures/blocks/wool_colored_white.png")),

            new LoomRecipe(new ResourceLocation(MOD_ID, "wool_block"), IIngredient.of(ItemsTFC.WOOL_CLOTH, 4), new ItemStack(Blocks.WOOL), 4, new ResourceLocation("minecraft", "textures/blocks/wool_colored_white.png"))
        );
    }

    @SubscribeEvent
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
            new QuernRecipe(IIngredient.of("maize"), new ItemStack(ItemFoodTFC.get(Food.CORNMEAL_FLOUR), 1)).setRegistryName("maize"),

            //Flux
            new QuernRecipe(IIngredient.of("gemBorax"), new ItemStack(ItemPowder.get(Powder.FLUX), 6)).setRegistryName("boarx"),
            new QuernRecipe(IIngredient.of("rockFlux"), new ItemStack(ItemPowder.get(Powder.FLUX), 2)).setRegistryName("flux"),

            //Redstone
            new QuernRecipe(IIngredient.of("gemCinnabar"), new ItemStack(Items.REDSTONE, 8)).setRegistryName("cinnabar"),
            new QuernRecipe(IIngredient.of("gemCryolite"), new ItemStack(Items.REDSTONE, 8)).setRegistryName("cryolite"),

            //Hematite
            new QuernRecipe(IIngredient.of("oreHematiteSmall"), new ItemStack(ItemPowder.get(Powder.HEMATITE_POWDER), 2)).setRegistryName("hematite_powder_from_small"),
            new QuernRecipe(IIngredient.of("oreHematitePoor"), new ItemStack(ItemPowder.get(Powder.HEMATITE_POWDER), 3)).setRegistryName("hematite_powder_from_poor"),
            new QuernRecipe(IIngredient.of("oreHematiteNormal"), new ItemStack(ItemPowder.get(Powder.HEMATITE_POWDER), 5)).setRegistryName("hematite_powder_from_normal"),
            new QuernRecipe(IIngredient.of("oreHematiteRich"), new ItemStack(ItemPowder.get(Powder.HEMATITE_POWDER), 7)).setRegistryName("hematite_powder_from_rich"),

            //Limonite
            new QuernRecipe(IIngredient.of("oreLimoniteSmall"), new ItemStack(ItemPowder.get(Powder.LIMONITE_POWDER), 2)).setRegistryName("limonite_powder_from_small"),
            new QuernRecipe(IIngredient.of("oreLimonitePoor"), new ItemStack(ItemPowder.get(Powder.LIMONITE_POWDER), 3)).setRegistryName("limonite_powder_from_poor"),
            new QuernRecipe(IIngredient.of("oreLimoniteNormal"), new ItemStack(ItemPowder.get(Powder.LIMONITE_POWDER), 5)).setRegistryName("limonite_powder_from_normal"),
            new QuernRecipe(IIngredient.of("oreLimoniteRich"), new ItemStack(ItemPowder.get(Powder.LIMONITE_POWDER), 7)).setRegistryName("limonite_powder_from_rich"),

            //Malachite
            new QuernRecipe(IIngredient.of("oreMalachiteSmall"), new ItemStack(ItemPowder.get(Powder.MALACHITE_POWDER), 2)).setRegistryName("malachite_powder_from_small"),
            new QuernRecipe(IIngredient.of("oreMalachitePoor"), new ItemStack(ItemPowder.get(Powder.MALACHITE_POWDER), 3)).setRegistryName("malachite_powder_from_poor"),
            new QuernRecipe(IIngredient.of("oreMalachiteNormal"), new ItemStack(ItemPowder.get(Powder.MALACHITE_POWDER), 5)).setRegistryName("malachite_powder_from_normal"),
            new QuernRecipe(IIngredient.of("oreMalachiteRich"), new ItemStack(ItemPowder.get(Powder.MALACHITE_POWDER), 7)).setRegistryName("malachite_powder_from_rich"),

            //Bone meal
            new QuernRecipe(IIngredient.of("bone"), new ItemStack(Items.DYE, 3, EnumDyeColor.WHITE.getDyeDamage())).setRegistryName("bone_meal_from_bone"),
            new QuernRecipe(IIngredient.of(Blocks.BONE_BLOCK), new ItemStack(Items.DYE, 9, EnumDyeColor.WHITE.getDyeDamage())).setRegistryName("bone_meal_from_bone_block"),

            //Misc
            new QuernRecipe(IIngredient.of("gemSylvite"), new ItemStack(ItemPowder.get(Powder.FERTILIZER), 4)).setRegistryName("sylvite"),
            new QuernRecipe(IIngredient.of("gemSulfur"), new ItemStack(ItemPowder.get(Powder.SULFUR_POWDER), 4)).setRegistryName("sulfur"),
            new QuernRecipe(IIngredient.of("gemSaltpeter"), new ItemStack(ItemPowder.get(Powder.SALTPETER_POWDER), 4)).setRegistryName("saltpeter"),
            new QuernRecipe(IIngredient.of("rockRocksalt"), new ItemStack(ItemPowder.get(Powder.SALT), 4)).setRegistryName("rocksalt"),
            new QuernRecipe(IIngredient.of(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 2)).setRegistryName("blaze_powder"),
            new QuernRecipe(IIngredient.of("gemLapisLazuli"), new ItemStack(ItemPowder.get(Powder.LAPIS_LAZULI_POWDER), 4)).setRegistryName("lapis_lazuli"),
            new QuernRecipe(IIngredient.of("gemGraphite"), new ItemStack(ItemPowder.get(Powder.GRAPHITE_POWDER), 4)).setRegistryName("graphite_powder"),
            new QuernRecipe(IIngredient.of("gemKaolinite"), new ItemStack(ItemPowder.get(Powder.KAOLINITE_POWDER), 4)).setRegistryName("kaolinite_powder"),
            new QuernRecipeRandomGem(IIngredient.of("gemKimberlite"), Gem.DIAMOND).setRegistryName("diamonds")
        );
    }

    private static void addAnvil(IForgeRegistry<AnvilRecipe> registry, Metal.ItemType inputType, Metal.ItemType outputType, boolean onlyToolMetals, ForgeRule... rules)
    {
        // Helper method for adding all recipes that take ItemType -> ItemType
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            if (onlyToolMetals && !metal.isToolMetal())
                continue;

            // Create a recipe for each metal / item type combination
            ItemStack input = new ItemStack(ItemMetal.get(metal, inputType));
            ItemStack output = new ItemStack(ItemMetal.get(metal, outputType));
            if (!input.isEmpty() && !output.isEmpty())
            {
                //noinspection ConstantConditions
                registry.register(new AnvilRecipe(new ResourceLocation(MOD_ID, (outputType.name() + "_" + metal.getRegistryName().getPath()).toLowerCase()), IIngredient.of(input), output, metal.getTier(), rules));
            }
        }
    }

    private static void addAnvil(IForgeRegistry<AnvilRecipe> registry, ResourceLocation inputMetalLoc, ResourceLocation outputMetalLoc)
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
                registry.register(new AnvilRecipe(new ResourceLocation(MOD_ID, ("ingot_" + outputMetal.getRegistryName().getPath()).toLowerCase()), IIngredient.of(input), output, outputMetal.getTier(), HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST));
            }
        }
    }

    private static void addAnvil(IForgeRegistry<AnvilRecipe> registry, String recipeName, Metal.ItemType inputType, ResourceLocation inputMetalRes, ItemStack output, Metal.Tier tier, ForgeRule... rules)
    {
        // Helper method for adding METAL -> STACK
        Metal inputMetal = TFCRegistries.METALS.getValue(inputMetalRes);
        if (inputMetal != null && !output.isEmpty())
        {
            ItemStack input = new ItemStack(ItemMetal.get(inputMetal, inputType));
            if (!input.isEmpty() && !output.isEmpty())
            {
                registry.register(new AnvilRecipe(new ResourceLocation(MOD_ID, recipeName), IIngredient.of(input), output, tier, rules));
            }
        }
    }

    private static void addWelding(IForgeRegistry<WeldingRecipe> registry, Metal.ItemType inputType, Metal.ItemType outputType)
    {
        addWelding(registry, inputType, inputType, outputType, false);
    }

    private static void addWelding(IForgeRegistry<WeldingRecipe> registry, Metal.ItemType inputType1, Metal.ItemType inputType2, Metal.ItemType outputType, boolean onlyToolMetals)
    {
        // Helper method for adding all recipes that take ItemType -> ItemType
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            if (onlyToolMetals && !metal.isToolMetal())
                continue;

            // Create a recipe for each metal / item type combination
            ItemStack input1 = new ItemStack(ItemMetal.get(metal, inputType1));
            ItemStack input2 = new ItemStack(ItemMetal.get(metal, inputType2));
            ItemStack output = new ItemStack(outputType.isArmor() ? ItemMetalArmor.get(metal, outputType) : ItemMetal.get(metal, outputType));
            if (!input1.isEmpty() && !input2.isEmpty() && !output.isEmpty())
            {
                // Note: Welding recipes require one less than the tier of the metal
                //noinspection ConstantConditions
                registry.register(new WeldingRecipe(new ResourceLocation(MOD_ID, (outputType.name() + "_" + metal.getRegistryName().getPath()).toLowerCase()), IIngredient.of(input1), IIngredient.of(input2), output, metal.getTier().previous()));
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
                registry.register(new WeldingRecipe(new ResourceLocation(MOD_ID, ("ingot_" + outputMetal.getRegistryName().getPath()).toLowerCase()), IIngredient.of(input1), IIngredient.of(input2), output, outputMetal.getTier().previous()));
            }
        }
    }
}