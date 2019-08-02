/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

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
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.objects.items.itemblock.ItemBlockTFC;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.objects.items.rock.ItemBrickTFC;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.items.rock.ItemRockToolHead;
import net.dries007.tfc.objects.items.wood.ItemDoorTFC;
import net.dries007.tfc.objects.items.wood.ItemLumberTFC;
import net.dries007.tfc.util.agriculture.Crop;
import net.dries007.tfc.util.agriculture.Food;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;
import static net.dries007.tfc.util.Helpers.getNull;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class ItemsTFC
{
    public static final ItemDebug WAND = getNull();
    public static final ItemFireStarter FIRESTARTER = getNull();
    public static final ItemGoldPan GOLDPAN = getNull();
    public static final ItemMisc STRAW = getNull();
    public static final ItemHandstone HANDSTONE = getNull();

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
    @GameRegistry.ObjectHolder("food/sugarcane")
    public static final Item SUGARCANE = getNull();

    @GameRegistry.ObjectHolder("ceramics/fire_clay")
    public static final ItemFireClay FIRE_CLAY = getNull();

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

    @GameRegistry.ObjectHolder("bloom/unrefined")
    public static final ItemBloom UNREFINED_BLOOM = getNull();
    @GameRegistry.ObjectHolder("bloom/refined")
    public static final ItemBloom REFINED_BLOOM = getNull();

    @GameRegistry.ObjectHolder("powder/salt")
    public static final ItemPowder SALT = getNull();

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

        {
            for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                simpleItems.add(register(r, "rock/" + rock.getRegistryName().getPath().toLowerCase(), new ItemRock(rock), CT_ROCK_ITEMS));
            for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                simpleItems.add(register(r, "brick/" + rock.getRegistryName().getPath().toLowerCase(), new ItemBrickTFC(rock), CT_ROCK_ITEMS));
        }

        {
            Builder<ItemOreTFC> b = new Builder<>();
            for (Ore ore : TFCRegistries.ORES.getValuesCollection())
                b.add(register(r, "ore/" + ore.getRegistryName().getPath(), new ItemOreTFC(ore), CT_ROCK_ITEMS));
            allOreItems = b.build();

            for (Ore ore : TFCRegistries.ORES.getValuesCollection())
                if (ore.isGraded())
                    simpleItems.add(register(r, "ore/small/" + ore.getRegistryName().getPath(), new ItemSmallOre(ore), CT_ROCK_ITEMS));
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
                if (type.hasType(metal))
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
            simpleItems.add(register(r, "wood/lumber/" + wood.getRegistryName().getPath(), new ItemLumberTFC(wood), CT_WOOD));

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

            registerPottery(simpleItems, r, "ceramics/unfired/vessel", "ceramics/fired/vessel", new ItemUnfiredSmallVessel(false), new ItemSmallVessel(false));
            registerPottery(null, r, "ceramics/unfired/vessel_glazed", "ceramics/fired/vessel_glazed", new ItemUnfiredSmallVessel(true), new ItemSmallVessel(true));

            registerPottery(simpleItems, r, "ceramics/unfired/spindle", "ceramics/fired/spindle");
            registerPottery(simpleItems, r, "ceramics/unfired/pot", "ceramics/fired/pot");
            registerPottery(simpleItems, r, "ceramics/unfired/jug", "ceramics/fired/jug");
            registerPottery(simpleItems, r, "ceramics/unfired/bowl", "ceramics/fired/bowl");
            registerPottery(simpleItems, r, "ceramics/unfired/fire_brick", "ceramics/fired/fire_brick");

            simpleItems.add(register(r, "ceramics/fire_clay", new ItemFireClay(), CT_MISC));

        }

        for (Crop crop : Crop.values())
        {
            simpleItems.add(register(r, "crop/seeds/" + crop.name().toLowerCase(), new ItemSeedsTFC(crop), CT_FOOD));
        }

        simpleItems.add(register(r, "crop/product/jute", new ItemMisc(Size.TINY, Weight.LIGHT), CT_MISC));
        simpleItems.add(register(r, "crop/product/jute_fiber", new ItemMisc(Size.TINY, Weight.LIGHT), CT_MISC));
        simpleItems.add(register(r, "crop/product/burlap_cloth", new ItemMisc(Size.TINY, Weight.LIGHT), CT_MISC));

        for (Food food : Food.values())
        {
            simpleItems.add(register(r, "food/" + food.name().toLowerCase(), new ItemFoodTFC(food), CT_FOOD));
        }

        // FLAT
        for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
        {
            r.register(new ItemFlat(rock).setRegistryName(MOD_ID, "flat/" + rock.getRegistryName().getPath().toLowerCase()));
        }

        simpleItems.add(register(r, "firestarter", new ItemFireStarter(), CT_MISC));
        simpleItems.add(register(r, "straw", new ItemMisc(Size.SMALL, Weight.LIGHT, "kindling", "straw"), CT_MISC));
        simpleItems.add(register(r, "handstone", new ItemHandstone(), CT_MISC));

        simpleItems.add(register(r, "spindle", new ItemCraftingTool(40, Size.NORMAL, Weight.MEDIUM, "spindle"), CT_MISC));

        simpleItems.add(register(r, "bloom/unrefined", new ItemBloom(), CT_MISC));
        simpleItems.add(register(r, "bloom/refined", new ItemBloom(), CT_MISC));

        // Animal Hides
        for (ItemAnimalHide.HideSize size : ItemAnimalHide.HideSize.values())
        {
            for (ItemAnimalHide.HideType type : ItemAnimalHide.HideType.values())
            {
                if (type == ItemAnimalHide.HideType.SOAKED)
                {
                    simpleItems.add(register(r, ("hide/" + type.name() + "/" + size.name()).toLowerCase(), new ItemAnimalHide.Soaked(type, size), CT_MISC));
                }
                else
                {
                    simpleItems.add(register(r, ("hide/" + type.name() + "/" + size.name()).toLowerCase(), new ItemAnimalHide(type, size), CT_MISC));
                }
            }
        }

        simpleItems.add(register(r, "animal/product/wool", new ItemMisc(Size.TINY, Weight.LIGHT), CT_MISC));
        simpleItems.add(register(r, "animal/product/wool_yarn", new ItemMisc(Size.TINY, Weight.LIGHT, "string"), CT_MISC));
        simpleItems.add(register(r, "animal/product/wool_cloth", new ItemMisc(Size.TINY, Weight.LIGHT, "cloth_high_quality"), CT_MISC));
        simpleItems.add(register(r, "animal/product/silk_cloth", new ItemMisc(Size.TINY, Weight.LIGHT, "cloth_high_quality"), CT_MISC));

        register(r, "goldpan", new ItemGoldPan(), CT_MISC);

        // Note: if you add items you don't need to put them in this list of todos. Feel free to add them where they make sense :)
        // todo: Bow? Arrows?
        // todo: Fishing rod?
        // todo: (white) dye? (so white dye isn't bonemeal)
        // todo: ink?
        // todo: clay? (or catch rightclick with event, that should work too)
        // todo: rope? (could just keep vanilla version)
        // todo: shears? Higher durability versions for different metals (iron/streels/others?)?
        // todo: flint & steel? Higher durability versions for different metals (iron/steels)?
        // todo: fluid glass bottles? (alcohols/water/vinegar/brine)

        // todo: water jug (make this have durability/multiple rounds of water?)

        // todo: minecart with chest (so the chest dropped is the right kind of wood)
        // todo: custom buckets: Wood (finite) & steel (infinite/classic/source)
        // todo: wool, yarn, cloth (wool, silk & burlap)
        // todo: mortar
        // todo: hides (raw, soaked, scraped, prepared)
        // todo: straw

        // todo: jute & jute fiber
        // todo: quiver
        // todo: millstone (quern)

        // todo: foods & plants & seeds & fruits & fruit tree saplings & berries & berry bushes

        allSimpleItems = simpleItems.build();
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
