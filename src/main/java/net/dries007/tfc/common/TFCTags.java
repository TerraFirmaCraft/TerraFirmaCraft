/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.util.Helpers;

public class TFCTags
{
    public static class Blocks
    {
        public static final TagKey<Block> CAN_TRIGGER_COLLAPSE = create("can_trigger_collapse");
        public static final TagKey<Block> CAN_START_COLLAPSE = create("can_start_collapse");
        public static final TagKey<Block> CAN_COLLAPSE = create("can_collapse");
        public static final TagKey<Block> CAN_LANDSLIDE = create("can_landslide");
        public static final TagKey<Block> SUPPORTS_LANDSLIDE = create("supports_landslide"); // Non-full blocks that count as full blocks for the purposes of landslide side support check
        public static final TagKey<Block> TOUGHNESS_1 = create("toughness_1"); // Tags for toughness of materials w.r.t falling blocks
        public static final TagKey<Block> TOUGHNESS_2 = create("toughness_2"); // Tags for toughness of materials w.r.t falling blocks
        public static final TagKey<Block> TOUGHNESS_3 = create("toughness_3"); // Tags for toughness of materials w.r.t falling blocks
        public static final TagKey<Block> GRASS = create("grass"); // Used for connected textures on grass blocks, different from the vanilla/forge tag
        public static final TagKey<Block> TREE_GROWS_ON = create("tree_grows_on"); // Used for tree growth
        public static final TagKey<Block> BUSH_PLANTABLE_ON = create("bush_plantable_on"); // Used for plant placement
        public static final TagKey<Block> GRASS_PLANTABLE_ON = create("grass_plantable_on"); // Grass has a slightly expanded placing allowance
        public static final TagKey<Block> PLANTS = create("plants"); // for some decoration placement
        public static final TagKey<Block> SINGLE_BLOCK_REPLACEABLE = create("single_block_replaceable"); // blocks that features can safely destroy
        public static final TagKey<Block> SEA_BUSH_PLANTABLE_ON = create("sea_bush_plantable_on"); // Used for sea plant placement
        public static final TagKey<Block> CREEPING_PLANTABLE_ON = create("creeping_plantable_on");
        public static final TagKey<Block> KELP_TREE = create("kelp_tree");
        public static final TagKey<Block> KELP_BRANCH = create("kelp_branch");
        public static final TagKey<Block> WALL_CORALS = create("wall_corals");
        public static final TagKey<Block> CORALS = create("corals");
        public static final TagKey<Block> SPREADING_BUSH = create("spreading_bush");
        public static final TagKey<Block> ANY_SPREADING_BUSH = create("any_spreading_bush");
        public static final TagKey<Block> THORNY_BUSHES = create("thorny_bushes"); // Bushes that damage entities walking through them
        public static final TagKey<Block> FRUIT_TREE_BRANCH = create("fruit_tree_branch");
        public static final TagKey<Block> FRUIT_TREE_LEAVES = create("fruit_tree_leaves");
        public static final TagKey<Block> FRUIT_TREE_SAPLING = create("fruit_tree_sapling");
        public static final TagKey<Block> SUPPORT_BEAM = create("support_beam");
        public static final TagKey<Block> WORKBENCH = create("workbench");
        public static final TagKey<Block> THATCH_BED_THATCH = create("thatch_bed_thatch");
        public static final TagKey<Block> SNOW = create("snow"); // Blocks that cover grass with snow.
        public static final TagKey<Block> CAN_BE_SNOW_PILED = create("can_be_snow_piled"); // Blocks that can be replaced with snow piles
        public static final TagKey<Block> CAN_BE_ICE_PILED = create("can_be_ice_piled"); // Blocks that need to be replaced with ice piles, either from ice freezing below it, or ice freezing inside the block itself.
        public static final TagKey<Block> BREAKS_WHEN_ISOLATED = create("breaks_when_isolated"); // When surrounded on all six sides by air, this block will break and drop itself
        public static final TagKey<Block> LIT_BY_DROPPED_TORCH = create("lit_by_dropped_torch"); // Causes dropped torches to start fires on them
        public static final TagKey<Block> CHARCOAL_COVER_WHITELIST = create("charcoal_cover_whitelist"); // things that skip the valid cover block check on charcoal pits
        public static final TagKey<Block> FORGE_INSULATION = create("forge_insulation"); // blocks that can hold a forge inside them
        public static final TagKey<Block> FORGE_INVISIBLE_WHITELIST = create("forge_invisible_whitelist"); // i.e., crucibles. stuff that can be in a forge's chimney
        public static final TagKey<Block> BLOOMERY_INSULATION = create("bloomery_insulation"); // valid blocks for bloomery structure
        public static final TagKey<Block> BLAST_FURNACE_INSULATION = create("blast_furnace_insulation"); // valid blocks for blast furnace structure
        public static final TagKey<Block> SCRAPING_SURFACE = create("scraping_surface"); // surfaces you can scrape hides on
        public static final TagKey<Block> CAN_CARVE = create("can_carve"); // carvable by TFC world carvers
        public static final TagKey<Block> LOGS_THAT_LOG = create("logs_that_log"); // logs that are cut down in entire trees
        public static final TagKey<Block> NEEDS_STONE_TOOL = create("needs_stone_tool"); // Equivalent to vanilla wood
        public static final TagKey<Block> NEEDS_COPPER_TOOL = create("needs_copper_tool"); // Equivalent to vanilla stone
        public static final TagKey<Block> NEEDS_BRONZE_TOOL = create("needs_bronze_tool"); // Equivalent to vanilla iron
        public static final TagKey<Block> NEEDS_WROUGHT_IRON_TOOL = create("needs_wrought_iron_tool");
        public static final TagKey<Block> NEEDS_STEEL_TOOL = create("needs_steel_tool"); // Equivalent to vanilla diamond
        public static final TagKey<Block> NEEDS_BLACK_STEEL_TOOL = create("needs_black_steel_tool");
        public static final TagKey<Block> NEEDS_COLORED_STEEL_TOOL = create("needs_colored_steel_tool"); // Equivalent to vanilla netherite
        public static final TagKey<Block> MINEABLE_WITH_PROPICK = create("mineable_with_propick");
        public static final TagKey<Block> MINEABLE_WITH_CHISEL = create("mineable_with_chisel");
        public static final TagKey<Block> MINEABLE_WITH_HAMMER = create("mineable_with_hammer");
        public static final TagKey<Block> MINEABLE_WITH_KNIFE = create("mineable_with_knife");
        public static final TagKey<Block> MINEABLE_WITH_SCYTHE = create("mineable_with_scythe");
        public static final TagKey<Block> PROSPECTABLE = create("prospectable"); // can be found with the prospector pick
        public static final TagKey<Block> CAN_BE_PANNED = create("can_be_panned"); // can be picked up with a pan
        public static final TagKey<Block> CONVERTS_TO_HUMUS = create("converts_to_humus");
        public static final TagKey<Block> WILD_CROP_GROWS_ON = create("wild_crop_grows_on"); // Used for wild crops
        public static final TagKey<Block> FARMLAND = create("farmland"); // Crops that are not wild can grow on this
        public static final TagKey<Block> POWDER_SNOW_REPLACEABLE = create("powder_snow_replaceable"); // on feature gen, can be replaced by powder snow
        public static final TagKey<Block> CREATES_UPWARD_BUBBLES = create("creates_upward_bubbles"); // bubble columns
        public static final TagKey<Block> CREATES_DOWNWARD_BUBBLES = create("creates_downward_bubbles");
        public static final TagKey<Block> RABBIT_RAIDABLE = create("rabbit_raidable"); // rabbits will break it

        private static TagKey<Block> create(String id)
        {
            return TagKey.create(Registry.BLOCK_REGISTRY, Helpers.identifier(id));
        }
    }

    public static class Fluids
    {
        public static final TagKey<Fluid> MIXABLE = create("mixable");
        public static final TagKey<Fluid> HYDRATING = create("hydrating"); // Fluids that work to hydrate farmland, berry bushes, or other growing things
        public static final TagKey<Fluid> USABLE_IN_POT = create("usable_in_pot");
        public static final TagKey<Fluid> USABLE_IN_JUG = create("usable_in_jug");
        public static final TagKey<Fluid> USABLE_IN_WOODEN_BUCKET = create("usable_in_wooden_bucket");
        public static final TagKey<Fluid> USABLE_IN_RED_STEEL_BUCKET = create("usable_in_red_steel_bucket");
        public static final TagKey<Fluid> USABLE_IN_BLUE_STEEL_BUCKET = create("usable_in_blue_steel_bucket");
        public static final TagKey<Fluid> USABLE_IN_BARREL = create("usable_in_barrel");
        public static final TagKey<Fluid> SCRIBING_INK = create("scribing_ink");
        public static final TagKey<Fluid> USABLE_IN_SLUICE = create("usable_in_sluice");

        private static TagKey<Fluid> create(String id)
        {
            return TagKey.create(Registry.FLUID_REGISTRY, Helpers.identifier(id));
        }
    }

    public static class Items
    {
        public static final TagKey<Item> THATCH_BED_HIDES = create("thatch_bed_hides");
        public static final TagKey<Item> FIREPIT_KINDLING = create("firepit_kindling");
        public static final TagKey<Item> FIREPIT_STICKS = create("firepit_sticks");
        public static final TagKey<Item> FIREPIT_LOGS = create("firepit_logs");
        public static final TagKey<Item> STARTS_FIRES_WITH_DURABILITY = create("starts_fires_with_durability");
        public static final TagKey<Item> STARTS_FIRES_WITH_ITEMS = create("starts_fires_with_items");
        public static final TagKey<Item> EXTINGUISHER = create("extinguisher");
        public static final TagKey<Item> LOG_PILE_LOGS = create("log_pile_logs");
        public static final TagKey<Item> PIT_KILN_STRAW = create("pit_kiln_straw");
        public static final TagKey<Item> PIT_KILN_LOGS = create("pit_kiln_logs");
        public static final TagKey<Item> CAN_BE_LIT_ON_TORCH = create("can_be_lit_on_torch");
        public static final TagKey<Item> FIREPIT_FUEL = create("firepit_fuel");
        public static final TagKey<Item> FORGE_FUEL = create("forge_fuel");
        public static final TagKey<Item> BLAST_FURNACE_FUEL = create("blast_furnace_fuel");
        public static final TagKey<Item> HANDSTONE = create("handstone");
        public static final TagKey<Item> SCRAPABLE = create("scrapable");
        public static final TagKey<Item> KNIVES = create("knives");
        public static final TagKey<Item> HOES = create("hoes");
        public static final TagKey<Item> HAMMERS = create("hammers");
        public static final TagKey<Item> CHISELS = create("chisels");
        public static final TagKey<Item> FLUX = create("flux");
        public static final TagKey<Item> ANVILS = create("anvils");
        public static final TagKey<Item> TUYERES = create("tuyeres");
        public static final TagKey<Item> ROCK_KNAPPING = create("rock_knapping");
        public static final TagKey<Item> CLAY_KNAPPING = create("clay_knapping");
        public static final TagKey<Item> FIRE_CLAY_KNAPPING = create("fire_clay_knapping");
        public static final TagKey<Item> LEATHER_KNAPPING = create("leather_knapping");
        public static final TagKey<Item> AXES_THAT_LOG = create("axes_that_log"); // Axes which cut down entire trees
        public static final TagKey<Item> BUSH_CUTTING_TOOLS = create("bush_cutting_tools"); // Tools which can be used to create cuttings from bushes.
        public static final TagKey<Item> COMPOST_GREENS = create("compost_greens");
        public static final TagKey<Item> COMPOST_BROWNS = create("compost_browns");
        public static final TagKey<Item> COMPOST_POISONS = create("compost_poisons");
        public static final TagKey<Item> USABLE_ON_TOOL_RACK = create("usable_on_tool_rack");
        public static final TagKey<Item> USABLE_IN_POWDER_KEG = create("usable_in_powder_keg");
        public static final TagKey<Item> SOUP_BOWLS = create("soup_bowls"); // Bowls that when right clicked on a pot, can extract soup
        public static final TagKey<Item> SALAD_BOWLS = create("salad_bowls"); // Bowls that when right clicked, open the salad UI
        public static final TagKey<Item> USABLE_IN_SALAD = create("foods/usable_in_salad"); // Items that are valid ingredients for a salad
        public static final TagKey<Item> PIG_FOOD = create("pig_food");
        public static final TagKey<Item> COW_FOOD = create("cow_food");
        public static final TagKey<Item> YAK_FOOD = create("yak_food");
        public static final TagKey<Item> GOAT_FOOD = create("goat_food");
        public static final TagKey<Item> ALPACA_FOOD = create("alpaca_food");
        public static final TagKey<Item> SHEEP_FOOD = create("sheep_food");
        public static final TagKey<Item> MUSK_OX_FOOD = create("musk_ox_food");
        public static final TagKey<Item> CHICKEN_FOOD = create("chicken_food");
        public static final TagKey<Item> DUCK_FOOD = create("duck_food");
        public static final TagKey<Item> QUAIL_FOOD = create("quail_food");
        public static final TagKey<Item> SCRIBING_INK = create("scribing_ink");
        public static final TagKey<Item> SANDWICH_BREAD = create("sandwich_bread");
        public static final TagKey<Item> SMALL_FISHING_BAIT = create("small_fishing_bait");
        public static final TagKey<Item> LARGE_FISHING_BAIT = create("large_fishing_bait");
        public static final TagKey<Item> HOLDS_SMALL_FISHING_BAIT = create("holds_small_fishing_bait");
        public static final TagKey<Item> HOLDS_LARGE_FISHING_BAIT = create("holds_large_fishing_bait");
        public static final TagKey<Item> CAN_BE_SALTED = create("foods/can_be_salted");
        public static final TagKey<Item> PILEABLE_INGOTS = create("pileable_ingots"); // Ingots that can be added to piles
        public static final TagKey<Item> PILEABLE_SHEETS = create("pileable_sheets"); // Sheets that can be added to piles

        private static TagKey<Item> create(String id)
        {
            return TagKey.create(Registry.ITEM_REGISTRY, Helpers.identifier(id));
        }
    }

    public static class Entities
    {
        public static final TagKey<EntityType<?>> TURTLE_FRIENDS = create("turtle_friends");
        public static final TagKey<EntityType<?>> SPAWNS_ON_COLD_BLOCKS = create("spawns_on_cold_blocks"); // if ice is a valid spawn
        public static final TagKey<EntityType<?>> DESTROYS_FLOATING_PLANTS = create("destroys_floating_plants");
        public static final TagKey<EntityType<?>> BUBBLE_COLUMN_IMMUNE = create("bubble_column_immune");
        public static final TagKey<EntityType<?>> NEEDS_LARGE_FISHING_BAIT = create("needs_large_fishing_bait");

        private static TagKey<EntityType<?>> create(String id)
        {
            return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, Helpers.identifier(id));
        }
    }
}