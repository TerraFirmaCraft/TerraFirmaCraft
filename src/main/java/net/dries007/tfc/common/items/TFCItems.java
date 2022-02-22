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
import net.minecraft.world.item.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.Gem;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.common.TFCItemGroup.*;

/**
 * Collection of all TFC items.
 * Organized by {@link TFCItemGroup}
 * Unused is as the registry object fields themselves may be unused but they are required to register each item.
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

    public static final Map<Wood, RegistryObject<Item>> LUMBER = Helpers.mapOfKeys(Wood.class, wood -> register("wood/lumber/" + wood.name(), MISC));

    public static final Map<Wood, RegistryObject<Item>> SUPPORTS = Helpers.mapOfKeys(Wood.class, wood ->
        register("wood/support/" + wood.name(), () -> new StandingAndWallBlockItem(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.VERTICAL_SUPPORT).get(), TFCBlocks.WOODS.get(wood).get(Wood.BlockType.HORIZONTAL_SUPPORT).get(), new Item.Properties().tab(WOOD)))
    );

    public static final Map<Wood, RegistryObject<Item>> BOATS = Helpers.mapOfKeys(Wood.class, wood -> register("wood/boat/" + wood.name(), () -> new TFCBoatItem(TFCEntities.BOATS.get(wood), new Item.Properties().tab(WOOD))));

    // Food

    public static final Map<Food, RegistryObject<Item>> FOOD = Helpers.mapOfKeys(Food.class, food -> register("food/" + food.name(), () -> new DecayingItem(new Item.Properties().food(food.getFoodProperties()).tab(TFCItemGroup.FOOD))));

    // Flora

    public static final Map<Crop, RegistryObject<Item>> CROP_SEEDS = Helpers.mapOfKeys(Crop.class, crop ->
        register("seeds/" + crop.name().toLowerCase(), () -> new ItemNameBlockItem(TFCBlocks.CROPS.get(crop).get(), new Item.Properties().tab(FLORA)))
    );

    public static final Map<Coral, RegistryObject<Item>> CORAL_FANS = Helpers.mapOfKeys(Coral.class, color ->
        register("coral/" + color.toString() + "_coral_fan", () -> new StandingAndWallBlockItem(TFCBlocks.CORAL.get(color).get(Coral.BlockType.CORAL_FAN).get(), TFCBlocks.CORAL.get(color).get(Coral.BlockType.CORAL_WALL_FAN).get(), new Item.Properties().tab(FLORA)))
    );

    public static final Map<Coral, RegistryObject<Item>> DEAD_CORAL_FANS = Helpers.mapOfKeys(Coral.class, color ->
        register("coral/" + color.toString() + "_dead_coral_fan", () -> new StandingAndWallBlockItem(TFCBlocks.CORAL.get(color).get(Coral.BlockType.DEAD_CORAL_FAN).get(), TFCBlocks.CORAL.get(color).get(Coral.BlockType.DEAD_CORAL_WALL_FAN).get(), new Item.Properties().tab(FLORA)))
    );

    // Decorations

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

    public static final RegistryObject<Item> BRASS_MECHANISMS = register("brass_mechanisms", MISC);
    public static final RegistryObject<Item> BURLAP_CLOTH = register("burlap_cloth", MISC);
    public static final RegistryObject<Item> COMPOST = register("compost", MISC);
    public static final RegistryObject<Item> DAUB = register("daub", MISC);
    public static final RegistryObject<Item> DIRTY_JUTE_NET = register("dirty_jute_net", MISC);
    public static final RegistryObject<Item> FIRE_CLAY = register("fire_clay", MISC);
    public static final RegistryObject<Item> FIRESTARTER = register("firestarter", () -> new FirestarterItem(new Item.Properties().tab(MISC).defaultDurability(8)));
    public static final RegistryObject<Item> GLASS_SHARD = register("glass_shard", MISC);
    public static final RegistryObject<Item> GLUE = register("glue", MISC);
    public static final RegistryObject<Item> HALTER = register("halter", MISC);
    public static final RegistryObject<Item> JUTE = register("jute", MISC);
    public static final RegistryObject<Item> JUTE_FIBER = register("jute_fiber", MISC);
    public static final RegistryObject<Item> JUTE_NET = register("jute_net", MISC);
    public static final RegistryObject<Item> HANDSTONE = register("handstone", () -> new Item(new Item.Properties().tab(MISC).defaultDurability(250)));
    public static final RegistryObject<Item> MORTAR = register("mortar", MISC);
    public static final RegistryObject<Item> OLIVE_PASTE = register("olive_paste", MISC);
    public static final RegistryObject<Item> ROTTEN_COMPOST = register("rotten_compost", () -> new RottenCompostItem(new Item.Properties().tab(MISC)));
    public static final RegistryObject<Item> SILK_CLOTH = register("silk_cloth", MISC);
    public static final RegistryObject<Item> SPINDLE = register("spindle", () -> new Item(new Item.Properties().tab(MISC).defaultDurability(40)));
    public static final RegistryObject<Item> STICK_BUNCH = register("stick_bunch", MISC);
    public static final RegistryObject<Item> STICK_BUNDLE = register("stick_bundle", MISC);
    public static final RegistryObject<Item> STRAW = register("straw", MISC);
    public static final RegistryObject<Item> WOODEN_BUCKET = register("wooden_bucket", () -> new WoodenBucketItem(new Item.Properties().tab(MISC).stacksTo(1), TFCConfig.SERVER.woodenBucketCapacity::get));
    public static final RegistryObject<Item> WOOL = register("wool", MISC);
    public static final RegistryObject<Item> WOOL_CLOTH = register("wool_cloth", MISC);
    public static final RegistryObject<Item> WOOL_YARN = register("wool_yarn", MISC);
    public static final RegistryObject<Item> WROUGHT_IRON_GRILL = register("wrought_iron_grill", MISC);

    public static final RegistryObject<Item> EMPTY_PAN = register("pan/empty", () -> new EmptyPanItem(new Item.Properties().tab(MISC)));
    public static final RegistryObject<Item> FILLED_PAN = register("pan/filled", () -> new PanItem(new Item.Properties().tab(MISC).stacksTo(1)));

    public static final RegistryObject<Item> COD_EGG = registerSpawnEgg(TFCEntities.COD, 12691306, 15058059);
    public static final RegistryObject<Item> PUFFERFISH_EGG = registerSpawnEgg(TFCEntities.PUFFERFISH, 16167425, 3654642);
    public static final RegistryObject<Item> TROPICAL_FISH_EGG = registerSpawnEgg(TFCEntities.TROPICAL_FISH, 15690005, 16775663);
    public static final RegistryObject<Item> JELLYFISH_EGG = registerSpawnEgg(TFCEntities.JELLYFISH, 0xE83D0E, 0x11F2F2);
    public static final RegistryObject<Item> SALMON_EGG = registerSpawnEgg(TFCEntities.SALMON, 10489616, 951412);
    public static final RegistryObject<Item> BLUEGILL_EGG = registerSpawnEgg(TFCEntities.BLUEGILL, 0x00658A, 0xE3E184);

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
    public static final RegistryObject<Item> JUG = register("ceramic/jug", () -> new JugItem(new Item.Properties().tab(MISC).stacksTo(1), TFCConfig.SERVER.jugCapacity::get));

    public static final RegistryObject<Item> UNFIRED_POT = register("ceramic/unfired_pot", MISC);
    public static final RegistryObject<Item> POT = register("ceramic/pot", MISC);

    public static final RegistryObject<Item> UNFIRED_SPINDLE_HEAD = register("ceramic/unfired_spindle_head", MISC);
    public static final RegistryObject<Item> SPINDLE_HEAD = register("ceramic/spindle_head", MISC);

    public static final RegistryObject<Item> UNFIRED_VESSEL = register("ceramic/unfired_vessel", MISC);
    public static final RegistryObject<Item> VESSEL = register("ceramic/vessel", () -> new VesselItem(new Item.Properties().tab(MISC).stacksTo(1)));

    public static final Map<DyeColor, RegistryObject<Item>> UNFIRED_GLAZED_VESSELS = Helpers.mapOfKeys(DyeColor.class, color ->
        register("ceramic/" + color + "_unfired_vessel", MISC)
    );

    public static final Map<DyeColor, RegistryObject<Item>> GLAZED_VESSELS = Helpers.mapOfKeys(DyeColor.class, color ->
        register("ceramic/" + color + "_glazed_vessel", () -> new VesselItem(new Item.Properties().tab(MISC).stacksTo(1)))
    );

    public static final Map<Metal.ItemType, RegistryObject<Item>> UNFIRED_MOLDS = Helpers.mapOfKeys(Metal.ItemType.class, Metal.ItemType::hasMold, type ->
        register("ceramic/unfired_" + type.name() + "_mold", MISC)
    );

    public static final Map<Metal.ItemType, RegistryObject<Item>> MOLDS = Helpers.mapOfKeys(Metal.ItemType.class, Metal.ItemType::hasMold, type ->
        register("ceramic/" + type.name() + "_mold", () -> new MoldItem(type, new Item.Properties().tab(MISC).stacksTo(1)))
    );

    // Fluid Buckets

    public static final Map<Metal.Default, RegistryObject<BucketItem>> METAL_FLUID_BUCKETS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        register("bucket/metal/" + metal.name(), () -> new BucketItem(TFCFluids.METALS.get(metal).getSecond(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)))
    );

    public static final Map<SimpleFluid, RegistryObject<BucketItem>> SIMPLE_FLUID_BUCKETS = Helpers.mapOfKeys(SimpleFluid.class, fluid ->
        register("bucket/" + fluid.getId(), () -> new BucketItem(TFCFluids.SIMPLE_FLUIDS.get(fluid).getSecond(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)))
    );

    public static final RegistryObject<BucketItem> SALT_WATER_BUCKET = register("bucket/salt_water", () -> new BucketItem(TFCFluids.SALT_WATER.getSecond(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));
    public static final RegistryObject<BucketItem> SPRING_WATER_BUCKET = register("bucket/spring_water", () -> new BucketItem(TFCFluids.SPRING_WATER.getSecond(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));

    public static final RegistryObject<MobBucketItem> COD_BUCKET = register("bucket/cod", () -> new TFCMobBucketItem(TFCEntities.COD, TFCFluids.SALT_WATER.getSecond(), () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));
    public static final RegistryObject<MobBucketItem> PUFFERFISH_BUCKET = register("bucket/pufferfish", () -> new TFCMobBucketItem(TFCEntities.PUFFERFISH, TFCFluids.SALT_WATER.getSecond(), () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));
    public static final RegistryObject<MobBucketItem> JELLYFISH_BUCKET = register("bucket/jellyfish", () -> new TFCMobBucketItem(TFCEntities.JELLYFISH, TFCFluids.SALT_WATER.getSecond(), () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));
    public static final RegistryObject<MobBucketItem> TROPICAL_FISH_BUCKET = register("bucket/tropical_fish", () -> new TFCMobBucketItem(TFCEntities.TROPICAL_FISH, TFCFluids.SALT_WATER.getSecond(), () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));

    public static final RegistryObject<MobBucketItem> BLUEGILL_BUCKET = register("bucket/bluegill", () -> new TFCMobBucketItem(TFCEntities.BLUEGILL, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));
    public static final RegistryObject<MobBucketItem> SALMON_BUCKET = register("bucket/salmon", () -> new TFCMobBucketItem(TFCEntities.SALMON, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));


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