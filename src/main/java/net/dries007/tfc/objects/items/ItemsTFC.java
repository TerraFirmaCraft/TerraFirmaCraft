package net.dries007.tfc.objects.items;

import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.objects.CreativeTabsTFC.CT_MISC;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class ItemsTFC
{
    private ItemsTFC() {}

    public static final ItemDebug WAND = null;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> r = event.getRegistry();

        register(r, "wand", new ItemDebug());

        register_item_block(r, BlocksTFC.DEBUG);
        register_item_block(r, BlocksTFC.PEAT);
        register_item_block(r, BlocksTFC.PEAT_GRASS);

        BlocksTFC.getAllBlockRockVariants().forEach(x -> register_item_block(r, x));
        BlocksTFC.getAllLogBlocks().forEach(x -> register_item_block(r, x));
        BlocksTFC.getAllLeafBlocks().forEach(x -> register_item_block(r, x));
        BlocksTFC.getAllPlankBlocks().forEach(x -> register_item_block(r, x));
        BlocksTFC.getAllOreBlocks().forEach(x -> register_item_block(r, x));
    }

    private static void register_item_block(IForgeRegistry<Item> r, Block block)
    {
        r.register(new ItemBlock(block).setRegistryName(block.getRegistryName()).setCreativeTab(block.getCreativeTabToDisplayOn()));
    }

    private static void register(IForgeRegistry<Item> r, String name, Item item)
    {
        item.setRegistryName(MOD_ID, name);
        item.setUnlocalizedName(MOD_ID + "." + name.replace('_', '.'));
        item.setCreativeTab(CT_MISC);
        r.register(item);
    }
}
