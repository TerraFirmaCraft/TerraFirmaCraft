/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import java.util.Arrays;
import javax.annotation.Nonnull;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.client.render.*;
import net.dries007.tfc.objects.Gem;
import net.dries007.tfc.objects.blocks.BlockSlabTFC;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;
import net.dries007.tfc.objects.blocks.stone.BlockFarmlandTFC;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.blocks.wood.BlockSaplingTFC;
import net.dries007.tfc.objects.items.ItemGem;
import net.dries007.tfc.objects.items.ItemGoldPan;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemMold;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.te.*;
import net.dries007.tfc.world.classic.ClimateRenderHelper;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
public final class ClientRegisterEvents
{
    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public static void registerModels(ModelRegistryEvent event)
    {
        // ITEMS //

        // Simple Items
        for (Item item : ItemsTFC.getAllSimpleItems())
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName().toString()));

        // Dye color Items
        for (EnumDyeColor color : EnumDyeColor.values())
        {
            ModelLoader.setCustomModelResourceLocation(ItemsTFC.CERAMICS_UNFIRED_VESSEL_GLAZED, color.getDyeDamage(), new ModelResourceLocation(ItemsTFC.CERAMICS_UNFIRED_VESSEL_GLAZED.getRegistryName().toString()));
            ModelLoader.setCustomModelResourceLocation(ItemsTFC.CERAMICS_FIRED_VESSEL_GLAZED, color.getDyeDamage(), new ModelResourceLocation(ItemsTFC.CERAMICS_FIRED_VESSEL_GLAZED.getRegistryName().toString()));
        }

        // Gems
        for (ItemGem item : ItemsTFC.getAllGemItems())
            for (Gem.Grade grade : Gem.Grade.values())
                registerEnumBasedMetaItems("gem", grade, item);

        // Ore Items
        for (ItemOreTFC item : ItemsTFC.getAllOreItems())
            if (item.ore.isGraded())
                for (Ore.Grade grade : Ore.Grade.values())
                    registerEnumBasedMetaItems("ore", grade, item);
            else
                registerEnumBasedMetaItems("ore", Ore.Grade.NORMAL, item);

        // Gold Pan
        ModelLoader.registerItemVariants(ItemsTFC.GOLDPAN, Arrays.stream(ItemGoldPan.TYPES).map(e -> new ResourceLocation(MOD_ID, "goldpan/" + e)).toArray(ResourceLocation[]::new));
        for (int meta = 0; meta < ItemGoldPan.TYPES.length; meta++)
            ModelLoader.setCustomModelResourceLocation(ItemsTFC.GOLDPAN, meta, new ModelResourceLocation(MOD_ID + ":goldpan/" + ItemGoldPan.TYPES[meta]));
        ModelLoader.registerItemVariants(ItemsTFC.GOLDPAN, Arrays.stream(ItemGoldPan.TYPES).map(e -> new ResourceLocation(MOD_ID, "goldpan/" + e)).toArray(ResourceLocation[]::new));

        // Ceramic Molds
        ModelBakery.registerItemVariants(ItemMold.get(Metal.ItemType.INGOT), new ModelResourceLocation(ItemMold.get(Metal.ItemType.INGOT).getRegistryName() + "/unknown"));
        for (Metal.ItemType value : Metal.ItemType.values())
        {
            ItemMold item = ItemMold.get(value);
            if (item == null) continue;

            ModelBakery.registerItemVariants(item, new ModelResourceLocation(item.getRegistryName().toString() + "/empty"));
            ModelBakery.registerItemVariants(item, TFCRegistries.METALS.getValuesCollection()
                .stream()
                .filter(value::hasMold)
                .map(x -> new ModelResourceLocation(item.getRegistryName().toString() + "/" + x.getRegistryName().getPath()))
                .toArray(ModelResourceLocation[]::new));
            ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition()
            {
                private ModelResourceLocation FALLBACK = new ModelResourceLocation(item.getRegistryName().toString() + "/empty");

                @Override
                @Nonnull
                public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack)
                {
                    IFluidHandler cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                    if (cap instanceof IMoldHandler)
                    {
                        Metal metal = ((IMoldHandler) cap).getMetal();
                        if (metal != null)
                        {
                            return new ModelResourceLocation(stack.getItem().getRegistryName() + "/" + metal.getRegistryName().getPath());
                        }
                    }
                    return FALLBACK;
                }
            });
        }

        // Item Blocks
        for (ItemBlock item : BlocksTFC.getAllNormalItemBlocks())
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "normal"));

        for (ItemBlock item : BlocksTFC.getAllInventoryItemBlocks())
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));

        for (ItemBlock item : BlocksTFC.getAllBarrelItemBlocks())
        {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "sealed=false"));
            ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation(item.getRegistryName(), "sealed=true"));
        }

        // BLOCKS - STATE MAPPERS //

        // Blocks with Ignored Properties
        for (Block block : BlocksTFC.getAllFluidBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockFluidBase.LEVEL).build());

        for (Block block : BlocksTFC.getAllFenceGateBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockFenceGate.POWERED).build());

        for (Block block : BlocksTFC.getAllLeafBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockLeaves.DECAYABLE).build());

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

        for (Block block : BlocksTFC.getAllChestBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockChest.FACING).build());

        for (BlockSlabTFC.Half block : BlocksTFC.getAllSlabBlocks())
        {
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockSlabTFC.VARIANT).build());
            ModelLoader.setCustomStateMapper(block.doubleSlab, new StateMap.Builder().ignore(BlockSlabTFC.VARIANT).build());
        }

        BlocksTFC.getAllBlockRockVariants().stream().filter(x -> x.type == Rock.Type.FARMLAND).forEach(e ->
            ModelLoader.setCustomStateMapper(e, new StateMap.Builder().ignore(BlockFarmlandTFC.MOISTURE).build())
        );

        // Empty Models
        ModelLoader.setCustomStateMapper(BlocksTFC.PIT_KILN, blockIn -> ImmutableMap.of(BlocksTFC.PIT_KILN.getDefaultState(), new ModelResourceLocation("tfc:empty")));
        ModelLoader.setCustomStateMapper(BlocksTFC.WORLD_ITEM, blockIn -> ImmutableMap.of(BlocksTFC.WORLD_ITEM.getDefaultState(), new ModelResourceLocation("tfc:empty")));
        ModelLoader.setCustomStateMapper(BlocksTFC.INGOT_PILE, blockIn -> ImmutableMap.of(BlocksTFC.INGOT_PILE.getDefaultState(), new ModelResourceLocation("tfc:empty")));

        // TESRs //

        ClientRegistry.bindTileEntitySpecialRenderer(TEChestTFC.class, new TESRChestTFC());
        ClientRegistry.bindTileEntitySpecialRenderer(TEToolRack.class, new TESRToolRack());
        ClientRegistry.bindTileEntitySpecialRenderer(TEPitKiln.class, new TESRPitKiln());
        ClientRegistry.bindTileEntitySpecialRenderer(TEWorldItem.class, new TESRWorldItem());
        ClientRegistry.bindTileEntitySpecialRenderer(TEIngotPile.class, new TESRIngotPile());
        ClientRegistry.bindTileEntitySpecialRenderer(TEBellows.class, new TESRBellows());
        ClientRegistry.bindTileEntitySpecialRenderer(TEBarrel.class, new TESRBarrel());
        ClientRegistry.bindTileEntitySpecialRenderer(TEAnvilTFC.class, new TESRAnvil());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerColorHandlerBlocks(ColorHandlerEvent.Block event)
    {
        BlockColors blockcolors = event.getBlockColors();

        // Grass Colors
        IBlockColor grassColor = (state, worldIn, pos, tintIndex) -> {
            if (pos != null)
            {
                ClimateRenderHelper.ClimateData data = ClimateRenderHelper.get(pos);
                // This internally will call CalendarTFC to get the month based temperature
                // Base Temp Range is <-25, 20>, Month Adj Range is <-30, 30>
                double temp = MathHelper.clamp((data.getTemperature() + 30) / 30, 0, 1);
                // Rainfall is in <0, 500>, although 99% of the time it is within a smaller range of <50, 450>, so trim and clamp as necessary
                double rain = MathHelper.clamp((data.getRainfall() - 50) / 400, 0, 1);
                return ColorizerGrass.getGrassColor(temp, rain);
            }
            return ColorizerGrass.getGrassColor(0.5, 0.5);
        };

        // Foliage Color
        IBlockColor foliageColor = (state, worldIn, pos, tintIndex) -> {
            if (pos != null)
            {
                ClimateRenderHelper.ClimateData data = ClimateRenderHelper.get(pos);
                // This internally will call CalendarTFC to get the month based temperature
                // Base Temp Range is <-25, 20>, Month Adj Range is <-30, 30>
                double temp = MathHelper.clamp((data.getTemperature() + 30) / 30, 0, 1);
                // Rainfall is in <0, 500>, although 99% of the time it is within a smaller range of <50, 450>, so trim and clamp as necessary
                double rain = MathHelper.clamp((data.getRainfall() - 50) / 400, 0, 1);
                return ColorizerGrass.getGrassColor(temp, rain);
            }
            return ColorizerGrass.getGrassColor(0.5, 0.5);
        };

        blockcolors.registerBlockColorHandler(grassColor, BlocksTFC.PEAT_GRASS);
        blockcolors.registerBlockColorHandler(grassColor, BlocksTFC.getAllBlockRockVariants().stream().filter(x -> x.type.isGrass).toArray(BlockRockVariant[]::new));
        // This is talking about tall grass vs actual grass blocks
        blockcolors.registerBlockColorHandler(grassColor, BlocksTFC.getAllGrassBlocks().toArray(new BlockPlantTFC[0]));

        blockcolors.registerBlockColorHandler(foliageColor, BlocksTFC.getAllLeafBlocks().toArray(new Block[0]));
        blockcolors.registerBlockColorHandler(foliageColor, BlocksTFC.getAllPlantBlocks().toArray(new BlockPlantTFC[0]));

        blockcolors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> BlockFarmlandTFC.TINT[state.getValue(BlockFarmlandTFC.MOISTURE)],
            BlocksTFC.getAllBlockRockVariants().stream().filter(x -> x.type == Rock.Type.FARMLAND).toArray(BlockRockVariant[]::new));

        blockcolors.registerBlockColorHandler((state, worldIn, pos, tintIndex) ->
                worldIn != null && pos != null ? BiomeColorHelper.getWaterColorAtPos(worldIn, pos) : 0,
            BlocksTFC.getAllFluidBlocks().stream().filter(x -> x.getDefaultState().getMaterial() == Material.WATER).toArray(BlockFluidBase[]::new));
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
            BlocksTFC.getAllLeafBlocks().toArray(new BlockLeavesTFC[0]));

        itemColors.registerItemColorHandler((stack, tintIndex) -> tintIndex == 1 ? EnumDyeColor.byDyeDamage(stack.getItemDamage()).getColorValue() : 0xFFFFFF,
            ItemsTFC.CERAMICS_UNFIRED_VESSEL_GLAZED, ItemsTFC.CERAMICS_FIRED_VESSEL_GLAZED);

        itemColors.registerItemColorHandler((stack, tintIndex) ->
                event.getBlockColors().colorMultiplier(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex),
            BlocksTFC.getAllPlantBlocks().toArray(new BlockPlantTFC[0]));

        itemColors.registerItemColorHandler((stack, tintIndex) ->
                event.getBlockColors().colorMultiplier(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex),
            BlocksTFC.getAllGrassBlocks().toArray(new BlockPlantTFC[0]));
    }

    /**
     * Turns "gem/diamond" + enum NORMAL into "gem/normal/diamond"
     */
    @SideOnly(Side.CLIENT)
    private static void registerEnumBasedMetaItems(String prefix, Enum e, Item item)
    {
        //noinspection ConstantConditions
        String registryName = item.getRegistryName().getPath();
        StringBuilder path = new StringBuilder(MOD_ID).append(':');
        if (!Strings.isNullOrEmpty(prefix)) path.append(prefix).append('/');
        path.append(e.name());
        if (!Strings.isNullOrEmpty(prefix))
            path.append(registryName.replace(prefix, "")); // There well be a '/' at the start of registryName due to the prefix, so don't add an extra one.
        else path.append('/').append(registryName);
        ModelLoader.setCustomModelResourceLocation(item, e.ordinal(), new ModelResourceLocation(path.toString().toLowerCase()));
    }
}
