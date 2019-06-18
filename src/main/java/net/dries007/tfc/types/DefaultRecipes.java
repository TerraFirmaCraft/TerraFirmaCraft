/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.recipes.*;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.ItemAnimalHide;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemMold;
import net.dries007.tfc.objects.items.ceramics.ItemUnfiredMold;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.rock.ItemRockToolHead;
import net.dries007.tfc.util.forge.ForgeRule;
import net.dries007.tfc.world.classic.CalendarTFC;

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
            new BarrelRecipe(IIngredient.of(FRESH_WATER, 300), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.SCRAPED, ItemAnimalHide.HideSize.SMALL)), null, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.PREPARED, ItemAnimalHide.HideSize.SMALL)), 8 * CalendarTFC.TICKS_IN_HOUR).setRegistryName(MOD_ID, "small_prepared_hide"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER, 400), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.SCRAPED, ItemAnimalHide.HideSize.MEDIUM)), null, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.PREPARED, ItemAnimalHide.HideSize.MEDIUM)), 8 * CalendarTFC.TICKS_IN_HOUR).setRegistryName(MOD_ID, "medium_prepared_hide"),
            new BarrelRecipe(IIngredient.of(FRESH_WATER, 500), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.SCRAPED, ItemAnimalHide.HideSize.LARGE)), null, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.PREPARED, ItemAnimalHide.HideSize.LARGE)), 8 * CalendarTFC.TICKS_IN_HOUR).setRegistryName(MOD_ID, "large_prepared_hide"),
            new BarrelRecipe(IIngredient.of(LIMEWATER, 300), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.RAW, ItemAnimalHide.HideSize.SMALL)), null, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.SOAKED, ItemAnimalHide.HideSize.SMALL)), 8 * CalendarTFC.TICKS_IN_HOUR).setRegistryName(MOD_ID, "small_soaked_hide"),
            new BarrelRecipe(IIngredient.of(LIMEWATER, 400), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.RAW, ItemAnimalHide.HideSize.MEDIUM)), null, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.SOAKED, ItemAnimalHide.HideSize.MEDIUM)), 8 * CalendarTFC.TICKS_IN_HOUR).setRegistryName(MOD_ID, "medium_soaked_hide"),
            new BarrelRecipe(IIngredient.of(LIMEWATER, 500), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.RAW, ItemAnimalHide.HideSize.LARGE)), null, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.SOAKED, ItemAnimalHide.HideSize.LARGE)), 8 * CalendarTFC.TICKS_IN_HOUR).setRegistryName(MOD_ID, "large_soaked_hide"),
            new BarrelRecipe(IIngredient.of(TANNIN, 300), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.PREPARED, ItemAnimalHide.HideSize.SMALL)), null, new ItemStack(Items.LEATHER), 8 * CalendarTFC.TICKS_IN_HOUR).setRegistryName("leather_small_hide"),
            new BarrelRecipe(IIngredient.of(TANNIN, 400), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.PREPARED, ItemAnimalHide.HideSize.MEDIUM)), null, new ItemStack(Items.LEATHER, 2), 8 * CalendarTFC.TICKS_IN_HOUR).setRegistryName("leather_medium_hide"),
            new BarrelRecipe(IIngredient.of(TANNIN, 500), IIngredient.of(ItemAnimalHide.get(ItemAnimalHide.HideType.PREPARED, ItemAnimalHide.HideSize.LARGE)), null, new ItemStack(Items.LEATHER, 3), 8 * CalendarTFC.TICKS_IN_HOUR).setRegistryName("leather_large_hide"),
            // Misc
            new BarrelRecipe(IIngredient.of(FRESH_WATER, 1000), IIngredient.of("logWoodTannin"), new FluidStack(TANNIN, 10000), ItemStack.EMPTY, 8 * CalendarTFC.TICKS_IN_HOUR).setRegistryName(MOD_ID, "tannin"),
            // todo: enslave Claycorp for recipe magics
            // todo: jute
            // todo: sugar
            // todo: all alcohol recipes
            // todo: vinegar (many variants, use "fruit" ore dict and IIngredient.of(int, Fluid...)
            // todo: brine + food? (may have to have a discussion about how we should handle "traits" on food.)
            // todo: pickling (same as above)
            // todo: mortar
            // todo: curdled milk -> cheese (use an empty IIngredient for the item)
            // todo: flavored cheese (figure out how to handle this?)

            // Instant recipes: set the duration to 0
            // todo: brine
            // todo: limewater
            new BarrelRecipe(IIngredient.of(FRESH_WATER, 500), IIngredient.of("dustFlux"), new FluidStack(LIMEWATER, 500), ItemStack.EMPTY, 0).setRegistryName(MOD_ID, "limewater")
            // todo: curdled milk (make it a simpler calculation)
        );
    }

    @SubscribeEvent
    public static void onRegisterKnappingRecipeEvent(RegistryEvent.Register<KnappingRecipe> event)
    {
        /* STONE TOOL HEADS */

        for (Rock.ToolType type : Rock.ToolType.values())
        {
            // This covers all stone -> single tool head recipes
            KnappingRecipe r = new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, type)), type.getPattern());
            event.getRegistry().register(r.setRegistryName(MOD_ID, type.name().toLowerCase() + "_head"));
        }
        // these recipes cover all cases where multiple stone items can be made
        // recipes are already mirror checked
        event.getRegistry().registerAll(
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.KNIFE), 2), "X  X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName(MOD_ID, "knife_head_1"),
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.KNIFE), 2), "X   X", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName(MOD_ID, "knife_head_2"),
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.KNIFE), 2), " X X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName(MOD_ID, "knife_head_3"),
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.HOE), 2), "XXXXX", "XX   ", "     ", "XXXXX", "XX   ").setRegistryName(MOD_ID, "hoe_head_1"),
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.HOE), 2), "XXXXX", "XX   ", "     ", "XXXXX", "   XX").setRegistryName(MOD_ID, "hoe_head_2")
        );

        /* CLAY ITEMS */

        for (Metal.ItemType type : Metal.ItemType.values())
        {
            if (type.hasMold(null))
            {
                event.getRegistry().register(new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemUnfiredMold.get(type)), type.getPattern()).setRegistryName(MOD_ID, type.name().toLowerCase() + "_mold"));
            }
        }

        event.getRegistry().registerAll(
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_VESSEL), " XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX ").setRegistryName(MOD_ID, "clay_small_vessel"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_JUG), " X   ", "XXXX ", "XXX X", "XXXX ", "XXX  ").setRegistryName(MOD_ID, "clay_jug"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_POT), "X   X", "X   X", "X   X", "XXXXX", " XXX ").setRegistryName(MOD_ID, "clay_pot"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_BOWL, 2), "X   X", " XXX ").setRegistryName(MOD_ID, "clay_bowl"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_BOWL, 4), "X   X", " XXX ", "     ", "X   X", " XXX ").setRegistryName(MOD_ID, "clay_bowl_2")
            //new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(BlocksTFC.CERAMICS_LARGE_VESSEL), "XXXX", "X  X", "X  X", "X  X", "XXXX").setRegistryName(MOD_ID, "clay_large_vessel"),
        );

        /* LEATHER ITEMS */

        event.getRegistry().registerAll(
            new KnappingRecipe.Simple(KnappingRecipe.Type.LEATHER, true, new ItemStack(Items.LEATHER_HELMET), "XXXXX", "X   X", "X   X", "     ", "     ").setRegistryName(MOD_ID, "leather_helmet"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.LEATHER, true, new ItemStack(Items.LEATHER_CHESTPLATE), "X   X", "XXXXX", "XXXXX", "XXXXX", "XXXXX").setRegistryName(MOD_ID, "leather_chestplate"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.LEATHER, true, new ItemStack(Items.LEATHER_LEGGINGS), "XXXXX", "XXXXX", "XX XX", "XX XX", "XX XX").setRegistryName(MOD_ID, "leather_leggings"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.LEATHER, true, new ItemStack(Items.LEATHER_BOOTS), "XX   ", "XX   ", "XX   ", "XXXX ", "XXXXX").setRegistryName(MOD_ID, "leather_boots"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.LEATHER, true, new ItemStack(Items.SADDLE), "  X  ", "XXXXX", "XXXXX", "XXXXX", "  X  ").setRegistryName(MOD_ID, "leather_saddle")
        );

        /* FIRE CLAY ITEMS */

        event.getRegistry().registerAll(
            new KnappingRecipe.Simple(KnappingRecipe.Type.FIRE_CLAY, true, new ItemStack(BlocksTFC.CRUCIBLE), "X   X", "X   X", "X   X", "X   X", "XXXXX").setRegistryName(MOD_ID, "fire_clay_crucible"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.FIRE_CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_FIRE_BRICK, 2), "XX XX", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName(MOD_ID, "fire_clay_fire_brick")
        );

    }

    @SubscribeEvent
    public static void onRegisterPitKilnRecipeEvent(RegistryEvent.Register<PitKilnRecipe> event)
    {
        IForgeRegistry<PitKilnRecipe> r = event.getRegistry();

        /* Pottery Items */

        for (Metal.ItemType type : Metal.ItemType.values())
        {
            if (type.hasMold(null))
            {
                // Fired molds
                r.register(new PitKilnRecipe(IIngredient.of(ItemMold.get(type))).setRegistryName(MOD_ID, "mold_" + type.name().toLowerCase() + "_fireable"));
                // Unfired molds
                r.register(new PitKilnRecipe(IIngredient.of(ItemUnfiredMold.get(type)), new ItemStack(ItemMold.get(type))).setRegistryName(MOD_ID, "mold_" + type.name().toLowerCase()));
            }
        }

        // Fired ceramic vessels
        r.register(new PitKilnRecipe(IIngredient.of(ItemsTFC.CERAMICS_FIRED_VESSEL)).setRegistryName(MOD_ID, "fired_vessel_fireable"));
        r.register(new PitKilnRecipe(IIngredient.of(ItemsTFC.CERAMICS_FIRED_VESSEL_GLAZED)).setRegistryName(MOD_ID, "fired_vessel_fireable_glazed"));

        // Unfired ceramic vessels
        r.register(new PitKilnRecipe(IIngredient.of(ItemsTFC.CERAMICS_UNFIRED_VESSEL), new ItemStack(ItemsTFC.CERAMICS_FIRED_VESSEL)).setRegistryName("fired_vessel"));
        for (EnumDyeColor color : EnumDyeColor.values())
        {
            r.register(new PitKilnRecipe(IIngredient.of(new ItemStack(ItemsTFC.CERAMICS_UNFIRED_VESSEL_GLAZED, color.getMetadata())), new ItemStack(ItemsTFC.CERAMICS_FIRED_VESSEL_GLAZED, color.getMetadata())).setRegistryName(MOD_ID, "fired_vessel_glazed_" + color.getName()));
        }
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
        addAnvil(r, DOUBLE_SHEET, UNFINISHED_BOOTS, true, BEND_LAST, BEND_SECOND_LAST, SHRINK_THIRD_LAST);

        r.register(new AnvilRecipeMeasurable(new ResourceLocation(MOD_ID, "refining_bloom"), IIngredient.of(ItemsTFC.UNREFINED_BLOOM), new ItemStack(ItemsTFC.REFINED_BLOOM), Metal.Tier.TIER_II, HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST));
        r.register(new AnvilRecipeSplitting(new ResourceLocation(MOD_ID, "splitting_bloom"), IIngredient.of(ItemsTFC.REFINED_BLOOM), 100, Metal.Tier.TIER_II, PUNCH_LAST));
        r.register(new AnvilRecipeMeasurable(new ResourceLocation(MOD_ID, "refine_bloom_ingot"), IIngredient.of(ItemsTFC.REFINED_BLOOM), new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON, INGOT)), 100, Metal.Tier.TIER_II, HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST));

        // Steel Working
        addAnvil(r, PIG_IRON, HIGH_CARBON_STEEL);
        addAnvil(r, HIGH_CARBON_STEEL, STEEL);
        addAnvil(r, HIGH_CARBON_BLACK_STEEL, BLACK_STEEL);
        addAnvil(r, HIGH_CARBON_BLUE_STEEL, BLUE_STEEL);
        addAnvil(r, HIGH_CARBON_RED_STEEL, RED_STEEL);
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
        addWelding(r, UNFINISHED_GREAVES, DOUBLE_SHEET, GREAVES, true);
        addWelding(r, UNFINISHED_BOOTS, SHEET, BOOTS, true);

        // Steel Welding
        addWelding(r, WEAK_STEEL, PIG_IRON, HIGH_CARBON_BLACK_STEEL);
        addWelding(r, WEAK_BLUE_STEEL, BLACK_STEEL, HIGH_CARBON_BLUE_STEEL);
        addWelding(r, WEAK_RED_STEEL, BLACK_STEEL, HIGH_CARBON_RED_STEEL);

        // Special Recipes

        // todo: shears
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
            ItemStack output = new ItemStack(ItemMetal.get(metal, outputType));
            if (!input1.isEmpty() && !input2.isEmpty() && !output.isEmpty())
            {
                // Note: Welding recipes require one less than the tier of the metal
                //noinspection ConstantConditions
                registry.register(new WeldingRecipe(new ResourceLocation(MOD_ID, (outputType.name() + "_" + metal.getRegistryName().getPath()).toLowerCase()), input1, input2, output, metal.getTier().previous()));
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
                registry.register(new WeldingRecipe(new ResourceLocation(MOD_ID, ("ingot_" + outputMetal.getRegistryName().getPath()).toLowerCase()), input1, input2, output, outputMetal.getTier().previous()));
            }
        }
    }
}
