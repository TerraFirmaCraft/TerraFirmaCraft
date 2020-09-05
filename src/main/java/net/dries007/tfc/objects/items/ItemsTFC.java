/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSnow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.*;
import net.dries007.tfc.objects.Gem;
import net.dries007.tfc.objects.Powder;
import net.dries007.tfc.objects.blocks.BlockSlabTFC;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockDoorTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.items.ceramics.*;
import net.dries007.tfc.objects.items.food.ItemDynamicBowlFood;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.objects.items.food.ItemSandwich;
import net.dries007.tfc.objects.items.itemblock.ItemBlockTFC;
import net.dries007.tfc.objects.items.itemblock.ItemBlockTorch;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.metal.ItemMetalBucket;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.objects.items.rock.ItemBrickTFC;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.items.rock.ItemRockToolHead;
import net.dries007.tfc.objects.items.wood.ItemBoatTFC;
import net.dries007.tfc.objects.items.wood.ItemDoorTFC;
import net.dries007.tfc.objects.items.wood.ItemLumberTFC;
import net.dries007.tfc.objects.items.wood.ItemWoodenBucket;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.agriculture.Crop;
import net.dries007.tfc.util.agriculture.Food;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;
import static net.dries007.tfc.util.Helpers.getNull;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class ItemsTFC
{
    public static final ItemGoldPan GOLDPAN = getNull();
    public static final ItemMisc STRAW = getNull();
    public static final Item HANDSTONE = getNull();
    public static final Item WROUGHT_IRON_GRILL = getNull();
    public static final Item GLUE = getNull();
    public static final Item BRASS_MECHANISMS = getNull();

    @GameRegistry.ObjectHolder("crop/product/jute")
    public static final ItemMisc JUTE = getNull();
    @GameRegistry.ObjectHolder("crop/product/jute_fiber")
    public static final ItemMisc JUTE_FIBER = getNull();
    @GameRegistry.ObjectHolder("crop/product/burlap_cloth")
    public static final ItemMisc BURLAP_CLOTH = getNull();
    @GameRegistry.ObjectHolder("animal/product/wool")
    public static final ItemMisc WOOL = getNull();
    @GameRegistry.ObjectHolder("animal/product/wool_yarn")
    public static final ItemMisc WOOL_YARN = getNull();
    @GameRegistry.ObjectHolder("animal/product/wool_cloth")
    public static final ItemMisc WOOL_CLOTH = getNull();
    @GameRegistry.ObjectHolder("animal/product/silk_cloth")
    public static final ItemMisc SILK_CLOTH = getNull();

    @GameRegistry.ObjectHolder("ceramics/unfired/fire_brick")
    public static final ItemPottery UNFIRED_FIRE_BRICK = getNull();
    @GameRegistry.ObjectHolder("ceramics/fired/fire_brick")
    public static final ItemPottery FIRED_FIRE_BRICK = getNull();
    @GameRegistry.ObjectHolder("ceramics/unfired/vessel")
    public static final ItemPottery UNFIRED_VESSEL = getNull();
    @GameRegistry.ObjectHolder("ceramics/fired/vessel")
    public static final ItemPottery FIRED_VESSEL = getNull();
    @GameRegistry.ObjectHolder("ceramics/unfired/vessel_glazed")
    public static final ItemPottery UNFIRED_VESSEL_GLAZED = getNull();
    @GameRegistry.ObjectHolder("ceramics/fired/vessel_glazed")
    public static final ItemPottery FIRED_VESSEL_GLAZED = getNull();
    @GameRegistry.ObjectHolder("ceramics/unfired/jug")
    public static final ItemPottery UNFIRED_JUG = getNull();
    @GameRegistry.ObjectHolder("ceramics/fired/jug")
    public static final ItemPottery FIRED_JUG = getNull();
    @GameRegistry.ObjectHolder("ceramics/unfired/pot")
    public static final ItemPottery UNFIRED_POT = getNull();
    @GameRegistry.ObjectHolder("ceramics/fired/pot")
    public static final ItemPottery FIRED_POT = getNull();
    @GameRegistry.ObjectHolder("ceramics/unfired/bowl")
    public static final ItemPottery UNFIRED_BOWL = getNull();
    @GameRegistry.ObjectHolder("ceramics/fired/bowl")
    public static final ItemPottery FIRED_BOWL = getNull();
    @GameRegistry.ObjectHolder("ceramics/unfired/spindle")
    public static final ItemPottery UNFIRED_SPINDLE = getNull();
    @GameRegistry.ObjectHolder("ceramics/fired/spindle")
    public static final ItemPottery FIRED_SPINDLE = getNull();
    @GameRegistry.ObjectHolder("ceramics/unfired/large_vessel")
    public static final ItemPottery UNFIRED_LARGE_VESSEL = getNull();
    @GameRegistry.ObjectHolder("ceramics/unfired/crucible")
    public static final ItemPottery UNFIRED_CRUCIBLE = getNull();
    @GameRegistry.ObjectHolder("ceramics/fire_clay")
    public static final Item FIRE_CLAY = getNull();

    @GameRegistry.ObjectHolder("bloom/unrefined")
    public static final ItemBloom UNREFINED_BLOOM = getNull();
    @GameRegistry.ObjectHolder("bloom/refined")
    public static final ItemBloom REFINED_BLOOM = getNull();

    public static final ItemTFC MORTAR = getNull();

    public static final ItemTFC HALTER = getNull();

    @GameRegistry.ObjectHolder("powder/salt")
    public static final ItemPowder SALT = getNull();

    @GameRegistry.ObjectHolder("quiver")
    public static final ItemQuiver QUIVER = getNull();

    public static final ItemWoodenBucket WOODEN_BUCKET = getNull();

    @GameRegistry.ObjectHolder("metal/bucket/blue_steel")
    public static final ItemMetalBucket BLUE_STEEL_BUCKET = getNull();
    @GameRegistry.ObjectHolder("metal/bucket/red_steel")
    public static final ItemMetalBucket RED_STEEL_BUCKET = getNull();

    @GameRegistry.ObjectHolder("dye/black")
    public static final ItemMisc DYE_BLACK = getNull();
    @GameRegistry.ObjectHolder("dye/blue")
    public static final ItemMisc DYE_BLUE = getNull();
    @GameRegistry.ObjectHolder("dye/brown")
    public static final ItemMisc DYE_BROWN = getNull();
    @GameRegistry.ObjectHolder("dye/white")
    public static final ItemMisc DYE_WHITE = getNull();

    @GameRegistry.ObjectHolder("ceramics/unfired/clay_brick")
    public static final ItemPottery UNFIRED_BRICK = getNull();

    @GameRegistry.ObjectHolder("ceramics/unfired/clay_flower_pot")
    public static final ItemPottery UNFIRED_FLOWER_POT = getNull();

    @GameRegistry.ObjectHolder("ore/gypsum")
    public static final ItemOreTFC GYPSUM = getNull();

    @GameRegistry.ObjectHolder("crop/product/jute_disc")
    public static final Item JUTE_DISC = getNull();
    @GameRegistry.ObjectHolder("crop/product/jute_net")
    public static final Item JUTE_NET = getNull();
    @GameRegistry.ObjectHolder("crop/product/dirty_jute_net")
    public static final Item DIRTY_JUTE_NET = getNull();
    @GameRegistry.ObjectHolder("food/olive_paste")
    public static final Item OLIVE_PASTE = getNull();
    @GameRegistry.ObjectHolder("glass_shard")
    public static final Item GLASS_SHARD = getNull();
    @GameRegistry.ObjectHolder("stick_bunch")
    public static final Item STICK_BUNCH = getNull();
    @GameRegistry.ObjectHolder("wood_ash")
    public static final Item WOOD_ASH = getNull();

    private static ImmutableList<Item> allSimpleItems;
    private static ImmutableList<ItemOreTFC> allOreItems;
    private static ImmutableList<ItemGem> allGemItems;

    public static ImmutableList<Item> getAllSimpleItems()
    {
        return allSimpleItems;
    }

    public static ImmutableList<ItemOreTFC> getAllOreItems()
    {
        return allOreItems;
    }

    public static ImmutableList<ItemGem> getAllGemItems()
    {
        return allGemItems;
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> r = event.getRegistry();
        Builder<Item> simpleItems = ImmutableList.builder();

        simpleItems.add(register(r, "wand", new ItemDebug(), CT_MISC));
        simpleItems.add(register(r, "mortar", new ItemMisc(Size.TINY, Weight.VERY_LIGHT, "mortar"), CT_MISC));
        simpleItems.add(register(r, "halter", new ItemMisc(Size.SMALL, Weight.LIGHT, "halter"), CT_MISC));
        register(r, "wooden_bucket", new ItemWoodenBucket(), CT_WOOD); //not a simple item, use a custom model
        register(r, "metal/bucket/blue_steel", new ItemMetalBucket(Metal.BLUE_STEEL, Metal.ItemType.BUCKET), CT_METAL); //not a simple item, use a custom model
        register(r, "metal/bucket/red_steel", new ItemMetalBucket(Metal.RED_STEEL, Metal.ItemType.BUCKET), CT_METAL); //not a simple item, use a custom model

        {
            for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                simpleItems.add(register(r, "rock/" + rock.getRegistryName().getPath().toLowerCase(), new ItemRock(rock), CT_ROCK_ITEMS));
            for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                simpleItems.add(register(r, "brick/" + rock.getRegistryName().getPath().toLowerCase(), new ItemBrickTFC(rock), CT_ROCK_ITEMS));
        }

        {
            Builder<ItemOreTFC> b = new Builder<>();
            for (Ore ore : TFCRegistries.ORES.getValuesCollection())
            {
                b.add(register(r, "ore/" + ore.getRegistryName().getPath(), new ItemOreTFC(ore), CT_ROCK_ITEMS));
                if (ore.isGraded())
                {
                    simpleItems.add(register(r, "ore/small/" + ore.getRegistryName().getPath(), new ItemSmallOre(ore), CT_ROCK_ITEMS));
                }
            }
            allOreItems = b.build();

        }

        {
            Builder<ItemGem> b = new Builder<>();
            for (Gem gem : Gem.values())
                b.add(register(r, "gem/" + gem.name().toLowerCase(), new ItemGem(gem), CT_GEMS));
            allGemItems = b.build();
        }

        for (Metal.ItemType type : Metal.ItemType.values())
        {
            for (Metal metal : TFCRegistries.METALS.getValuesCollection())
            {
                if (type != Metal.ItemType.BUCKET && type.hasType(metal)) // buckets registered separately
                {
                    simpleItems.add(register(r, "metal/" + type.name().toLowerCase() + "/" + metal.getRegistryName().getPath(), Metal.ItemType.create(metal, type), CT_METAL));
                }
            }
        }

        BlocksTFC.getAllNormalItemBlocks().forEach(x -> registerItemBlock(r, x));
        BlocksTFC.getAllInventoryItemBlocks().forEach(x -> registerItemBlock(r, x));
        BlocksTFC.getAllBarrelItemBlocks().forEach(x -> registerItemBlock(r, x));

        for (BlockLogTFC log : BlocksTFC.getAllLogBlocks())
            simpleItems.add(register(r, log.getRegistryName().getPath(), new ItemBlockTFC(log), CT_WOOD));

        for (BlockDoorTFC door : BlocksTFC.getAllDoorBlocks())
            simpleItems.add(register(r, door.getRegistryName().getPath(), new ItemDoorTFC(door), CT_DECORATIONS));

        for (BlockSlabTFC.Half slab : BlocksTFC.getAllSlabBlocks())
            simpleItems.add(register(r, slab.getRegistryName().getPath(), new ItemSlabTFC(slab, slab, slab.doubleSlab), CT_DECORATIONS));

        for (Tree wood : TFCRegistries.TREES.getValuesCollection())
        {
            simpleItems.add(register(r, "wood/lumber/" + wood.getRegistryName().getPath(), new ItemLumberTFC(wood), CT_WOOD));
            simpleItems.add(register(r, "wood/boat/" + wood.getRegistryName().getPath(), new ItemBoatTFC(wood), CT_WOOD));
        }

        simpleItems.add(register(r, "stick_bunch", new ItemMisc(Size.NORMAL, Weight.LIGHT), CT_WOOD));
        simpleItems.add(register(r, "stick_bundle", new ItemMisc(Size.VERY_LARGE, Weight.MEDIUM, "log_wood", "stick_bundle"), CT_WOOD));
        simpleItems.add(register(r, "wood_ash", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_WOOD));

        for (RockCategory cat : TFCRegistries.ROCK_CATEGORIES.getValuesCollection())
        {
            for (Rock.ToolType type : Rock.ToolType.values())
            {
                simpleItems.add(register(r, "stone/" + type.name().toLowerCase() + "/" + cat.getRegistryName().getPath(), type.create(cat), CT_ROCK_ITEMS));
                simpleItems.add(register(r, "stone/" + type.name().toLowerCase() + "_head/" + cat.getRegistryName().getPath(), new ItemRockToolHead(cat, type), CT_ROCK_ITEMS));
            }
        }

        for (Powder powder : Powder.values())
            simpleItems.add(register(r, "powder/" + powder.name().toLowerCase(), new ItemPowder(powder), CT_MISC));

        { // POTTERY
            for (Metal.ItemType type : Metal.ItemType.values())
            {
                if (type.hasMold(null))
                {
                    // Not using registerPottery here because the ItemMold uses a custom ItemModelMesher, meaning it can't be in simpleItems
                    ItemPottery item = new ItemMold(type);
                    register(r, "ceramics/fired/mold/" + type.name().toLowerCase(), item, CT_POTTERY);
                    simpleItems.add(register(r, "ceramics/unfired/mold/" + type.name().toLowerCase(), new ItemUnfiredMold(type), CT_POTTERY));
                }
            }

            simpleItems.add(register(r, "ceramics/unfired/large_vessel", new ItemUnfiredLargeVessel(), CT_POTTERY));
            simpleItems.add(register(r, "ceramics/unfired/crucible", new ItemPottery(Size.LARGE, Weight.VERY_HEAVY), CT_POTTERY));

            registerPottery(simpleItems, r, "ceramics/unfired/vessel", "ceramics/fired/vessel", new ItemUnfiredSmallVessel(false), new ItemSmallVessel(false));
            registerPottery(null, r, "ceramics/unfired/vessel_glazed", "ceramics/fired/vessel_glazed", new ItemUnfiredSmallVessel(true), new ItemSmallVessel(true));

            ItemPottery firedPot = new ItemPottery(Size.LARGE, Weight.LIGHT);
            registerPottery(simpleItems, r, "ceramics/unfired/pot", "ceramics/fired/pot", new ItemPottery(Size.LARGE, Weight.LIGHT), firedPot);
            OreDictionaryHelper.register(firedPot, "cooking_pot");

            ItemPottery firedBowl = new ItemPottery(Size.VERY_SMALL, Weight.VERY_LIGHT);
            registerPottery(simpleItems, r, "ceramics/unfired/bowl", "ceramics/fired/bowl", new ItemPottery(Size.VERY_SMALL, Weight.VERY_LIGHT), firedBowl);
            OreDictionaryHelper.register(firedBowl, "bowl");

            registerPottery(simpleItems, r, "ceramics/unfired/spindle", "ceramics/fired/spindle");
            registerPottery(simpleItems, r, "ceramics/unfired/fire_brick", "ceramics/fired/fire_brick");

            simpleItems.add(register(r, "ceramics/fire_clay", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT, "fire_clay"), CT_MISC));

            simpleItems.add(register(r, "ceramics/unfired/jug", new ItemPottery(), CT_POTTERY));
            register(r, "ceramics/fired/jug", new ItemJug(), CT_POTTERY);
            simpleItems.add(register(r, "ceramics/unfired/clay_brick", new ItemPottery(), CT_POTTERY));
            simpleItems.add(register(r, "ceramics/unfired/clay_flower_pot", new ItemPottery(), CT_POTTERY));

        }

        for (Crop crop : Crop.values())
        {
            simpleItems.add(register(r, "crop/seeds/" + crop.name().toLowerCase(), new ItemSeedsTFC(crop), CT_FOOD));
        }

        simpleItems.add(register(r, "crop/product/jute", new ItemMisc(Size.SMALL, Weight.VERY_LIGHT), CT_MISC));
        simpleItems.add(register(r, "crop/product/jute_fiber", new ItemMisc(Size.SMALL, Weight.VERY_LIGHT), CT_MISC));
        simpleItems.add(register(r, "crop/product/burlap_cloth", new ItemMisc(Size.SMALL, Weight.VERY_LIGHT), CT_MISC));

        // All simple foods (not meals) just use ItemFood and are registered here
        for (Food food : Food.values())
        {
            if (food.getCategory() != Food.Category.MEAL)
            {
                simpleItems.add(register(r, "food/" + food.name().toLowerCase(), new ItemFoodTFC(food), CT_FOOD));
            }
        }
        // Complex foods that require special classes go here
        for (Food food : new Food[] {Food.BARLEY_BREAD_SANDWICH, Food.CORNBREAD_SANDWICH, Food.OAT_BREAD_SANDWICH, Food.RICE_BREAD_SANDWICH, Food.RYE_BREAD_SANDWICH, Food.WHEAT_BREAD_SANDWICH})
        {
            simpleItems.add(register(r, "food/" + food.name().toLowerCase(), new ItemSandwich(food), CT_FOOD));
        }
        for (Food food : new Food[] {Food.SOUP_GRAIN, Food.SOUP_FRUIT, Food.SOUP_VEGETABLE, Food.SOUP_MEAT, Food.SOUP_DAIRY})
        {
            simpleItems.add(register(r, "food/" + food.name().toLowerCase(), new ItemDynamicBowlFood(food), CT_FOOD));
        }
        for (Food food : new Food[] {Food.SALAD_GRAIN, Food.SALAD_FRUIT, Food.SALAD_VEGETABLE, Food.SALAD_MEAT, Food.SALAD_DAIRY})
        {
            simpleItems.add(register(r, "food/" + food.name().toLowerCase(), new ItemDynamicBowlFood(food), CT_FOOD));
        }

        //olive oil production
        simpleItems.add(register(r, "food/olive_paste", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_FOOD)); //not edible
        simpleItems.add(register(r, "crop/product/jute_disc", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        simpleItems.add(register(r, "crop/product/olive_jute_disc", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_FOOD)); //not edible
        simpleItems.add(register(r, "crop/product/jute_net", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        simpleItems.add(register(r, "crop/product/dirty_jute_net", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));

        simpleItems.add(register(r, "firestarter", new ItemFireStarter(), CT_MISC));
        simpleItems.add(register(r, "straw", new ItemMisc(Size.SMALL, Weight.VERY_LIGHT, "kindling", "straw"), CT_MISC));
        simpleItems.add(register(r, "glass_shard", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT), CT_MISC));
        simpleItems.add(register(r, "handstone", new ItemCraftingTool(250, Size.NORMAL, Weight.VERY_HEAVY, "handstone"), CT_MISC));

        simpleItems.add(register(r, "spindle", new ItemCraftingTool(40, Size.NORMAL, Weight.MEDIUM, "spindle"), CT_MISC));

        simpleItems.add(register(r, "bloom/unrefined", new ItemBloom(false), CT_MISC));
        simpleItems.add(register(r, "bloom/refined", new ItemBloom(true), CT_MISC));

        // Animal Hides
        for (ItemAnimalHide.HideSize size : ItemAnimalHide.HideSize.values())
        {
            for (ItemAnimalHide.HideType type : ItemAnimalHide.HideType.values())
            {
                simpleItems.add(register(r, ("hide/" + type.name() + "/" + size.name()).toLowerCase(), new ItemAnimalHide(type, size), CT_MISC));
            }
        }

        simpleItems.add(register(r, "quiver", new ItemQuiver(), CT_MISC));

        simpleItems.add(register(r, "animal/product/wool", new ItemMisc(Size.SMALL, Weight.LIGHT), CT_MISC));
        simpleItems.add(register(r, "animal/product/wool_yarn", new ItemMisc(Size.VERY_SMALL, Weight.VERY_LIGHT, "string"), CT_MISC));
        simpleItems.add(register(r, "animal/product/wool_cloth", new ItemMisc(Size.SMALL, Weight.LIGHT, "cloth_high_quality"), CT_MISC));
        simpleItems.add(register(r, "animal/product/silk_cloth", new ItemMisc(Size.SMALL, Weight.LIGHT, "cloth_high_quality"), CT_MISC));

        simpleItems.add(register(r, "dye/black", new ItemMisc(Size.TINY, Weight.LIGHT, "dye_black"), CT_MISC));
        simpleItems.add(register(r, "dye/blue", new ItemMisc(Size.TINY, Weight.LIGHT, "dye_blue"), CT_MISC));
        simpleItems.add(register(r, "dye/white", new ItemMisc(Size.TINY, Weight.LIGHT, "dye_white"), CT_MISC));
        simpleItems.add(register(r, "dye/brown", new ItemMisc(Size.TINY, Weight.LIGHT, "dye_brown"), CT_MISC));
        simpleItems.add(register(r, "alabaster_brick", new ItemMisc(Size.VERY_SMALL, Weight.LIGHT), CT_MISC));
        simpleItems.add(register(r, "glue", new ItemMisc(Size.TINY, Weight.LIGHT, "slimeball", "glue"), CT_MISC));
        simpleItems.add(register(r, "brass_mechanisms", new ItemMisc(Size.NORMAL, Weight.LIGHT), CT_MISC));

        register(r, "goldpan", new ItemGoldPan(), CT_MISC);
        simpleItems.add(register(r, "wrought_iron_grill", new ItemMisc(Size.LARGE, Weight.HEAVY, "grill"), CT_MISC));

        allSimpleItems = simpleItems.build();

        OreDictionaryHelper.init();
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerVanillaOverrides(RegistryEvent.Register<Item> event)
    {
        // Vanilla Overrides. Used for small tweaks on vanilla items, rather than replacing them outright
        TerraFirmaCraft.getLog().info("The below warnings about unintended overrides are normal. The override is intended. ;)");
        event.getRegistry().registerAll(
            new ItemSnow(Blocks.SNOW_LAYER).setRegistryName("minecraft", "snow_layer"),
            new ItemGlassBottleTFC().setRegistryName(Items.GLASS_BOTTLE.getRegistryName()).setTranslationKey("glassBottle")
        );

        if (ConfigTFC.General.OVERRIDES.enableTorchOverride)
        {
            event.getRegistry().register(new ItemBlockTorch(Blocks.TORCH).setRegistryName("minecraft", "torch"));
        }
    }

    public static void init()
    {
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
            if (metal.getToolMetal() != null)
                metal.getToolMetal().setRepairItem(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SCRAP)));
    }

    private static void registerPottery(Builder<Item> items, IForgeRegistry<Item> r, String nameUnfired, String nameFired)
    {
        registerPottery(items, r, nameUnfired, nameFired, new ItemPottery(), new ItemPottery());
    }

    private static void registerPottery(Builder<Item> items, IForgeRegistry<Item> r, String nameUnfired, String nameFired, ItemPottery unfiredItem, ItemPottery firedItem)
    {
        register(r, nameFired, firedItem, CT_POTTERY);
        register(r, nameUnfired, unfiredItem, CT_POTTERY);

        if (items != null)
        {
            items.add(firedItem, unfiredItem);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static void registerItemBlock(IForgeRegistry<Item> r, ItemBlock item)
    {
        item.setRegistryName(item.getBlock().getRegistryName());
        item.setCreativeTab(item.getBlock().getCreativeTab());
        r.register(item);
    }

    private static <T extends Item> T register(IForgeRegistry<Item> r, String name, T item, CreativeTabs ct)
    {
        item.setRegistryName(MOD_ID, name);
        item.setTranslationKey(MOD_ID + "." + name.replace('/', '.'));
        item.setCreativeTab(ct);
        r.register(item);
        return item;
    }
}
