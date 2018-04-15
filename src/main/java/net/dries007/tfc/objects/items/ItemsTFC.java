package net.dries007.tfc.objects.items;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.dries007.tfc.objects.*;
import net.dries007.tfc.objects.blocks.BlockSlabTFC;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockDoorTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.objects.items.pottery.*;
import net.dries007.tfc.objects.items.rock.*;
import net.dries007.tfc.objects.items.wood.ItemDoorTFC;
import net.dries007.tfc.objects.items.wood.ItemLogTFC;
import net.dries007.tfc.objects.items.wood.ItemLumberTFC;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.reflect.InvocationTargetException;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class ItemsTFC
{
    private ItemsTFC() {}

    public static final ItemDebug WAND = null;
    public static final ItemFireStarter FIRESTARTER = null;
    public static final ItemGoldPan GOLDPAN = null;

    @GameRegistry.ObjectHolder("mold/ingo/empty")
    public static final ItemFiredPottery MOLD_INGOT_EMPTY = null;
    @GameRegistry.ObjectHolder("ceramics/unfired/vessel")
    public static final ItemUnfiredPottery CERAMICS_UNFIRED_VERSSEL = null;
    @GameRegistry.ObjectHolder("ceramics/fired/vessel")
    public static final ItemSmallVessel CERAMICS_FIRED_VESSEL = null;
    @GameRegistry.ObjectHolder("ceramics/fired/vessel_glazed")
    public static final ItemSmallVessel CERAMICS_FIRED_VESSEL_GLAZED = null;

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
    public static void registerItems(RegistryEvent.Register<Item> event) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        IForgeRegistry<Item> r = event.getRegistry();

        Builder<Item> simpleItems = ImmutableList.builder();

        simpleItems.add(register(r, "wand", new ItemDebug(), CT_MISC));

        {
            for (Rock rock : Rock.values())
                simpleItems.add(register(r, "rock/" + rock.name().toLowerCase(), new ItemRock(rock), CT_ROCK_ITEMS));
            for (Rock rock : Rock.values())
                simpleItems.add(register(r, "brick/" + rock.name().toLowerCase(), new ItemBrickTFC(rock), CT_ROCK_ITEMS));
        }

        {
            Builder<ItemOreTFC> b = new Builder<>();
            for (Ore ore : Ore.values())
                b.add(register(r, "ore/" + ore.name().toLowerCase(), new ItemOreTFC(ore), CT_ROCK_ITEMS));
            allOreItems = b.build();

            for (Ore ore : Ore.values())
                if (ore.graded)
                    simpleItems.add(register(r, "ore/small/" + ore.name().toLowerCase(), new ItemSmallOre(ore), CT_ROCK_ITEMS));
        }

        {
            Builder<ItemGem> b = new Builder<>();
            for (Gem gem : Gem.values())
                b.add(register(r, "gem/" + gem.name().toLowerCase(), new ItemGem(gem), CT_GEMS));
            allGemItems = b.build();
        }

        for (Metal.ItemType type : Metal.ItemType.values())
        {
            for (Metal metal : Metal.values())
            {
                if (!metal.hasType(type) || type.clazz == null) continue;
                simpleItems.add(register(r, ("metal/"+ type + "/" + metal).toLowerCase(), type.clazz.getConstructor(Metal.class, Metal.ItemType.class).newInstance(metal, type), CT_METAL));
            }
        }

        BlocksTFC.getAllNormalItemBlocks().forEach(x -> register_item_block(r, x));
        BlocksTFC.getAllInventoryItemBlocks().forEach(x -> register_item_block(r, x));

        for (BlockLogTFC log : BlocksTFC.getAllLogBlocks())
            simpleItems.add(register(r, log.getRegistryName().getResourcePath(), new ItemLogTFC(log), CT_WOOD));

        for (BlockDoorTFC door : BlocksTFC.getAllDoorBlocks())
            simpleItems.add(register(r, door.getRegistryName().getResourcePath(), new ItemDoorTFC(door), CT_DECORATIONS));

        for (BlockSlabTFC.Half slab : BlocksTFC.getAllSlabBlocks())
            simpleItems.add(register(r, slab.getRegistryName().getResourcePath(), new ItemSlab(slab, slab, slab.doubleSlab), CT_DECORATIONS));

        for (Wood wood : Wood.values())
            simpleItems.add(register(r, "wood/lumber/" + wood.name().toLowerCase(), new ItemLumberTFC(wood), CT_WOOD));

        for (Rock.Category cat : Rock.Category.values())
        {
            simpleItems.add(register(r, "stone/axe/" + cat.name().toLowerCase(), new ItemRockAxe(cat), CT_ROCK_ITEMS));
            simpleItems.add(register(r, "stone/shovel/" + cat.name().toLowerCase(), new ItemRockShovel(cat), CT_ROCK_ITEMS));
            simpleItems.add(register(r, "stone/hoe/" + cat.name().toLowerCase(), new ItemRockHoe(cat), CT_ROCK_ITEMS));
            simpleItems.add(register(r, "stone/knife/" + cat.name().toLowerCase(), new ItemRockKnife(cat), CT_ROCK_ITEMS));
            simpleItems.add(register(r, "stone/javelin/" + cat.name().toLowerCase(), new ItemRockJavelin(cat), CT_ROCK_ITEMS));
            simpleItems.add(register(r, "stone/hammer/" + cat.name().toLowerCase(), new ItemRockHammer(cat), CT_ROCK_ITEMS));
        }

        for (Powder powder : Powder.values())
            simpleItems.add(register(r, "powder/" + powder.name().toLowerCase(), new ItemPowder(powder), CT_MISC));

        { // POTTERY
            for (Metal.ItemType type : Metal.ItemType.values())
            {
                if (!type.hasMold) continue;
                simpleItems.add(register(r, "mold/" + type.name().toLowerCase() + "/unfired", new ItemUnfiredMold(type), CT_POTTERY));
                simpleItems.add(register(r, "mold/" + type.name().toLowerCase() + "/empty", new ItemMold(type), CT_POTTERY));
                for (Metal metal : Metal.values())
                {
                    if (!metal.hasType(type) || metal.tier != Metal.Tier.TIER_I) continue;
                    simpleItems.add(register(r, ("mold/" + type.name() + "/" + metal.name()).toLowerCase(), new ItemFilledMold(type, metal), CT_POTTERY));
                }
            }
            for (Metal metal : Metal.values())
                simpleItems.add(register(r, ("mold/ingot/" + metal.name()).toLowerCase(), new ItemFilledMold(Metal.ItemType.UNSHAPED, metal), CT_POTTERY));
            simpleItems.add(register(r, "mold/ingot/unfired", new ItemUnfiredMold(Metal.ItemType.INGOT), CT_POTTERY));
            simpleItems.add(register(r, "mold/ingot/empty", new ItemFiredPottery(), CT_POTTERY));

            simpleItems.add(register(r, "ceramics/unfired/vessel", new ItemUnfiredPottery(), CT_POTTERY));
            simpleItems.add(register(r, "ceramics/fired/vessel", new ItemSmallVessel(false), CT_POTTERY));
            register(r, "ceramics/fired/vessel_glazed", new ItemSmallVessel(true), CT_POTTERY);

            simpleItems.add(register(r, "ceramics/unfired/spindle", new ItemUnfiredPottery(), CT_POTTERY));
            simpleItems.add(register(r, "ceramics/fired/spindle", new ItemFiredPottery(), CT_POTTERY));
            simpleItems.add(register(r, "ceramics/unfired/pot", new ItemUnfiredPottery(), CT_POTTERY));
            simpleItems.add(register(r, "ceramics/fired/pot", new ItemFiredPottery(), CT_POTTERY));
            simpleItems.add(register(r, "ceramics/unfired/jug", new ItemUnfiredPottery(), CT_POTTERY));
            simpleItems.add(register(r, "ceramics/fired/jug", new ItemFiredPottery(), CT_POTTERY));
            simpleItems.add(register(r, "ceramics/unfired/bowl", new ItemUnfiredPottery(), CT_POTTERY));
            simpleItems.add(register(r, "ceramics/fired/bowl", new ItemFiredPottery(), CT_POTTERY));
            simpleItems.add(register(r, "ceramics/unfired/fire_brick", new ItemUnfiredPottery(), CT_POTTERY));
            simpleItems.add(register(r, "ceramics/fired/fire_brick", new ItemFiredPottery(), CT_POTTERY));

            simpleItems.add(register(r, "ceramics/fire_clay", new Item(), CT_MISC));
        }

        // FLAT
        for (Rock rock : Rock.values())
            r.register(new ItemFlat(rock).setRegistryName(MOD_ID, "flat/" + rock.name().toLowerCase()));
        r.register(new ItemFlat().setRegistryName(MOD_ID, "flat/leather"));
        r.register(new ItemFlat().setRegistryName(MOD_ID, "flat/clay"));
        r.register(new ItemFlat().setRegistryName(MOD_ID, "flat/fire_clay"));

        simpleItems.add(register(r, "firestarter", new ItemFireStarter(), CT_MISC));
        register(r, "goldpan", new ItemGoldPan(), CT_MISC);

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
        // todo: fire clay & fire brick & fire bricks (block)

        // todo: bloom & raw bloom ( = IMetalObject)
        // todo: jute & jute fiber
        // todo: quiver
        // todo: millstone (quern)

        // todo: foods & plants & seeds & fruits & fruit tree saplings & berries & berry bushes

        allSimpleItems = simpleItems.build();
    }

    private static void register_item_block(IForgeRegistry<Item> r, Block block)
    {
        //noinspection ConstantConditions
        r.register(new ItemBlock(block).setRegistryName(block.getRegistryName()).setCreativeTab(block.getCreativeTabToDisplayOn()));
    }

    private static <T extends Item> T register(IForgeRegistry<Item> r, String name, T item, CreativeTabs ct)
    {
        item.setRegistryName(MOD_ID, name);
        item.setUnlocalizedName(MOD_ID + "." + name.replace('/', '.'));
        item.setCreativeTab(ct);
        r.register(item);
        return item;
    }

    public static void init()
    {
        for (Metal metal : Metal.values())
            if (metal.toolMetal != null)
                metal.toolMetal.setRepairItem(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SCRAP)));
    }
}
