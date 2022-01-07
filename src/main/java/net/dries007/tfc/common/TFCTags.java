/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.util.Helpers;

public class TFCTags
{
    public static class Blocks
    {
        public static final Tag.Named<Block> CAN_TRIGGER_COLLAPSE = create("can_trigger_collapse");
        public static final Tag.Named<Block> CAN_START_COLLAPSE = create("can_start_collapse");
        public static final Tag.Named<Block> CAN_COLLAPSE = create("can_collapse");
        public static final Tag.Named<Block> CAN_LANDSLIDE = create("can_landslide");
        public static final Tag.Named<Block> SUPPORTS_LANDSLIDE = create("supports_landslide"); // Non-full blocks that count as full blocks for the purposes of landslide side support check
        public static final Tag.Named<Block> GRASS = create("grass"); // Used for connected textures on grass blocks, different from the vanilla/forge tag
        public static final Tag.Named<Block> TREE_GROWS_ON = create("tree_grows_on"); // Used for tree growth
        public static final Tag.Named<Block> BUSH_PLANTABLE_ON = create("bush_plantable_on"); // Used for plant placement
        public static final Tag.Named<Block> PLANT = create("plant"); // for some decoration placement
        public static final Tag.Named<Block> SEA_BUSH_PLANTABLE_ON = create("sea_bush_plantable_on"); // Used for sea plant placement
        public static final Tag.Named<Block> CREEPING_PLANTABLE_ON = create("creeping_plantable_on");
        public static final Tag.Named<Block> KELP_TREE = create("kelp_tree");
        public static final Tag.Named<Block> KELP_BRANCH = create("kelp_branch");
        public static final Tag.Named<Block> WALL_CORALS = create("wall_corals");
        public static final Tag.Named<Block> CORALS = create("corals");
        public static final Tag.Named<Block> SPREADING_BUSH = create("spreading_bush");
        public static final Tag.Named<Block> ANY_SPREADING_BUSH = create("any_spreading_bush");
        public static final Tag.Named<Block> THORNY_BUSHES = create("thorny_bushes"); // Bushes that damage entities walking through them
        public static final Tag.Named<Block> FRUIT_TREE_BRANCH = create("fruit_tree_branch");
        public static final Tag.Named<Block> FRUIT_TREE_LEAVES = create("fruit_tree_leaves");
        public static final Tag.Named<Block> FRUIT_TREE_SAPLING = create("fruit_tree_sapling");
        public static final Tag.Named<Block> SUPPORT_BEAM = create("support_beam");
        public static final Tag.Named<Block> WORKBENCH = create("workbench");
        public static final Tag.Named<Block> THATCH_BED_THATCH = create("thatch_bed_thatch");
        public static final Tag.Named<Block> SNOW = create("snow"); // Blocks that cover grass with snow.
        public static final Tag.Named<Block> CAN_BE_SNOW_PILED = create("can_be_snow_piled"); // Blocks that can be replaced with snow piles
        public static final Tag.Named<Block> CAN_BE_ICE_PILED = create("can_be_ice_piled"); // Blocks that need to be replaced with ice piles, either from ice freezing below it, or ice freezing inside the block itself.
        public static final Tag.Named<Block> BREAKS_WHEN_ISOLATED = create("breaks_when_isolated"); // When surrounded on all six sides by air, this block will break and drop itself
        public static final Tag.Named<Block> SMALL_SPIKE = create("small_spike");
        public static final Tag.Named<Block> LIT_BY_DROPPED_TORCH = create("lit_by_dropped_torch"); // Causes dropped torches to start fires on them
        public static final Tag.Named<Block> CHARCOAL_COVER_WHITELIST = create("charcoal_cover_whitelist"); // things that skip the valid cover block check on charcoal pits
        public static final Tag.Named<Block> FORGE_INSULATION = create("forge_insulation"); // blocks that can hold a forge inside them
        public static final Tag.Named<Block> FORGE_INVISIBLE_WHITELIST = create("forge_invisible_whitelist"); // i.e., crucibles. stuff that can be in a forge's chimney
        public static final Tag.Named<Block> SCRAPING_SURFACE = create("scraping_surface"); // surfaces you can scrape hides on
        public static final Tag.Named<Block> CAN_CARVE = create("can_carve"); // carvable by TFC world carvers
        public static final Tag.Named<Block> LOGS_THAT_LOG = create("logs_that_log"); // logs that are cut down in entire trees
        public static final Tag.Named<Block> NEEDS_STONE_TOOL = create("needs_stone_tool"); // Equivalent to vanilla wood
        public static final Tag.Named<Block> NEEDS_COPPER_TOOL = create("needs_copper_tool"); // Equivalent to vanilla stone
        public static final Tag.Named<Block> NEEDS_BRONZE_TOOL = create("needs_bronze_tool"); // Equivalent to vanilla iron
        public static final Tag.Named<Block> NEEDS_WROUGHT_IRON_TOOL = create("needs_wrought_iron_tool");
        public static final Tag.Named<Block> NEEDS_STEEL_TOOL = create("needs_steel_tool"); // Equivalent to vanilla diamond
        public static final Tag.Named<Block> NEEDS_BLACK_STEEL_TOOL = create("needs_black_steel_tool");
        public static final Tag.Named<Block> NEEDS_COLORED_STEEL_TOOL = create("needs_colored_steel_tool"); // Equivalent to vanilla netherite
        public static final Tag.Named<Block> MINEABLE_WITH_PROPICK = create("mineable_with_propick");
        public static final Tag.Named<Block> MINEABLE_WITH_CHISEL = create("mineable_with_chisel");
        public static final Tag.Named<Block> MINEABLE_WITH_HAMMER = create("mineable_with_hammer");
        public static final Tag.Named<Block> MINEABLE_WITH_KNIFE = create("mineable_with_knife");
        public static final Tag.Named<Block> MINEABLE_WITH_SCYTHE = create("mineable_with_scythe");
        public static final Tag.Named<Block> PROSPECTABLE = create("prospectable"); // can be found with the prospector pick

        private static Tag.Named<Block> create(String id)
        {
            return BlockTags.createOptional(Helpers.identifier(id));
        }
    }

    public static class Fluids
    {
        public static final Tag.Named<Fluid> MIXABLE = create("mixable");
        public static final Tag.Named<Fluid> HYDRATING = create("hydrating"); // Fluids that work to hydrate farmland, berry bushes, or other growing things
        public static final Tag.Named<Fluid> USABLE_IN_POT = create("usable_in_pot");
        public static final Tag.Named<Fluid> USABLE_IN_JUG = create("usable_in_jug");

        private static Tag.Named<Fluid> create(String id)
        {
            return FluidTags.createOptional(Helpers.identifier(id));
        }
    }

    public static class Items
    {
        public static final Tag.Named<Item> THATCH_BED_HIDES = create("thatch_bed_hides");
        public static final Tag.Named<Item> FIREPIT_KINDLING = create("firepit_kindling");
        public static final Tag.Named<Item> FIREPIT_STICKS = create("firepit_sticks");
        public static final Tag.Named<Item> FIREPIT_LOGS = create("firepit_logs");
        public static final Tag.Named<Item> STARTS_FIRES_WITH_DURABILITY = create("starts_fires_with_durability");
        public static final Tag.Named<Item> STARTS_FIRES_WITH_ITEMS = create("starts_fires_with_items");
        public static final Tag.Named<Item> EXTINGUISHER = create("extinguisher");
        public static final Tag.Named<Item> LOG_PILE_LOGS = create("log_pile_logs");
        public static final Tag.Named<Item> PIT_KILN_STRAW = create("pit_kiln_straw");
        public static final Tag.Named<Item> PIT_KILN_LOGS = create("pit_kiln_logs");
        public static final Tag.Named<Item> CAN_BE_LIT_ON_TORCH = create("can_be_lit_on_torch");
        public static final Tag.Named<Item> FIREPIT_FUEL = create("firepit_fuel");
        public static final Tag.Named<Item> FORGE_FUEL = create("forge_fuel");
        public static final Tag.Named<Item> HANDSTONE = create("handstone");
        public static final Tag.Named<Item> SCRAPABLE = create("scrapable");
        public static final Tag.Named<Item> KNIVES = create("knives");
        public static final Tag.Named<Item> HOES = create("hoes");
        public static final Tag.Named<Item> ROCK_KNAPPING = create("rock_knapping");
        public static final Tag.Named<Item> CLAY_KNAPPING = create("clay_knapping");
        public static final Tag.Named<Item> FIRE_CLAY_KNAPPING = create("fire_clay_knapping");
        public static final Tag.Named<Item> LEATHER_KNAPPING = create("leather_knapping");
        public static final Tag.Named<Item> AXES_THAT_LOG = create("axes_that_log"); // Axes which cut down entire trees
        public static final Tag.Named<Item> BUSH_CUTTING_TOOLS = create("bush_cutting_tools"); // Tools which can be used to create cuttings from bushes.
        public static final Tag.Named<Item> WATTLE_STICKS = create("wattle_sticks");

        private static Tag.Named<Item> create(String id)
        {
            return ItemTags.createOptional(Helpers.identifier(id));
        }
    }
}