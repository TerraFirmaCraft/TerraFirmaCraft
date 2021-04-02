/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

import net.dries007.tfc.util.Helpers;

public class TFCTags
{
    public static class Blocks
    {
        public static final ITag.INamedTag<Block> CAN_TRIGGER_COLLAPSE = create("can_trigger_collapse");
        public static final ITag.INamedTag<Block> CAN_START_COLLAPSE = create("can_start_collapse");
        public static final ITag.INamedTag<Block> CAN_COLLAPSE = create("can_collapse");
        public static final ITag.INamedTag<Block> CAN_LANDSLIDE = create("can_landslide");
        public static final ITag.INamedTag<Block> SUPPORTS_LANDSLIDE = create("supports_landslide"); // Non-full blocks that count as full blocks for the purposes of landslide side support check
        public static final ITag.INamedTag<Block> GRASS = create("grass"); // Used for connected textures on grass blocks, different from the vanilla/forge tag
        public static final ITag.INamedTag<Block> TREE_GROWS_ON = create("tree_grows_on"); // Used for tree growth
        public static final ITag.INamedTag<Block> BUSH_PLANTABLE_ON = create("bush_plantable_on"); // Used for plant placement
        public static final ITag.INamedTag<Block> PLANT = create("plant"); // for some decoration placement
        public static final ITag.INamedTag<Block> SEA_BUSH_PLANTABLE_ON = create("sea_bush_plantable_on"); // Used for sea plant placement
        public static final ITag.INamedTag<Block> CREEPING_PLANTABLE_ON = create("creeping_plantable_on");
        public static final ITag.INamedTag<Block> KELP_TREE = create("kelp_tree");
        public static final ITag.INamedTag<Block> KELP_FLOWER = create("kelp_flower");
        public static final ITag.INamedTag<Block> KELP_BRANCH = create("kelp_branch");
        public static final ITag.INamedTag<Block> WALL_CORALS = create("wall_corals");
        public static final ITag.INamedTag<Block> CORALS = create("corals");
        public static final ITag.INamedTag<Block> SPREADING_BUSH = create("spreading_bush");
        public static final ITag.INamedTag<Block> ANY_SPREADING_BUSH = create("any_spreading_bush");
        public static final ITag.INamedTag<Block> FRUIT_TREE_BRANCH = create("fruit_tree_branch");
        public static final ITag.INamedTag<Block> FRUIT_TREE_LEAVES = create("fruit_tree_leaves");
        public static final ITag.INamedTag<Block> FRUIT_TREE_SAPLING = create("fruit_tree_sapling");

        public static final ITag.INamedTag<Block> THATCH_BED_THATCH = create("thatch_bed_thatch");

        public static final ITag.INamedTag<Block> SNOW = create("snow"); // Blocks that cover grass with snow.
        public static final ITag.INamedTag<Block> CAN_BE_SNOW_PILED = create("can_be_snow_piled"); // Blocks that can be replaced with snow piles

        public static final ITag.INamedTag<Block> BREAKS_WHEN_ISOLATED = create("breaks_when_isolated"); // When surrounded on all six sides by air, this block will break and drop itself
        public static final ITag.INamedTag<Block> SMALL_SPIKE = create("small_spike");
        public static final ITag.INamedTag<Block> LIT_BY_DROPPED_TORCH = create("lit_by_dropped_torch"); // Causes dropped torches to start fires on them
        public static final ITag.INamedTag<Block> CHARCOAL_COVER_WHITELIST = create("charcoal_cover_whitelist"); // things that skip the valid cover block check on charcoal pits

        private static ITag.INamedTag<Block> create(String id)
        {
            return BlockTags.bind(Helpers.identifier(id).toString());
        }
    }

    public static class Fluids
    {
        public static final ITag.INamedTag<Fluid> MIXABLE = create("mixable");

        private static ITag.INamedTag<Fluid> create(String id)
        {
            return FluidTags.bind(Helpers.identifier(id).toString());
        }
    }

    public static class Items
    {
        public static final ITag.INamedTag<Item> THATCH_BED_HIDES = create("thatch_bed_hides");
        public static final ITag.INamedTag<Item> FIREPIT_KINDLING = create("firepit_kindling");
        public static final ITag.INamedTag<Item> FIREPIT_STICKS = create("firepit_sticks");
        public static final ITag.INamedTag<Item> FIREPIT_LOGS = create("firepit_logs");
        public static final ITag.INamedTag<Item> STARTS_FIRES_WITH_DURABILITY = create("starts_fires_with_durability");
        public static final ITag.INamedTag<Item> STARTS_FIRES_WITH_ITEMS = create("starts_fires_with_items");
        public static final ITag.INamedTag<Item> EXTINGUISHER = create("extinguisher");
        public static final ITag.INamedTag<Item> LOG_PILE_LOGS = create("log_pile_logs");
        public static final ITag.INamedTag<Item> PIT_KILN_STRAW = create("pit_kiln_straw");
        public static final ITag.INamedTag<Item> PIT_KILN_LOGS = create("pit_kiln_logs");
        public static final ITag.INamedTag<Item> CAN_BE_LIT_ON_TORCH = create("can_be_lit_on_torch");
        public static final ITag.INamedTag<Item> FIREPIT_FUEL = create("firepit_fuel");
        public static final ITag.INamedTag<Item> BLOOMERY_FUEL = create("bloomery_fuel");
        public static final ITag.INamedTag<Item> FORGE_FUEL = create("forge_fuel");

        private static ITag.INamedTag<Item> create(String id)
        {
            return ItemTags.bind(Helpers.identifier(id).toString());
        }
    }
}