/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.TFCCreativeTabs;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.TFCTiers;
import net.dries007.tfc.common.blocks.Gem;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.aquatic.Fish;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidId;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.accessor.ItemAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.Metal;
import net.dries007.tfc.util.registry.IdHolder;
import net.dries007.tfc.util.registry.RegistryHolder;

import static net.dries007.tfc.TerraFirmaCraft.*;

/**
 * Collection of all TFC items.
 * Organized by {@link TFCCreativeTabs}
 * Unused is as the registry object fields themselves may be unused, but they are required to register each item.
 * Whenever possible, avoid using hardcoded references to these, prefer tags or recipes.
 */
@SuppressWarnings("unused")
public final class TFCItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MOD_ID);

    // Ores

    public static final Map<Ore, ItemId> ORES = Helpers.mapOfKeys(Ore.class, ore -> !ore.isGraded(), type ->
        register("ore/" + type.name())
    );
    public static final Map<Ore, Map<Ore.Grade, ItemId>> GRADED_ORES = Helpers.mapOfKeys(Ore.class, Ore::isGraded, ore ->
        Helpers.mapOfKeys(Ore.Grade.class, grade ->
            register("ore/" + grade.name() + '_' + ore.name())
        )
    );

    public static final Map<Gem, ItemId> GEMS = Helpers.mapOfKeys(Gem.class, gem ->
        register("gem/" + gem.name())
    );

    // Rock Stuff

    public static final Map<RockCategory, Map<RockCategory.ItemType, ItemId>> ROCK_TOOLS = Helpers.mapOfKeys(RockCategory.class, category ->
        Helpers.mapOfKeys(RockCategory.ItemType.class, type ->
            register("stone/" + type.name() + "/" + category.name(), () -> type.create(category))
        )
    );

    public static final Map<Rock, ItemId> BRICKS = Helpers.mapOfKeys(Rock.class, type ->
        register("brick/" + type.name())
    );

    // Metal

    public static final Map<Metal.Default, Map<Metal.ItemType, ItemId>> METAL_ITEMS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        Helpers.mapOfKeys(Metal.ItemType.class, type -> type.has(metal), type ->
            register("metal/" + type.name() + "/" + metal.name(), () -> type.create(metal))
        )
    );

    // Wood

    public static final Map<Wood, ItemId> LUMBER = Helpers.mapOfKeys(Wood.class, wood -> register("wood/lumber/" + wood.name()));

    public static final Map<Wood, ItemId> SUPPORTS = Helpers.mapOfKeys(Wood.class, wood ->
        register("wood/support/" + wood.name(), () -> new StandingAndWallBlockItem(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.VERTICAL_SUPPORT).get(), TFCBlocks.WOODS.get(wood).get(Wood.BlockType.HORIZONTAL_SUPPORT).get(), new Item.Properties(), Direction.DOWN))
    );

    public static final Map<Wood, ItemId> BOATS = Helpers.mapOfKeys(Wood.class, wood -> register("wood/boat/" + wood.name(), () -> new TFCBoatItem(TFCEntities.BOATS.get(wood), new Item.Properties())));

    public static final Map<Wood, ItemId> CHEST_MINECARTS = Helpers.mapOfKeys(Wood.class, wood -> register("wood/chest_minecart/" + wood.name(), () -> new TFCMinecartItem(new Item.Properties(), TFCEntities.CHEST_MINECART, () -> TFCBlocks.WOODS.get(wood).get(Wood.BlockType.CHEST).get().asItem())));

    public static final Map<Wood, ItemId> SIGNS = Helpers.mapOfKeys(Wood.class, wood -> register("wood/sign/" + wood.name(), () -> new SignItem(new Item.Properties(), TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SIGN).get(), TFCBlocks.WOODS.get(wood).get(Wood.BlockType.WALL_SIGN).get())));

    public static final Map<Wood, Map<Metal.Default, ItemId>> HANGING_SIGNS = Helpers.mapOfKeys(Wood.class, wood ->
        Helpers.mapOfKeys(Metal.Default.class, Metal.Default::allParts, metal ->
            register("wood/hanging_sign/" + metal.name() + "/" + wood.name(), () -> new HangingSignItem(TFCBlocks.CEILING_HANGING_SIGNS.get(wood).get(metal).get(), TFCBlocks.WALL_HANGING_SIGNS.get(wood).get(metal).get(), new Item.Properties()))
        )
    );

    // Food

    public static final Map<Food, ItemId> FOOD = Helpers.mapOfKeys(Food.class, food ->
        register("food/" + food.name(), () -> new Item(food.createProperties()))
    );
    public static final Map<Food, ItemId> FRUIT_PRESERVES = Helpers.mapOfKeys(Food.class, Food::isFruit, food ->
        register("jar/" + food.name(), () -> new JarItem(new Item.Properties(), false))
    );
    public static final Map<Food, ItemId> UNSEALED_FRUIT_PRESERVES = Helpers.mapOfKeys(Food.class, Food::isFruit, food ->
        register("jar/" + food.name() + "_unsealed", () -> new JarItem(new Item.Properties(), true))
    );
    public static final Map<Nutrient, ItemId> SOUPS = Helpers.mapOfKeys(Nutrient.class, nutrient ->
        register("food/" + nutrient.name() + "_soup", () -> new Item(new Item.Properties()))
    );
    public static final Map<Nutrient, ItemId> SALADS = Helpers.mapOfKeys(Nutrient.class, nutrient ->
        register("food/" + nutrient.name() + "_salad", () -> new Item(new Item.Properties()))
    );

    // Flora

    public static final Map<Crop, ItemId> CROP_SEEDS = Helpers.mapOfKeys(Crop.class, crop ->
        register("seeds/" + crop.name(), () -> new ItemNameBlockItem(TFCBlocks.CROPS.get(crop).get(), new Item.Properties()))
    );

    public static final Map<Coral, ItemId> CORAL_FANS = Helpers.mapOfKeys(Coral.class, color ->
        register("coral/" + color.toString() + "_coral_fan", () -> new StandingAndWallBlockItem(TFCBlocks.CORAL.get(color).get(Coral.BlockType.CORAL_FAN).get(), TFCBlocks.CORAL.get(color).get(Coral.BlockType.CORAL_WALL_FAN).get(), new Item.Properties(), Direction.DOWN))
    );

    public static final Map<Coral, ItemId> DEAD_CORAL_FANS = Helpers.mapOfKeys(Coral.class, color ->
        register("coral/" + color.toString() + "_dead_coral_fan", () -> new StandingAndWallBlockItem(TFCBlocks.CORAL.get(color).get(Coral.BlockType.DEAD_CORAL_FAN).get(), TFCBlocks.CORAL.get(color).get(Coral.BlockType.DEAD_CORAL_WALL_FAN).get(), new Item.Properties(), Direction.DOWN))
    );

    // Decorations

    public static final ItemId LOAM_MUD_BRICK = register("mud_brick/loam");
    public static final ItemId SILTY_LOAM_MUD_BRICK = register("mud_brick/silty_loam");
    public static final ItemId SANDY_LOAM_MUD_BRICK = register("mud_brick/sandy_loam");
    public static final ItemId SILT_MUD_BRICK = register("mud_brick/silt");

    public static final ItemId ALABASTER_BRICK = register("alabaster_brick");
    public static final ItemId TORCH = register("torch", () -> new TorchItem(TFCBlocks.TORCH.get(), TFCBlocks.WALL_TORCH.get(), new Item.Properties()));
    public static final ItemId DEAD_TORCH = register("dead_torch", () -> new StandingAndWallBlockItem(TFCBlocks.DEAD_TORCH.get(), TFCBlocks.DEAD_WALL_TORCH.get(), new Item.Properties(), Direction.DOWN));

    // Misc

    public static final Map<HideItemType, Map<HideItemType.Size, ItemId>> HIDES = Helpers.mapOfKeys(HideItemType.class, type ->
        Helpers.mapOfKeys(HideItemType.Size.class, size ->
            register(size.name() + '_' + type.name() + "_hide", () -> new Item(new Item.Properties()))
        )
    );

    public static final Map<Gem, ItemId> GEM_DUST = Helpers.mapOfKeys(Gem.class, gem -> register("powder/" + gem.name()));
    public static final Map<Powder, ItemId> POWDERS = Helpers.mapOfKeys(Powder.class, powder -> register("powder/" + powder.name()));
    public static final Map<Ore, ItemId> ORE_POWDERS = Helpers.mapOfKeys(Ore.class, Ore::isGraded, ore -> register("powder/" + ore.name()));

    public static final ItemId CERAMIC_BLOWPIPE = register("ceramic_blowpipe", () -> new BlowpipeItem(new Item.Properties()));
    public static final ItemId BLOWPIPE = register("blowpipe", () -> new BlowpipeItem(new Item.Properties()));
    public static final ItemId CERAMIC_BLOWPIPE_WITH_GLASS = register("ceramic_blowpipe_with_glass", () -> new GlassBlowpipeItem(new Item.Properties(), 0.1f));
    public static final ItemId BLOWPIPE_WITH_GLASS = register("blowpipe_with_glass", () -> new GlassBlowpipeItem(new Item.Properties(), 0f));
    public static final ItemId JACKS = register("jacks", () -> new GlassworkingItem(new Item.Properties(), GlassOperation.PINCH));
    public static final ItemId PADDLE = register("paddle", () -> new GlassworkingItem(new Item.Properties(), GlassOperation.FLATTEN));
    public static final ItemId GEM_SAW = register("gem_saw", () -> new GemSawItem(TFCTiers.BRONZE, new Item.Properties()));
    public static final ItemId SILICA_GLASS_BATCH = register("silica_glass_batch");
    public static final ItemId HEMATITIC_GLASS_BATCH = register("hematitic_glass_batch");
    public static final ItemId OLIVINE_GLASS_BATCH = register("olivine_glass_batch");
    public static final ItemId VOLCANIC_GLASS_BATCH = register("volcanic_glass_batch");
    public static final ItemId LAMP_GLASS = register("lamp_glass");
    public static final ItemId LENS = register("lens");
    public static final ItemId SILICA_GLASS_BOTTLE = register("silica_glass_bottle", () -> new GlassBottleItem(new Item.Properties(), TFCConfig.SERVER.silicaGlassBottleCapacity, TFCConfig.SERVER.silicaGlassBottleBreakChance, TFCTags.Fluids.USABLE_IN_JUG));
    public static final ItemId HEMATITIC_GLASS_BOTTLE = register("hematitic_glass_bottle", () -> new GlassBottleItem(new Item.Properties(), TFCConfig.SERVER.hematiticGlassBottleCapacity, TFCConfig.SERVER.hematiticGlassBottleBreakChance, TFCTags.Fluids.USABLE_IN_JUG));
    public static final ItemId VOLCANIC_GLASS_BOTTLE = register("volcanic_glass_bottle", () -> new GlassBottleItem(new Item.Properties(), TFCConfig.SERVER.volcanicGlassBottleCapacity, TFCConfig.SERVER.volcanicGlassBottleBreakChance, TFCTags.Fluids.USABLE_IN_JUG));
    public static final ItemId OLIVINE_GLASS_BOTTLE = register("olivine_glass_bottle", () -> new GlassBottleItem(new Item.Properties(), TFCConfig.SERVER.olivineGlassBottleCapacity, TFCConfig.SERVER.olivineGlassBottleBreakChance, TFCTags.Fluids.USABLE_IN_JUG));
    public static final ItemId EMPTY_JAR = register("empty_jar", () -> new JarItem(new Item.Properties(), false));
    public static final ItemId EMPTY_JAR_WITH_LID = register("empty_jar_with_lid", () -> new JarItem(new Item.Properties(), false));
    public static final ItemId JAR_LID = register("jar_lid", () -> new Item(new Item.Properties()));

    public static final ItemId BONE_NEEDLE = register("bone_needle", () -> new Item(new Item.Properties().durability(64)));
    public static final ItemId BLANK_DISC = register("blank_disc");
    public static final ItemId BLUBBER = register("blubber");
    public static final ItemId BRASS_MECHANISMS = register("brass_mechanisms");
    public static final ItemId BURLAP_CLOTH = register("burlap_cloth");
    public static final ItemId COMPOST = register("compost");
    public static final ItemId DAUB = register("daub");
    public static final ItemId DIRTY_JUTE_NET = register("dirty_jute_net");
    public static final ItemId FIRE_CLAY = register("fire_clay");
    public static final ItemId FIRESTARTER = register("firestarter", () -> new FirestarterItem(new Item.Properties().durability(8)));
    public static final ItemId GOAT_HORN = register("goat_horn");
    public static final ItemId GLOW_ARROW = register("glow_arrow", () -> new GlowArrowItem(new Item.Properties()));
    public static final ItemId GLUE = register("glue");
    public static final ItemId HAND_WHEEL = register("hand_wheel", () -> new Item(new Item.Properties().durability(250)));
    public static final ItemId JUTE = register("jute");
    public static final ItemId JUTE_FIBER = register("jute_fiber");
    public static final ItemId JUTE_NET = register("jute_net");
    public static final ItemId KAOLIN_CLAY = register("kaolin_clay");
    public static final ItemId HANDSTONE = register("handstone", () -> new Item(new Item.Properties().durability(250)));
    public static final ItemId MORTAR = register("mortar");
    public static final ItemId OLIVE_PASTE = register("olive_paste");
    public static final ItemId PAPYRUS = register("papyrus");
    public static final ItemId PAPYRUS_STRIP = register("papyrus_strip");
    public static final ItemId PURE_NITROGEN = register("pure_nitrogen");
    public static final ItemId PURE_PHOSPHORUS = register("pure_phosphorus");
    public static final ItemId PURE_POTASSIUM = register("pure_potassium");
    public static final ItemId ROTTEN_COMPOST = register("rotten_compost", () -> new RottenCompostItem(new Item.Properties()));
    public static final ItemId SILK_CLOTH = register("silk_cloth");
    public static final ItemId SANDPAPER = register("sandpaper", () -> new Item(new Item.Properties().durability(40)));
    public static final ItemId SOAKED_PAPYRUS_STRIP = register("soaked_papyrus_strip");
    public static final ItemId SOOT = register("soot");
    public static final ItemId SPINDLE = register("spindle", () -> new Item(new Item.Properties().durability(40)));
    public static final ItemId STICK_BUNCH = register("stick_bunch");
    public static final ItemId STICK_BUNDLE = register("stick_bundle");
    public static final ItemId STRAW = register("straw");
    public static final ItemId TREATED_HIDE = register("treated_hide");
    public static final ItemId UNREFINED_PAPER = register("unrefined_paper");
    public static final ItemId WOODEN_BUCKET = register("wooden_bucket", () -> new FluidContainerItem(new Item.Properties(), TFCConfig.SERVER.woodenBucketCapacity, TFCTags.Fluids.USABLE_IN_WOODEN_BUCKET, true, false));
    public static final ItemId WOOL = register("wool");
    public static final ItemId WOOL_CLOTH = register("wool_cloth", () -> new GlassworkingItem(new Item.Properties(), GlassOperation.ROLL));
    public static final ItemId WOOL_YARN = register("wool_yarn");
    public static final ItemId WROUGHT_IRON_GRILL = register("wrought_iron_grill");
    public static final ItemId RAW_IRON_BLOOM = register("raw_iron_bloom");
    public static final ItemId REFINED_IRON_BLOOM = register("refined_iron_bloom");

    public static final ItemId EMPTY_PAN = register("pan/empty", () -> new EmptyPanItem(new Item.Properties()));
    public static final ItemId FILLED_PAN = register("pan/filled", () -> new PanItem(new Item.Properties().stacksTo(1)));

    public static final ItemId WINDMILL_BLADE = register("windmill_blade", () -> new WindmillBladeItem(new Item.Properties(), DyeColor.WHITE));
    public static final Map<DyeColor, ItemId> COLORED_WINDMILL_BLADES = Helpers.mapOfKeys(DyeColor.class, color -> color != DyeColor.WHITE, color ->
        register(color.getSerializedName() + "_windmill_blade", () -> new WindmillBladeItem(new Item.Properties(), color))
    );
    public static final Map<Fish, ItemId> FRESHWATER_FISH_EGGS = Helpers.mapOfKeys(Fish.class, fish -> registerSpawnEgg(TFCEntities.FRESHWATER_FISH.get(fish), fish.getEggColor1(), fish.getEggColor2()));

    public static final ItemId COD_EGG = registerSpawnEgg(TFCEntities.COD, 12691306, 15058059);
    public static final ItemId PUFFERFISH_EGG = registerSpawnEgg(TFCEntities.PUFFERFISH, 16167425, 3654642);
    public static final ItemId TROPICAL_FISH_EGG = registerSpawnEgg(TFCEntities.TROPICAL_FISH, 15690005, 16775663);
    public static final ItemId JELLYFISH_EGG = registerSpawnEgg(TFCEntities.JELLYFISH, 0xE83D0E, 0x11F2F2);
    public static final ItemId LOBSTER_EGG = registerSpawnEgg(TFCEntities.LOBSTER, 0xa63521, 0x312042);
    public static final ItemId CRAYFISH_EGG = registerSpawnEgg(TFCEntities.CRAYFISH, 0x6f6652, 0x694150);
    public static final ItemId ISOPOD_EGG = registerSpawnEgg(TFCEntities.ISOPOD, 0xb970ba, 0x969377);
    public static final ItemId HORSESHOE_CRAB_EGG = registerSpawnEgg(TFCEntities.HORSESHOE_CRAB, 0x45e2ed, 0x45e2ed);
    public static final ItemId DOLPHIN_EGG = registerSpawnEgg(TFCEntities.DOLPHIN, 2243405, 16382457);
    public static final ItemId ORCA_EGG = registerSpawnEgg(TFCEntities.ORCA, 0x000000, 0xffffff);
    public static final ItemId MANATEE_EGG = registerSpawnEgg(TFCEntities.MANATEE, 0x65786C, 0x7FCFCF);
    public static final ItemId TURTLE_EGG = registerSpawnEgg(TFCEntities.TURTLE, 15198183, 44975);
    public static final ItemId PENGUIN_EGG = registerSpawnEgg(TFCEntities.PENGUIN, 0xFFEA00, 0x47452C);
    public static final ItemId FROG_EGG = registerSpawnEgg(TFCEntities.FROG, 13661252, 16762748);
    public static final ItemId POLAR_BEAR_EGG = registerSpawnEgg(TFCEntities.POLAR_BEAR, 15921906, 9803152);
    public static final ItemId GRIZZLY_BEAR_EGG = registerSpawnEgg(TFCEntities.GRIZZLY_BEAR, 0x964B00, 0xD2B48C);
    public static final ItemId BLACK_BEAR_EGG = registerSpawnEgg(TFCEntities.BLACK_BEAR, 0x000000, 0x333333);
    public static final ItemId COUGAR_EGG = registerSpawnEgg(TFCEntities.COUGAR, 0xb37f51, 0xe5c6a7);
    public static final ItemId PANTHER_EGG = registerSpawnEgg(TFCEntities.PANTHER, 0x000000, 0xffff00);
    public static final ItemId LION_EGG = registerSpawnEgg(TFCEntities.LION, 0xf4d988, 0x663d18);
    public static final ItemId SABERTOOTH_EGG = registerSpawnEgg(TFCEntities.SABERTOOTH, 0xc28a30, 0xc2beb2);
    public static final ItemId TIGER_EGG = registerSpawnEgg(TFCEntities.TIGER, 0xef9246, 0x291d1f);
    public static final ItemId CROCODILE_EGG = registerSpawnEgg(TFCEntities.CROCODILE, 0x7a8a6c, 0xf7deb2);
    public static final ItemId WOLF_EGG = registerSpawnEgg(TFCEntities.WOLF, 14144467, 0xc2beb2);
    public static final ItemId HYENA_EGG = registerSpawnEgg(TFCEntities.HYENA, 0xbf9e71, 0x3f3422);
    public static final ItemId DIREWOLF_EGG = registerSpawnEgg(TFCEntities.DIREWOLF, 0x6f3b12, 14144467);
    public static final ItemId SQUID_EGG = registerSpawnEgg(TFCEntities.SQUID, 2243405, 7375001);
    public static final ItemId OCTOPOTEUTHIS_EGG = registerSpawnEgg(TFCEntities.OCTOPOTEUTHIS, 611926, 8778172);
    public static final ItemId PIG_EGG = registerSpawnEgg(TFCEntities.PIG, 15771042, 14377823);
    public static final ItemId COW_EGG = registerSpawnEgg(TFCEntities.COW, 4470310, 10592673);
    public static final ItemId GOAT_EGG = registerSpawnEgg(TFCEntities.GOAT, 0xDDDDDD, 0x776677);
    public static final ItemId YAK_EGG = registerSpawnEgg(TFCEntities.YAK, 0xd7c18e, 0x6f3b12);
    public static final ItemId ALPACA_EGG = registerSpawnEgg(TFCEntities.ALPACA, 0x00CC66, 0x006633);
    public static final ItemId SHEEP_EGG = registerSpawnEgg(TFCEntities.SHEEP, 0xFFFFFF, 0xEEEEEE);
    public static final ItemId MUSK_OX_EGG = registerSpawnEgg(TFCEntities.MUSK_OX, 0x6f3b12, 0xd7c18e);
    public static final ItemId CHICKEN_EGG = registerSpawnEgg(TFCEntities.CHICKEN, 10592673, 16711680);
    public static final ItemId DUCK_EGG = registerSpawnEgg(TFCEntities.DUCK, 0x654b17, 0x2a803e);
    public static final ItemId QUAIL_EGG = registerSpawnEgg(TFCEntities.QUAIL, 0x8081a5, 0xDDDDDD);
    public static final ItemId RABBIT_EGG = registerSpawnEgg(TFCEntities.RABBIT, 10051392, 7555121);
    public static final ItemId FOX_EGG = registerSpawnEgg(TFCEntities.FOX, 14005919, 1339625);
    public static final ItemId BOAR_EGG = registerSpawnEgg(TFCEntities.BOAR, 0x8081a5, 0x006633);
    public static final ItemId WILDEBEEST_EGG = registerSpawnEgg(TFCEntities.WILDEBEEST, 0x3f3224, 0x83705d);
    public static final ItemId OCELOT_EGG = registerSpawnEgg(TFCEntities.OCELOT, 15720061, 5653556);
    public static final ItemId BONGO_EGG = registerSpawnEgg(TFCEntities.BONGO, 0xb35936, 0x006633);
    public static final ItemId CARIBOU_EGG = registerSpawnEgg(TFCEntities.CARIBOU, 0x3b4547c, 0xd2c2b1);
    public static final ItemId DEER_EGG = registerSpawnEgg(TFCEntities.DEER, 0x6f3b12, 0x006633);
    public static final ItemId GAZELLE_EGG = registerSpawnEgg(TFCEntities.GAZELLE, 0x3c2417, 0xbf8237);
    public static final ItemId MOOSE_EGG = registerSpawnEgg(TFCEntities.MOOSE, 0x654b17, 0xDDDDDD);
    public static final ItemId GROUSE_EGG = registerSpawnEgg(TFCEntities.GROUSE, 0xbda878, 0xDDDDDD);
    public static final ItemId PHEASANT_EGG = registerSpawnEgg(TFCEntities.PHEASANT, 0xe59c10, 0x004195);
    public static final ItemId TURKEY_EGG = registerSpawnEgg(TFCEntities.TURKEY, 0x758db3, 0xf16b7a);
    public static final ItemId PEAFOWL_EGG = registerSpawnEgg(TFCEntities.PEAFOWL, 0x034bb3, 0x3c9d52);
    public static final ItemId RAT_EGG = registerSpawnEgg(TFCEntities.RAT, 0xd7c18e, 12623485);
    public static final ItemId DONKEY_EGG = registerSpawnEgg(TFCEntities.DONKEY, 5457209, 8811878);
    public static final ItemId MULE_EGG = registerSpawnEgg(TFCEntities.MULE, 1769984, 5321501);
    public static final ItemId HORSE_EGG = registerSpawnEgg(TFCEntities.HORSE, 12623485, 15656192);
    public static final ItemId CAT_EGG = registerSpawnEgg(TFCEntities.CAT, 15714446, 9794134);
    public static final ItemId DOG_EGG = registerSpawnEgg(TFCEntities.DOG, 14144467, 13545366);
    public static final ItemId PANDA_EGG = registerSpawnEgg(TFCEntities.PANDA, 15198183, 1776418);

    // Pottery

    public static final ItemId UNFIRED_BRICK = register("ceramic/unfired_brick");
    public static final ItemId UNFIRED_CRUCIBLE = register("ceramic/unfired_crucible");
    public static final ItemId UNFIRED_FLOWER_POT = register("ceramic/unfired_flower_pot");
    public static final ItemId UNFIRED_PAN = register("ceramic/unfired_pan");
    public static final ItemId UNFIRED_BLOWPIPE = register("ceramic/unfired_blowpipe");

    public static final ItemId UNFIRED_BOWL = register("ceramic/unfired_bowl");
    // Ceramic bowl is registered as a block

    public static final ItemId UNFIRED_FIRE_BRICK = register("ceramic/unfired_fire_brick");
    public static final ItemId FIRE_BRICK = register("ceramic/fire_brick");

    public static final ItemId UNFIRED_JUG = register("ceramic/unfired_jug");
    public static final ItemId JUG = register("ceramic/jug", () -> new JugItem(new Item.Properties().stacksTo(1), TFCConfig.SERVER.jugCapacity, TFCTags.Fluids.USABLE_IN_JUG));

    public static final ItemId UNFIRED_POT = register("ceramic/unfired_pot");
    public static final ItemId POT = register("ceramic/pot");

    public static final ItemId UNFIRED_SPINDLE_HEAD = register("ceramic/unfired_spindle_head");
    public static final ItemId SPINDLE_HEAD = register("ceramic/spindle_head");

    public static final ItemId UNFIRED_VESSEL = register("ceramic/unfired_vessel");
    public static final ItemId VESSEL = register("ceramic/vessel", () -> new VesselItem(new Item.Properties()));

    public static final Map<DyeColor, ItemId> UNFIRED_GLAZED_VESSELS = Helpers.mapOfKeys(DyeColor.class, color ->
        register("ceramic/" + color + "_unfired_vessel")
    );

    public static final Map<DyeColor, ItemId> GLAZED_VESSELS = Helpers.mapOfKeys(DyeColor.class, color ->
        register("ceramic/" + color + "_glazed_vessel", () -> new VesselItem(new Item.Properties()))
    );

    public static final Map<Metal.ItemType, ItemId> UNFIRED_MOLDS = Helpers.mapOfKeys(Metal.ItemType.class, Metal.ItemType::hasMold, type ->
        register("ceramic/unfired_" + type.name() + "_mold")
    );

    public static final Map<Metal.ItemType, ItemId> MOLDS = Helpers.mapOfKeys(Metal.ItemType.class, Metal.ItemType::hasMold, type ->
        register("ceramic/" + type.name() + "_mold", () -> new MoldItem(type, new Item.Properties()))
    );

    public static final ItemId UNFIRED_BELL_MOLD = register("ceramic/unfired_bell_mold");
    public static final ItemId BELL_MOLD = register("ceramic/bell_mold", () -> new MoldItem(TFCConfig.SERVER.moldBellCapacity, TFCTags.Fluids.USABLE_IN_BELL_MOLD, new Item.Properties()));
    public static final ItemId UNFIRED_FIRE_INGOT_MOLD = register("ceramic/unfired_fire_ingot_mold");
    public static final ItemId FIRE_INGOT_MOLD = register("ceramic/fire_ingot_mold", () -> new MoldItem(TFCConfig.SERVER.moldFireIngotCapacity, TFCTags.Fluids.USABLE_IN_INGOT_MOLD, new Item.Properties()));

    public static final ItemId UNFIRED_LARGE_VESSEL = register("ceramic/unfired_large_vessel");
    public static final Map<DyeColor, ItemId> UNFIRED_GLAZED_LARGE_VESSELS = Helpers.mapOfKeys(DyeColor.class, color ->
        register("ceramic/unfired_large_vessel/" + color)
    );

    // Fluid Buckets

    public static final Map<FluidId, ItemId> FLUID_BUCKETS = FluidId.mapOf(fluid ->
        register("bucket/" + fluid.name(), () -> new BucketItem(fluid.fluid(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)))
    );

    public static final ItemId RED_STEEL_BUCKET = register("metal/bucket/red_steel", () -> new FluidContainerItem(new Item.Properties(), () -> FluidHelpers.BUCKET_VOLUME, TFCTags.Fluids.USABLE_IN_RED_STEEL_BUCKET, true, false));
    public static final ItemId BLUE_STEEL_BUCKET = register("metal/bucket/blue_steel", () -> new FluidContainerItem(new Item.Properties(), () -> FluidHelpers.BUCKET_VOLUME, TFCTags.Fluids.USABLE_IN_BLUE_STEEL_BUCKET, true, false));

    public static final ItemId COD_BUCKET = register("bucket/cod", () -> new MobBucketItem(TFCEntities.COD, TFCFluids.SALT_WATER.source(), () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final ItemId PUFFERFISH_BUCKET = register("bucket/pufferfish", () -> new MobBucketItem(TFCEntities.PUFFERFISH, TFCFluids.SALT_WATER.source(), () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final ItemId JELLYFISH_BUCKET = register("bucket/jellyfish", () -> new MobBucketItem(TFCEntities.JELLYFISH, TFCFluids.SALT_WATER.source(), () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final ItemId TROPICAL_FISH_BUCKET = register("bucket/tropical_fish", () -> new MobBucketItem(TFCEntities.TROPICAL_FISH, TFCFluids.SALT_WATER.source(), () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final Map<Fish, ItemId> FRESHWATER_FISH_BUCKETS = Helpers.mapOfKeys(Fish.class, fish -> register("bucket/" + fish.getSerializedName(), () -> new MobBucketItem(TFCEntities.FRESHWATER_FISH.get(fish), () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))));

    public static void editItemMaxDamage()
    {
        ((ItemAccessor) Items.FLINT_AND_STEEL).accessor$setMaxDamage(TFCTiers.STEEL.getUses());
    }

    private static <T extends Mob> ItemId registerSpawnEgg(IdHolder<EntityType<T>> entity, int color1, int color2)
    {
        return register("spawn_egg/" + entity.getId().getPath(), () -> new DeferredSpawnEggItem(entity.holder(), color1, color2, new Item.Properties()));
    }

    private static ItemId register(String name)
    {
        return register(name, () -> new Item(new Item.Properties()));
    }

    private static ItemId register(String name, Supplier<Item> item)
    {
        return new ItemId(ITEMS.register(name.toLowerCase(Locale.ROOT), item));
    }
    
    public record ItemId(DeferredHolder<Item, Item> holder) implements RegistryHolder<Item, Item> {}
}