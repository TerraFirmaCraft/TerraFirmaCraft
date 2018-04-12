package net.dries007.tfc.objects.items;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.dries007.tfc.objects.Gem;
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.blocks.BlockDoorTFC;
import net.dries007.tfc.objects.blocks.BlockLogTFC;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class ItemsTFC
{
    private ItemsTFC() {}

    public static final ItemDebug WAND = null;

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

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
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
                b.add(register(r, "ore/" + ore.name().toLowerCase(), new ItemOreTFC(ore), CT_ORE_ITEMS));
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
            for (Metal metal : Metal.values())
            {
                if (type.toolItem && !metal.toolMetal) continue;
                if (metal == Metal.UNKNOWN && !(type == Metal.ItemType.INGOT || type == Metal.ItemType.UNSHAPED)) continue;
                simpleItems.add(register(r, ("metal/"+ type + "/" + metal).toLowerCase(), new ItemMetal(metal, type), CT_METAL));
            }
        }

        BlocksTFC.getAllNormalItemBlocks().forEach(x -> register_item_block(r, x));
        BlocksTFC.getAllInventoryItemBlocks().forEach(x -> register_item_block(r, x));

        for (BlockLogTFC log : BlocksTFC.getAllLogBlocks())
            simpleItems.add(register(r, log.getRegistryName().getResourcePath(), new ItemLogTFC(log), CT_WOOD));

        for (BlockDoorTFC door : BlocksTFC.getAllDoorBlocks())
            simpleItems.add(register(r, door.getRegistryName().getResourcePath(), new ItemDoorTFC(door), CT_WOOD));

        allSimpleItems = simpleItems.build();
    }

    private static void register_item_block(IForgeRegistry<Item> r, Block block)
    {
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
}
