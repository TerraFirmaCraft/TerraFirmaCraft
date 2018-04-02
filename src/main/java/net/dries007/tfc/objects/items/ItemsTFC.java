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
import static net.dries007.tfc.objects.CreativeTab.CT_MISC;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class ItemsTFC
{
    private ItemsTFC() {}

    public static final ItemDebug WAND = null;

    @SubscribeEvent
    public static void addItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> r = event.getRegistry();

        register(r, "wand", new ItemDebug());

        register_item_block(r, BlocksTFC.DEBUG);

        register_item_block(r, BlocksTFC.RAW_GRANITE);
        register_item_block(r, BlocksTFC.RAW_DIORITE);
        register_item_block(r, BlocksTFC.RAW_GABBRO);
        register_item_block(r, BlocksTFC.RAW_SHALE);
        register_item_block(r, BlocksTFC.RAW_CLAYSTONE);
        register_item_block(r, BlocksTFC.RAW_ROCKSALT);
        register_item_block(r, BlocksTFC.RAW_LIMESTONE);
        register_item_block(r, BlocksTFC.RAW_CONGLOMERATE);
        register_item_block(r, BlocksTFC.RAW_DOLOMITE);
        register_item_block(r, BlocksTFC.RAW_CHERT);
        register_item_block(r, BlocksTFC.RAW_CHALK);
        register_item_block(r, BlocksTFC.RAW_RHYOLITE);
        register_item_block(r, BlocksTFC.RAW_BASALT);
        register_item_block(r, BlocksTFC.RAW_ANDESITE);
        register_item_block(r, BlocksTFC.RAW_DACITE);
        register_item_block(r, BlocksTFC.RAW_QUARTZITE);
        register_item_block(r, BlocksTFC.RAW_SLATE);
        register_item_block(r, BlocksTFC.RAW_PHYLLITE);
        register_item_block(r, BlocksTFC.RAW_SCHIST);
        register_item_block(r, BlocksTFC.RAW_GNEISS);
        register_item_block(r, BlocksTFC.RAW_MARBLE);
        register_item_block(r, BlocksTFC.SMOOTH_GRANITE);
        register_item_block(r, BlocksTFC.SMOOTH_DIORITE);
        register_item_block(r, BlocksTFC.SMOOTH_GABBRO);
        register_item_block(r, BlocksTFC.SMOOTH_SHALE);
        register_item_block(r, BlocksTFC.SMOOTH_CLAYSTONE);
        register_item_block(r, BlocksTFC.SMOOTH_ROCKSALT);
        register_item_block(r, BlocksTFC.SMOOTH_LIMESTONE);
        register_item_block(r, BlocksTFC.SMOOTH_CONGLOMERATE);
        register_item_block(r, BlocksTFC.SMOOTH_DOLOMITE);
        register_item_block(r, BlocksTFC.SMOOTH_CHERT);
        register_item_block(r, BlocksTFC.SMOOTH_CHALK);
        register_item_block(r, BlocksTFC.SMOOTH_RHYOLITE);
        register_item_block(r, BlocksTFC.SMOOTH_BASALT);
        register_item_block(r, BlocksTFC.SMOOTH_ANDESITE);
        register_item_block(r, BlocksTFC.SMOOTH_DACITE);
        register_item_block(r, BlocksTFC.SMOOTH_QUARTZITE);
        register_item_block(r, BlocksTFC.SMOOTH_SLATE);
        register_item_block(r, BlocksTFC.SMOOTH_PHYLLITE);
        register_item_block(r, BlocksTFC.SMOOTH_SCHIST);
        register_item_block(r, BlocksTFC.SMOOTH_GNEISS);
        register_item_block(r, BlocksTFC.SMOOTH_MARBLE);
        register_item_block(r, BlocksTFC.COBBLE_GRANITE);
        register_item_block(r, BlocksTFC.COBBLE_DIORITE);
        register_item_block(r, BlocksTFC.COBBLE_GABBRO);
        register_item_block(r, BlocksTFC.COBBLE_SHALE);
        register_item_block(r, BlocksTFC.COBBLE_CLAYSTONE);
        register_item_block(r, BlocksTFC.COBBLE_ROCKSALT);
        register_item_block(r, BlocksTFC.COBBLE_LIMESTONE);
        register_item_block(r, BlocksTFC.COBBLE_CONGLOMERATE);
        register_item_block(r, BlocksTFC.COBBLE_DOLOMITE);
        register_item_block(r, BlocksTFC.COBBLE_CHERT);
        register_item_block(r, BlocksTFC.COBBLE_CHALK);
        register_item_block(r, BlocksTFC.COBBLE_RHYOLITE);
        register_item_block(r, BlocksTFC.COBBLE_BASALT);
        register_item_block(r, BlocksTFC.COBBLE_ANDESITE);
        register_item_block(r, BlocksTFC.COBBLE_DACITE);
        register_item_block(r, BlocksTFC.COBBLE_QUARTZITE);
        register_item_block(r, BlocksTFC.COBBLE_SLATE);
        register_item_block(r, BlocksTFC.COBBLE_PHYLLITE);
        register_item_block(r, BlocksTFC.COBBLE_SCHIST);
        register_item_block(r, BlocksTFC.COBBLE_GNEISS);
        register_item_block(r, BlocksTFC.COBBLE_MARBLE);
        register_item_block(r, BlocksTFC.BRICK_GRANITE);
        register_item_block(r, BlocksTFC.BRICK_DIORITE);
        register_item_block(r, BlocksTFC.BRICK_GABBRO);
        register_item_block(r, BlocksTFC.BRICK_SHALE);
        register_item_block(r, BlocksTFC.BRICK_CLAYSTONE);
        register_item_block(r, BlocksTFC.BRICK_ROCKSALT);
        register_item_block(r, BlocksTFC.BRICK_LIMESTONE);
        register_item_block(r, BlocksTFC.BRICK_CONGLOMERATE);
        register_item_block(r, BlocksTFC.BRICK_DOLOMITE);
        register_item_block(r, BlocksTFC.BRICK_CHERT);
        register_item_block(r, BlocksTFC.BRICK_CHALK);
        register_item_block(r, BlocksTFC.BRICK_RHYOLITE);
        register_item_block(r, BlocksTFC.BRICK_BASALT);
        register_item_block(r, BlocksTFC.BRICK_ANDESITE);
        register_item_block(r, BlocksTFC.BRICK_DACITE);
        register_item_block(r, BlocksTFC.BRICK_QUARTZITE);
        register_item_block(r, BlocksTFC.BRICK_SLATE);
        register_item_block(r, BlocksTFC.BRICK_PHYLLITE);
        register_item_block(r, BlocksTFC.BRICK_SCHIST);
        register_item_block(r, BlocksTFC.BRICK_GNEISS);
        register_item_block(r, BlocksTFC.BRICK_MARBLE);
        register_item_block(r, BlocksTFC.SAND_GRANITE);
        register_item_block(r, BlocksTFC.SAND_DIORITE);
        register_item_block(r, BlocksTFC.SAND_GABBRO);
        register_item_block(r, BlocksTFC.SAND_SHALE);
        register_item_block(r, BlocksTFC.SAND_CLAYSTONE);
        register_item_block(r, BlocksTFC.SAND_ROCKSALT);
        register_item_block(r, BlocksTFC.SAND_LIMESTONE);
        register_item_block(r, BlocksTFC.SAND_CONGLOMERATE);
        register_item_block(r, BlocksTFC.SAND_DOLOMITE);
        register_item_block(r, BlocksTFC.SAND_CHERT);
        register_item_block(r, BlocksTFC.SAND_CHALK);
        register_item_block(r, BlocksTFC.SAND_RHYOLITE);
        register_item_block(r, BlocksTFC.SAND_BASALT);
        register_item_block(r, BlocksTFC.SAND_ANDESITE);
        register_item_block(r, BlocksTFC.SAND_DACITE);
        register_item_block(r, BlocksTFC.SAND_QUARTZITE);
        register_item_block(r, BlocksTFC.SAND_SLATE);
        register_item_block(r, BlocksTFC.SAND_PHYLLITE);
        register_item_block(r, BlocksTFC.SAND_SCHIST);
        register_item_block(r, BlocksTFC.SAND_GNEISS);
        register_item_block(r, BlocksTFC.SAND_MARBLE);
        register_item_block(r, BlocksTFC.GRAVEL_GRANITE);
        register_item_block(r, BlocksTFC.GRAVEL_DIORITE);
        register_item_block(r, BlocksTFC.GRAVEL_GABBRO);
        register_item_block(r, BlocksTFC.GRAVEL_SHALE);
        register_item_block(r, BlocksTFC.GRAVEL_CLAYSTONE);
        register_item_block(r, BlocksTFC.GRAVEL_ROCKSALT);
        register_item_block(r, BlocksTFC.GRAVEL_LIMESTONE);
        register_item_block(r, BlocksTFC.GRAVEL_CONGLOMERATE);
        register_item_block(r, BlocksTFC.GRAVEL_DOLOMITE);
        register_item_block(r, BlocksTFC.GRAVEL_CHERT);
        register_item_block(r, BlocksTFC.GRAVEL_CHALK);
        register_item_block(r, BlocksTFC.GRAVEL_RHYOLITE);
        register_item_block(r, BlocksTFC.GRAVEL_BASALT);
        register_item_block(r, BlocksTFC.GRAVEL_ANDESITE);
        register_item_block(r, BlocksTFC.GRAVEL_DACITE);
        register_item_block(r, BlocksTFC.GRAVEL_QUARTZITE);
        register_item_block(r, BlocksTFC.GRAVEL_SLATE);
        register_item_block(r, BlocksTFC.GRAVEL_PHYLLITE);
        register_item_block(r, BlocksTFC.GRAVEL_SCHIST);
        register_item_block(r, BlocksTFC.GRAVEL_GNEISS);
        register_item_block(r, BlocksTFC.GRAVEL_MARBLE);
        register_item_block(r, BlocksTFC.DIRT_GRANITE);
        register_item_block(r, BlocksTFC.DIRT_DIORITE);
        register_item_block(r, BlocksTFC.DIRT_GABBRO);
        register_item_block(r, BlocksTFC.DIRT_SHALE);
        register_item_block(r, BlocksTFC.DIRT_CLAYSTONE);
        register_item_block(r, BlocksTFC.DIRT_ROCKSALT);
        register_item_block(r, BlocksTFC.DIRT_LIMESTONE);
        register_item_block(r, BlocksTFC.DIRT_CONGLOMERATE);
        register_item_block(r, BlocksTFC.DIRT_DOLOMITE);
        register_item_block(r, BlocksTFC.DIRT_CHERT);
        register_item_block(r, BlocksTFC.DIRT_CHALK);
        register_item_block(r, BlocksTFC.DIRT_RHYOLITE);
        register_item_block(r, BlocksTFC.DIRT_BASALT);
        register_item_block(r, BlocksTFC.DIRT_ANDESITE);
        register_item_block(r, BlocksTFC.DIRT_DACITE);
        register_item_block(r, BlocksTFC.DIRT_QUARTZITE);
        register_item_block(r, BlocksTFC.DIRT_SLATE);
        register_item_block(r, BlocksTFC.DIRT_PHYLLITE);
        register_item_block(r, BlocksTFC.DIRT_SCHIST);
        register_item_block(r, BlocksTFC.DIRT_GNEISS);
        register_item_block(r, BlocksTFC.DIRT_MARBLE);
        register_item_block(r, BlocksTFC.GRASS_GRANITE);
        register_item_block(r, BlocksTFC.GRASS_DIORITE);
        register_item_block(r, BlocksTFC.GRASS_GABBRO);
        register_item_block(r, BlocksTFC.GRASS_SHALE);
        register_item_block(r, BlocksTFC.GRASS_CLAYSTONE);
        register_item_block(r, BlocksTFC.GRASS_ROCKSALT);
        register_item_block(r, BlocksTFC.GRASS_LIMESTONE);
        register_item_block(r, BlocksTFC.GRASS_CONGLOMERATE);
        register_item_block(r, BlocksTFC.GRASS_DOLOMITE);
        register_item_block(r, BlocksTFC.GRASS_CHERT);
        register_item_block(r, BlocksTFC.GRASS_CHALK);
        register_item_block(r, BlocksTFC.GRASS_RHYOLITE);
        register_item_block(r, BlocksTFC.GRASS_BASALT);
        register_item_block(r, BlocksTFC.GRASS_ANDESITE);
        register_item_block(r, BlocksTFC.GRASS_DACITE);
        register_item_block(r, BlocksTFC.GRASS_QUARTZITE);
        register_item_block(r, BlocksTFC.GRASS_SLATE);
        register_item_block(r, BlocksTFC.GRASS_PHYLLITE);
        register_item_block(r, BlocksTFC.GRASS_SCHIST);
        register_item_block(r, BlocksTFC.GRASS_GNEISS);
        register_item_block(r, BlocksTFC.GRASS_MARBLE);
        register_item_block(r, BlocksTFC.DRY_GRASS_GRANITE);
        register_item_block(r, BlocksTFC.DRY_GRASS_DIORITE);
        register_item_block(r, BlocksTFC.DRY_GRASS_GABBRO);
        register_item_block(r, BlocksTFC.DRY_GRASS_SHALE);
        register_item_block(r, BlocksTFC.DRY_GRASS_CLAYSTONE);
        register_item_block(r, BlocksTFC.DRY_GRASS_ROCKSALT);
        register_item_block(r, BlocksTFC.DRY_GRASS_LIMESTONE);
        register_item_block(r, BlocksTFC.DRY_GRASS_CONGLOMERATE);
        register_item_block(r, BlocksTFC.DRY_GRASS_DOLOMITE);
        register_item_block(r, BlocksTFC.DRY_GRASS_CHERT);
        register_item_block(r, BlocksTFC.DRY_GRASS_CHALK);
        register_item_block(r, BlocksTFC.DRY_GRASS_RHYOLITE);
        register_item_block(r, BlocksTFC.DRY_GRASS_BASALT);
        register_item_block(r, BlocksTFC.DRY_GRASS_ANDESITE);
        register_item_block(r, BlocksTFC.DRY_GRASS_DACITE);
        register_item_block(r, BlocksTFC.DRY_GRASS_QUARTZITE);
        register_item_block(r, BlocksTFC.DRY_GRASS_SLATE);
        register_item_block(r, BlocksTFC.DRY_GRASS_PHYLLITE);
        register_item_block(r, BlocksTFC.DRY_GRASS_SCHIST);
        register_item_block(r, BlocksTFC.DRY_GRASS_GNEISS);
        register_item_block(r, BlocksTFC.DRY_GRASS_MARBLE);
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
