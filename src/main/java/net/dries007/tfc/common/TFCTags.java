/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
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
        public static final TagKey<Block> NOT_SOLID_SUPPORTING = create("not_solid_supporting"); // Blocks that don't count as supporting the block above for the purposes of collapse start checks
        public static final TagKey<Block> TOUGHNESS_1 = create("toughness_1"); // Tags for toughness of materials w.r.t falling blocks
        public static final TagKey<Block> TOUGHNESS_2 = create("toughness_2"); // Tags for toughness of materials w.r.t falling blocks
        public static final TagKey<Block> TOUGHNESS_3 = create("toughness_3"); // Tags for toughness of materials w.r.t falling blocks
        public static final TagKey<Block> GRASS = create("grass"); // Used for connected textures on grass blocks, different from the vanilla/forge tag
        public static final TagKey<Block> TREE_GROWS_ON = create("tree_grows_on"); // Used for tree growth
        public static final TagKey<Block> SPREADING_FRUIT_GROWS_ON = create("spreading_fruit_grows_on"); // pumpkins and melons will grow here
        public static final TagKey<Block> BUSH_PLANTABLE_ON = create("bush_plantable_on"); // Used for plant placement
        public static final TagKey<Block> GRASS_PLANTABLE_ON = create("grass_plantable_on"); // Grass has a slightly expanded placing allowance
        public static final TagKey<Block> PLANTS = create("plants"); // for some decoration placement
        public static final TagKey<Block> SINGLE_BLOCK_REPLACEABLE = create("single_block_replaceable"); // blocks that features can safely destroy
        public static final TagKey<Block> SEA_BUSH_PLANTABLE_ON = create("sea_bush_plantable_on"); // Used for sea plant placement
        public static final TagKey<Block> HALOPHYTE = create("halophyte"); // Used for all saltwater plants that generate in salt marshes
        public static final TagKey<Block> HALOPHYTE_PLANTABLE_ON = create("halophyte_plantable_on"); // Used for halophyte placement
        public static final TagKey<Block> CREEPING_PLANTABLE_ON = create("creeping_plantable_on");
        public static final TagKey<Block> CREEPING_STONE_PLANTABLE_ON = create("creeping_stone_plantable_on");
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
        public static final TagKey<Block> SUPPORT_BEAM = create("support_beams");
        public static final TagKey<Block> WORKBENCHES = create("workbenches");
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
        public static final TagKey<Block> MINEABLE_WITH_GLASS_SAW = create("mineable_with_glass_saw");
        public static final TagKey<Block> PROSPECTABLE = create("prospectable"); // can be found with the prospector pick
        public static final TagKey<Block> CONVERTS_TO_HUMUS = create("converts_to_humus");
        public static final TagKey<Block> WILD_CROP_GROWS_ON = create("wild_crop_grows_on"); // Used for wild crops
        public static final TagKey<Block> FARMLAND = create("farmland"); // Crops that are not wild can grow on this
        public static final TagKey<Block> POWDER_SNOW_REPLACEABLE = create("powder_snow_replaceable"); // on feature gen, can be replaced by powder snow
        public static final TagKey<Block> RABBIT_RAIDABLE = create("rabbit_raidable"); // rabbits will break it
        public static final TagKey<Block> FOX_RAIDABLE = create("fox_raidable"); // foxes will eat the berries. only applies to seasonal plant blocks
        public static final TagKey<Block> SEASONAL_LEAVES = create("seasonal_leaves"); // drops fall leaf particles
        public static final TagKey<Block> PET_SITS_ON = create("pet_sits_on"); // pet prefers to sit here.
        public static final TagKey<Block> MINECART_HOLDABLE = create("minecart_holdable"); // blocks that carts will hold. this is a block tag to ensure it can render in the cart.
        public static final TagKey<Block> SNOW_LAYER_SURVIVES_ON = create("snow_layer_survives_on");
        public static final TagKey<Block> MONSTER_SPAWNS_ON = create("monster_spawns_on");
        public static final TagKey<Block> BOTTOM_SUPPORT_ACCEPTED = create("bottom_support_accepted"); // ignored by devices for bottom support
        public static final TagKey<Block> TIDE_POOL_BLOCKS = create("tide_pool_blocks"); // groundcover blocks that spawn in tide pools
        public static final TagKey<Block> GLASS_POURING_TABLE = create("glass_pouring_table");
        public static final TagKey<Block> GLASS_BASIN_BLOCKS = create("glass_basin_blocks");
        public static final TagKey<Block> EXPLOSION_PROOF = create("explosion_proof");
        public static final TagKey<Block> POWDERKEG_BREAKING_BLOCKS = create("powderkeg_breaking_blocks");
        public static final TagKey<Block> KAOLIN_CLAY_REPLACEABLE = create("kaolin_clay_replaceable");

        private static TagKey<Block> create(String id)
        {
            return TagKey.create(Registries.BLOCK, Helpers.identifier(id));
        }
    }

    public static class Fluids
    {
        /**
         * These vanilla tags, historically, were required for <strong>any</strong> fluid behavior, and so may still be used that way. Do not rely on them only containing water or lava, instead, reference the below tags for more specific fluid grouping.
         */
        public static final TagKey<Fluid> WATER_LIKE = FluidTags.WATER;
        public static final TagKey<Fluid> LAVA_LIKE = FluidTags.LAVA;

        /**
         * These water tags contain only source fluids of given water variants.
         * <ul>
         *     <li>{@code ANY_FRESH_WATER} contains only fresh water (vanilla), and river water</li>
         *     <li>{@code ANY_INFINITE_WATER} contains that, plus salt water. It is also all waters which are dynamically colored</li>
         *     <li>{@code ANY_WATER} contains that, plus spring water</li>
         * </ul>
         */
        public static final TagKey<Fluid> ANY_FRESH_WATER = create("any_fresh_water");
        public static final TagKey<Fluid> ANY_INFINITE_WATER = create("any_infinite_water");
        public static final TagKey<Fluid> ANY_WATER = create("any_water");


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
        public static final TagKey<Fluid> USABLE_IN_INGOT_MOLD = create("usable_in_ingot_mold");
        public static final TagKey<Fluid> USABLE_IN_TOOL_HEAD_MOLD = create("usable_in_tool_head_mold");
        public static final TagKey<Fluid> USABLE_IN_BELL_MOLD = create("usable_in_bell_mold");

        private static TagKey<Fluid> create(String id)
        {
            return TagKey.create(Registries.FLUID, Helpers.identifier(id));
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
        public static final TagKey<Item> HAND_WHEEL = create("hand_wheel");
        public static final TagKey<Item> SCRAPABLE = create("scrapable");
        public static final TagKey<Item> KNIVES = create("knives");
        public static final TagKey<Item> HOES = create("hoes");
        public static final TagKey<Item> HAMMERS = create("hammers");
        public static final TagKey<Item> CHISELS = create("chisels");
        public static final TagKey<Item> FLUX = create("flux");
        public static final TagKey<Item> ANVILS = create("anvils");
        public static final TagKey<Item> TUYERES = create("tuyeres");
        public static final TagKey<Item> ANY_KNAPPING = create("any_knapping");
        public static final TagKey<Item> ROCK_KNAPPING = create("rock_knapping");
        public static final TagKey<Item> AXES_THAT_LOG = create("axes_that_log"); // Axes which cut down entire trees
        public static final TagKey<Item> INEFFICIENT_LOGGING_AXES = create("inefficient_logging_axes"); // Axes which are 60% efficient at destroying logs
        public static final TagKey<Item> COMPOST_GREENS = create("compost_greens");
        public static final TagKey<Item> COMPOST_GREENS_LOW = create("compost_greens_low");
        public static final TagKey<Item> COMPOST_GREENS_HIGH = create("compost_greens_high");
        public static final TagKey<Item> COMPOST_BROWNS = create("compost_browns");
        public static final TagKey<Item> COMPOST_BROWNS_LOW = create("compost_browns_low");
        public static final TagKey<Item> COMPOST_BROWNS_HIGH = create("compost_browns_high");
        public static final TagKey<Item> COMPOST_POISONS = create("compost_poisons");
        public static final TagKey<Item> USABLE_ON_TOOL_RACK = create("usable_on_tool_rack");
        public static final TagKey<Item> USABLE_IN_POWDER_KEG = create("usable_in_powder_keg");
        public static final TagKey<Item> SOUP_BOWLS = create("soup_bowls"); // Bowls that when right clicked on a pot, can extract soup
        public static final TagKey<Item> SALAD_BOWLS = create("salad_bowls"); // Bowls that when right clicked, open the salad UI
        public static final TagKey<Item> USABLE_IN_SALAD = create("foods/usable_in_salad"); // Items that are valid ingredients for a salad
        public static final TagKey<Item> FOODS = create("foods");
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
        public static final TagKey<Item> DONKEY_FOOD = create("donkey_food");
        public static final TagKey<Item> MULE_FOOD = create("mule_food");
        public static final TagKey<Item> HORSE_FOOD = create("horse_food");
        public static final TagKey<Item> CAT_FOOD = create("cat_food");
        public static final TagKey<Item> DOG_FOOD = create("dog_food");
        public static final TagKey<Item> PENGUIN_FOOD = create("penguin_food");
        public static final TagKey<Item> TURTLE_FOOD = create("turtle_food");
        public static final TagKey<Item> FROG_FOOD = create("frog_food");
        public static final TagKey<Item> RABBIT_FOOD = create("rabbit_food");
        public static final TagKey<Item> SCRIBING_INK = create("scribing_ink");
        public static final TagKey<Item> SMALL_FISHING_BAIT = create("small_fishing_bait");
        public static final TagKey<Item> LARGE_FISHING_BAIT = create("large_fishing_bait");
        public static final TagKey<Item> HOLDS_SMALL_FISHING_BAIT = create("holds_small_fishing_bait");
        public static final TagKey<Item> HOLDS_LARGE_FISHING_BAIT = create("holds_large_fishing_bait");
        public static final TagKey<Item> PILEABLE_INGOTS = create("pileable_ingots"); // Ingots that can be added to piles
        public static final TagKey<Item> PILEABLE_DOUBLE_INGOTS = create("pileable_double_ingots"); // Double Ingots that can be added to piles
        public static final TagKey<Item> PILEABLE_SHEETS = create("pileable_sheets"); // Sheets that can be added to piles
        public static final TagKey<Item> FOX_SPAWNS_WITH = create("fox_spawns_with"); // fox has a chance to spawn with this in its mouth
        public static final TagKey<Item> MOB_FEET_ARMOR = create("mob_feet_armor"); // armor that mobs can put on their feet
        public static final TagKey<Item> MOB_LEG_ARMOR = create("mob_leg_armor"); // armor that mobs can put on their legs
        public static final TagKey<Item> MOB_CHEST_ARMOR = create("mob_chest_armor"); // armor that mobs can put on their chest
        public static final TagKey<Item> MOB_HEAD_ARMOR = create("mob_head_armor"); // armor that mobs can put on their head
        public static final TagKey<Item> MOB_MAINHAND_WEAPONS = create("mob_mainhand_weapons"); // armor that mobs can put on their mainhand
        public static final TagKey<Item> MOB_OFFHAND_WEAPONS = create("mob_offhand_weapons"); // armor that mobs can put on their mainhand
        public static final TagKey<Item> SKELETON_WEAPONS = create("skeleton_weapons"); // stuff we force skeletons to hold. includes javelins and bows
        public static final TagKey<Item> DISABLED_MONSTER_HELD_ITEMS = create("disabled_monster_held_items"); // items Monsters will not spawn holding. also gated with ServerConfig#enableVanillaMobsSpawningWithVanillaEquipment
        public static final TagKey<Item> DEALS_SLASHING_DAMAGE = create("deals_slashing_damage");
        public static final TagKey<Item> DEALS_PIERCING_DAMAGE = create("deals_piercing_damage");
        public static final TagKey<Item> DEALS_CRUSHING_DAMAGE = create("deals_crushing_damage");
        public static final TagKey<Item> FLUID_ITEM_INGREDIENT_EMPTY_CONTAINERS = create("fluid_item_ingredient_empty_containers"); // Containers that are filled, as examples for FluidItemIngredient
        public static final TagKey<Item> PLACED_ITEM_BLACKLIST = create("placed_item_blacklist"); // items that cannot go in placed items, for whatever reason
        public static final TagKey<Item> PLACED_ITEM_WHITELIST = create("placed_item_whitelist"); // items that can go in placed items. not used unless enabled
        public static final TagKey<Item> DAUB = create("daub"); // acts like daub on wattle blocks
        public static final TagKey<Item> PIGLIN_BARTERING_INGOTS = create("piglin_bartering_ingots"); // ingots that piglins will trade for.
        public static final TagKey<Item> CARRIED_BY_HORSE = create("carried_by_horse");
        public static final TagKey<Item> WAXES_SCRAPING_SURFACE = create("waxes_scraping_surface");
        public static final TagKey<Item> POWDERS = create("powders");
        public static final TagKey<Item> CAN_BE_SALTED = create("foods/can_be_salted");
        public static final TagKey<Item> GLASS_BATCHES = create("glass_batches");
        public static final TagKey<Item> BLOWPIPES = create("blowpipes");
        public static final TagKey<Item> ALL_BLOWPIPES = create("all_blowpipes");
        public static final TagKey<Item> SILICA_ITEMS = create("silica_items");
        public static final TagKey<Item> HEMATITIC_ITEMS = create("hematitic_items");
        public static final TagKey<Item> OLIVINE_ITEMS = create("olivine_items");
        public static final TagKey<Item> VOLCANIC_ITEMS = create("volcanic_items");
        public static final TagKey<Item> IGNEOUS_INTRUSIVE_ITEMS = create("igneous_intrusive_items");
        public static final TagKey<Item> IGNEOUS_EXTRUSIVE_ITEMS = create("igneous_extrusive_items");
        public static final TagKey<Item> METAMORPHIC_ITEMS = create("metamorphic_items");
        public static final TagKey<Item> SEDIMENTARY_ITEMS = create("sedimentary_items");
        public static final TagKey<Item> JARS = create("jars");
        public static final TagKey<Item> SEALED_JARS = create("sealed_jars");
        public static final TagKey<Item> UNSEALED_JARS = create("unsealed_jars");
        public static final TagKey<Item> ALL_WINDMILL_BLADES = create("all_windmill_blades");
        public static final TagKey<Item> SEWING_DARK_CLOTH = create("sewing_dark_cloth");
        public static final TagKey<Item> SEWING_LIGHT_CLOTH = create("sewing_light_cloth");
        public static final TagKey<Item> SEWING_NEEDLES = create("sewing_needles");
        public static final TagKey<Item> EMPTY_JAR_WITH_LID = create("empty_jar_with_lid");

        public static TagKey<Item> mobEquipmentSlotTag(EquipmentSlot slot)
        {
            return switch (slot)
                {
                    case MAINHAND -> TFCTags.Items.MOB_MAINHAND_WEAPONS;
                    case OFFHAND -> TFCTags.Items.MOB_OFFHAND_WEAPONS;
                    case FEET -> TFCTags.Items.MOB_FEET_ARMOR;
                    case LEGS -> TFCTags.Items.MOB_LEG_ARMOR;
                    case CHEST -> TFCTags.Items.MOB_CHEST_ARMOR;
                    case HEAD -> TFCTags.Items.MOB_HEAD_ARMOR;
                };
        }

        private static TagKey<Item> create(String id)
        {
            return TagKey.create(Registries.ITEM, Helpers.identifier(id));
        }
    }

    public static class Entities
    {
        public static final TagKey<EntityType<?>> TURTLE_FRIENDS = create("turtle_friends");
        public static final TagKey<EntityType<?>> SPAWNS_ON_COLD_BLOCKS = create("spawns_on_cold_blocks"); // if ice is a valid spawn
        public static final TagKey<EntityType<?>> BUBBLE_COLUMN_IMMUNE = create("bubble_column_immune");
        public static final TagKey<EntityType<?>> NEEDS_LARGE_FISHING_BAIT = create("needs_large_fishing_bait");
        public static final TagKey<EntityType<?>> HUNTS_LAND_PREY = create("hunts_land_prey");
        public static final TagKey<EntityType<?>> HUNTED_BY_LAND_PREDATORS = create("hunted_by_land_predators");
        public static final TagKey<EntityType<?>> OCEAN_PREDATORS = create("ocean_predators");
        public static final TagKey<EntityType<?>> HUNTED_BY_OCEAN_PREDATORS = create("hunted_by_ocean_predators");
        public static final TagKey<EntityType<?>> VANILLA_MONSTERS = create("vanilla_monsters");
        public static final TagKey<EntityType<?>> DEALS_SLASHING_DAMAGE = create("deals_slashing_damage");
        public static final TagKey<EntityType<?>> DEALS_PIERCING_DAMAGE = create("deals_piercing_damage");
        public static final TagKey<EntityType<?>> DEALS_CRUSHING_DAMAGE = create("deals_crushing_damage");
        public static final TagKey<EntityType<?>> HORSES = create("horses");
        public static final TagKey<EntityType<?>> DESTROYED_BY_LEAVES = create("destroyed_by_leaves");
        public static final TagKey<EntityType<?>> LEASHABLE_WILD_ANIMALS = create("leashable_wild_animals"); // entities that can be leashed that aren't normally leashable. default empty
        public static final TagKey<EntityType<?>> PESTS = create("pests"); // spawned during infestations
        public static final TagKey<EntityType<?>> HUNTED_BY_CATS = create("hunted_by_cats");
        public static final TagKey<EntityType<?>> HUNTED_BY_DOGS = create("hunted_by_dogs");
        public static final TagKey<EntityType<?>> SMALL_FISH = create("small_fish");

        private static TagKey<EntityType<?>> create(String id)
        {
            return TagKey.create(Registries.ENTITY_TYPE, Helpers.identifier(id));
        }
    }

    public static class Biomes
    {
        public static final TagKey<Biome> HAS_PREDICTABLE_WINDS = create("has_predictable_winds");

        private static TagKey<Biome> create(String id)
        {
            return TagKey.create(Registries.BIOME, Helpers.identifier(id));
        }
    }
}