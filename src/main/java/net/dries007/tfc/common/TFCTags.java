/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.Tags;

import net.dries007.tfc.ForgeEventHandler;
import net.dries007.tfc.client.render.blockentity.BowlBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.JarsBlockEntityRenderer;
import net.dries007.tfc.client.render.blockentity.TripHammerBlockEntityRenderer;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.common.recipes.ingredients.FluidContentIngredient;
import net.dries007.tfc.config.ServerConfig;
import net.dries007.tfc.mixin.MobMixin;
import net.dries007.tfc.util.Helpers;

public class TFCTags
{
    public static class Blocks
    {
        public static final TagKey<Block> PLANTS = tag("plants"); // for some decoration placement
        public static final TagKey<Block> SINGLE_BLOCK_REPLACEABLE = tag("single_block_replaceable"); // blocks that features can safely destroy
        public static final TagKey<Block> HALOPHYTE = tag("halophyte"); // Used for all saltwater plants that generate in salt marshes
        public static final TagKey<Block> KELP_TREE = tag("kelp_tree");
        public static final TagKey<Block> KELP_BRANCH = tag("kelp_branch");
        public static final TagKey<Block> SPREADING_BUSH = tag("spreading_bush");
        public static final TagKey<Block> ANY_SPREADING_BUSH = tag("any_spreading_bush");
        public static final TagKey<Block> THORNY_BUSHES = tag("thorny_bushes"); // Bushes that damage entities walking through them
        public static final TagKey<Block> FRUIT_TREE_BRANCH = tag("fruit_tree_branch");
        public static final TagKey<Block> FRUIT_TREE_LEAVES = tag("fruit_tree_leaves");
        public static final TagKey<Block> FRUIT_TREE_SAPLING = tag("fruit_tree_sapling");
        public static final TagKey<Block> THATCH_BED_THATCH = tag("thatch_bed_thatch");
        public static final TagKey<Block> SNOW = tag("snow"); // Blocks that cover grass with snow.
        public static final TagKey<Block> CAN_BE_SNOW_PILED = tag("can_be_snow_piled"); // Blocks that can be replaced with snow piles
        public static final TagKey<Block> CAN_BE_ICE_PILED = tag("can_be_ice_piled"); // Blocks that need to be replaced with ice piles, either from ice freezing below it, or ice freezing inside the block itself.
        public static final TagKey<Block> LIT_BY_DROPPED_TORCH = tag("lit_by_dropped_torch"); // Causes dropped torches to start fires on them
        public static final TagKey<Block> CONVERTS_TO_HUMUS = tag("converts_to_humus");
        public static final TagKey<Block> POWDER_SNOW_REPLACEABLE = tag("powder_snow_replaceable"); // on feature gen, can be replaced by powder snow
        public static final TagKey<Block> RABBIT_RAIDABLE = tag("rabbit_raidable"); // rabbits will break it
        public static final TagKey<Block> FOX_RAIDABLE = tag("fox_raidable"); // foxes will eat the berries. only applies to seasonal plant blocks
        public static final TagKey<Block> PET_SITS_ON = tag("pet_sits_on"); // pet prefers to sit here.
        public static final TagKey<Block> MINECART_HOLDABLE = tag("minecart_holdable"); // blocks that carts will hold. this is a block tag to ensure it can render in the cart.
        public static final TagKey<Block> MONSTER_SPAWNS_ON = tag("monster_spawns_on");
        public static final TagKey<Block> BOTTOM_SUPPORT_ACCEPTED = tag("bottom_support_accepted"); // ignored by devices for bottom support
        public static final TagKey<Block> TIDE_POOL_BLOCKS = tag("tide_pool_blocks"); // groundcover blocks that spawn in tide pools
        public static final TagKey<Block> EXPLOSION_PROOF = tag("explosion_proof");
        public static final TagKey<Block> POWDERKEG_BREAKING_BLOCKS = tag("powderkeg_breaking_blocks");
        public static final TagKey<Block> KAOLIN_CLAY_REPLACEABLE = tag("kaolin_clay_replaceable");

        // ===== Data Generated ===== //

        /** If the block can trigger collapses to start nearby when mined */
        public static final TagKey<Block> CAN_TRIGGER_COLLAPSE = tag("can_trigger_collapse");
        /** If the block can form the epicenter of a collapse */
        public static final TagKey<Block> CAN_START_COLLAPSE = tag("can_start_collapse");
        /** If the block can collapse, by default turning into itself. Do not check this, use {@link CollapseRecipe#canCollapse} */
        public static final TagKey<Block> CAN_COLLAPSE = tag("can_collapse");

        /** If the block can landslide, by default turning into itself. Do not check this, use {@link LandslideRecipe#canLandslide} */
        public static final TagKey<Block> CAN_LANDSLIDE = tag("can_landslide");

        /** Non-full blocks that count as full blocks for the purposes of landslide side support check */
        public static final TagKey<Block> SUPPORTS_LANDSLIDE = tag("supports_landslide");
        /** Blocks that don't count as supporting the block above for the purposes of collapse start checks */
        public static final TagKey<Block> NOT_SOLID_SUPPORTING = tag("not_solid_supporting");
        /** Tags for toughness of materials w.r.t falling blocks */
        public static final TagKey<Block> TOUGHNESS_1 = tag("toughness_1");
        public static final TagKey<Block> TOUGHNESS_2 = tag("toughness_2");
        public static final TagKey<Block> TOUGHNESS_3 = tag("toughness_3");
        /** When surrounded on all six sides by air, this block will break and drop itself */
        public static final TagKey<Block> BREAKS_WHEN_ISOLATED = tag("breaks_when_isolated");
        /** Leaf blocks that spawn leaf particles in the fall */
        public static final TagKey<Block> SEASONAL_LEAVES = tag("seasonal_leaves");
        /**
         * Used in world generation to select random coral blocks. Seperate from the vanilla tags because we don't want to include vanilla coral blocks
         * @see BlockTags#CORALS
         * @see BlockTags#WALL_CORALS
         */
        public static final TagKey<Block> SALT_WATER_CORAL_PLANTS = tag("salt_water_coral_plants");
        public static final TagKey<Block> SALT_WATER_CORALS = tag("salt_water_corals");
        public static final TagKey<Block> SALT_WATER_WALL_CORALS = tag("salt_water_wall_corals");

        // Tags.Blocks.STONES includes raw + hardened
        public static final TagKey<Block> STONES_RAW = commonTag("stones/raw");
        public static final TagKey<Block> STONES_HARDENED = commonTag("stones/hardened");
        public static final TagKey<Block> STONES_SMOOTH = commonTag("stones/smooth");
        public static final TagKey<Block> STONES_SMOOTH_SLABS = commonTag("stones/smooth_slabs");
        public static final TagKey<Block> STONES_SPIKE = commonTag("stones/spike");
        public static final TagKey<Block> STONES_PRESSURE_PLATES = commonTag("stones/pressure_plate");
        /** Includes normal and mossy loose stones */
        public static final TagKey<Block> STONES_LOOSE = commonTag("stones/loose");
        /** Common solid stone blocks, including stone, cobble, bricks, smooth, plus non-stone bricks */
        public static final TagKey<Block> INSULATION = tag("insulation");

        public static final TagKey<Block> LAMPS = tag("lamps");
        public static final TagKey<Block> ANVILS = tag("anvils");

        /** Logs that can be cut down in entire trees via axes */
        public static final TagKey<Block> LOGS_THAT_LOG = tag("logs_that_log");
        public static final TagKey<Block> WORKBENCHES = tag("workbenches");
        public static final TagKey<Block> SUPPORT_BEAMS = tag("support_beams");

        /** Blocks that are valid for covering a charcoal pit, in addition to the default non-flammable solid blocks */
        public static final TagKey<Block> CHARCOAL_PIT_INSULATION = tag("charcoal_pit_insulation");
        /** Blocks that are valid to surround a charcoal forge */
        public static final TagKey<Block> CHARCOAL_FORGE_INSULATION = tag("charcoal_forge_insulation");
        /** Blocks that are valid to obscure a charcoal forge without obscuring chimney access i.e. crucibles*/
        public static final TagKey<Block> CHARCOAL_FORGE_INVISIBLE = tag("charcoal_forge_invisible");
        /** Blocks that are valid for a bloomery multiblock structure */
        public static final TagKey<Block> BLOOMERY_INSULATION = tag("bloomery_insulation");
        /** Blocks that are valid for a blast furnace multiblock structure */
        public static final TagKey<Block> BLAST_FURNACE_INSULATION = tag("blast_furnace_insulation");
        /** Blocks that can be used for a scraping recipe */
        public static final TagKey<Block> SCRAPING_SURFACE = tag("scraping_surface");
        /** Blocks that you can pour hot glass on, to create glass panes */
        public static final TagKey<Block> GLASS_POURING_TABLE = tag("glass_pouring_table");
        /** Block that you can pour hot glass in, to create glass blocks*/
        public static final TagKey<Block> GLASS_BASIN_BLOCKS = tag("glass_basin_blocks");

        /** Both these are empty by default, but provided for potential compatibility */
        public static final TagKey<Block> MINEABLE_WITH_PROPICK = tag("mineable/propick");
        public static final TagKey<Block> MINEABLE_WITH_CHISEL = tag("mineable/chisel");
        /** Includes logs */
        public static final TagKey<Block> MINEABLE_WITH_HAMMER = tag("mineable/hammer");
        /** Both knives and scythes inherit from the vanilla hoe tag, which we repurpose as "sharp tools" */
        public static final TagKey<Block> MINEABLE_WITH_KNIFE = tag("mineable/knife");
        public static final TagKey<Block> MINEABLE_WITH_SCYTHE = tag("mineable/scythe");
        /** Includes glass blocks only */
        public static final TagKey<Block> MINEABLE_WITH_GLASS_SAW = tag("mineable/glass_saw");
        /** Unique tag used only for TFC hoes, which doesn't inherit the vanilla hoe tag */
        public static final TagKey<Block> MINEABLE_WITH_HOE = tag("mineable/hoe");

        public static final TagKey<Block> PROSPECTABLE = tag("prospectable"); // can be found with the prospector pick

        /** The vanilla tag {@link BlockTags#DIRT} contains all dirt, grass, and mud. These tags mostly only contain the respective TFC blocks and identical ones */
        public static final TagKey<Block> DIRT = tag("dirt");
        public static final TagKey<Block> GRASS = tag("grass");
        /** Used for non-wild crop growth. */
        public static final TagKey<Block> FARMLANDS = tag("farmlands");
        public static final TagKey<Block> PATHS = tag("paths");
        public static final TagKey<Block> MUD = tag("mud");
        public static final TagKey<Block> MUD_BRICKS = tag("mud_bricks");
        /** Includes kaolin clay, both grass and clay block variants */
        public static final TagKey<Block> CLAYS = tag("clays");
        public static final TagKey<Block> KAOLIN_CLAYS = tag("clays/kaolin");

        public static final TagKey<Block> TREE_GROWS_ON = tag("tree_grows_on"); // Used for tree growth
        public static final TagKey<Block> WILD_CROP_GROWS_ON = tag("wild_crop_grows_on"); // Used for wild crops
        public static final TagKey<Block> SPREADING_FRUIT_GROWS_ON = tag("spreading_fruit_grows_on"); // pumpkins and melons will grow here
        public static final TagKey<Block> BUSH_PLANTABLE_ON = tag("bush_plantable_on"); // Used for plant placement
        public static final TagKey<Block> GRASS_PLANTABLE_ON = tag("grass_plantable_on"); // Grass has a slightly expanded placing allowance
        public static final TagKey<Block> SEA_BUSH_PLANTABLE_ON = tag("sea_bush_plantable_on"); // Used for sea plant placement
        public static final TagKey<Block> HALOPHYTE_PLANTABLE_ON = tag("halophyte_plantable_on"); // Used for halophyte placement
        public static final TagKey<Block> CREEPING_STONE_PLANTABLE_ON = tag("creeping_stone_plantable_on");

        /** Used in the Field Guide to display indicators in a multiblock */
        public static final TagKey<Block> CLAY_INDICATORS = tag("clay_indicators");

        private static TagKey<Block> tag(String name)
        {
            return TagKey.create(Registries.BLOCK, Helpers.identifier(name));
        }

        private static TagKey<Block> commonTag(String name)
        {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Fluids
    {
        /**
         * These vanilla tags, historically, were required for <strong>any</strong> fluid behavior, and so may still be used that way. Do not rely on them only containing water or lava, instead, reference the below tags for more specific fluid grouping.
         */
        public static final TagKey<Fluid> WATER_LIKE = FluidTags.WATER;
        public static final TagKey<Fluid> LAVA_LIKE = FluidTags.LAVA;

        // ===== Data Generated ===== //

        /**
         * These water tags contain only source fluids of given water variants.
         * <ul>
         *     <li>{@code ANY_FRESH_WATER} contains only fresh water (vanilla), and river water</li>
         *     <li>{@code ANY_INFINITE_WATER} contains that, plus salt water and spring water</li>
         * </ul>
         */
        public static final TagKey<Fluid> ANY_FRESH_WATER = create("any_fresh_water");
        public static final TagKey<Fluid> ANY_INFINITE_WATER = create("any_infinite_water");

        public static final TagKey<Fluid> FRESH_WATER = create("fresh_water");
        public static final TagKey<Fluid> INFINITE_WATER = create("infinite_water");

        public static final TagKey<Fluid> MIXABLE = create("mixable");
        /** Fluids that work to hydrate farmland, berry bushes, or other plants that query hydration */
        public static final TagKey<Fluid> HYDRATING = create("hydrating");

        public static final TagKey<Fluid> ALCOHOLS = create("alcohols");
        public static final TagKey<Fluid> MOLTEN_METALS = create("molten_metals");

        public static final TagKey<Fluid> DRINKABLES = create("drinkables");
        public static final TagKey<Fluid> INGREDIENTS = create("ingredients");

        public static final TagKey<Fluid> USABLE_IN_POT = create("usable_in_pot");
        public static final TagKey<Fluid> USABLE_IN_JUG = create("usable_in_jug");
        public static final TagKey<Fluid> USABLE_IN_WOODEN_BUCKET = create("usable_in_wooden_bucket");
        public static final TagKey<Fluid> USABLE_IN_RED_STEEL_BUCKET = create("usable_in_red_steel_bucket");
        public static final TagKey<Fluid> USABLE_IN_BLUE_STEEL_BUCKET = create("usable_in_blue_steel_bucket");
        public static final TagKey<Fluid> USABLE_IN_BARREL = create("usable_in_barrel");
        public static final TagKey<Fluid> USABLE_IN_SCRIBING_TABLE = create("usable_in_scribing_table");
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
        // todo: figure out if we really want to support these, or replace with the tool action and say to hell with fire charges
        public static final TagKey<Item> STARTS_FIRES_WITH_DURABILITY = tag("starts_fires_with_durability");
        public static final TagKey<Item> STARTS_FIRES_WITH_ITEMS = tag("starts_fires_with_items");
        // todo: probably remove hand wheel, no plan to include it
        public static final TagKey<Item> HAND_WHEEL = tag("hand_wheel");
        // todo: now we have interface mixins, use an inject into IItemStackExtension to make this actually work?
        // And then, really, this doesn't need to be in TFC anymore >.>
        public static final TagKey<Item> PIGLIN_BARTERING_INGOTS = tag("piglin_bartering_ingots"); // ingots that piglins will trade for.


        // ===== Data Generated ===== //

        // Foods
        public static final TagKey<Item> FOODS = Tags.Items.FOODS;
        public static final TagKey<Item> FRUITS = Tags.Items.FOODS_FRUIT;
        public static final TagKey<Item> VEGETABLES = Tags.Items.FOODS_VEGETABLE;
        public static final TagKey<Item> RAW_MEATS = Tags.Items.FOODS_RAW_MEAT;
        public static final TagKey<Item> COOKED_MEATS = Tags.Items.FOODS_COOKED_MEAT;
        public static final TagKey<Item> MEATS = commonTag("foods/meat");
        public static final TagKey<Item> RAW_FISH = Tags.Items.FOODS_RAW_FISH;
        public static final TagKey<Item> COOKED_FISH = Tags.Items.FOODS_COOKED_FISH;
        public static final TagKey<Item> FISH = commonTag("foods/fish");
        public static final TagKey<Item> FLOUR = commonTag("foods/flour");
        public static final TagKey<Item> DOUGH = commonTag("foods/dough");
        public static final TagKey<Item> GRAINS = commonTag("foods/grain");
        public static final TagKey<Item> BREAD = commonTag("foods/bread");
        public static final TagKey<Item> DAIRY = commonTag("foods/dairy");
        public static final TagKey<Item> SALADS = commonTag("foods/salad");
        public static final TagKey<Item> SOUPS = Tags.Items.FOODS_SOUP;
        public static final TagKey<Item> PRESERVES = tag("foods/preserves");
        public static final TagKey<Item> SEALED_PRESERVES = tag("foods/sealed_preserves");
        public static final TagKey<Item> SWEETENERS = tag("sweeteners");
        public static final TagKey<Item> BOWLS = commonTag("bowls");
        /** Bowls that interact with soup pot recipes */
        public static final TagKey<Item> SOUP_BOWLS = tag("soup_bowls");
        /** Bowls that interact with salad recipes */
        public static final TagKey<Item> SALAD_BOWLS = tag("salad_bowls");
        public static final TagKey<Item> USABLE_IN_SALAD = tag("usable_in_salad");
        public static final TagKey<Item> USABLE_IN_SOUP = tag("usable_in_soup");
        public static final TagKey<Item> USABLE_IN_SANDWICH = tag("usable_in_sandwich");
        public static final TagKey<Item> USABLE_IN_JAM_SANDWICH = tag("usable_in_jam_sandwich");
        public static final TagKey<Item> CAN_BE_SALTED = tag("foods/can_be_salted");

        // Animal Foods
        public static final TagKey<Item> PIG_FOOD = tag("pig_food");
        public static final TagKey<Item> COW_FOOD = tag("cow_food");
        public static final TagKey<Item> YAK_FOOD = tag("yak_food");
        public static final TagKey<Item> GOAT_FOOD = tag("goat_food");
        public static final TagKey<Item> ALPACA_FOOD = tag("alpaca_food");
        public static final TagKey<Item> SHEEP_FOOD = tag("sheep_food");
        public static final TagKey<Item> MUSK_OX_FOOD = tag("musk_ox_food");
        public static final TagKey<Item> CHICKEN_FOOD = tag("chicken_food");
        public static final TagKey<Item> DUCK_FOOD = tag("duck_food");
        public static final TagKey<Item> QUAIL_FOOD = tag("quail_food");
        public static final TagKey<Item> DONKEY_FOOD = tag("donkey_food");
        public static final TagKey<Item> MULE_FOOD = tag("mule_food");
        public static final TagKey<Item> HORSE_FOOD = tag("horse_food");
        public static final TagKey<Item> CAT_FOOD = tag("cat_food");
        public static final TagKey<Item> DOG_FOOD = tag("dog_food");
        public static final TagKey<Item> PENGUIN_FOOD = tag("penguin_food");
        public static final TagKey<Item> TURTLE_FOOD = tag("turtle_food");
        public static final TagKey<Item> FROG_FOOD = tag("frog_food");
        public static final TagKey<Item> RABBIT_FOOD = tag("rabbit_food");

        // Compost
        public static final TagKey<Item> COMPOST_GREENS = tag("compost_greens");
        public static final TagKey<Item> COMPOST_GREENS_LOW = tag("compost_greens/low");
        public static final TagKey<Item> COMPOST_GREENS_MEDIUM = tag("compost_greens/medium");
        public static final TagKey<Item> COMPOST_GREENS_HIGH = tag("compost_greens/high");
        public static final TagKey<Item> COMPOST_BROWNS = tag("compost_browns");
        public static final TagKey<Item> COMPOST_BROWNS_LOW = tag("compost_browns/low");
        public static final TagKey<Item> COMPOST_BROWNS_MEDIUM = tag("compost_browns/medium");
        public static final TagKey<Item> COMPOST_BROWNS_HIGH = tag("compost_browns/high");
        public static final TagKey<Item> COMPOST_POISONS = tag("compost_poisons");

        // Fishing
        public static final TagKey<Item> SMALL_FISHING_BAIT = tag("small_fishing_bait");
        public static final TagKey<Item> LARGE_FISHING_BAIT = tag("large_fishing_bait");
        public static final TagKey<Item> HOLDS_SMALL_FISHING_BAIT = tag("holds_small_fishing_bait");
        public static final TagKey<Item> HOLDS_LARGE_FISHING_BAIT = tag("holds_large_fishing_bait");

        public static final TagKey<Item> PLANTS = tag("plants");
        public static final TagKey<Item> WILD_CROPS = tag("wild_crops");

        // Colored Items
        public static final TagKey<Item> COLORED_WOOL = tag("colored_wool");
        public static final TagKey<Item> COLORED_CARPETS = tag("colored_carpets");
        public static final TagKey<Item> COLORED_BEDS = tag("colored_beds");
        public static final TagKey<Item> COLORED_BANNERS = tag("colored_banners");
        public static final TagKey<Item> COLORED_TERRACOTTA = tag("colored_terracotta");
        public static final TagKey<Item> COLORED_GLAZED_TERRACOTTA = tag("colored_glazed_terracotta");
        public static final TagKey<Item> COLORED_SHULKER_BOXES = tag("colored_shulker_boxes");
        public static final TagKey<Item> COLORED_CONCRETE_POWDER = tag("colored_concrete_powder");
        public static final TagKey<Item> COLORED_CANDLES = tag("colored_candles");
        public static final TagKey<Item> COLORED_WINDMILL_BLADES = tag("colored_windmill_blades");
        public static final TagKey<Item> COLORED_RAW_ALABASTER = tag("colored_raw_alabaster");
        public static final TagKey<Item> COLORED_ALABASTER_BRICKS = tag("colored_alabaster_bricks");
        public static final TagKey<Item> COLORED_POLISHED_ALABASTER = tag("colored_polished_alabaster");
        public static final TagKey<Item> COLORED_VESSELS = tag("colored_vessels");
        public static final TagKey<Item> COLORED_LARGE_VESSELS = tag("colored_large_vessels");

        // Collections of blocks / items
        public static final TagKey<Item> TOOL_RACKS = tag("tool_racks");
        public static final TagKey<Item> SCRIBING_TABLES = tag("scribing_tables");
        public static final TagKey<Item> SEWING_TABLES = tag("sewing_tables");
        public static final TagKey<Item> SLUICES = tag("sluices");
        public static final TagKey<Item> LOOMS = tag("looms");
        /** Tag for only TFC barrels, unlike {@link Tags.Items#BARRELS} which includes vanilla type barrels */
        public static final TagKey<Item> BARRELS = tag("barrels");
        public static final TagKey<Item> VESSELS = tag("vessels");
        public static final TagKey<Item> UNFIRED_VESSELS = tag("unfired_vessels");
        public static final TagKey<Item> FIRED_VESSELS = tag("fired_vessels");
        public static final TagKey<Item> LARGE_VESSELS = tag("large_vessels");
        public static final TagKey<Item> UNFIRED_LARGE_VESSELS = tag("unfired_large_vessels");
        public static final TagKey<Item> FIRED_LARGE_VESSELS = tag("fired_large_vessels");
        public static final TagKey<Item> MOLDS = tag("molds");
        public static final TagKey<Item> UNFIRED_MOLDS = tag("unfired_molds");
        public static final TagKey<Item> FIRED_MOLDS = tag("fired_molds");
        public static final TagKey<Item> LAMPS = tag(Blocks.LAMPS);
        public static final TagKey<Item> MINECARTS = commonTag("minecarts");
        public static final TagKey<Item> ORE_PIECES = tag("ore_pieces");
        public static final TagKey<Item> SMALL_ORE_PIECES = tag("small_ore_pieces");
        public static final TagKey<Item> WATER_WHEELS = tag("water_wheels");
        public static final TagKey<Item> WINDMILL_BLADES = tag("windmill_blades");
        public static final TagKey<Item> AXLES = tag("axles");
        public static final TagKey<Item> GEAR_BOXES = tag("gear_boxes");
        public static final TagKey<Item> CLUTCHES = tag("clutches");
        public static final TagKey<Item> SUPPORT_BEAMS = tag("support_beams");
        public static final TagKey<Item> LUMBER = tag("lumber");

        // Common Tags
        public static final TagKey<Item> DOUBLE_INGOTS = commonTag("double_ingots");
        public static final TagKey<Item> SHEETS = commonTag("sheets");
        public static final TagKey<Item> DOUBLE_SHEETS = commonTag("double_sheets");
        public static final TagKey<Item> DOUBLE_SHEETS_ANY_BRONZE = tag("double_sheets/any_bronze");

        /**
         * <h3>Tools</h3>
         * Other tags are defined in {@link ItemTags} for vanilla tool types, and common tag conventions on {@link Tags.Items}. We add
         * our own tool types here, which are added to the root {@code c:tools} tag, as well as adding to any conventional and vanilla tags.
         * <p>
         * If defining in-code behavior, consider using a {@link ItemAbility} instead
         */
        public static final TagKey<Item> TOOLS_HAMMER = commonTag("tools/hammer");
        public static final TagKey<Item> TOOLS_SAW = commonTag("tools/saw");
        public static final TagKey<Item> TOOLS_SCYTHE = commonTag("tools/scythe");
        public static final TagKey<Item> TOOLS_KNIFE = commonTag("tools/knife");
        public static final TagKey<Item> TOOLS_CHISEL = commonTag("tools/chisel");
        public static final TagKey<Item> TOOLS_GLASSWORKING = commonTag("tools/glassworking");
        /** Blowpipes without glass batches */
        public static final TagKey<Item> TOOLS_BLOWPIPE = commonTag("tools/blowpipe");
        /** Used in loot tables to prevent dropping of certain items */
        public static final TagKey<Item> TOOLS_SHARP = tag("tools/sharp");

        // Damage Types
        public static final TagKey<Item> DEALS_SLASHING_DAMAGE = tag("deals_slashing_damage");
        public static final TagKey<Item> DEALS_PIERCING_DAMAGE = tag("deals_piercing_damage");
        public static final TagKey<Item> DEALS_CRUSHING_DAMAGE = tag("deals_crushing_damage");

        // Glass
        public static final TagKey<Item> GLASS_BATCHES = tag("glass_batches");
        public static final TagKey<Item> GLASS_BATCHES_T2 = tag("glass_batches_tier_2");
        public static final TagKey<Item> GLASS_BATCHES_T3 = tag("glass_batches_tier_3");
        public static final TagKey<Item> GLASS_BATCHES_NOT_T1 = tag("glass_batches_not_tier_1");
        /** Blowpipes with glass batches */
        public static final TagKey<Item> GLASS_BLOWPIPES = tag("glass_blowpipes");
        /** The union of {@link #TOOLS_BLOWPIPE} and {@link #GLASS_BLOWPIPES} */
        public static final TagKey<Item> BLOWPIPES = tag("all_blowpipes");
        public static final TagKey<Item> GLASS_POWDERS = tag("glass_powders");
        public static final TagKey<Item> GLASS_BOTTLES = tag("glass_bottles");
        public static final TagKey<Item> GLASS_POTASH = tag("glass_potash");

        public static final TagKey<Item> SILICA_SAND = commonTag("sands/silica");
        public static final TagKey<Item> OLIVINE_SAND = commonTag("sands/olivine");
        public static final TagKey<Item> HEMATITIC_SAND = commonTag("sands/hematitic");
        public static final TagKey<Item> VOLCANIC_SAND = commonTag("sands/volcanic");

        // Misc. Crafting Ingredients
        public static final TagKey<Item> HIGH_QUALITY_CLOTH = tag("high_quality_cloth");
        public static final TagKey<Item> GEM_POWDERS = tag("gem_powders");
        public static final TagKey<Item> BOOKS = commonTag("books");
        /** Used by patchouli */
        public static final TagKey<Item> ORE_DEPOSITS = tag("ore_deposits");

        // Device Required Items
        public static final TagKey<Item> FIREPIT_KINDLING = tag("firepit_kindling");
        public static final TagKey<Item> FIREPIT_STICKS = tag("firepit_sticks");
        public static final TagKey<Item> FIREPIT_LOGS = tag("firepit_logs");
        public static final TagKey<Item> LOG_PILE_LOGS = tag("log_pile_logs");
        public static final TagKey<Item> PIT_KILN_STRAW = tag("pit_kiln_straw");
        public static final TagKey<Item> PIT_KILN_LOGS = tag("pit_kiln_logs");
        /** Axes that are 60% efficient at destroying logs */
        public static final TagKey<Item> INEFFICIENT_LOGGING_AXES = tag("inefficient_logging_axes");
        /** Items (sticks) that can be right-clicked on a lit torch to turn themselves into a torch */
        public static final TagKey<Item> CAN_BE_LIT_ON_TORCH = tag("can_be_lit_on_torch");
        /** Individual tags, used by knapping types, that define what items can be knapped */
        public static final TagKey<Item> ROCK_KNAPPING = tag("rock_knapping");
        public static final TagKey<Item> CLAY_KNAPPING = tag("clay_knapping");
        public static final TagKey<Item> FIRE_CLAY_KNAPPING = tag("fire_clay_knapping");
        public static final TagKey<Item> LEATHER_KNAPPING = tag("leather_knapping");
        public static final TagKey<Item> GOAT_HORN_KNAPPING = tag("goat_horn_knapping");
        /** Handstones that can be used in the quern */
        public static final TagKey<Item> QUERN_HANDSTONES = tag("quern_handstones");
        public static final TagKey<Item> SEWING_DARK_CLOTH = tag("sewing_dark_cloth");
        public static final TagKey<Item> SEWING_LIGHT_CLOTH = tag("sewing_light_cloth");
        public static final TagKey<Item> SEWING_NEEDLES = tag("sewing_needles");
        public static final TagKey<Item> FIREPIT_FUEL = tag("firepit_fuel");
        public static final TagKey<Item> FORGE_FUEL = tag("forge_fuel");
        public static final TagKey<Item> BLAST_FURNACE_FUEL = tag("blast_furnace_fuel");
        public static final TagKey<Item> BLAST_FURNACE_SHEETS = tag("blast_furnace_sheets");
        public static final TagKey<Item> BLAST_FURNACE_TUYERES = tag("blast_furnace_tuyeres");
        public static final TagKey<Item> TOOL_RACK_TOOLS = tag("usable_on_tool_rack");
        public static final TagKey<Item> POWDER_KEG_FUEL = tag("usable_in_powder_keg");
        /**
         * Items that can be used in a trip hammer. This is a technical tag, and it must match the items in
         * {@link TripHammerBlockEntityRenderer#HAMMER_TEXTURES}
         */
        public static final TagKey<Item> TRIP_HAMMERS = tag("trip_hammers");
        public static final TagKey<Item> WELDING_FLUX = tag("welding_flux");
        public static final TagKey<Item> SCRIBING_INK = tag("scribing_ink");
        public static final TagKey<Item> THATCH_BED_HIDES = tag("thatch_bed_hides");
        /**
         * Jar items that can be placed on a shelf block. This is a technical tag, and it must match the items in
         * {@link JarsBlockEntityRenderer#MODELS}
         */
        public static final TagKey<Item> SHELF_JARS = tag("shelf_jars");
        /**
         * Powders that can be placed in a bowl. This is a technical tag, and it must match the items in
         * {@link BowlBlockEntityRenderer#TEXTURES}
         */
        public static final TagKey<Item> BOWL_POWDERS = tag("bowl_powders");
        /** Items used in scraping recipes that can wax a surface */
        public static final TagKey<Item> SCRAPING_WAXES = tag("waxes");
        /**
         * Mapping of {@link RockCategory} to tags that are used for rock knapping ingredients
         */
        public static final Map<RockCategory, TagKey<Item>> STONES_LOOSE_CATEGORY = Helpers.mapOf(RockCategory.class, type -> tag("stones/loose/" + type.getSerializedName()));

        // Technical Tags
        /** Containers that can be filled, in the display values for {@link FluidContentIngredient} */
        public static final TagKey<Item> FLUID_ITEM_INGREDIENT_EMPTY_CONTAINERS = tag("fluid_item_ingredient_empty_containers");
        /**
         * Items that mobs will not spawn with in hand.
         * @see ServerConfig#enableVanillaMobsSpawningWithVanillaEquipment
         */
        public static final TagKey<Item> DISABLED_MONSTER_HELD_ITEMS = tag("disabled_monster_held_items");
        /** Items that a fox can spawn with in the mouth */
        public static final TagKey<Item> FOX_SPAWNS_WITH = tag("fox_spawns_with");
        /** Chests, barrels, etc. that can be used as a horse saddlebag */
        public static final TagKey<Item> CARRIED_BY_HORSE = tag("carried_by_horse");


        /**
         * Armor that mobs may randomly spawn with. This replaces vanilla armors if present, and will replace armor if not present
         * @see MobMixin
         */
        public static final TagKey<Item> MOB_FEET_ARMOR = tag("mob_feet_armor");
        public static final TagKey<Item> MOB_LEG_ARMOR = tag("mob_leg_armor");
        public static final TagKey<Item> MOB_CHEST_ARMOR = tag("mob_chest_armor");
        public static final TagKey<Item> MOB_HEAD_ARMOR = tag("mob_head_armor");

        /** Items that skeletons are set to hold, forced by {@link ForgeEventHandler#onEntityJoinLevel} Includes javelins and bows */
        public static final TagKey<Item> SKELETON_WEAPONS = tag("skeleton_weapons");

        // Block Tags - Stone
        public static final TagKey<Item> STONES_RAW = tag(Blocks.STONES_RAW);
        public static final TagKey<Item> STONES_HARDENED = tag(Blocks.STONES_HARDENED);
        public static final TagKey<Item> STONES_SMOOTH = tag(Blocks.STONES_SMOOTH);
        public static final TagKey<Item> STONES_SMOOTH_SLABS = tag(Blocks.STONES_SMOOTH_SLABS);
        public static final TagKey<Item> STONES_PRESSURE_PLATES = tag(Blocks.STONES_PRESSURE_PLATES);
        public static final TagKey<Item> STONES_LOOSE = tag(Blocks.STONES_LOOSE);

        // Block Tags - Earth
        public static final TagKey<Item> DIRT = tag(Blocks.DIRT);
        public static final TagKey<Item> GRASS = tag(Blocks.GRASS);
        public static final TagKey<Item> MUD = tag(Blocks.MUD);
        public static final TagKey<Item> MUD_BRICKS = tag(Blocks.MUD_BRICKS);

        // Block Tags - Misc
        public static final TagKey<Item> ANVILS = tag(Blocks.ANVILS);
        public static final TagKey<Item> WORKBENCHES = tag(Blocks.WORKBENCHES);


        private static TagKey<Item> tag(TagKey<Block> blockTag)
        {
            return TagKey.create(Registries.ITEM, blockTag.location());
        }

        private static TagKey<Item> tag(String name)
        {
            return TagKey.create(Registries.ITEM, Helpers.identifier(name));
        }

        private static TagKey<Item> commonTag(String name)
        {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
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