/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.common.TFCItemGroup;
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
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidType;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.accessor.ItemAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

import static net.dries007.tfc.TerraFirmaCraft.*;
import static net.dries007.tfc.common.TFCItemGroup.*;

/**
 * Collection of all TFC items.
 * Organized by {@link TFCItemGroup}
 * Unused is as the registry object fields themselves may be unused, but they are required to register each item.
 * Whenever possible, avoid using hardcoded references to these, prefer tags or recipes.
 */
@SuppressWarnings("unused")
public final class TFCItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    // Ores

    public static final Map<Ore, RegistryObject<Item>> ORES = Helpers.mapOfKeys(Ore.class, ore -> !ore.isGraded(), type ->
        register("ore/" + type.name(), TFCItemGroup.ORES)
    );
    public static final Map<Ore, Map<Ore.Grade, RegistryObject<Item>>> GRADED_ORES = Helpers.mapOfKeys(Ore.class, Ore::isGraded, ore ->
        Helpers.mapOfKeys(Ore.Grade.class, grade ->
            register("ore/" + grade.name() + '_' + ore.name(), TFCItemGroup.ORES)
        )
    );

    public static final Map<Gem, RegistryObject<Item>> GEMS = Helpers.mapOfKeys(Gem.class, gem ->
        register("gem/" + gem.name(), TFCItemGroup.ORES)
    );

    // Rock Stuff

    public static final Map<RockCategory, Map<RockCategory.ItemType, RegistryObject<Item>>> ROCK_TOOLS = Helpers.mapOfKeys(RockCategory.class, category ->
        Helpers.mapOfKeys(RockCategory.ItemType.class, type ->
            register("stone/" + type.name() + "/" + category.name(), () -> type.create(category))
        )
    );

    public static final Map<Rock, RegistryObject<Item>> BRICKS = Helpers.mapOfKeys(Rock.class, type ->
        register("brick/" + type.name(), ROCK_STUFFS)
    );

    // Metal

    public static final Map<Metal.Default, Map<Metal.ItemType, RegistryObject<Item>>> METAL_ITEMS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        Helpers.mapOfKeys(Metal.ItemType.class, type -> type.has(metal), type ->
            register("metal/" + type.name() + "/" + metal.name(), () -> type.create(metal))
        )
    );

    // Wood

    public static final Map<Wood, RegistryObject<Item>> LUMBER = Helpers.mapOfKeys(Wood.class, wood -> register("wood/lumber/" + wood.name(), WOOD));

    public static final Map<Wood, RegistryObject<Item>> SUPPORTS = Helpers.mapOfKeys(Wood.class, wood ->
        register("wood/support/" + wood.name(), () -> new StandingAndWallBlockItem(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.VERTICAL_SUPPORT).get(), TFCBlocks.WOODS.get(wood).get(Wood.BlockType.HORIZONTAL_SUPPORT).get(), new Item.Properties().tab(WOOD)))
    );

    public static final Map<Wood, RegistryObject<Item>> BOATS = Helpers.mapOfKeys(Wood.class, wood -> register("wood/boat/" + wood.name(), () -> new TFCBoatItem(TFCEntities.BOATS.get(wood), new Item.Properties().tab(WOOD))));

    public static final Map<Wood, RegistryObject<Item>> CHEST_MINECARTS = Helpers.mapOfKeys(Wood.class, wood -> register("wood/chest_minecart/" + wood.name(), () -> new TFCMinecartItem(new Item.Properties().tab(WOOD), TFCEntities.CHEST_MINECART, () -> TFCBlocks.WOODS.get(wood).get(Wood.BlockType.CHEST).get().asItem())));

    public static final Map<Wood, RegistryObject<Item>> SIGNS = Helpers.mapOfKeys(Wood.class, wood -> register("wood/sign/" + wood.name(), () -> new SignItem(new Item.Properties().tab(WOOD), TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SIGN).get(), TFCBlocks.WOODS.get(wood).get(Wood.BlockType.WALL_SIGN).get())));


    // Food

    public static final Map<Food, RegistryObject<Item>> FOOD = Helpers.mapOfKeys(Food.class, food ->
        register("food/" + food.name(), () -> new DecayingItem(food.createProperties()))
    );
    public static final Map<Nutrient, RegistryObject<DecayingItem>> SOUPS = Helpers.mapOfKeys(Nutrient.class, nutrient ->
        register("food/" + nutrient.name() + "_soup", () -> new DecayingItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build()).tab(TFCItemGroup.FOOD)))
    );
    public static final Map<Nutrient, RegistryObject<DecayingItem>> SALADS = Helpers.mapOfKeys(Nutrient.class, nutrient ->
        register("food/" + nutrient.name() + "_salad", () -> new DecayingItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build()).tab(TFCItemGroup.FOOD)))
    );

    @Deprecated(forRemoval = true, since = "1.18.2") // todo: 1.20. move to food enum
    public static final Map<Grain, RegistryObject<Item>> SANDWICHES = Helpers.mapOfKeys(Grain.class, grain ->
        register("food/" + grain.name() + "_bread_sandwich", () -> new DecayingItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build()).tab(TFCItemGroup.FOOD)))
    );


    // Flora

    public static final Map<Crop, RegistryObject<Item>> CROP_SEEDS = Helpers.mapOfKeys(Crop.class, crop ->
        register("seeds/" + crop.name(), () -> new ItemNameBlockItem(TFCBlocks.CROPS.get(crop).get(), new Item.Properties().tab(FLORA)))
    );

    public static final Map<Coral, RegistryObject<Item>> CORAL_FANS = Helpers.mapOfKeys(Coral.class, color ->
        register("coral/" + color.toString() + "_coral_fan", () -> new StandingAndWallBlockItem(TFCBlocks.CORAL.get(color).get(Coral.BlockType.CORAL_FAN).get(), TFCBlocks.CORAL.get(color).get(Coral.BlockType.CORAL_WALL_FAN).get(), new Item.Properties().tab(FLORA)))
    );

    public static final Map<Coral, RegistryObject<Item>> DEAD_CORAL_FANS = Helpers.mapOfKeys(Coral.class, color ->
        register("coral/" + color.toString() + "_dead_coral_fan", () -> new StandingAndWallBlockItem(TFCBlocks.CORAL.get(color).get(Coral.BlockType.DEAD_CORAL_FAN).get(), TFCBlocks.CORAL.get(color).get(Coral.BlockType.DEAD_CORAL_WALL_FAN).get(), new Item.Properties().tab(FLORA)))
    );

    // Decorations

    public static final RegistryObject<Item> LOAM_MUD_BRICK = register("mud_brick/loam", DECORATIONS);
    public static final RegistryObject<Item> SILTY_LOAM_MUD_BRICK = register("mud_brick/silty_loam", DECORATIONS);
    public static final RegistryObject<Item> SANDY_LOAM_MUD_BRICK = register("mud_brick/sandy_loam", DECORATIONS);
    public static final RegistryObject<Item> SILT_MUD_BRICK = register("mud_brick/silt", DECORATIONS);

    public static final RegistryObject<Item> ALABASTER_BRICK = register("alabaster_brick", DECORATIONS);
    public static final RegistryObject<Item> TORCH = register("torch", () -> new TorchItem(TFCBlocks.TORCH.get(), TFCBlocks.WALL_TORCH.get(), new Item.Properties().tab(DECORATIONS)));
    public static final RegistryObject<Item> TORCH_DEAD = register("dead_torch", () -> new StandingAndWallBlockItem(TFCBlocks.DEAD_TORCH.get(), TFCBlocks.DEAD_WALL_TORCH.get(), new Item.Properties().tab(DECORATIONS)));

    // Misc

    public static final Map<HideItemType, Map<HideItemType.Size, RegistryObject<Item>>> HIDES = Helpers.mapOfKeys(HideItemType.class, type ->
        Helpers.mapOfKeys(HideItemType.Size.class, size ->
            register(size.name() + '_' + type.name() + "_hide", () -> new Item(new Item.Properties().tab(MISC)))
        )
    );

    public static final Map<Gem, RegistryObject<Item>> GEM_DUST = Helpers.mapOfKeys(Gem.class, gem ->
        register("powder/" + gem.name(), MISC)
    );

    public static final Map<Powder, RegistryObject<Item>> POWDERS = Helpers.mapOfKeys(Powder.class, powder ->
        register("powder/" + powder.name(), MISC)
    );

    public static final RegistryObject<Item> BARREL_RACK = register("barrel_rack", () -> new BlockItem(TFCBlocks.BARREL_RACK.get(), new Item.Properties().tab(MISC)));
    public static final RegistryObject<Item> BLANK_DISC = register("blank_disc", MISC);
    public static final RegistryObject<Item> BLUBBER = register("blubber", MISC);
    public static final RegistryObject<Item> BRASS_MECHANISMS = register("brass_mechanisms", MISC);
    public static final RegistryObject<Item> BURLAP_CLOTH = register("burlap_cloth", MISC);
    public static final RegistryObject<Item> COMPOST = register("compost", MISC);
    public static final RegistryObject<Item> DAUB = register("daub", MISC);
    public static final RegistryObject<Item> DIRTY_JUTE_NET = register("dirty_jute_net", MISC);
    public static final RegistryObject<Item> FIRE_CLAY = register("fire_clay", MISC);
    public static final RegistryObject<Item> FIRESTARTER = register("firestarter", () -> new FirestarterItem(new Item.Properties().tab(MISC).defaultDurability(8)));
    public static final RegistryObject<Item> GLASS_SHARD = register("glass_shard", MISC);
    public static final RegistryObject<Item> GLOW_ARROW = register("glow_arrow", () -> new GlowArrowItem(new Item.Properties().tab(MISC)));
    public static final RegistryObject<Item> GLUE = register("glue", MISC);
    public static final RegistryObject<Item> JUTE = register("jute", MISC);
    public static final RegistryObject<Item> JUTE_FIBER = register("jute_fiber", MISC);
    public static final RegistryObject<Item> JUTE_NET = register("jute_net", MISC);
    public static final RegistryObject<Item> HANDSTONE = register("handstone", () -> new Item(new Item.Properties().tab(MISC).defaultDurability(250)));
    public static final RegistryObject<Item> MORTAR = register("mortar", MISC);
    public static final RegistryObject<Item> OLIVE_PASTE = register("olive_paste", MISC);
    public static final RegistryObject<Item> PAPYRUS = register("papyrus", MISC);
    public static final RegistryObject<Item> PAPYRUS_STRIP = register("papyrus_strip", MISC);
    public static final RegistryObject<Item> PURE_NITROGEN = register("pure_nitrogen", MISC);
    public static final RegistryObject<Item> PURE_PHOSPHORUS = register("pure_phosphorus", MISC);
    public static final RegistryObject<Item> PURE_POTASSIUM = register("pure_potassium", MISC);
    public static final RegistryObject<Item> ROTTEN_COMPOST = register("rotten_compost", () -> new RottenCompostItem(new Item.Properties().tab(MISC)));
    public static final RegistryObject<Item> SILK_CLOTH = register("silk_cloth", MISC);
    public static final RegistryObject<Item> SOAKED_PAPYRUS_STRIP = register("soaked_papyrus_strip", MISC);
    public static final RegistryObject<Item> SOOT = register("soot", MISC);
    public static final RegistryObject<Item> SPINDLE = register("spindle", () -> new Item(new Item.Properties().tab(MISC).defaultDurability(40)));
    public static final RegistryObject<Item> STICK_BUNCH = register("stick_bunch", MISC);
    public static final RegistryObject<Item> STICK_BUNDLE = register("stick_bundle", MISC);
    public static final RegistryObject<Item> STRAW = register("straw", MISC);
    public static final RegistryObject<Item> UNREFINED_PAPER = register("unrefined_paper", MISC);
    public static final RegistryObject<Item> WOODEN_BUCKET = register("wooden_bucket", () -> new FluidContainerItem(new Item.Properties().tab(MISC), TFCConfig.SERVER.woodenBucketCapacity, TFCTags.Fluids.USABLE_IN_WOODEN_BUCKET, true, false));
    public static final RegistryObject<Item> WOOL = register("wool", MISC);
    public static final RegistryObject<Item> WOOL_CLOTH = register("wool_cloth", MISC);
    public static final RegistryObject<Item> WOOL_YARN = register("wool_yarn", MISC);
    public static final RegistryObject<Item> WROUGHT_IRON_GRILL = register("wrought_iron_grill", MISC);
    public static final RegistryObject<Item> RAW_IRON_BLOOM = register("raw_iron_bloom", MISC);
    public static final RegistryObject<Item> REFINED_IRON_BLOOM = register("refined_iron_bloom", MISC);


    public static final RegistryObject<Item> EMPTY_PAN = register("pan/empty", () -> new EmptyPanItem(new Item.Properties().tab(MISC)));
    public static final RegistryObject<Item> FILLED_PAN = register("pan/filled", () -> new PanItem(new Item.Properties().tab(MISC).stacksTo(1)));

    public static final RegistryObject<Item> COD_EGG = registerSpawnEgg(TFCEntities.COD, 12691306, 15058059);
    public static final RegistryObject<Item> PUFFERFISH_EGG = registerSpawnEgg(TFCEntities.PUFFERFISH, 16167425, 3654642);
    public static final RegistryObject<Item> TROPICAL_FISH_EGG = registerSpawnEgg(TFCEntities.TROPICAL_FISH, 15690005, 16775663);
    public static final RegistryObject<Item> JELLYFISH_EGG = registerSpawnEgg(TFCEntities.JELLYFISH, 0xE83D0E, 0x11F2F2);
    public static final RegistryObject<Item> SALMON_EGG = registerSpawnEgg(TFCEntities.SALMON, 10489616, 951412);
    public static final RegistryObject<Item> BLUEGILL_EGG = registerSpawnEgg(TFCEntities.BLUEGILL, 0x00658A, 0xE3E184);
    public static final RegistryObject<Item> LOBSTER_EGG = registerSpawnEgg(TFCEntities.LOBSTER, 0xa63521, 0x312042);
    public static final RegistryObject<Item> CRAYFISH_EGG = registerSpawnEgg(TFCEntities.CRAYFISH, 0x6f6652, 0x694150);
    public static final RegistryObject<Item> ISOPOD_EGG = registerSpawnEgg(TFCEntities.ISOPOD, 0xb970ba, 0x969377);
    public static final RegistryObject<Item> HORSESHOE_CRAB_EGG = registerSpawnEgg(TFCEntities.HORSESHOE_CRAB, 0x45e2ed, 0x45e2ed);
    public static final RegistryObject<Item> DOLPHIN_EGG = registerSpawnEgg(TFCEntities.DOLPHIN, 2243405, 16382457);
    public static final RegistryObject<Item> ORCA_EGG = registerSpawnEgg(TFCEntities.ORCA, 0x000000, 0xffffff);
    public static final RegistryObject<Item> MANATEE_EGG = registerSpawnEgg(TFCEntities.MANATEE, 0x65786C, 0x7FCFCF);
    public static final RegistryObject<Item> TURTLE_EGG = registerSpawnEgg(TFCEntities.TURTLE, 15198183, 44975);
    public static final RegistryObject<Item> PENGUIN_EGG = registerSpawnEgg(TFCEntities.PENGUIN, 0xFFEA00, 0x47452C);
    public static final RegistryObject<Item> POLAR_BEAR_EGG = registerSpawnEgg(TFCEntities.POLAR_BEAR, 15921906, 9803152);
    public static final RegistryObject<Item> GRIZZLY_BEAR_EGG = registerSpawnEgg(TFCEntities.GRIZZLY_BEAR, 0x964B00, 0xD2B48C);
    public static final RegistryObject<Item> BLACK_BEAR_EGG = registerSpawnEgg(TFCEntities.BLACK_BEAR, 0x000000, 0x333333);
    public static final RegistryObject<Item> COUGAR_EGG = registerSpawnEgg(TFCEntities.COUGAR, 0xb37f51, 0xe5c6a7);
    public static final RegistryObject<Item> PANTHER_EGG = registerSpawnEgg(TFCEntities.PANTHER, 0x000000, 0xffff00);
    public static final RegistryObject<Item> LION_EGG = registerSpawnEgg(TFCEntities.LION, 0xf4d988, 0x663d18);
    public static final RegistryObject<Item> SABERTOOTH_EGG = registerSpawnEgg(TFCEntities.SABERTOOTH, 0xc28a30, 0xc2beb2);
    public static final RegistryObject<Item> WOLF_EGG = registerSpawnEgg(TFCEntities.WOLF, 14144467, 0xc2beb2);
    public static final RegistryObject<Item> DIREWOLF_EGG = registerSpawnEgg(TFCEntities.DIREWOLF, 0x6f3b12, 14144467);
    public static final RegistryObject<Item> SQUID_EGG = registerSpawnEgg(TFCEntities.SQUID, 2243405, 7375001);
    public static final RegistryObject<Item> OCTOPOTEUTHIS_EGG = registerSpawnEgg(TFCEntities.OCTOPOTEUTHIS, 611926, 8778172);
    public static final RegistryObject<Item> PIG_EGG = registerSpawnEgg(TFCEntities.PIG, 15771042, 14377823);
    public static final RegistryObject<Item> COW_EGG = registerSpawnEgg(TFCEntities.COW, 4470310, 10592673);
    public static final RegistryObject<Item> GOAT_EGG = registerSpawnEgg(TFCEntities.GOAT, 0xDDDDDD, 0x776677);
    public static final RegistryObject<Item> YAK_EGG = registerSpawnEgg(TFCEntities.YAK, 0xd7c18e, 0x6f3b12);
    public static final RegistryObject<Item> ALPACA_EGG = registerSpawnEgg(TFCEntities.ALPACA, 0x00CC66, 0x006633);
    public static final RegistryObject<Item> SHEEP_EGG = registerSpawnEgg(TFCEntities.SHEEP, 0xFFFFFF, 0xEEEEEE);
    public static final RegistryObject<Item> MUSK_OX_EGG = registerSpawnEgg(TFCEntities.MUSK_OX, 0x6f3b12, 0xd7c18e);
    public static final RegistryObject<Item> CHICKEN_EGG = registerSpawnEgg(TFCEntities.CHICKEN, 10592673, 16711680);
    public static final RegistryObject<Item> DUCK_EGG = registerSpawnEgg(TFCEntities.DUCK, 0x654b17, 0x2a803e);
    public static final RegistryObject<Item> QUAIL_EGG = registerSpawnEgg(TFCEntities.QUAIL, 0x8081a5, 0xDDDDDD);
    public static final RegistryObject<Item> RABBIT_EGG = registerSpawnEgg(TFCEntities.RABBIT, 10051392, 7555121);
    public static final RegistryObject<Item> FOX_EGG = registerSpawnEgg(TFCEntities.FOX, 14005919, 1339625);
    public static final RegistryObject<Item> BOAR_EGG = registerSpawnEgg(TFCEntities.BOAR, 0x8081a5, 0x006633);
    public static final RegistryObject<Item> OCELOT_EGG = registerSpawnEgg(TFCEntities.OCELOT, 15720061, 5653556);
    public static final RegistryObject<Item> DEER_EGG = registerSpawnEgg(TFCEntities.DEER, 0x6f3b12, 0x006633);
    public static final RegistryObject<Item> MOOSE_EGG = registerSpawnEgg(TFCEntities.MOOSE, 0x654b17, 0xDDDDDD);
    public static final RegistryObject<Item> GROUSE_EGG = registerSpawnEgg(TFCEntities.GROUSE, 0xbda878, 0xDDDDDD);
    public static final RegistryObject<Item> PHEASANT_EGG = registerSpawnEgg(TFCEntities.PHEASANT, 0xe59c10, 0x004195);
    public static final RegistryObject<Item> TURKEY_EGG = registerSpawnEgg(TFCEntities.TURKEY, 0x758db3, 0xf16b7a);
    public static final RegistryObject<Item> RAT_EGG = registerSpawnEgg(TFCEntities.RAT, 0xd7c18e, 12623485);
    public static final RegistryObject<Item> DONKEY_EGG = registerSpawnEgg(TFCEntities.DONKEY, 5457209, 8811878);
    public static final RegistryObject<Item> MULE_EGG = registerSpawnEgg(TFCEntities.MULE, 1769984, 5321501);
    public static final RegistryObject<Item> HORSE_EGG = registerSpawnEgg(TFCEntities.HORSE, 12623485, 15656192);
    public static final RegistryObject<Item> CAT_EGG = registerSpawnEgg(TFCEntities.CAT, 15714446, 9794134);
    public static final RegistryObject<Item> DOG_EGG = registerSpawnEgg(TFCEntities.DOG, 14144467, 13545366);
    public static final RegistryObject<Item> PANDA_EGG = registerSpawnEgg(TFCEntities.PANDA, 15198183, 1776418);

    // Pottery

    public static final RegistryObject<Item> UNFIRED_BRICK = register("ceramic/unfired_brick", MISC);
    public static final RegistryObject<Item> UNFIRED_CRUCIBLE = register("ceramic/unfired_crucible", MISC);
    public static final RegistryObject<Item> UNFIRED_FLOWER_POT = register("ceramic/unfired_flower_pot", MISC);
    public static final RegistryObject<Item> UNFIRED_PAN = register("ceramic/unfired_pan", MISC);

    public static final RegistryObject<Item> UNFIRED_BOWL = register("ceramic/unfired_bowl", MISC);
    public static final RegistryObject<Item> BOWL = register("ceramic/bowl", MISC);

    public static final RegistryObject<Item> UNFIRED_FIRE_BRICK = register("ceramic/unfired_fire_brick", MISC);
    public static final RegistryObject<Item> FIRE_BRICK = register("ceramic/fire_brick", MISC);

    public static final RegistryObject<Item> UNFIRED_JUG = register("ceramic/unfired_jug", MISC);
    public static final RegistryObject<Item> JUG = register("ceramic/jug", () -> new JugItem(new Item.Properties().tab(MISC).stacksTo(1), TFCConfig.SERVER.jugCapacity, TFCTags.Fluids.USABLE_IN_JUG));

    public static final RegistryObject<Item> UNFIRED_POT = register("ceramic/unfired_pot", MISC);
    public static final RegistryObject<Item> POT = register("ceramic/pot", MISC);

    public static final RegistryObject<Item> UNFIRED_SPINDLE_HEAD = register("ceramic/unfired_spindle_head", MISC);
    public static final RegistryObject<Item> SPINDLE_HEAD = register("ceramic/spindle_head", MISC);

    public static final RegistryObject<Item> UNFIRED_VESSEL = register("ceramic/unfired_vessel", MISC);
    public static final RegistryObject<Item> VESSEL = register("ceramic/vessel", () -> new VesselItem(new Item.Properties().tab(MISC)));

    public static final Map<DyeColor, RegistryObject<Item>> UNFIRED_GLAZED_VESSELS = Helpers.mapOfKeys(DyeColor.class, color ->
        register("ceramic/" + color + "_unfired_vessel", MISC)
    );

    public static final Map<DyeColor, RegistryObject<Item>> GLAZED_VESSELS = Helpers.mapOfKeys(DyeColor.class, color ->
        register("ceramic/" + color + "_glazed_vessel", () -> new VesselItem(new Item.Properties().tab(MISC)))
    );

    public static final Map<Metal.ItemType, RegistryObject<Item>> UNFIRED_MOLDS = Helpers.mapOfKeys(Metal.ItemType.class, Metal.ItemType::hasMold, type ->
        register("ceramic/unfired_" + type.name() + "_mold", MISC)
    );

    public static final Map<Metal.ItemType, RegistryObject<Item>> MOLDS = Helpers.mapOfKeys(Metal.ItemType.class, Metal.ItemType::hasMold, type ->
        register("ceramic/" + type.name() + "_mold", () -> new MoldItem(type, new Item.Properties().tab(MISC)))
    );

    public static final RegistryObject<Item> UNFIRED_BELL_MOLD = register("ceramic/unfired_bell_mold", MISC);
    public static final RegistryObject<Item> BELL_MOLD = register("ceramic/bell_mold", () -> new MoldItem(TFCConfig.SERVER.moldBellCapacity::get, TFCTags.Fluids.USABLE_IN_BELL_MOLD, new Item.Properties().tab(MISC)));
    public static final RegistryObject<Item> UNFIRED_FIRE_INGOT_MOLD = register("ceramic/unfired_fire_ingot_mold", MISC);
    public static final RegistryObject<Item> FIRE_INGOT_MOLD = register("ceramic/fire_ingot_mold", () -> new MoldItem(TFCConfig.SERVER.moldFireIngotCapacity::get, TFCTags.Fluids.USABLE_IN_INGOT_MOLD, new Item.Properties().tab(MISC)));

    public static final RegistryObject<Item> UNFIRED_LARGE_VESSEL = register("ceramic/unfired_large_vessel", MISC);
    public static final Map<DyeColor, RegistryObject<Item>> UNFIRED_GLAZED_LARGE_VESSELS = Helpers.mapOfKeys(DyeColor.class, color ->
        register("ceramic/unfired_large_vessel/" + color, MISC)
    );

    // Fluid Buckets

    public static final Map<FluidType, RegistryObject<BucketItem>> FLUID_BUCKETS = FluidType.mapOf(fluid ->
        register("bucket/" + fluid.name(), () -> new BucketItem(fluid.fluid(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)))
    );

    public static final RegistryObject<FluidContainerItem> RED_STEEL_BUCKET = register("metal/bucket/red_steel", () -> new FluidContainerItem(new Item.Properties().tab(MISC), () -> FluidHelpers.BUCKET_VOLUME, TFCTags.Fluids.USABLE_IN_RED_STEEL_BUCKET, true, true));
    public static final RegistryObject<FluidContainerItem> BLUE_STEEL_BUCKET = register("metal/bucket/blue_steel", () -> new FluidContainerItem(new Item.Properties().tab(MISC), () -> FluidHelpers.BUCKET_VOLUME, TFCTags.Fluids.USABLE_IN_BLUE_STEEL_BUCKET, true, true));

    public static final RegistryObject<MobBucketItem> COD_BUCKET = register("bucket/cod", () -> new MobBucketItem(TFCEntities.COD, TFCFluids.SALT_WATER.source(), () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));
    public static final RegistryObject<MobBucketItem> PUFFERFISH_BUCKET = register("bucket/pufferfish", () -> new MobBucketItem(TFCEntities.PUFFERFISH, TFCFluids.SALT_WATER.source(), () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));
    public static final RegistryObject<MobBucketItem> JELLYFISH_BUCKET = register("bucket/jellyfish", () -> new MobBucketItem(TFCEntities.JELLYFISH, TFCFluids.SALT_WATER.source(), () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));
    public static final RegistryObject<MobBucketItem> TROPICAL_FISH_BUCKET = register("bucket/tropical_fish", () -> new MobBucketItem(TFCEntities.TROPICAL_FISH, TFCFluids.SALT_WATER.source(), () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));

    public static final RegistryObject<MobBucketItem> BLUEGILL_BUCKET = register("bucket/bluegill", () -> new MobBucketItem(TFCEntities.BLUEGILL, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));
    public static final RegistryObject<MobBucketItem> SALMON_BUCKET = register("bucket/salmon", () -> new MobBucketItem(TFCEntities.SALMON, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));

    public static void editItemMaxDamage()
    {
        ((ItemAccessor) Items.FLINT_AND_STEEL).accessor$setMaxDamage(TFCTiers.STEEL.getUses());
    }

    private static <T extends EntityType<? extends Mob>> RegistryObject<Item> registerSpawnEgg(RegistryObject<T> entity, int color1, int color2)
    {
        return register("spawn_egg/" + entity.getId().getPath(), () -> new ForgeSpawnEggItem(entity, color1, color2, new Item.Properties().tab(MISC)));
    }

    private static RegistryObject<Item> register(String name, CreativeModeTab group)
    {
        return register(name, () -> new Item(new Item.Properties().tab(group)));
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item)
    {
        return ITEMS.register(name.toLowerCase(Locale.ROOT), item);
    }
}