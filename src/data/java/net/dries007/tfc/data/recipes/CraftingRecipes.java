package net.dries007.tfc.data.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.HideItemType;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.AdvancedShapedRecipe;
import net.dries007.tfc.common.recipes.AdvancedShapelessRecipe;
import net.dries007.tfc.common.recipes.CastingCraftingRecipe;
import net.dries007.tfc.common.recipes.FoodCombiningCraftingRecipe;
import net.dries007.tfc.common.recipes.ingredients.AndIngredient;
import net.dries007.tfc.common.recipes.ingredients.FluidContentIngredient;
import net.dries007.tfc.common.recipes.ingredients.LacksTraitIngredient;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.dries007.tfc.common.recipes.outputs.AddBaitToRodModifier;
import net.dries007.tfc.common.recipes.outputs.AddGlassModifier;
import net.dries007.tfc.common.recipes.outputs.AddPowderModifier;
import net.dries007.tfc.common.recipes.outputs.AddTraitModifier;
import net.dries007.tfc.common.recipes.outputs.CopyFoodModifier;
import net.dries007.tfc.common.recipes.outputs.CopyForgingBonusModifier;
import net.dries007.tfc.common.recipes.outputs.CopyInputModifier;
import net.dries007.tfc.common.recipes.outputs.CopyOldestFoodModifier;
import net.dries007.tfc.common.recipes.outputs.DamageCraftingRemainderModifier;
import net.dries007.tfc.common.recipes.outputs.ExtraProductModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.common.recipes.outputs.MealModifier;
import net.dries007.tfc.util.Metal;

public interface CraftingRecipes extends Recipes
{
    default void craftingRecipes()
    {
        // Removed Crafting Recipes
        remove(
            "anvil",
            "barrel",
            "beetroot_soup",
            "bone_meal",
            "bookshelf",
            "bow",
            "bricks",
            "bucket",
            "campfire",
            "chest",
            "chest_minecart",
            "coast_armor_trim_smithing_template",
            "composter",
            "crafting_table",
            "creeper_banner_pattern",
            "dune_armor_trim_smithing_template",
            "enchanting_table",
            "eye_armor_trim_smithing_template",
            "fishing_rod",
            "fletching_table",
            "flower_banner_pattern",
            "flower_pot",
            "furnace",
            "glass_bottle",
            "glass_pane",
            "host_armor_trim_smithing_template",
            "iron_door",
            "iron_trapdoor",
            "jack_o_lantern",
            "lantern",
            "leather_boots",
            "leather_chestplate",
            "leather_horse_armor",
            "leather_leggings",
            "lectern",
            "loom",
            "melon",
            "melon_seeds",
            "mojang_banner_pattern",
            "mushroom_stew",
            "pumpkin_pie",
            "rabbit_stew_from_brown_mushroom",
            "rabbit_stew_from_red_mushroom",
            "raiser_armor_trim_smithing_template",
            "rib_armor_trim_smithing_template",
            "sentry_armor_trim_smithing_template",
            "shaper_armor_trim_smithing_template",
            "silence_armor_trim_smithing_template",
            "skull_banner_pattern",
            "smoker",
            "snout_armor_trim_smithing_template",
            "soul_campfire",
            "soul_lantern",
            "soul_torch",
            "spire_armor_trim_smithing_template",
            "stone_axe",
            "stone_hoe",
            "stone_shovel",
            "stone_sword",
            "suspicious_stew",
            "tide_armor_trim_smithing_template",
            "tinted_glass",
            "torch",
            "trapped_chest",
            "turtle_helmet",
            "vex_armor_trim_smithing_template",
            "ward_armor_trim_smithing_template",
            "wayfinder_armor_trim_smithing_template",
            "wild_armor_trim_smithing_template",
            "wooden_axe",
            "wooden_hoe",
            "wooden_pickaxe",
            "wooden_shovel",
            "wooden_sword"
        );
        for (String material : List.of("diamond", "golden", "iron"))
            remove(
                material + "_axe",
                material + "_boots",
                material + "_chestplate",
                material + "_helmet",
                material + "_hoe",
                material + "_leggings",
                material + "_pickaxe",
                material + "_shovel",
                material + "_sword"
            );
        for (DyeColor color : DyeColor.values())
        {
            if (color != DyeColor.WHITE) remove(color.getSerializedName() + "_banner");
            remove(
                color.getSerializedName() + "_stained_glass",
                color.getSerializedName() + "_stained_glass_pane",
                color.getSerializedName() + "_stained_glass_pane_from_glass_pane"
            );
        }

        // Instance Recipes
        add("casting", CastingCraftingRecipe.INSTANCE);
        add("food_combining", FoodCombiningCraftingRecipe.INSTANCE);

        // ===== Crafting Recipes =====

        TFCItems.GEMS.forEach((gem, item) -> recipe()
            .damageInputs()
            .input(TFCItems.SANDPAPER)
            .input(TFCItems.ORES.get(gem))
            .shapeless(item));

        TFCBlocks.ALABASTER_BRICKS.forEach((color, block) -> addDecorations(block, TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color)));
        TFCBlocks.POLISHED_ALABASTER.forEach((color, block) -> addDecorations(block, TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color)));

        recipe()
            .damageInputs()
            .input(TFCTags.Items.TOOLS_CHISEL)
            .input(TFCItems.ORES.get(Ore.GYPSUM))
            .shapeless(TFCItems.ALABASTER_BRICK, 4);
        recipeBricksWithMortar(TFCItems.ALABASTER_BRICK, TFCBlocks.PLAIN_ALABASTER_BRICKS, 4);

        TFCItems.UNFIRED_GLAZED_VESSELS.forEach((color, item) -> recipe()
            .input(dyeOf(color))
            .input(TFCItems.UNFIRED_VESSEL)
            .shapeless(item));
        TFCItems.UNFIRED_GLAZED_LARGE_VESSELS.forEach((color, item) -> recipe()
            .input(dyeOf(color))
            .input(TFCItems.UNFIRED_LARGE_VESSEL)
            .shapeless(item));

        addGrains(Food.BARLEY, Food.BARLEY_GRAIN, Food.BARLEY_FLOUR, Food.BARLEY_DOUGH, Food.BARLEY_BREAD, Food.BARLEY_BREAD_SANDWICH, Food.BARLEY_BREAD_JAM_SANDWICH);
        addGrains(Food.MAIZE, Food.MAIZE_GRAIN, Food.MAIZE_FLOUR, Food.MAIZE_DOUGH, Food.MAIZE_BREAD, Food.MAIZE_BREAD_SANDWICH, Food.MAIZE_BREAD_JAM_SANDWICH);
        addGrains(Food.OAT, Food.OAT_GRAIN, Food.OAT_FLOUR, Food.OAT_DOUGH, Food.OAT_BREAD, Food.OAT_BREAD_SANDWICH, Food.OAT_BREAD_JAM_SANDWICH);
        addGrains(Food.RICE, Food.RICE_GRAIN, Food.RICE_FLOUR, Food.RICE_DOUGH, Food.RICE_BREAD, Food.RICE_BREAD_SANDWICH, Food.RICE_BREAD_JAM_SANDWICH);
        addGrains(Food.RYE, Food.RYE_GRAIN, Food.RYE_FLOUR, Food.RYE_DOUGH, Food.RYE_BREAD, Food.RYE_BREAD_SANDWICH, Food.RYE_BREAD_JAM_SANDWICH);
        addGrains(Food.WHEAT, Food.WHEAT_GRAIN, Food.WHEAT_FLOUR, Food.WHEAT_DOUGH, Food.WHEAT_BREAD, Food.WHEAT_BREAD_SANDWICH, Food.WHEAT_BREAD_JAM_SANDWICH);

        addTools(Metal.ItemType.AXE_HEAD, Metal.ItemType.AXE);
        addTools(Metal.ItemType.CHISEL_HEAD, Metal.ItemType.CHISEL);
        addTools(Metal.ItemType.HAMMER_HEAD, Metal.ItemType.HAMMER);
        addTools(Metal.ItemType.JAVELIN_HEAD, Metal.ItemType.JAVELIN);
        addTools(Metal.ItemType.HOE_HEAD, Metal.ItemType.HOE);
        addTools(Metal.ItemType.KNIFE_BLADE, Metal.ItemType.KNIFE);
        addTools(Metal.ItemType.MACE_HEAD, Metal.ItemType.MACE);
        addTools(Metal.ItemType.PICKAXE_HEAD, Metal.ItemType.PICKAXE);
        addTools(Metal.ItemType.PROPICK_HEAD, Metal.ItemType.PROPICK);
        addTools(Metal.ItemType.SAW_BLADE, Metal.ItemType.SAW);
        addTools(Metal.ItemType.SCYTHE_BLADE, Metal.ItemType.SCYTHE);
        addTools(Metal.ItemType.SHOVEL_HEAD, Metal.ItemType.SHOVEL);
        addTools(Metal.ItemType.SWORD_BLADE, Metal.ItemType.SWORD);

        for (Metal metal : Metal.values())
        {
            final var blocks = TFCBlocks.METALS.get(metal);
            final var items = TFCItems.METAL_ITEMS.get(metal);

            if (metal.defaultParts())
            {
                recipe()
                    .input('S', ingredientOf(metal, Metal.ItemType.SHEET))
                    .input('W', ItemTags.PLANKS)
                    .input('H', TFCTags.Items.TOOLS_HAMMER)
                    .pattern(" SH", "SWS", " S ")
                    .damageInputs()
                    .shaped(blocks.get(Metal.BlockType.BLOCK));
            }

            if (metal.allParts())
            {
                recipe()
                    .input('#', ingredientOf(metal, Metal.ItemType.DOUBLE_INGOT))
                    .pattern("###", " # ", "###")
                    .shaped(blocks.get(Metal.BlockType.ANVIL));
                recipe()
                    .input('S', Tags.Items.RODS_WOODEN)
                    .input('L', Tags.Items.STRINGS)
                    .input('X', items.get(Metal.ItemType.FISH_HOOK))
                    .pattern("  S", " SL", "SXL")
                    .copyForging()
                    .source(2, 1)
                    .shaped(items.get(Metal.ItemType.FISHING_ROD));
                recipe()
                    .input(items.get(Metal.ItemType.UNFINISHED_LAMP))
                    .input(TFCItems.LAMP_GLASS)
                    .shapeless(blocks.get(Metal.BlockType.LAMP));
                recipe()
                    .input('S', ingredientOf(metal, Metal.ItemType.DOUBLE_SHEET))
                    .input('H', Items.LEATHER_HORSE_ARMOR)
                    .input('J', TFCItems.JUTE_FIBER)
                    .pattern("JHJ", "SSS")
                    .shaped(items.get(Metal.ItemType.HORSE_ARMOR));
            }
        }

        for (Rock rock : Rock.values())
        {
            final var blocks = TFCBlocks.ROCK_BLOCKS.get(rock);
            final var brick = TFCItems.BRICKS.get(rock);

            recipe()
                .input('X', brick)
                .input('M', TFCItems.MORTAR)
                .pattern("X X", "MXM")
                .shaped(blocks.get(Rock.BlockType.AQUEDUCT));
            recipe()
                .input(Ingredient.of(
                    blocks.get(Rock.BlockType.LOOSE),
                    blocks.get(Rock.BlockType.MOSSY_LOOSE)))
                .input(TFCTags.Items.TOOLS_CHISEL)
                .damageInputs()
                .shapeless(brick);
            recipeBricksWithMortar(brick, blocks.get(Rock.BlockType.BRICKS), 4);
            recipeWithTool(TFCTags.Items.TOOLS_CHISEL, brick, blocks.get(Rock.BlockType.BUTTON));
            recipeWithTool(TFCTags.Items.TOOLS_CHISEL, blocks.get(Rock.BlockType.BRICKS), blocks.get(Rock.BlockType.CHISELED));
            recipe()
                .input(blocks.get(Rock.BlockType.COBBLE))
                .shapeless(blocks.get(Rock.BlockType.LOOSE), 4);
            recipeWithTool(TFCTags.Items.TOOLS_HAMMER, blocks.get(Rock.BlockType.BRICKS), blocks.get(Rock.BlockType.CRACKED_BRICKS));
            recipeBricksWithMortar(blocks.get(Rock.BlockType.RAW), blocks.get(Rock.BlockType.HARDENED), 2);
            recipe2x2(blocks.get(Rock.BlockType.LOOSE), blocks.get(Rock.BlockType.COBBLE), 1);
            recipe()
                .input(blocks.get(Rock.BlockType.MOSSY_COBBLE))
                .shapeless(blocks.get(Rock.BlockType.MOSSY_LOOSE), 4);
            recipe2x2(blocks.get(Rock.BlockType.MOSSY_LOOSE), blocks.get(Rock.BlockType.MOSSY_COBBLE), 1);
            recipe()
                .input('C', TFCTags.Items.TOOLS_CHISEL)
                .input('X', TFCItems.BRICKS.get(rock))
                .pattern(" C", "XX")
                .damageInputs()
                .shaped(blocks.get(Rock.BlockType.PRESSURE_PLATE));
            recipeWithTool(TFCTags.Items.TOOLS_CHISEL, blocks.get(Rock.BlockType.RAW), blocks.get(Rock.BlockType.SMOOTH));

            TFCBlocks.ROCK_DECORATIONS.get(rock).forEach((type, decorations) -> addDecorations(blocks.get(type), decorations));
        }

        TFCBlocks.SANDSTONE.forEach((color, blocks) -> {
            recipeWithTool(TFCTags.Items.TOOLS_CHISEL, blocks.get(SandstoneBlockType.RAW), blocks.get(SandstoneBlockType.SMOOTH));
            recipeWithTool(TFCTags.Items.TOOLS_CHISEL, blocks.get(SandstoneBlockType.SMOOTH), blocks.get(SandstoneBlockType.CUT));

            TFCBlocks.SANDSTONE_DECORATIONS.get(color).forEach((type, decorations) -> addDecorations(blocks.get(type), decorations));
        });

        for (SoilBlockType.Variant soil : SoilBlockType.Variant.values())
        {
            final Function<SoilBlockType, ItemLike> blocks = key -> TFCBlocks.SOIL.get(key).get(soil);
            recipe()
                .input(blocks.apply(SoilBlockType.MUD))
                .input(TFCItems.STRAW)
                .shapeless(blocks.apply(SoilBlockType.DRYING_BRICKS));
            recipe2x2(soil.mudBrick(), blocks.apply(SoilBlockType.MUD_BRICKS), 1);
            addDecorations(blocks.apply(SoilBlockType.MUD_BRICKS), TFCBlocks.MUD_BRICK_DECORATIONS.get(soil));
            recipe()
                .input(blocks.apply(SoilBlockType.MUD))
                .input(TFCBlocks.TREE_ROOTS)
                .shapeless(blocks.apply(SoilBlockType.MUDDY_ROOTS));

            for (int n = 1; n <= 8; n++)
                recipe("" + n)
                    .input(FluidContentIngredient.of(Fluids.WATER, 100))
                    .input(blocks.apply(SoilBlockType.DIRT), n)
                    .shapeless(blocks.apply(SoilBlockType.MUD), n);
        }

        addTools(RockCategory.ItemType.AXE_HEAD, RockCategory.ItemType.AXE);
        addTools(RockCategory.ItemType.HAMMER_HEAD, RockCategory.ItemType.HAMMER);
        addTools(RockCategory.ItemType.HOE_HEAD, RockCategory.ItemType.HOE);
        addTools(RockCategory.ItemType.JAVELIN_HEAD, RockCategory.ItemType.JAVELIN);
        addTools(RockCategory.ItemType.KNIFE_HEAD, RockCategory.ItemType.KNIFE);
        addTools(RockCategory.ItemType.SHOVEL_HEAD, RockCategory.ItemType.SHOVEL);

        for (Wood wood : Wood.values())
        {
            final var blocks = TFCBlocks.WOODS.get(wood);
            final var lumber = TFCItems.LUMBER.get(wood);
            final var planks = blocks.get(Wood.BlockType.PLANKS);

            recipe()
                .input('W', blocks.get(Wood.BlockType.STRIPPED_LOG))
                .input('G', TFCItems.GLUE)
                .pattern("WGW")
                .shaped(blocks.get(Wood.BlockType.AXLE), 4);
            recipe()
                .input('L', lumber)
                .pattern("L L", "L L", "LLL")
                .shaped(blocks.get(Wood.BlockType.BARREL));
            recipe()
                .input(blocks.get(Wood.BlockType.AXLE))
                .input(ingredientOf(Metal.STEEL, Metal.ItemType.INGOT))
                .shapeless(blocks.get(Wood.BlockType.BLADED_AXLE));
            recipe()
                .input('P', planks)
                .pattern("P P", "PPP")
                .shaped(TFCItems.BOATS.get(wood));
            recipe()
                .input('L', lumber)
                .input('S', Tags.Items.RODS_WOODEN)
                .pattern("LLL", "SSS", "LLL")
                .shaped(blocks.get(Wood.BlockType.BOOKSHELF));
            recipe()
                .input(planks)
                .shapeless(blocks.get(Wood.BlockType.BUTTON));
            recipe()
                .input('L', lumber)
                .pattern("LLL", "L L", "LLL")
                .shaped(blocks.get(Wood.BlockType.CHEST));
            recipe()
                .input(blocks.get(Wood.BlockType.CHEST))
                .input(Items.MINECART)
                .shapeless(TFCItems.CHEST_MINECARTS.get(wood));
            recipe()
                .input('L', lumber)
                .input('S', blocks.get(Wood.BlockType.STRIPPED_LOG))
                .input('M', TFCItems.BRASS_MECHANISMS)
                .input('A', blocks.get(Wood.BlockType.AXLE))
                .input('R', Tags.Items.DUSTS_REDSTONE)
                .pattern("LSL", "MAR", "LSL")
                .shaped(blocks.get(Wood.BlockType.CLUTCH), 2);
            recipe()
                .input('L', lumber)
                .pattern("LL", "LL", "LL")
                .shaped(blocks.get(Wood.BlockType.DOOR), 2);
            recipe()
                .input('L', lumber)
                .input('S', blocks.get(Wood.BlockType.STRIPPED_LOG))
                .input('A', blocks.get(Wood.BlockType.AXLE))
                .pattern(" S ", "LAL", " S ")
                .shaped(blocks.get(Wood.BlockType.ENCASED_AXLE), 4);
            recipe()
                .input('P', planks)
                .input('L', lumber)
                .pattern("PLP", "PLP")
                .shaped(blocks.get(Wood.BlockType.FENCE), 8);
            recipe()
                .input('P', planks)
                .input('L', lumber)
                .pattern("LPL", "LPL")
                .shaped(blocks.get(Wood.BlockType.FENCE_GATE), 2);
            recipe()
                .input('L', lumber)
                .input('M', TFCItems.BRASS_MECHANISMS)
                .pattern(" L ", "LML", " L ")
                .shaped(blocks.get(Wood.BlockType.GEAR_BOX), 2);
            recipe()
                .input('L', lumber)
                .input('B', blocks.get(Wood.BlockType.BOOKSHELF))
                .pattern("LLL", " B ", " L ")
                .shaped(blocks.get(Wood.BlockType.LECTERN));
            recipe()
                .input('P', blocks.get(Wood.BlockType.LOG))
                .input('L', lumber)
                .pattern("PLP", "PLP")
                .shaped(blocks.get(Wood.BlockType.LOG_FENCE), 8);
            recipe()
                .input('L', lumber)
                .input('S', Tags.Items.RODS_WOODEN)
                .pattern("LLL", "LSL", "L L")
                .shaped(blocks.get(Wood.BlockType.LOOM));
            recipe("from_logs")
                .input(TFCTags.Items.TOOLS_SAW)
                .input(logsTagOf(wood))
                .damageInputs()
                .shapeless(lumber, 8);
            recipe("from_planks")
                .input(TFCTags.Items.TOOLS_SAW)
                .input(planks)
                .damageInputs()
                .shapeless(lumber, 4);
            recipe2x2(lumber, planks, 1);
            recipe()
                .input('L', lumber)
                .pattern("LL")
                .shaped(blocks.get(Wood.BlockType.PRESSURE_PLATE));
            recipe()
                .input('F', Tags.Items.FEATHERS)
                .input('D', Tags.Items.DYES_BLACK)
                .input('S', blocks.get(Wood.BlockType.SLAB))
                .input('W', planks)
                .pattern("F D", "SSS", "W W")
                .shaped(blocks.get(Wood.BlockType.SCRIBING_TABLE));
            recipe()
                .input('S', Tags.Items.TOOLS_SHEAR)
                .input('L', Tags.Items.LEATHERS)
                .input('P', planks)
                .input('G', blocks.get(Wood.BlockType.LOG))
                .pattern(" LS", "PPP", "G G")
                .shaped(blocks.get(Wood.BlockType.SEWING_TABLE));
            recipe()
                .input('L', lumber)
                .input('P', planks)
                .input('S', Tags.Items.RODS_WOODEN)
                .pattern("PPP", "L L", "S S")
                .shaped(blocks.get(Wood.BlockType.JAR_SHELF), 2);
            recipe()
                .input('L', lumber)
                .input('S', Tags.Items.RODS_WOODEN)
                .pattern("LLL", "LLL", " S ")
                .shaped(blocks.get(Wood.BlockType.SIGN), 3);
            recipe()
                .input('#', planks)
                .pattern("###")
                .shaped(blocks.get(Wood.BlockType.SLAB), 6);
            recipe()
                .input('#', planks)
                .pattern("#  ", "## ", "###")
                .shaped(blocks.get(Wood.BlockType.STAIRS), 8);
            recipe()
                .input('L', lumber)
                .input('S', Tags.Items.RODS_WOODEN)
                .pattern("  S", " SL", "SLL")
                .shaped(blocks.get(Wood.BlockType.SLUICE));
            recipe()
                .input('L', logsTagOf(wood))
                .input('S', TFCTags.Items.TOOLS_SAW)
                .pattern("LS", "L ")
                .shaped(TFCItems.SUPPORTS.get(wood), 8);
            recipe()
                .input('L', lumber)
                .pattern("LLL", "   ", "LLL")
                .shaped(blocks.get(Wood.BlockType.TOOL_RACK));
            recipe()
                .input('L', lumber)
                .pattern("LLL", "LLL")
                .shaped(blocks.get(Wood.BlockType.TRAPDOOR));
            recipe()
                .input(blocks.get(Wood.BlockType.CHEST))
                .input(Items.TRIPWIRE_HOOK)
                .shapeless(blocks.get(Wood.BlockType.TRAPPED_CHEST));
            recipe()
                .input('L', lumber)
                .input('P', planks)
                .input('A', blocks.get(Wood.BlockType.AXLE))
                .pattern("LPL", "PAP", "LPL")
                .shaped(blocks.get(Wood.BlockType.WATER_WHEEL));
            recipe2x2(blocks.get(Wood.BlockType.LOG), blocks.get(Wood.BlockType.WOOD), 3);
            recipe2x2(planks, blocks.get(Wood.BlockType.WORKBENCH), 1);
        }

        for (DyeColor color : DyeColor.values())
        {
            replace(color.getSerializedName() + "_bed")
                .input('D', dyeOf(color))
                .input('H', TFCTags.Items.HIGH_QUALITY_CLOTH)
                .input('L', TFCTags.Items.LUMBER)
                .pattern("DDD", "HHH", "LLL")
                .shaped(dyedOf(color, "bed"));
            replace(color.getSerializedName() + "_concrete_powder")
                .input(Tags.Items.GRAVELS, 4)
                .input(Tags.Items.SANDS, 4)
                .input(dyeOf(color))
                .shapeless(dyedOf(color, "concrete_powder"));
        }

        for (HideItemType.Size size : HideItemType.Size.values())
        {
            final Function<HideItemType, ItemLike> hides = type -> TFCItems.HIDES.get(type).get(size);

            recipe()
                .input(hides.apply(HideItemType.SHEEPSKIN))
                .input(TFCTags.Items.TOOLS_KNIFE)
                .damageInputs()
                .extraProduct(TFCItems.WOOL, 1 + size.ordinal())
                .shapeless(hides.apply(HideItemType.RAW));
            recipe("from_" + size.name().toLowerCase(Locale.ROOT))
                .input(hides.apply(HideItemType.SCRAPED))
                .input(TFCTags.Items.TOOLS_HAMMER)
                .input(TFCBlocks.GROUNDCOVER.get(GroundcoverBlockType.PUMICE))
                .damageInputs()
                .shapeless(TFCItems.TREATED_HIDE, 1 + size.ordinal());
        }

        TFCItems.FRUIT_PRESERVES.forEach((food, item) ->
            recipe()
                .input(notRotten(Ingredient.of(item)))
                .shapeless(TFCItems.UNSEALED_FRUIT_PRESERVES.get(food)));

        replace("activator_rail")
            .input('S', Tags.Items.RODS_WOODEN)
            .input('I', ingredientOf(Metal.WROUGHT_IRON, Metal.ItemType.ROD))
            .input('T', Items.REDSTONE_TORCH)
            .pattern("ISI", "ITI", "ISI")
            .shaped(Items.ACTIVATOR_RAIL, 4);
        recipe()
            .input('S', Tags.Items.RODS_WOODEN)
            .input('I', ingredientOf(Metal.STEEL, Metal.ItemType.ROD))
            .input('T', Items.REDSTONE_TORCH)
            .pattern("ISI", "ITI", "ISI")
            .shaped(Items.ACTIVATOR_RAIL, 8);
        replace("comparator")
            .input('S', TFCTags.Items.STONES_SMOOTH)
            .input('R', Tags.Items.DUSTS_REDSTONE)
            .input('T', Items.REDSTONE_TORCH)
            .pattern(" T ", "TRT", "SSS")
            .shaped(Items.COMPARATOR);
        replace("daylight_detector")
            .input('L', TFCItems.LENS)
            .input('R', Tags.Items.DUSTS_REDSTONE)
            .input('P', TFCTags.Items.LUMBER)
            .pattern("L", "R", "P")
            .shaped(Items.DAYLIGHT_DETECTOR);
        replace("detector_rail")
            .input('P', TFCTags.Items.STONES_PRESSURE_PLATES)
            .input('I', ingredientOf(Metal.WROUGHT_IRON, Metal.ItemType.ROD))
            .input('R', Tags.Items.DUSTS_REDSTONE)
            .pattern("I I", "IPI", "IRI")
            .shaped(Items.DETECTOR_RAIL, 4);
        recipe()
            .input('P', TFCTags.Items.STONES_PRESSURE_PLATES)
            .input('I', ingredientOf(Metal.STEEL, Metal.ItemType.ROD))
            .input('R', Tags.Items.DUSTS_REDSTONE)
            .pattern("I I", "IPI", "IRI")
            .shaped(Items.DETECTOR_RAIL, 8);
        replace("heavy_weighted_pressure_plate")
            .input('I', ingredientOf(Metal.WROUGHT_IRON, Metal.ItemType.INGOT))
            .pattern("II")
            .shaped(Items.HEAVY_WEIGHTED_PRESSURE_PLATE);
        replace("hopper")
            .input('C', Tags.Items.CHESTS_WOODEN)
            .input('I', ingredientOf(Metal.WROUGHT_IRON, Metal.ItemType.SHEET))
            .pattern("I I", " C ")
            .shaped(Items.HOPPER);
        recipe()
            .input('C', Tags.Items.CHESTS_WOODEN)
            .input('I', ingredientOf(Metal.STEEL, Metal.ItemType.SHEET))
            .pattern("I I", " C ")
            .shaped(Items.HOPPER, 2);
        replace("minecart")
            .input('X', ingredientOf(Metal.WROUGHT_IRON, Metal.ItemType.SHEET))
            .pattern("X X", "XXX")
            .shaped(Items.MINECART, 2);
        recipe()
            .input('X', ingredientOf(Metal.STEEL, Metal.ItemType.SHEET))
            .pattern("X X", "XXX")
            .shaped(Items.MINECART, 4);
        replace("observer")
            .input('C', Tags.Items.COBBLESTONES_NORMAL)
            .input('R', Tags.Items.DUSTS_REDSTONE)
            .input('M', TFCItems.BRASS_MECHANISMS)
            .pattern("CCC", "RRM", "CCC")
            .shaped(Items.OBSERVER);
        replace("piston")
            .input('L', TFCTags.Items.LUMBER)
            .input('C', Tags.Items.COBBLESTONES_NORMAL)
            .input('R', Tags.Items.DUSTS_REDSTONE)
            .input('M', TFCItems.BRASS_MECHANISMS)
            .pattern("LLL", "CMC", "CRC")
            .shaped(Items.PISTON);
        replace("powered_rail")
            .input('R', ingredientOf(Metal.GOLD, Metal.ItemType.ROD))
            .input('S', Tags.Items.RODS_WOODEN)
            .input('D', Tags.Items.DUSTS_REDSTONE)
            .pattern("R R", "RSR", "RDR")
            .shaped(Items.POWERED_RAIL, 16);
        replace("rail")
            .input('R', ingredientOf(Metal.WROUGHT_IRON, Metal.ItemType.ROD))
            .input('S', Tags.Items.RODS_WOODEN)
            .pattern("R R", "RSR", "R R")
            .shaped(Items.RAIL, 32);
        recipe()
            .input('R', ingredientOf(Metal.STEEL, Metal.ItemType.ROD))
            .input('S', Tags.Items.RODS_WOODEN)
            .pattern("R R", "RSR", "R R")
            .shaped(Items.RAIL, 64);
        replace("repeater")
            .input('S', TFCTags.Items.STONES_SMOOTH)
            .input('R', Tags.Items.DUSTS_REDSTONE)
            .input('T', Items.REDSTONE_TORCH)
            .pattern("TRT", "SSS")
            .shaped(Items.REPEATER);
        replace("sticky_piston")
            .input('G', TFCItems.GLUE)
            .input('P', Items.PISTON)
            .pattern("G", "P")
            .shaped(Items.STICKY_PISTON);
        replace("tripwire_hook")
            .input('S', ingredientOf(Metal.WROUGHT_IRON, Metal.ItemType.SHEET))
            .input('L', TFCTags.Items.LUMBER)
            .input('R', Tags.Items.RODS_WOODEN)
            .pattern("S", "L", "R")
            .shaped(Items.TRIPWIRE_HOOK);


        replace("armor_stand")
            .input('R', Tags.Items.RODS_WOODEN)
            .input('S', TFCTags.Items.STONES_SMOOTH_SLABS)
            .pattern("R R", " R ", "RSR")
            .shaped(Items.ARMOR_STAND);
        recipe()
            .input(Ingredient.of(TFCItems.POWDERS.get(Powder.COKE), TFCItems.POWDERS.get(Powder.CHARCOAL)))
            .shapeless(Items.BLACK_DYE);
        recipe()
            .input(Ingredient.of(TFCItems.ORE_POWDERS.get(Ore.GRAPHITE), TFCItems.ORE_POWDERS.get(Ore.LAPIS_LAZULI)))
            .shapeless(Items.BLUE_DYE);
        replace("bowl")
            .input('L', TFCTags.Items.LUMBER)
            .input('G', TFCItems.GLUE)
            .pattern("LGL", " L ")
            .shaped(Items.BOWL);
        recipe()
            .input(Ingredient.of(TFCItems.ORE_POWDERS.get(Ore.GARNIERITE)))
            .shapeless(Items.BROWN_DYE);
        replace("clock")
            .input('G', ingredientOf(Metal.GOLD, Metal.ItemType.SHEET))
            .input('R', Tags.Items.DUSTS_REDSTONE)
            .input('M', TFCItems.BRASS_MECHANISMS)
            .pattern("GRG", "RMR", "GRG")
            .shaped(Items.CLOCK);
        replace("compass")
            .input('L', TFCItems.LENS)
            .input('R', Ingredient.of(
                TFCBlocks.SMALL_ORES.get(Ore.MAGNETITE),
                TFCItems.GRADED_ORES.get(Ore.MAGNETITE).get(Ore.Grade.POOR),
                TFCItems.GRADED_ORES.get(Ore.MAGNETITE).get(Ore.Grade.NORMAL),
                TFCItems.GRADED_ORES.get(Ore.MAGNETITE).get(Ore.Grade.RICH)
            ))
            .input('B', TFCTags.Items.BOWLS)
            .pattern("L", "R", "B")
            .shaped(Items.COMPASS);
        replace("crossbow")
            .input('L', TFCTags.Items.LUMBER)
            .input('R', ingredientOf(Metal.WROUGHT_IRON, Metal.ItemType.ROD))
            .input('S', Tags.Items.STRINGS)
            .input('T', Items.TRIPWIRE_HOOK)
            .pattern("LRL", "STS", " L ")
            .shaped(Items.CROSSBOW);
        recipe()
            .input(TFCTags.Items.ROCK_KNAPPING)
            .input(TFCItems.BLANK_DISC)
            .shapeless(Items.MUSIC_DISC_11);
        replace("fire_charge")
            .input(Items.GUNPOWDER)
            .input(ItemTags.COALS)
            .input(TFCItems.FIRESTARTER)
            .shapeless(Items.FIRE_CHARGE);
        replace("flint_and_steel")
            .input(ingredientOf(Metal.STEEL, Metal.ItemType.ROD))
            .input(Items.FLINT)
            .shapeless(Items.FLINT_AND_STEEL);
        recipe()
            .input(Ingredient.of(
                TFCItems.ORE_POWDERS.get(Ore.CASSITERITE),
                TFCItems.ORE_POWDERS.get(Ore.MAGNETITE),
                TFCItems.ORE_POWDERS.get(Ore.SPHALERITE),
                TFCItems.ORE_POWDERS.get(Ore.TETRAHEDRITE)
            ))
            .shapeless(Items.GRAY_DYE);
        recipe()
            .input(Ingredient.of(TFCItems.ORE_POWDERS.get(Ore.MALACHITE), TFCItems.ORE_POWDERS.get(Ore.BISMUTHINITE)))
            .shapeless(Items.GREEN_DYE);
        replace3x3BothWays("hay_block", "wheat", TFCItems.STRAW, Items.HAY_BLOCK);
        replace("item_frame")
            .input('S', TFCTags.Items.LUMBER)
            .input('L', Tags.Items.LEATHERS)
            .pattern("SSS", "SLS", "SSS")
            .shaped(Items.ITEM_FRAME, 4);
        replace("ladder")
            .input('L', TFCTags.Items.LUMBER)
            .pattern("L L", "L L", "L L")
            .shaped(Items.LADDER, 16);
        replace3x3BothWays(
            "lapis_block", "lapis_lazuli",
            Ingredient.of(Tags.Items.GEMS_LAPIS), TFCItems.ORES.get(Ore.LAPIS_LAZULI),
            Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS), Items.LAPIS_BLOCK);
        recipe()
            .input(TFCItems.ORE_POWDERS.get(Ore.NATIVE_SILVER))
            .shapeless(Items.LIGHT_GRAY_DYE);
        replace("lightning_rod")
            .input('X', ingredientOf(Metal.COPPER, Metal.ItemType.ROD))
            .pattern("X", "X", "X")
            .shaped(Items.LIGHTNING_ROD);
        replace("map")
            .input('P', Items.PAPER)
            .input('L', Tags.Items.LEATHERS)
            .pattern("PPP", "PLP", "PPP")
            .shaped(Items.MAP);
        recipe()
            .input(Items.PAPER)
            .input(Tags.Items.STRINGS)
            .shapeless(Items.NAME_TAG);
        recipe()
            .input(Ingredient.of(TFCItems.ORE_POWDERS.get(Ore.NATIVE_COPPER), TFCItems.ORE_POWDERS.get(Ore.SYLVITE)))
            .shapeless(Items.ORANGE_DYE);
        replace("painting")
            .input('S', Tags.Items.RODS_WOODEN)
            .input('H', TFCTags.Items.HIGH_QUALITY_CLOTH)
            .pattern("SSS", "SHS", "SSS")
            .shaped(Items.PAINTING);
        recipe()
            .input(TFCItems.POWDERS.get(Powder.KAOLINITE))
            .shapeless(Items.PINK_DYE);
        recipe()
            .input(TFCItems.ORE_POWDERS.get(Ore.HEMATITE))
            .shapeless(Items.RED_DYE);
        replace("shield")
            .input('L', TFCTags.Items.LUMBER)
            .input('G', TFCItems.GLUE)
            .pattern("LGL", "LLL", " L ")
            .shaped(Items.SHIELD);
        replace("spyglass")
            .input('L', TFCItems.LENS)
            .input('C', ingredientOf(Metal.COPPER, Metal.ItemType.SHEET))
            .input('M', TFCItems.BRASS_MECHANISMS)
            .pattern("L", "C", "M")
            .shaped(Items.SPYGLASS);
        replace("white_banner")
            .input('H', TFCTags.Items.HIGH_QUALITY_CLOTH)
            .input('S', Tags.Items.RODS_WOODEN)
            .pattern("H", "H", "S")
            .shaped(Items.WHITE_BANNER);
        recipe()
            .input(TFCItems.ORE_POWDERS.get(Ore.LIMONITE))
            .shapeless(Items.YELLOW_DYE);

        // todo: remaining recipes
        // todo: re-evaluate cauldron - do we need it? should we add it?
        // todo: re-evaluate crafting recipes for vanilla devices (i.e. crafting table, loom, smithing table) Which do we need?
        // todo: auto-generate clay knapping remainder / uncrafting recipes from inputs
        // todo: are uncrafing (i.e. slab, stair) recipes really wanted? or make sense?
        // todo: no crafting recipe for flux? (only quern)
        // todo: lingering water bottles, what?

        recipe()
            .inputIsPrimary(TFCTags.Items.GLASS_BATCHES)
            .input(TFCItems.CERAMIC_BLOWPIPE)
            .addGlass()
            .shapeless(TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS);
        recipe()
            .inputIsPrimary(TFCTags.Items.GLASS_BATCHES)
            .input(TFCItems.BLOWPIPE)
            .addGlass()
            .shapeless(TFCItems.BLOWPIPE_WITH_GLASS);
        recipe()
            .input(TFCItems.BLOWPIPE_WITH_GLASS)
            .shapeless(TFCItems.BLOWPIPE);
        recipe()
            .input(TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS)
            .shapeless(TFCItems.CERAMIC_BLOWPIPE);
        recipe()
            .inputIsPrimary(TFCTags.Items.GLASS_BLOWPIPES)
            .input(TFCTags.Items.GLASS_POWDERS)
            .copyInput()
            .addPowder()
            .shapeless("add_powder");
        recipe()
            .input(TFCTags.Items.GLASS_POTASH)
            .input(TFCItems.POWDERS.get(Powder.LIME))
            .input(TFCTags.Items.HEMATITIC_SAND)
            .shapeless(TFCItems.HEMATITIC_GLASS_BATCH, 4);
        recipe()
            .input(TFCTags.Items.GLASS_POTASH)
            .input(TFCItems.POWDERS.get(Powder.LIME))
            .input(TFCTags.Items.OLIVINE_SAND)
            .shapeless(TFCItems.OLIVINE_GLASS_BATCH, 4);
        recipe()
            .input(TFCTags.Items.GLASS_POTASH)
            .input(TFCItems.POWDERS.get(Powder.LIME))
            .input(TFCTags.Items.SILICA_SAND)
            .shapeless(TFCItems.SILICA_GLASS_BATCH, 4);
        recipe()
            .input(TFCTags.Items.GLASS_POTASH)
            .input(TFCItems.POWDERS.get(Powder.LIME))
            .input(TFCTags.Items.VOLCANIC_SAND)
            .shapeless(TFCItems.VOLCANIC_GLASS_BATCH, 4);

        recipe()
            .inputIsPrimary(TFCTags.Items.HOLDS_LARGE_FISHING_BAIT)
            .input(TFCTags.Items.LARGE_FISHING_BAIT)
            .copyInput()
            .addBait()
            .shapeless("add_large_bait");
        recipe()
            .inputIsPrimary(TFCTags.Items.HOLDS_SMALL_FISHING_BAIT)
            .input(TFCTags.Items.SMALL_FISHING_BAIT)
            .copyInput()
            .addBait()
            .shapeless("add_small_bait");
        recipe()
            .input(Tags.Items.SANDS, 4)
            .input(Tags.Items.GRAVELS, 4)
            .shapeless(TFCBlocks.AGGREGATE);
        recipe()
            .input('S', Items.SCAFFOLDING)
            .input('L', TFCTags.Items.LUMBER)
            .pattern("L L", " S ", "L L")
            .shaped(TFCBlocks.BARREL_RACK);
        recipe()
            .input('L', TFCTags.Items.LUMBER)
            .input('R', Tags.Items.LEATHERS)
            .pattern("LLL", "RRR", "LLL")
            .shaped(TFCBlocks.BELLOWS);
        recipe()
            .input(TFCItems.SOOT)
            .input(Items.GLASS_PANE)
            .shapeless(TFCItems.BLANK_DISC);
        recipe()
            .input('S', ingredientOf(Metal.WROUGHT_IRON, Metal.ItemType.SHEET))
            .input('C', TFCBlocks.CRUCIBLE)
            .pattern("SSS", "SCS", "SSS")
            .shaped(TFCBlocks.BLAST_FURNACE);
        recipe()
            .input('B', TFCTags.Items.DOUBLE_SHEETS_ANY_BRONZE)
            .pattern("BBB", "B B", "BBB")
            .shaped(TFCBlocks.BLOOMERY);
        recipeWithTool(TFCTags.Items.TOOLS_KNIFE, Items.BONE, TFCItems.BONE_NEEDLE);
        recipeBricksWithMortar(Items.BRICK, Items.BRICKS, 4);
        replace("cake")
            .input('M', FluidContentIngredient.of(Fluids.WATER, 100))
            .input('S', TFCTags.Items.SWEETENERS)
            .input('E', notRotten(Ingredient.of(Items.EGG)))
            .input('F', TFCTags.Items.FLOUR)
            .pattern(" M ", "SES", "FFF")
            .shaped(Items.CAKE);
        recipe()
            .input('L', TFCTags.Items.LUMBER)
            .input('D', ItemTags.DIRT)
            .pattern("L L", "LDL", "LLL")
            .shaped(TFCBlocks.COMPOSTER);
        recipe()
            .input('A', TFCTags.Items.AXLES)
            .input('M', TFCItems.BRASS_MECHANISMS)
            .input('S', ingredientOf(Metal.BRASS, Metal.ItemType.SHEET))
            .pattern("AM", " S")
            .shaped(TFCBlocks.CRANKSHAFT);
        recipe()
            .input(TFCItems.STRAW)
            .input(ItemTags.DIRT)
            .input(Items.CLAY_BALL)
            .shapeless(TFCItems.DAUB, 2);
        recipe("from_mud")
            .input(TFCTags.Items.MUD)
            .input(Items.CLAY_BALL)
            .shapeless(TFCItems.DAUB, 2);
        recipeBricksWithMortar(TFCItems.FIRE_BRICK, TFCBlocks.FIRE_BRICKS, 2);
        recipe()
            .input(TFCItems.POWDERS.get(Powder.KAOLINITE), 4)
            .input(TFCItems.ORE_POWDERS.get(Ore.GRAPHITE), 4)
            .input(Items.CLAY_BALL)
            .shapeless(TFCItems.FIRE_CLAY);
        recipe2x2(TFCItems.FIRE_CLAY, TFCBlocks.FIRE_CLAY_BLOCK, 1);
        recipe()
            .input('S', Tags.Items.RODS_WOODEN)
            .pattern(" S", "S ")
            .shaped(TFCItems.FIRESTARTER);
        recipe()
            .input(ingredientOf(Metal.BRASS, Metal.ItemType.ROD))
            .input(TFCTags.Items.GEM_POWDERS)
            .shapeless(TFCItems.GEM_SAW);
        recipe()
            .input('G', Items.GLOW_INK_SAC)
            .input('A', Items.ARROW)
            .pattern("AAA", "AGA", "AAA")
            .shaped(TFCItems.GLOW_ARROW, 8);
        replace3x3BothWays(
            "slime_block", "slime_ball",
            Ingredient.of(TFCItems.GLUE, Items.SLIME_BALL), TFCItems.GLUE,
            Ingredient.of(Tags.Items.STORAGE_BLOCKS_SLIME), Items.SLIME_BLOCK);
        recipe()
            .input(TFCItems.ORE_POWDERS.get(Ore.SALTPETER), 2)
            .input(TFCItems.ORE_POWDERS.get(Ore.SULFUR))
            .input(TFCItems.POWDERS.get(Powder.CHARCOAL))
            .shapeless(Items.GUNPOWDER, 4);
        recipe("with_graphite")
            .input(TFCItems.ORE_POWDERS.get(Ore.SALTPETER), 4)
            .input(TFCItems.ORE_POWDERS.get(Ore.SULFUR), 2)
            .input(TFCItems.POWDERS.get(Powder.CHARCOAL), 2)
            .input(TFCItems.ORE_POWDERS.get(Ore.GRAPHITE))
            .shapeless(Items.GUNPOWDER, 12);
        recipe()
            .input('S', Tags.Items.RODS_WOODEN)
            .input('B', Tags.Items.STONES)
            .pattern("S  ", "BBB")
            .shaped(TFCItems.HANDSTONE);
        recipe()
            .input(TFCItems.EMPTY_JAR)
            .input(TFCItems.JAR_LID)
            .shapeless(TFCItems.EMPTY_JAR_WITH_LID);
        recipe()
            .input('X', TFCItems.JUTE_FIBER)
            .pattern("X X", " X ", "X X")
            .shaped(TFCItems.JUTE_NET);
        replace("lead")
            .input('X', TFCItems.JUTE_FIBER)
            .pattern(" XX", " XX", "X  ")
            .shaped(Items.LEAD);
        recipe()
            .input(notRotten(Ingredient.of(TFCBlocks.MELON)))
            .input(TFCTags.Items.TOOLS_KNIFE)
            .shapeless(TFCItems.FOOD.get(Food.MELON_SLICE), 4);
        recipe()
            .input('L', TFCTags.Items.LUMBER)
            .input('S', TFCItems.STRAW)
            .pattern("S S", "LSL", "LLL")
            .shaped(TFCBlocks.NEST_BOX);
        recipe()
            .input('L', TFCTags.Items.LUMBER)
            .input('S', Tags.Items.RODS_WOODEN)
            .pattern("LL", "LL", "S ")
            .shaped(TFCItems.PADDLE);
        recipe()
            .input(TFCItems.PAPYRUS)
            .input(TFCTags.Items.TOOLS_KNIFE)
            .damageInputs()
            .shapeless(TFCItems.PAPYRUS_STRIP, 4);
        recipe()
            .input(TFCItems.TREATED_HIDE)
            .input(notRotten(Ingredient.of(Items.EGG)))
            .input(notRotten(Ingredient.of(TFCTags.Items.FLOUR)))
            .input(TFCItems.POWDERS.get(Powder.LIME))
            .shapeless(Items.PAPER, 2);
        recipe()
            .input(TFCBlocks.WHITE_KAOLIN_CLAY)
            .input(TFCItems.ORE_POWDERS.get(Ore.HEMATITE))
            .shapeless(TFCBlocks.PINK_KAOLIN_CLAY);
        recipe()
            .input(TFCBlocks.PINK_KAOLIN_CLAY)
            .input(TFCItems.ORE_POWDERS.get(Ore.HEMATITE))
            .shapeless(TFCBlocks.RED_KAOLIN_CLAY);
        recipe()
            .input('L', TFCTags.Items.LUMBER)
            .input('R', Tags.Items.DYES_RED)
            .input('S', Tags.Items.STRINGS)
            .pattern("LSL", "LRL", "LLL")
            .shaped(TFCBlocks.POWDERKEG);
        recipe()
            .input(TFCTags.Items.TOOLS_HAMMER)
            .input(notRotten(Ingredient.of(TFCBlocks.PUMPKIN)))
            .damageInputs()
            .shapeless(TFCItems.FOOD.get(Food.PUMPKIN_CHUNKS));
        recipe()
            .input(TFCTags.Items.TOOLS_KNIFE)
            .input(notRotten(Food.PUMPKIN_CHUNKS))
            .input(TFCTags.Items.SWEETENERS)
            .input(notRotten(Ingredient.of(Items.EGG)))
            .input(notRotten(Ingredient.of(TFCTags.Items.DOUGH)))
            .damageInputs()
            .shapeless(Items.PUMPKIN_PIE);
        recipe()
            .input('X', TFCTags.Items.STONES_RAW)
            .input('Y', TFCTags.Items.STONES_SMOOTH)
            .pattern("YYY", "XXX")
            .shaped(TFCBlocks.QUERN);
        recipe()
            .input(TFCItems.SOOT)
            .input(TFCItems.COMPOST)
            .shapeless(TFCItems.ROTTEN_COMPOST);
        recipe()
            .inputIsPrimary(AndIngredient.of(
                Ingredient.of(TFCTags.Items.CAN_BE_SALTED),
                NotRottenIngredient.INSTANCE,
                LacksTraitIngredient.of(FoodTraits.SALTED)
            ))
            .input(TFCItems.POWDERS.get(Powder.SALT))
            .addTrait(FoodTraits.SALTED)
            .shapeless("salting");
        recipe()
            .input(Items.PAPER)
            .input(TFCItems.POWDERS.get(Powder.FLUX))
            .input(TFCItems.GLUE)
            .input(TFCTags.Items.VOLCANIC_SAND)
            .input(TFCTags.Items.GEM_POWDERS)
            .shapeless(TFCItems.SANDPAPER);
        recipe()
            .input(TFCTags.Items.MUD_BRICKS)
            .input(TFCItems.DAUB)
            .shapeless(TFCBlocks.SMOOTH_MUD_BRICKS);
        recipe()
            .input(TFCItems.GLUE)
            .input(TFCItems.POWDERS.get(Powder.CHARCOAL))
            .input(TFCItems.POWDERS.get(Powder.WOOD_ASH))
            .shapeless(TFCItems.SOOT);
        recipe()
            .input('X', TFCItems.SPINDLE_HEAD)
            .input('Y', Tags.Items.RODS_WOODEN)
            .pattern("X", "Y")
            .shaped(TFCItems.SPINDLE);
        recipe()
            .input('P', TFCBlocks.STEEL_PIPE)
            .input('G', TFCItems.GLUE)
            .input('M', TFCItems.BRASS_MECHANISMS)
            .pattern("PGM", " P ")
            .shaped(TFCBlocks.STEEL_PUMP);
        recipe()
            .input('X', Tags.Items.RODS_WOODEN)
            .pattern("XXX", "XXX", "XXX")
            .shaped(TFCItems.STICK_BUNCH);
        recipe()
            .input('X', TFCItems.STICK_BUNCH)
            .pattern("X", "X")
            .shaped(TFCItems.STICK_BUNDLE);
        recipe("from_bunch")
            .input(TFCItems.STICK_BUNCH)
            .shapeless(Items.STICK, 9);
        recipe("from_bundle")
            .input(TFCItems.STICK_BUNDLE)
            .shapeless(Items.STICK, 18);
        recipe2x2(TFCItems.STRAW, TFCBlocks.THATCH, 1);
        recipe()
            .input(TFCBlocks.THATCH)
            .shapeless(TFCItems.STRAW, 4);
        recipe()
            .input('S', ingredientOf(Metal.STEEL, Metal.ItemType.SHEET))
            .input('M', TFCItems.BRASS_MECHANISMS)
            .input('R', ingredientOf(Metal.STEEL, Metal.ItemType.ROD))
            .pattern("SMR", "SMR")
            .shaped(TFCBlocks.TRIP_HAMMER);
        recipe()
            .input('X', ItemTags.LOGS)
            .pattern("X", "X")
            .shaped(TFCBlocks.WATTLE);
        recipe()
            .input('L', TFCTags.Items.LUMBER)
            .input('C', TFCItems.WOOL_CLOTH)
            .pattern("LLL", " CC")
            .shaped(TFCItems.WINDMILL_BLADES.get(DyeColor.WHITE));
        recipe()
            .input('L', TFCTags.Items.LUMBER)
            .pattern("L L", " L ")
            .shaped(TFCItems.WOODEN_BUCKET);
        recipe()
            .input(TFCItems.SPINDLE)
            .input(TFCItems.WOOL)
            .damageInputs()
            .shapeless(TFCItems.WOOL_YARN, 8);
    }

    /**
     * @return A builder for a new recipe with a name inferred from the output.
     */
    private Builder recipe()
    {
        return new Builder((name, r) -> {
            if (name != null) add(name, r);
            else add(r);
        });
    }

    /**
     * @return A builder for a new recipe with a name inferred from the output, plus a suffix. The suffix should not start with an underscore.
     */
    private Builder recipe(String suffix)
    {
        return new Builder((name, r) -> {
            assert !suffix.startsWith("_") : "recipe(String suffix) shouldn't start with an '_', it is added for you!";
            assert name == null : "Cannot use a named recipe and recipe(String suffix) at the same time!";
            add(nameOf(r.getResultItem(lookup()).getItem()) + "_" + suffix, r);
        });
    }

    /**
     * @return A builder for a recipe that will replace a vanilla recipe at {@code name}. Checks for conflicts with removals or other replacements.
     */
    private Builder replace(String name)
    {
        return new Builder((name1, r) -> {
            assert name1 == null : "Cannot used replace() with a named recipe!";
            replace(name, r);
        });
    }

    private void addDecorations(ItemLike input, DecorationBlockHolder output)
    {
        recipe()
            .input('#', input)
            .pattern("###")
            .shaped(output.slab().get(), 6);
        recipe()
            .input('#', input)
            .pattern("#  ", "## ", "###")
            .shaped(output.stair().get(), 8);
        recipe()
            .input('#', input)
            .pattern("###", "###")
            .shaped(output.wall().get(), 6);
    }

    private void addGrains(Food crop, Food grain, Food flour, Food dough, Food bread, Food sandwich, Food jamSandwich)
    {
        final var meal = new MealModifier(
            FoodData.ofFood(1f, 0.5f, 4.5f),
            List.of(
                new MealModifier.MealPortion(Optional.of(Ingredient.of(TFCItems.FOOD.get(bread))), 0.5f, 0.5f, 0.5f),
                new MealModifier.MealPortion(Optional.empty(), 0.8f, 0.8f, 0.8f)
            ));

        recipe()
            .input(notRotten(crop))
            .input(TFCTags.Items.TOOLS_KNIFE)
            .damageInputs()
            .copyFood()
            .extraProduct(TFCItems.STRAW)
            .shapeless(TFCItems.FOOD.get(grain));
        recipe()
            .input('K', TFCTags.Items.TOOLS_KNIFE)
            .input('B', notRotten(bread))
            .input('S', notRotten(Ingredient.of(TFCTags.Items.USABLE_IN_SANDWICH)))
            .pattern("KB ", "SSS", " B ")
            .addOutputModifier(meal)
            .shaped(TFCItems.FOOD.get(sandwich), 2);

        for (String pattern : List.of("JSS", "SJS", "SSJ"))
            recipe("" + pattern.indexOf('J'))
                .input('K', TFCTags.Items.TOOLS_KNIFE)
                .input('B', notRotten(bread))
                .input('S', notRotten(Ingredient.of(TFCTags.Items.USABLE_IN_JAM_SANDWICH)))
                .input('J', notRotten(Ingredient.of(TFCTags.Items.PRESERVES)))
                .pattern("KB ", pattern, " B ")
                .addOutputModifier(meal)
                .shaped(TFCItems.FOOD.get(jamSandwich), 2);

        for (int n = 1; n <= 8; n++)
            recipe("" + n)
                .input(notRotten(flour), n)
                .input(FluidContentIngredient.of(Fluids.WATER, 100))
                .copyOldestFood()
                .shapeless(TFCItems.FOOD.get(dough), n * 2);
    }

    private void addTools(Metal.ItemType input, Metal.ItemType output)
    {
        for (Metal metal : Metal.values())
            if (metal.allParts())
                recipe()
                    .input('S', Tags.Items.RODS_WOODEN)
                    .input('X', TFCItems.METAL_ITEMS.get(metal).get(input))
                    .pattern("X", "S")
                    .copyForging()
                    .source(0, 0)
                    .shaped(TFCItems.METAL_ITEMS.get(metal).get(output));
    }

    private void addTools(RockCategory.ItemType input, RockCategory.ItemType output)
    {
        for (RockCategory type : RockCategory.values())
            recipe()
                .input('S', Tags.Items.RODS_WOODEN)
                .input('X', TFCItems.ROCK_TOOLS.get(type).get(input))
                .pattern("X", "S")
                .shaped(TFCItems.ROCK_TOOLS.get(type).get(output));
    }

    private void recipeWithTool(TagKey<Item> tool, ItemLike input, ItemLike output)
    {
        recipe().input(input).input(tool).damageInputs().shapeless(output);
    }

    private void recipeBricksWithMortar(ItemLike brick, ItemLike bricks, int count)
    {
        recipe()
            .input('X', TFCItems.MORTAR)
            .input('Y', brick)
            .pattern("XYX", "YXY", "XYX")
            .shaped(bricks, count);
    }

    private void recipe2x2(ItemLike input, ItemLike output, int count)
    {
        recipe().input('X', input).pattern("XX", "XX").shaped(output, count);
    }

    private void replace3x3BothWays(String toStorage, String fromStorage, ItemLike item, ItemLike storage)
    {
        replace3x3BothWays(toStorage, fromStorage, Ingredient.of(item), item, Ingredient.of(storage), storage);
    }

    private void replace3x3BothWays(String toStorage, String fromStorage, Ingredient itemInput, ItemLike item, Ingredient storageInput, ItemLike storage)
    {
        replace(toStorage).input('X', itemInput).pattern("XXX", "XXX", "XXX").shaped(storage);
        replace(fromStorage).input(storageInput).shapeless(item, 9);
    }

    private Ingredient notRotten(Food food)
    {
        return notRotten(Ingredient.of(TFCItems.FOOD.get(food)));
    }

    private Ingredient notRotten(Ingredient food)
    {
        return AndIngredient.of(food, NotRottenIngredient.INSTANCE);
    }

    /**
     * A recipe builder capable of building shaped, shapeless recipes, optionally with both output and remainder features
     * of advanced shaped / shapeless recipes. It has some preliminary validations to ensure legal recipes are built
     */
    class Builder
    {
        final BiConsumer<String, Recipe<?>> onFinish;
        @Nullable String name = null;

        final List<ItemStackModifier> remainder = new ArrayList<>(); // For advanced recipes, remainder modifiers
        final List<ItemStackModifier> outputs = new ArrayList<>(); // For advanced recipes, output modifiers
        final NonNullList<Ingredient> ingredients = NonNullList.create(); // Shapeless recipes only
        final List<String> pattern = new ArrayList<>(); // Shaped recipes only
        final ImmutableMap.Builder<Character, Ingredient> keys = ImmutableMap.builder();
        int inputRow = 0, inputCol = 0;
        @Nullable Ingredient primaryInput = null;
        boolean needsAdvInput = false, hasAdvInputShaped = false, hasAdvInputShapeless = false;

        Builder(BiConsumer<String, Recipe<?>> onFinish)
        {
            this.onFinish = onFinish;
        }

        Builder damageInputs() { remainder.add(DamageCraftingRemainderModifier.INSTANCE); return this; }

        Builder copyOldestFood() { outputs.add(CopyOldestFoodModifier.INSTANCE); return this; }
        Builder copyFood() { outputs.add(CopyFoodModifier.INSTANCE); return this; }
        Builder copyForging() { needsAdvInput = true; return addOutputModifier(CopyForgingBonusModifier.INSTANCE); }
        Builder copyInput() { needsAdvInput = true; return addOutputModifier(CopyInputModifier.INSTANCE); }
        Builder addGlass() { needsAdvInput = true; return addOutputModifier(AddGlassModifier.INSTANCE); }
        Builder addPowder() { return addOutputModifier(AddPowderModifier.INSTANCE); }
        Builder addBait() { return addOutputModifier(AddBaitToRodModifier.INSTANCE); }
        Builder extraProduct(ItemLike item) { return extraProduct(item, 1); }
        Builder extraProduct(ItemLike item, int count) { return addOutputModifier(new ExtraProductModifier(new ItemStack(item))); }
        Builder addTrait(Holder<FoodTrait> trait) { return addOutputModifier(AddTraitModifier.of(trait)); }

        Builder addOutputModifier(ItemStackModifier modifier) { outputs.add(modifier); return this; }

        Builder input(ItemLike item) { return input(item, 1); }
        Builder input(ItemLike item, int count) { return input(Ingredient.of(item), count); }
        Builder input(TagKey<Item> item) { return input(item, 1); }
        Builder input(TagKey<Item> item, int count) { return input(Ingredient.of(item), count); }
        Builder input(Ingredient item) { return input(item, 1); }
        Builder input(Ingredient item, int count) { for (int n = 0; n < count; n++) ingredients.add(item); return this; }

        Builder inputIsPrimary(Item item) { return inputIsPrimary(Ingredient.of(item)); }
        Builder inputIsPrimary(TagKey<Item> item) { return inputIsPrimary(Ingredient.of(item)); }
        Builder inputIsPrimary(Ingredient item) { primaryInput = item; hasAdvInputShapeless = true; return input(item); }

        Builder input(char key, TagKey<Item> input) { return input(key, Ingredient.of(input)); }
        Builder input(char key, ItemLike input) { return input(key, Ingredient.of(input)); }
        Builder input(char key, Ingredient input) { keys.put(key, input); return this; }

        Builder source(int row, int col) { inputRow = row; inputCol = col; hasAdvInputShaped = true; return this; }

        Builder pattern(String... pattern) { this.pattern.addAll(List.of(pattern)); return this; }

        void shapeless(String name) { this.name = name; shapeless(ItemStack.EMPTY); }
        void shapeless(ItemLike output) { shapeless(output, 1); }
        void shapeless(ItemLike output, int count) { shapeless(new ItemStack(output, count)); }
        void shapeless(ItemStack output)
        {
            assert pattern.isEmpty() && keys.build().isEmpty() : "Mixing shaped and shapeless recipes";
            assert hasAdvInputShapeless || !needsAdvInput : "Missing a .inputIsPrimary(Ingredient) for a recipe which depends on input";
            assert !outputs.isEmpty() || !output.isEmpty() : "Either non-empty output, or output modifiers must be present";

            onFinish.accept(name, isAdvanced()
                ? new AdvancedShapelessRecipe("", CraftingBookCategory.MISC, ingredients, ItemStackProvider.of(output, outputs), remainder(), Optional.ofNullable(primaryInput))
                : new ShapelessRecipe("", CraftingBookCategory.MISC, output, ingredients));
        }

        void shaped(String name) { this.name = name; shaped(ItemStack.EMPTY); }
        void shaped(ItemLike output) { shaped(output, 1); }
        void shaped(ItemLike output, int count) { shaped(new ItemStack(output, count)); }
        void shaped(ItemStack output)
        {
            assert ingredients.isEmpty() : "Mixing shaped and shapeless recipes";
            assert hasAdvInputShaped || !needsAdvInput : "Missing a .source(int, int) for a recipe which depends on input";
            assert !outputs.isEmpty() || !output.isEmpty() : "Either non-empty output, or output modifiers must be present";

            final ShapedRecipePattern pattern = ShapedRecipePattern.of(keys.build(), this.pattern);
            onFinish.accept(name, isAdvanced()
                ? new ShapedRecipe("", CraftingBookCategory.MISC, pattern, output)
                : new AdvancedShapedRecipe("", CraftingBookCategory.MISC, pattern, true, ItemStackProvider.of(output, outputs), remainder(), inputRow, inputCol));
        }

        private Optional<ItemStackProvider> remainder()
        {
            return remainder.isEmpty() ? Optional.empty() :  Optional.of(ItemStackProvider.of(ItemStack.EMPTY, remainder));
        }

        private boolean isAdvanced()
        {
            return !remainder.isEmpty() || !outputs.isEmpty();
        }
    }
}
