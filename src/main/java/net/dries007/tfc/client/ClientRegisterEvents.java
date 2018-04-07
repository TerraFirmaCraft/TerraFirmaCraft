package net.dries007.tfc.client;

import net.dries007.tfc.objects.blocks.BlockTFCVariant;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
public final class ClientRegisterEvents
{
    private ClientRegisterEvents() {}

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(ItemsTFC.WAND, 0, new ModelResourceLocation(ItemsTFC.WAND.getRegistryName().toString()));

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlocksTFC.DEBUG), 0, new ModelResourceLocation(BlocksTFC.DEBUG.getRegistryName(), "inventory"));

        for (BlockFluidBase base : BlocksTFC.getAllFluidBlocks())
            ModelLoader.setCustomStateMapper(base, new StateMap.Builder().ignore(BlockFluidBase.LEVEL).build());

        for (BlockTFCVariant variant : BlocksTFC.getAllBlockTFCVariants())
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(variant), 0, new ModelResourceLocation(variant.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public static void registerColorHandlerBlocks(ColorHandlerEvent.Block event)
    {
        BlockColors blockcolors = event.getBlockColors();

        blockcolors.registerBlockColorHandler((state, worldIn, pos, tintIndex) ->
                        worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D),
                BlocksTFC.getAllBlockTFCVariants().stream().filter(x -> x.type.isColorIndexed).toArray(BlockTFCVariant[]::new));

        blockcolors.registerBlockColorHandler((state, worldIn, pos, tintIndex) ->
                        worldIn != null && pos != null ? BiomeColorHelper.getWaterColorAtPos(worldIn, pos) : 0,
                BlocksTFC.getAllFluidBlocks().stream().filter(x -> x.getDefaultState().getMaterial() == Material.WATER).toArray(BlockFluidBase[]::new));
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void registerColorHandlerItems(ColorHandlerEvent.Item event)
    {
        ItemColors itemColors = event.getItemColors();

        itemColors.registerItemColorHandler((stack, tintIndex) ->
                        event.getBlockColors().colorMultiplier(((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex),
                BlocksTFC.getAllBlockTFCVariants().stream().filter(x -> x.type.isColorIndexed).toArray(BlockTFCVariant[]::new));
    }
}
