/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.render.TESRChestTFC;
import net.dries007.tfc.client.render.TESRPitKiln;
import net.dries007.tfc.client.render.TESRWorldItem;
import net.dries007.tfc.objects.Gem;
import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.blocks.BlockSlabTFC;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.stone.BlockFarmlandTFC;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.blocks.wood.BlockSaplingTFC;
import net.dries007.tfc.objects.items.ItemGem;
import net.dries007.tfc.objects.items.ItemGoldPan;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.te.TEChestTFC;
import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.objects.te.TEWorldItem;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
public final class ClientRegisterEvents
{
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("ConstantConditions")
    public static void registerModels(ModelRegistryEvent event)
    {
        for (Item item : ItemsTFC.getAllSimpleItems())
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName().toString()));

        for (EnumDyeColor color : EnumDyeColor.values())
        {
            ModelLoader.setCustomModelResourceLocation(ItemsTFC.CERAMICS_UNFIRED_VESSEL_GLAZED, color.getDyeDamage(), new ModelResourceLocation(ItemsTFC.CERAMICS_UNFIRED_VESSEL_GLAZED.getRegistryName().toString()));
            ModelLoader.setCustomModelResourceLocation(ItemsTFC.CERAMICS_FIRED_VESSEL_GLAZED, color.getDyeDamage(), new ModelResourceLocation(ItemsTFC.CERAMICS_FIRED_VESSEL_GLAZED.getRegistryName().toString()));
        }

        ItemGoldPan.registerModels();

        for (ItemGem item : ItemsTFC.getAllGemItems())
            for (Gem.Grade grade : Gem.Grade.values())
                registerEnumBasedMetaItems("gem", grade, item);

        for (ItemOreTFC item : ItemsTFC.getAllOreItems())
            if (item.ore.graded)
                for (Ore.Grade grade : Ore.Grade.values())
                    registerEnumBasedMetaItems("ore", grade, item);
            else
                registerEnumBasedMetaItems("ore", Ore.Grade.NORMAL, item);

        for (Block block : BlocksTFC.getAllFluidBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockFluidBase.LEVEL).build());

        for (Block block : BlocksTFC.getAllFenceGateBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockFenceGate.POWERED).build());

        for (Block block : BlocksTFC.getAllLeafBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE).build());

        for (Block block : BlocksTFC.getAllOreBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockOreTFC.GRADE).build());

        for (Block block : BlocksTFC.getAllWallBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockWall.VARIANT).build());

        for (Block block : BlocksTFC.getAllLogBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockLogTFC.PLACED).build());

        for (Block block : BlocksTFC.getAllSaplingBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockSaplingTFC.STAGE).build());

        for (Block block : BlocksTFC.getAllDoorBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockDoor.POWERED).build());

        for (BlockSlabTFC.Half block : BlocksTFC.getAllSlabBlocks())
        {
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockSlabTFC.VARIANT).build());
            ModelLoader.setCustomStateMapper(block.doubleSlab, new StateMap.Builder().ignore(BlockSlabTFC.VARIANT).build());
        }

        BlocksTFC.getAllBlockRockVariants().stream().filter(x -> x.type == Rock.Type.FARMLAND).forEach(e ->
            ModelLoader.setCustomStateMapper(e, new StateMap.Builder().ignore(BlockFarmlandTFC.MOISTURE).build())
        );

        for (Block block : BlocksTFC.getAllChestBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockChest.FACING).build());
        ClientRegistry.bindTileEntitySpecialRenderer(TEChestTFC.class, new TESRChestTFC());

        ModelLoader.setCustomStateMapper(BlocksTFC.PIT_KILN, blockIn -> ImmutableMap.of(BlocksTFC.PIT_KILN.getDefaultState(), new ModelResourceLocation("tfc:empty")));
        ClientRegistry.bindTileEntitySpecialRenderer(TEPitKiln.class, new TESRPitKiln());

        ModelLoader.setCustomStateMapper(BlocksTFC.WORLD_ITEM, blockIn -> ImmutableMap.of(BlocksTFC.WORLD_ITEM.getDefaultState(), new ModelResourceLocation("tfc:empty")));
        ClientRegistry.bindTileEntitySpecialRenderer(TEWorldItem.class, new TESRWorldItem());

        for (Block block : BlocksTFC.getAllNormalItemBlocks())
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "normal"));

        for (Block block : BlocksTFC.getAllInventoryItemBlocks())
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerColorHandlerBlocks(ColorHandlerEvent.Block event)
    {
        BlockColors blockcolors = event.getBlockColors();

        blockcolors.registerBlockColorHandler((state, worldIn, pos, tintIndex) ->
                        worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D),
                BlocksTFC.getAllBlockRockVariants().stream().filter(x -> x.type.isGrass).toArray(BlockRockVariant[]::new));

        blockcolors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> BlockFarmlandTFC.TINT[state.getValue(BlockFarmlandTFC.MOISTURE)],
            BlocksTFC.getAllBlockRockVariants().stream().filter(x -> x.type == Rock.Type.FARMLAND).toArray(BlockRockVariant[]::new));

        blockcolors.registerBlockColorHandler((state, worldIn, pos, tintIndex) ->
                        worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D),
                BlocksTFC.PEAT_GRASS);

        blockcolors.registerBlockColorHandler((state, worldIn, pos, tintIndex) ->
                        worldIn != null && pos != null ? BiomeColorHelper.getWaterColorAtPos(worldIn, pos) : 0,
                BlocksTFC.getAllFluidBlocks().stream().filter(x -> x.getDefaultState().getMaterial() == Material.WATER).toArray(BlockFluidBase[]::new));

        blockcolors.registerBlockColorHandler((state, worldIn, pos, tintIndex) ->
                        worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic(),
                BlocksTFC.getAllLeafBlocks().toArray(new Block[0]));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public static void registerColorHandlerItems(ColorHandlerEvent.Item event)
    {
        ItemColors itemColors = event.getItemColors();

        itemColors.registerItemColorHandler((stack, tintIndex) ->
                        event.getBlockColors().colorMultiplier(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex),
                BlocksTFC.getAllBlockRockVariants().stream().filter(x -> x.type.isGrass).toArray(BlockRockVariant[]::new));

        itemColors.registerItemColorHandler((stack, tintIndex) ->
                        event.getBlockColors().colorMultiplier(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex),
                BlocksTFC.PEAT_GRASS);

        itemColors.registerItemColorHandler((stack, tintIndex) ->
                        event.getBlockColors().colorMultiplier(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex),
                BlocksTFC.getAllLeafBlocks().stream().toArray(BlockLeavesTFC[]::new));

        itemColors.registerItemColorHandler((stack, tintIndex) -> tintIndex == 1 ? EnumDyeColor.byDyeDamage(stack.getItemDamage()).getColorValue() : 0xFFFFFF,
                ItemsTFC.CERAMICS_UNFIRED_VESSEL_GLAZED, ItemsTFC.CERAMICS_FIRED_VESSEL_GLAZED);
    }

    /**
     * Turns "gem/diamond" + enum NORMAL into "gem/normal/diamond"
     */
    @SideOnly(Side.CLIENT)
    private static void registerEnumBasedMetaItems(String prefix, Enum e, Item item)
    {
        //noinspection ConstantConditions
        String registryName = item.getRegistryName().getResourcePath();
        StringBuilder path = new StringBuilder(MOD_ID).append(':');
        if (!Strings.isNullOrEmpty(prefix)) path.append(prefix).append('/');
        path.append(e.name());
        if (!Strings.isNullOrEmpty(prefix))
            path.append(registryName.replace(prefix, "")); // There well be a '/' at the start of registryName due to the prefix, so don't add an extra one.
        else path.append('/').append(registryName);
        ModelLoader.setCustomModelResourceLocation(item, e.ordinal(), new ModelResourceLocation(path.toString().toLowerCase()));
    }
}
