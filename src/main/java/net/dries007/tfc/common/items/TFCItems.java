/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.Gem;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.berrybush.Berry;
import net.dries007.tfc.common.blocks.fruittree.Fruit;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.types.*;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.common.TFCItemGroup.*;

/**
 * Collection of all TFC items.
 * Unused is as the registry object fields themselves may be unused but they are required to register each item.
 * Whenever possible, avoid using hardcoded references to these, prefer tags or recipes.
 */
@SuppressWarnings("unused")
public final class TFCItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    // Ores

    public static final Map<Ore.Default, RegistryObject<Item>> ORES = Helpers.mapOfKeys(Ore.Default.class, ore -> !ore.isGraded(), type ->
        register("ore/" + type.name().toLowerCase(), TFCItemGroup.ORES)
    );
    public static final Map<Ore.Default, Map<Ore.Grade, RegistryObject<Item>>> GRADED_ORES = Helpers.mapOfKeys(Ore.Default.class, Ore.Default::isGraded, ore ->
        Helpers.mapOfKeys(Ore.Grade.class, grade ->
            register(("ore/" + grade.name() + '_' + ore.name()).toLowerCase(), TFCItemGroup.ORES)
        )
    );

    public static final Map<Gem, RegistryObject<Item>> GEMS = Helpers.mapOfKeys(Gem.class, gem ->
        register(("gem/" + gem.name()).toLowerCase(), TFCItemGroup.ORES)
    );

    public static final Map<Metal.Default, Map<Metal.ItemType, RegistryObject<Item>>> METAL_ITEMS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        Helpers.mapOfKeys(Metal.ItemType.class, type -> type.hasMetal(metal), type ->
            register(("metal/" + type.name() + "/" + metal.name()).toLowerCase(), () -> type.create(metal))
        )
    );

    // Flora

    public static final Map<Coral, RegistryObject<Item>> CORAL_FANS = Helpers.mapOfKeys(Coral.class, color ->
        register("coral/" + color.toString().toLowerCase() + "_coral_fan", () -> new WallOrFloorItem(TFCBlocks.CORAL.get(color).get(Coral.BlockType.CORAL_FAN).get(), TFCBlocks.CORAL.get(color).get(Coral.BlockType.CORAL_WALL_FAN).get(), (new Item.Properties()).tab(FLORA)))
    );

    public static final Map<Coral, RegistryObject<Item>> DEAD_CORAL_FANS = Helpers.mapOfKeys(Coral.class, color ->
        register("coral/" + color.toString().toLowerCase() + "_dead_coral_fan", () -> new WallOrFloorItem(TFCBlocks.CORAL.get(color).get(Coral.BlockType.DEAD_CORAL_FAN).get(), TFCBlocks.CORAL.get(color).get(Coral.BlockType.DEAD_CORAL_WALL_FAN).get(), (new Item.Properties()).tab(FLORA)))
    );

    // Rock Stuff

    public static final Map<RockCategory, Map<RockCategory.ItemType, RegistryObject<Item>>> ROCK_TOOLS = Helpers.mapOfKeys(RockCategory.class, category ->
        Helpers.mapOfKeys(RockCategory.ItemType.class, type ->
            register(("stone/" + type.name() + "/" + category.name()).toLowerCase(), () -> type.create(category))
        )
    );

    public static final Map<Rock.Default, RegistryObject<Item>> BRICKS = Helpers.mapOfKeys(Rock.Default.class, type ->
        register("brick/" + type.name().toLowerCase(), MISC)
    );

    // Misc

    public static final Map<HideItemType, Map<HideItemType.Size, RegistryObject<Item>>> HIDES = Helpers.mapOfKeys(HideItemType.class, type ->
        Helpers.mapOfKeys(HideItemType.Size.class, size ->
            register((size.name() + '_' + type.name() + "_hide").toLowerCase(), () -> new Item(new Item.Properties().tab(MISC)))
        )
    );

    public static final Map<Gem, RegistryObject<Item>> GEM_DUST = Helpers.mapOfKeys(Gem.class, gem ->
        register(("powder/" + gem.name()).toLowerCase(), MISC)
    );

    public static final Map<Powder, RegistryObject<Item>> POWDERS = Helpers.mapOfKeys(Powder.class, powder ->
        register(("powder/" + powder.name()).toLowerCase(), MISC)
    );

    public static final Map<Wood.Default, RegistryObject<Item>> LUMBER = Helpers.mapOfKeys(Wood.Default.class, wood ->
        register(("wood/lumber/" + wood.name()).toLowerCase(), MISC)
    );

    public static final Map<Berry, RegistryObject<Item>> BERRIES = Helpers.mapOfKeys(Berry.class, berry ->
        register(("food/" + berry.name()).toLowerCase(), MISC)
    );

    public static final Map<Fruit, RegistryObject<Item>> FRUITS = Helpers.mapOfKeys(Fruit.class, fruit ->
        register(("food/" + fruit.name()).toLowerCase(), MISC)
    );

    public static final RegistryObject<Item> ALABASTER_BRICK = register("alabaster_brick", MISC);
    public static final RegistryObject<Item> BRASS_MECHANISMS = register("brass_mechanisms", MISC);
    public static final RegistryObject<Item> BURLAP_CLOTH = register("burlap_cloth", MISC);
    public static final RegistryObject<Item> DIRTY_JUTE_NET = register("dirty_jute_net", MISC);
    public static final RegistryObject<Item> FIRE_CLAY = register("fire_clay", MISC);
    public static final RegistryObject<Item> FIRESTARTER = register("firestarter", () -> new FirestarterItem(new Item.Properties().tab(MISC).defaultDurability(8)));
    public static final RegistryObject<Item> GLASS_SHARD = register("glass_shard", MISC);
    public static final RegistryObject<Item> GLUE = register("glue", MISC);
    public static final RegistryObject<Item> HALTER = register("halter", MISC);
    public static final RegistryObject<Item> JUTE = register("jute", MISC);
    public static final RegistryObject<Item> JUTE_DISC = register("jute_disc", MISC);
    public static final RegistryObject<Item> JUTE_FIBER = register("jute_fiber", MISC);
    public static final RegistryObject<Item> JUTE_NET = register("jute_net", MISC);
    public static final RegistryObject<Item> MORTAR = register("mortar", MISC);
    public static final RegistryObject<Item> OLIVE_JUTE_DISC = register("olive_jute_disc", MISC);
    public static final RegistryObject<Item> OLIVE_PASTE = register("olive_paste", MISC);
    public static final RegistryObject<Item> SILK_CLOTH = register("silk_cloth", MISC);
    public static final RegistryObject<Item> SPINDLE = register("spindle", () -> new Item(new Item.Properties().tab(MISC).defaultDurability(40)));
    public static final RegistryObject<Item> STICK_BUNCH = register("stick_bunch", MISC);
    public static final RegistryObject<Item> STICK_BUNDLE = register("stick_bundle", MISC);
    public static final RegistryObject<Item> STRAW = register("straw", MISC);
    public static final RegistryObject<Item> TORCH = register("torch", () -> new TorchItem(TFCBlocks.TORCH.get(), TFCBlocks.WALL_TORCH.get(), new Item.Properties().tab(DECORATIONS)));
    public static final RegistryObject<Item> TORCH_DEAD = register("dead_torch", () -> new WallOrFloorItem(TFCBlocks.DEAD_TORCH.get(), TFCBlocks.DEAD_WALL_TORCH.get(), new Item.Properties().tab(DECORATIONS)));
    public static final RegistryObject<Item> WOOL = register("wool", MISC);
    public static final RegistryObject<Item> WOOL_CLOTH = register("wool_cloth", MISC);
    public static final RegistryObject<Item> WOOL_YARN = register("wool_yarn", MISC);
    public static final RegistryObject<Item> WROUGHT_IRON_GRILL = register("wrought_iron_grill", MISC);

    // Pottery

    public static final RegistryObject<Item> UNFIRED_BRICK = register("ceramic/unfired_brick", MISC);
    public static final RegistryObject<Item> UNFIRED_CRUCIBLE = register("ceramic/unfired_crucible", MISC);
    public static final RegistryObject<Item> UNFIRED_FLOWER_POT = register("ceramic/unfired_flower_pot", MISC);

    public static final RegistryObject<Item> UNFIRED_BOWL = register("ceramic/unfired_bowl", MISC);
    public static final RegistryObject<Item> BOWL = register("ceramic/bowl", MISC);

    public static final RegistryObject<Item> UNFIRED_FIRE_BRICK = register("ceramic/unfired_fire_brick", MISC);
    public static final RegistryObject<Item> FIRE_BRICK = register("ceramic/fire_brick", MISC);

    public static final RegistryObject<Item> UNFIRED_JUG = register("ceramic/unfired_jug", MISC);
    public static final RegistryObject<Item> JUG = register("ceramic/jug", () -> new JugItem(new Item.Properties().tab(MISC)));

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

    //todo: molds

    // Fluid Buckets

    public static final Map<Metal.Default, RegistryObject<BucketItem>> METAL_FLUID_BUCKETS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        register("bucket/metal/" + metal.name().toLowerCase(), () -> new BucketItem(TFCFluids.METALS.get(metal).getSecond(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)))
    );

    public static final RegistryObject<BucketItem> SALT_WATER_BUCKET = register("bucket/salt_water", () -> new BucketItem(TFCFluids.SALT_WATER.getSecond(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));
    public static final RegistryObject<BucketItem> SPRING_WATER_BUCKET = register("bucket/spring_water", () -> new BucketItem(TFCFluids.SPRING_WATER.getSecond(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)));


    private static RegistryObject<Item> register(String name, ItemGroup group)
    {
        return register(name, () -> new Item(new Item.Properties().tab(group)));
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item)
    {
        return ITEMS.register(name, item);
    }
}