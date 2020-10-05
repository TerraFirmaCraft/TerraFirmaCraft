/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.*;
import net.dries007.tfc.objects.blocks.agriculture.*;
import net.dries007.tfc.objects.blocks.devices.*;
import net.dries007.tfc.objects.blocks.metal.*;
import net.dries007.tfc.objects.blocks.plants.BlockFloatingWaterTFC;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;
import net.dries007.tfc.objects.blocks.stone.*;
import net.dries007.tfc.objects.blocks.wood.*;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.fluids.properties.FluidWrapper;
import net.dries007.tfc.objects.items.itemblock.*;
import net.dries007.tfc.objects.te.*;
import net.dries007.tfc.util.agriculture.BerryBush;
import net.dries007.tfc.util.agriculture.Crop;
import net.dries007.tfc.util.agriculture.FruitTree;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.api.types.Rock.Type.*;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;
import static net.dries007.tfc.util.Helpers.getNull;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class BlocksTFC
{
    @GameRegistry.ObjectHolder("ceramics/fired/large_vessel")
    public static final BlockLargeVessel FIRED_LARGE_VESSEL = getNull();

    @GameRegistry.ObjectHolder("alabaster/bricks/plain")
    public static final BlockDecorativeStone ALABASTER_BRICKS_PLAIN = getNull();
    @GameRegistry.ObjectHolder("alabaster/polished/plain")
    public static final BlockDecorativeStone ALABASTER_POLISHED_PLAIN = getNull();
    @GameRegistry.ObjectHolder("alabaster/raw/plain")
    public static final BlockDecorativeStone ALABASTER_RAW_PLAIN = getNull();

    public static final BlockDebug DEBUG = getNull();
    public static final BlockPeat PEAT = getNull();
    public static final BlockPeat PEAT_GRASS = getNull();
    public static final BlockFirePit FIREPIT = getNull();
    public static final BlockThatch THATCH = getNull();
    public static final BlockThatchBed THATCH_BED = getNull();
    public static final BlockPitKiln PIT_KILN = getNull();
    public static final BlockPlacedItemFlat PLACED_ITEM_FLAT = getNull();
    public static final BlockPlacedItem PLACED_ITEM = getNull();
    public static final BlockPlacedHide PLACED_HIDE = getNull();
    public static final BlockCharcoalPile CHARCOAL_PILE = getNull();
    public static final BlockNestBox NEST_BOX = getNull();
    public static final BlockLogPile LOG_PILE = getNull();
    public static final BlockIngotPile INGOT_PILE = getNull();
    public static final BlockCharcoalForge CHARCOAL_FORGE = getNull();
    public static final BlockCrucible CRUCIBLE = getNull();
    public static final BlockMolten MOLTEN = getNull();
    public static final BlockBlastFurnace BLAST_FURNACE = getNull();
    public static final BlockBloom BLOOM = getNull();
    public static final BlockBloomery BLOOMERY = getNull();
    public static final BlockQuern QUERN = getNull();
    public static final BlockIceTFC SEA_ICE = getNull();
    public static final BlockPowderKeg POWDERKEG = getNull();
    public static final BlockGravel AGGREGATE = getNull();
    public static final Block FIRE_BRICKS = getNull();

    // All these are for use in model registration. Do not use for block lookups.
    // Use the static get methods in the classes instead.
    private static ImmutableList<ItemBlock> allNormalItemBlocks;
    private static ImmutableList<ItemBlock> allInventoryItemBlocks;
    private static ImmutableList<ItemBlockBarrel> allBarrelItemBlocks;

    private static ImmutableList<BlockFluidBase> allFluidBlocks;
    private static ImmutableList<BlockRockVariant> allBlockRockVariants;
    private static ImmutableList<BlockOreTFC> allOreBlocks;
    private static ImmutableList<BlockWallTFC> allWallBlocks;
    private static ImmutableList<BlockLogTFC> allLogBlocks;
    private static ImmutableList<BlockLeavesTFC> allLeafBlocks;
    private static ImmutableList<BlockFenceGateTFC> allFenceGateBlocks;
    private static ImmutableList<BlockSaplingTFC> allSaplingBlocks;
    private static ImmutableList<BlockDoorTFC> allDoorBlocks;
    private static ImmutableList<BlockTrapDoorWoodTFC> allTrapDoorWoodBlocks;
    private static ImmutableList<BlockTrapDoorMetalTFC> allTrapDoorMetalBlocks;
    private static ImmutableList<BlockStairsTFC> allStairsBlocks;
    private static ImmutableList<BlockSlabTFC.Half> allSlabBlocks;
    private static ImmutableList<BlockChestTFC> allChestBlocks;
    private static ImmutableList<BlockAnvilTFC> allAnvils;
    private static ImmutableList<BlockMetalSheet> allSheets;
    private static ImmutableList<BlockMetalLamp> allLamps;
    private static ImmutableList<BlockToolRack> allToolRackBlocks;
    private static ImmutableList<BlockCropTFC> allCropBlocks;
    private static ImmutableList<BlockCropDead> allDeadCropBlocks;
    private static ImmutableList<BlockPlantTFC> allPlantBlocks;
    private static ImmutableList<BlockPlantTFC> allGrassBlocks;
    private static ImmutableList<BlockLoom> allLoomBlocks;
    private static ImmutableList<BlockSupport> allSupportBlocks;

    private static ImmutableList<BlockFruitTreeSapling> allFruitTreeSaplingBlocks;
    private static ImmutableList<BlockFruitTreeTrunk> allFruitTreeTrunkBlocks;
    private static ImmutableList<BlockFruitTreeBranch> allFruitTreeBranchBlocks;
    private static ImmutableList<BlockFruitTreeLeaves> allFruitTreeLeavesBlocks;

    private static ImmutableList<BlockBerryBush> allBerryBushBlocks;

    public static ImmutableList<ItemBlock> getAllNormalItemBlocks()
    {
        return allNormalItemBlocks;
    }

    public static ImmutableList<ItemBlock> getAllInventoryItemBlocks()
    {
        return allInventoryItemBlocks;
    }

    public static ImmutableList<ItemBlockBarrel> getAllBarrelItemBlocks()
    {
        return allBarrelItemBlocks;
    }

    public static ImmutableList<BlockFluidBase> getAllFluidBlocks()
    {
        return allFluidBlocks;
    }

    public static ImmutableList<BlockRockVariant> getAllBlockRockVariants()
    {
        return allBlockRockVariants;
    }

    public static ImmutableList<BlockLogTFC> getAllLogBlocks()
    {
        return allLogBlocks;
    }

    public static ImmutableList<BlockLeavesTFC> getAllLeafBlocks()
    {
        return allLeafBlocks;
    }

    public static ImmutableList<BlockOreTFC> getAllOreBlocks()
    {
        return allOreBlocks;
    }

    public static ImmutableList<BlockFenceGateTFC> getAllFenceGateBlocks()
    {
        return allFenceGateBlocks;
    }

    public static ImmutableList<BlockWallTFC> getAllWallBlocks()
    {
        return allWallBlocks;
    }

    public static ImmutableList<BlockSaplingTFC> getAllSaplingBlocks()
    {
        return allSaplingBlocks;
    }

    public static ImmutableList<BlockDoorTFC> getAllDoorBlocks()
    {
        return allDoorBlocks;
    }

    public static ImmutableList<BlockTrapDoorWoodTFC> getAllTrapDoorWoodBlocks()
    {
        return allTrapDoorWoodBlocks;
    }

    public static ImmutableList<BlockTrapDoorMetalTFC> getAllTrapDoorMetalBlocks()
    {
        return allTrapDoorMetalBlocks;
    }

    public static ImmutableList<BlockStairsTFC> getAllStairsBlocks()
    {
        return allStairsBlocks;
    }

    public static ImmutableList<BlockSlabTFC.Half> getAllSlabBlocks()
    {
        return allSlabBlocks;
    }

    public static ImmutableList<BlockChestTFC> getAllChestBlocks()
    {
        return allChestBlocks;
    }

    public static ImmutableList<BlockAnvilTFC> getAllAnvils()
    {
        return allAnvils;
    }

    public static ImmutableList<BlockMetalSheet> getAllSheets()
    {
        return allSheets;
    }

    public static ImmutableList<BlockMetalLamp> getAllLamps()
    {
        return allLamps;
    }

    public static ImmutableList<BlockToolRack> getAllToolRackBlocks()
    {
        return allToolRackBlocks;
    }

    public static ImmutableList<BlockCropTFC> getAllCropBlocks()
    {
        return allCropBlocks;
    }

    public static ImmutableList<BlockCropDead> getAllDeadCropBlocks()
    {
        return allDeadCropBlocks;
    }

    public static ImmutableList<BlockPlantTFC> getAllPlantBlocks()
    {
        return allPlantBlocks;
    }

    public static ImmutableList<BlockPlantTFC> getAllGrassBlocks()
    {
        return allGrassBlocks;
    }

    public static ImmutableList<BlockLoom> getAllLoomBlocks()
    {
        return allLoomBlocks;
    }

    public static ImmutableList<BlockSupport> getAllSupportBlocks()
    {
        return allSupportBlocks;
    }

    public static ImmutableList<BlockFruitTreeSapling> getAllFruitTreeSaplingBlocks()
    {
        return allFruitTreeSaplingBlocks;
    }

    public static ImmutableList<BlockFruitTreeTrunk> getAllFruitTreeTrunkBlocks()
    {
        return allFruitTreeTrunkBlocks;
    }

    public static ImmutableList<BlockFruitTreeBranch> getAllFruitTreeBranchBlocks()
    {
        return allFruitTreeBranchBlocks;
    }

    public static ImmutableList<BlockFruitTreeLeaves> getAllFruitTreeLeavesBlocks()
    {
        return allFruitTreeLeavesBlocks;
    }

    public static ImmutableList<BlockBerryBush> getAllBerryBushBlocks()
    {
        return allBerryBushBlocks;
    }

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        // This is called here because it needs to wait until Metal registry has fired
        FluidsTFC.registerFluids();

        IForgeRegistry<Block> r = event.getRegistry();

        Builder<ItemBlock> normalItemBlocks = ImmutableList.builder();
        Builder<ItemBlock> inventoryItemBlocks = ImmutableList.builder();

        normalItemBlocks.add(new ItemBlockTFC(register(r, "debug", new BlockDebug(), CT_MISC)));

        normalItemBlocks.add(new ItemBlockTFC(register(r, "aggregate", new BlockAggregate(), CT_ROCK_BLOCKS)));
        normalItemBlocks.add(new ItemBlockTFC(register(r, "fire_clay_block", new BlockFireClay(), CT_ROCK_BLOCKS)));

        normalItemBlocks.add(new ItemBlockTFC(register(r, "peat", new BlockPeat(Material.GROUND), CT_ROCK_BLOCKS)));
        normalItemBlocks.add(new ItemBlockTFC(register(r, "peat_grass", new BlockPeatGrass(Material.GRASS), CT_ROCK_BLOCKS)));

        normalItemBlocks.add(new ItemBlockTFC(register(r, "thatch", new BlockThatch(), CT_DECORATIONS)));
        normalItemBlocks.add(new ItemBlockTFC(register(r, "fire_bricks", new BlockFireBrick(), CT_DECORATIONS)));

        normalItemBlocks.add(new ItemBlockTFC(register(r, "quern", new BlockQuern(), CT_MISC)));
        normalItemBlocks.add(new ItemBlockCrucible(register(r, "crucible", new BlockCrucible(), CT_MISC)));
        normalItemBlocks.add(new ItemBlockTFC(register(r, "blast_furnace", new BlockBlastFurnace(), CT_MISC)));
        inventoryItemBlocks.add(new ItemBlockTFC(register(r, "bellows", new BlockBellows(), CT_MISC)));
        inventoryItemBlocks.add(new ItemBlockTFC(register(r, "bloomery", new BlockBloomery(), CT_MISC)));
        inventoryItemBlocks.add(new ItemBlockTFC(register(r, "nest_box", new BlockNestBox(), CT_MISC)));
        inventoryItemBlocks.add(new ItemBlockSluice(register(r, "sluice", new BlockSluice(), CT_MISC)));

        normalItemBlocks.add(new ItemBlockTFC(register(r, "sea_ice", new BlockIceTFC(FluidsTFC.SALT_WATER.get()), CT_MISC)));

        normalItemBlocks.add(new ItemBlockLargeVessel(register(r, "ceramics/fired/large_vessel", new BlockLargeVessel(), CT_POTTERY)));
        normalItemBlocks.add(new ItemBlockPowderKeg(register(r, "powderkeg", new BlockPowderKeg(), CT_WOOD)));

        normalItemBlocks.add(new ItemBlockTFC(register(r, "alabaster/raw/plain", new BlockDecorativeStone(MapColor.SNOW), CT_DECORATIONS)));
        normalItemBlocks.add(new ItemBlockTFC(register(r, "alabaster/polished/plain", new BlockDecorativeStone(MapColor.SNOW), CT_DECORATIONS)));
        normalItemBlocks.add(new ItemBlockTFC(register(r, "alabaster/bricks/plain", new BlockDecorativeStone(MapColor.SNOW), CT_DECORATIONS)));

        for (EnumDyeColor dyeColor : EnumDyeColor.values())
        {
            BlockDecorativeStone polished = new BlockDecorativeStone(MapColor.getBlockColor(dyeColor));
            BlockDecorativeStone bricks = new BlockDecorativeStone(MapColor.getBlockColor(dyeColor));
            BlockDecorativeStone raw = new BlockDecorativeStone(MapColor.getBlockColor(dyeColor));

            normalItemBlocks.add(new ItemBlockTFC(register(r, "alabaster/polished/" + dyeColor.getName(), polished, CT_DECORATIONS)));
            normalItemBlocks.add(new ItemBlockTFC(register(r, "alabaster/bricks/" + dyeColor.getName(), bricks, CT_DECORATIONS)));
            normalItemBlocks.add(new ItemBlockTFC(register(r, "alabaster/raw/" + dyeColor.getName(), raw, CT_DECORATIONS)));

            BlockDecorativeStone.ALABASTER_POLISHED.put(dyeColor, polished);
            BlockDecorativeStone.ALABASTER_BRICKS.put(dyeColor, bricks);
            BlockDecorativeStone.ALABASTER_RAW.put(dyeColor, raw);
        }

        {
            // Apparently this is the way we're supposed to do things even though the fluid registry defaults. So we'll do it this way.
            Builder<BlockFluidBase> b = ImmutableList.builder();
            b.add(
                register(r, "fluid/hot_water", new BlockFluidHotWater()),
                register(r, "fluid/fresh_water", new BlockFluidWater(FluidsTFC.FRESH_WATER.get(), Material.WATER, false)),
                register(r, "fluid/salt_water", new BlockFluidWater(FluidsTFC.SALT_WATER.get(), Material.WATER, true))
            );
            for (FluidWrapper wrapper : FluidsTFC.getAllAlcoholsFluids())
            {
                b.add(register(r, "fluid/" + wrapper.get().getName(), new BlockFluidTFC(wrapper.get(), Material.WATER)));
            }
            for (FluidWrapper wrapper : FluidsTFC.getAllOtherFiniteFluids())
            {
                b.add(register(r, "fluid/" + wrapper.get().getName(), new BlockFluidTFC(wrapper.get(), Material.WATER)));
            }
            for (FluidWrapper wrapper : FluidsTFC.getAllMetalFluids())
            {
                b.add(register(r, "fluid/" + wrapper.get().getName(), new BlockFluidTFC(wrapper.get(), Material.LAVA)));
            }
            for (EnumDyeColor color : EnumDyeColor.values())
            {
                FluidWrapper wrapper = FluidsTFC.getFluidFromDye(color);
                b.add(register(r, "fluid/" + wrapper.get().getName(), new BlockFluidTFC(wrapper.get(), Material.WATER)));
            }
            allFluidBlocks = b.build();
        }

        {
            Builder<BlockRockVariant> b = ImmutableList.builder();
            for (Rock.Type type : Rock.Type.values())
            {
                for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                {
                    if (type != Rock.Type.ANVIL)
                    {
                        b.add(register(r, type.name().toLowerCase() + "/" + rock.getRegistryName().getPath(), BlockRockVariant.create(rock, type), CT_ROCK_BLOCKS));
                    }
                    else if (rock.getRockCategory().hasAnvil())
                    {
                        // Anvil registration is special, is has it's own folder
                        register(r, "anvil/" + rock.getRegistryName().getPath(), BlockRockVariant.create(rock, type));
                    }
                }
            }
            allBlockRockVariants = b.build();
            allBlockRockVariants.forEach(x ->
            {
                if (x.getType() == Rock.Type.SAND)
                {
                    normalItemBlocks.add(new ItemBlockHeat(x, 1, 600));
                }
                else if (x.getType() != Rock.Type.SPIKE && x.getType() != Rock.Type.ANVIL)
                {
                    normalItemBlocks.add(new ItemBlockTFC(x));
                }
            });
        }

        {
            Builder<BlockOreTFC> b = ImmutableList.builder();
            for (Ore ore : TFCRegistries.ORES.getValuesCollection())
                for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                    b.add(register(r, ("ore/" + ore.getRegistryName().getPath() + "/" + rock.getRegistryName().getPath()).toLowerCase(), new BlockOreTFC(ore, rock), CT_ROCK_BLOCKS));
            allOreBlocks = b.build();
            allOreBlocks.forEach(x -> normalItemBlocks.add(new ItemBlockTFC(x)));
        }

        {
            Builder<BlockLogTFC> logs = ImmutableList.builder();
            Builder<BlockLeavesTFC> leaves = ImmutableList.builder();
            Builder<BlockFenceGateTFC> fenceGates = ImmutableList.builder();
            Builder<BlockSaplingTFC> saplings = ImmutableList.builder();
            Builder<BlockDoorTFC> doors = ImmutableList.builder();
            Builder<BlockTrapDoorWoodTFC> trapDoors = ImmutableList.builder();
            Builder<BlockChestTFC> chests = ImmutableList.builder();
            Builder<BlockToolRack> toolRacks = ImmutableList.builder();
            Builder<ItemBlockBarrel> barrelItems = ImmutableList.builder();
            Builder<BlockPlantTFC> plants = ImmutableList.builder();
            Builder<BlockLoom> looms = ImmutableList.builder();
            Builder<BlockSupport> supports = ImmutableList.builder();

            // This loop is split up to organize the ordering of the creative tab
            // Do not optimize these loops back together
            // All bookshelves + item blocks
            for (Tree wood : TFCRegistries.TREES.getValuesCollection())
                normalItemBlocks.add(new ItemBlockTFC(register(r, "wood/bookshelf/" + wood.getRegistryName().getPath(), new BlockBookshelfTFC(wood), CT_DECORATIONS)));
            // All workbenches + item blocks
            for (Tree wood : TFCRegistries.TREES.getValuesCollection())
                normalItemBlocks.add(new ItemBlockTFC(register(r, "wood/workbench/" + wood.getRegistryName().getPath(), new BlockWorkbenchTFC(wood), CT_DECORATIONS)));
            // All fences + item blocks
            for (Tree wood : TFCRegistries.TREES.getValuesCollection())
                inventoryItemBlocks.add(new ItemBlockTFC(register(r, "wood/fence/" + wood.getRegistryName().getPath(), new BlockFenceTFC(wood), CT_DECORATIONS)));
            // All buttons + item blocks
            for (Tree wood : TFCRegistries.TREES.getValuesCollection())
                inventoryItemBlocks.add(new ItemBlockTFC(register(r, "wood/button/" + wood.getRegistryName().getPath(), new BlockButtonWoodTFC(wood), CT_DECORATIONS)));
            // All pressure plates + item blocks
            for (Tree wood : TFCRegistries.TREES.getValuesCollection())
                inventoryItemBlocks.add(new ItemBlockTFC(register(r, "wood/pressure_plate/" + wood.getRegistryName().getPath().toLowerCase(), new BlockWoodPressurePlateTFC(wood), CT_DECORATIONS)));

            // Other blocks that don't have specific order requirements
            for (Tree wood : TFCRegistries.TREES.getValuesCollection())
            {
                // Only block in the decorations category
                normalItemBlocks.add(new ItemBlockTFC(register(r, "wood/planks/" + wood.getRegistryName().getPath(), new BlockPlanksTFC(wood), CT_WOOD)));
                // Blocks with specific block collections don't matter
                logs.add(register(r, "wood/log/" + wood.getRegistryName().getPath(), new BlockLogTFC(wood), CT_WOOD));
                leaves.add(register(r, "wood/leaves/" + wood.getRegistryName().getPath(), new BlockLeavesTFC(wood), CT_WOOD));
                fenceGates.add(register(r, "wood/fence_gate/" + wood.getRegistryName().getPath(), new BlockFenceGateTFC(wood), CT_DECORATIONS));
                saplings.add(register(r, "wood/sapling/" + wood.getRegistryName().getPath(), new BlockSaplingTFC(wood), CT_WOOD));
                doors.add(register(r, "wood/door/" + wood.getRegistryName().getPath(), new BlockDoorTFC(wood), CT_DECORATIONS));
                trapDoors.add(register(r, "wood/trapdoor/" + wood.getRegistryName().getPath(), new BlockTrapDoorWoodTFC(wood), CT_DECORATIONS));
                chests.add(register(r, "wood/chest/" + wood.getRegistryName().getPath(), new BlockChestTFC(BlockChestTFC.TFCBASIC, wood), CT_DECORATIONS));
                chests.add(register(r, "wood/chest_trap/" + wood.getRegistryName().getPath(), new BlockChestTFC(BlockChestTFC.TFCTRAP, wood), CT_DECORATIONS));

                toolRacks.add(register(r, "wood/tool_rack/" + wood.getRegistryName().getPath(), new BlockToolRack(wood), CT_DECORATIONS));
                barrelItems.add(new ItemBlockBarrel(register(r, "wood/barrel/" + wood.getRegistryName().getPath(), new BlockBarrel(), CT_DECORATIONS)));

                looms.add(register(r, "wood/loom/" + wood.getRegistryName().getPath(), new BlockLoom(wood), CT_WOOD));
                supports.add(register(r, "wood/support/" + wood.getRegistryName().getPath(), new BlockSupport(wood), CT_WOOD));
            }

            allLogBlocks = logs.build();
            allLeafBlocks = leaves.build();
            allFenceGateBlocks = fenceGates.build();
            allSaplingBlocks = saplings.build();
            allDoorBlocks = doors.build();
            allTrapDoorWoodBlocks = trapDoors.build();
            allChestBlocks = chests.build();
            allToolRackBlocks = toolRacks.build();
            allLoomBlocks = looms.build();
            allSupportBlocks = supports.build();

            allBarrelItemBlocks = barrelItems.build();

            //logs are special
            allLeafBlocks.forEach(x -> normalItemBlocks.add(new ItemBlockTFC(x)));
            allFenceGateBlocks.forEach(x -> inventoryItemBlocks.add(new ItemBlockTFC(x)));
            allSaplingBlocks.forEach(x -> inventoryItemBlocks.add(new ItemBlockTFC(x)));

            // doors are special
            allTrapDoorWoodBlocks.forEach(x -> inventoryItemBlocks.add(new ItemBlockTFC(x)));
            allChestBlocks.forEach(x -> normalItemBlocks.add(new ItemBlockTFC(x)));
            allToolRackBlocks.forEach(x -> normalItemBlocks.add(new ItemBlockTFC(x)));
            allLoomBlocks.forEach(x -> normalItemBlocks.add(new ItemBlockTFC(x)));
            allSupportBlocks.forEach(x -> normalItemBlocks.add(new ItemBlockTFC(x)));
        }

        {
            Builder<BlockWallTFC> b = ImmutableList.builder();
            Builder<BlockStairsTFC> stairs = new Builder<>();
            Builder<BlockSlabTFC.Half> slab = new Builder<>();

            // Walls
            for (Rock.Type type : new Rock.Type[] {SMOOTH, COBBLE, BRICKS})
                for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                    b.add(register(r, ("wall/" + type.name() + "/" + rock.getRegistryName().getPath()).toLowerCase(), new BlockWallTFC(BlockRockVariant.get(rock, type)), CT_DECORATIONS));
            // Stairs
            for (Rock.Type type : new Rock.Type[] {SMOOTH, COBBLE, BRICKS})
                for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                    stairs.add(register(r, "stairs/" + (type.name() + "/" + rock.getRegistryName().getPath()).toLowerCase(), new BlockStairsTFC(rock, type), CT_DECORATIONS));
            for (Tree wood : TFCRegistries.TREES.getValuesCollection())
                stairs.add(register(r, "stairs/wood/" + wood.getRegistryName().getPath(), new BlockStairsTFC(wood), CT_DECORATIONS));

            // Full slabs are the same as full blocks, they are not saved to a list, they are kept track of by the halfslab version.
            for (Rock.Type type : new Rock.Type[] {SMOOTH, COBBLE, BRICKS})
                for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                    register(r, "double_slab/" + (type.name() + "/" + rock.getRegistryName().getPath()).toLowerCase(), new BlockSlabTFC.Double(rock, type));
            for (Tree wood : TFCRegistries.TREES.getValuesCollection())
                register(r, "double_slab/wood/" + wood.getRegistryName().getPath(), new BlockSlabTFC.Double(wood));

            // Slabs
            for (Rock.Type type : new Rock.Type[] {SMOOTH, COBBLE, BRICKS})
                for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                    slab.add(register(r, "slab/" + (type.name() + "/" + rock.getRegistryName().getPath()).toLowerCase(), new BlockSlabTFC.Half(rock, type), CT_DECORATIONS));
            for (Tree wood : TFCRegistries.TREES.getValuesCollection())
                slab.add(register(r, "slab/wood/" + wood.getRegistryName().getPath(), new BlockSlabTFC.Half(wood), CT_DECORATIONS));

            for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
            {
                // Redstone things
                inventoryItemBlocks.add(new ItemBlockTFC(register(r, "stone/button/" + rock.getRegistryName().getPath().toLowerCase(), new BlockButtonStoneTFC(rock), CT_DECORATIONS)));
                inventoryItemBlocks.add(new ItemBlockTFC(register(r, "stone/pressure_plate/" + rock.getRegistryName().getPath().toLowerCase(), new BlockPressurePlateTFC(rock), CT_DECORATIONS)));
            }

            allWallBlocks = b.build();
            allStairsBlocks = stairs.build();
            allSlabBlocks = slab.build();
            allWallBlocks.forEach(x -> inventoryItemBlocks.add(new ItemBlockTFC(x)));
            allStairsBlocks.forEach(x -> normalItemBlocks.add(new ItemBlockTFC(x)));
        }

        {
            Builder<BlockAnvilTFC> anvils = ImmutableList.builder();
            Builder<BlockMetalSheet> sheets = ImmutableList.builder();
            Builder<BlockMetalLamp> lamps = ImmutableList.builder();
            Builder<BlockTrapDoorMetalTFC> metalTrapdoors = ImmutableList.builder();

            for (Metal metal : TFCRegistries.METALS.getValuesCollection())
            {
                if (Metal.ItemType.ANVIL.hasType(metal))
                    anvils.add(register(r, "anvil/" + metal.getRegistryName().getPath(), new BlockAnvilTFC(metal), CT_METAL));
                if (Metal.ItemType.SHEET.hasType(metal))
                {
                    sheets.add(register(r, "sheet/" + metal.getRegistryName().getPath(), new BlockMetalSheet(metal), CT_METAL));
                    metalTrapdoors.add(register(r, "trapdoor/" + metal.getRegistryName().getPath(), new BlockTrapDoorMetalTFC(metal), CT_METAL));
                }
                if (Metal.ItemType.LAMP.hasType(metal))
                    lamps.add(register(r, "lamp/" + metal.getRegistryName().getPath(), new BlockMetalLamp(metal), CT_METAL));

            }

            allAnvils = anvils.build();
            allSheets = sheets.build();
            allLamps = lamps.build();
            allTrapDoorMetalBlocks = metalTrapdoors.build();
        }

        {
            Builder<BlockCropTFC> b = ImmutableList.builder();

            for (Crop crop : Crop.values())
            {
                b.add(register(r, "crop/" + crop.name().toLowerCase(), crop.createGrowingBlock()));
            }

            allCropBlocks = b.build();
        }

        {
            Builder<BlockCropDead> b = ImmutableList.builder();

            for (Crop crop : Crop.values())
            {
                b.add(register(r, "dead_crop/" + crop.name().toLowerCase(), crop.createDeadBlock()));
            }

            allDeadCropBlocks = b.build();
        }

        {
            Builder<BlockFruitTreeSapling> fSaplings = ImmutableList.builder();
            Builder<BlockFruitTreeTrunk> fTrunks = ImmutableList.builder();
            Builder<BlockFruitTreeBranch> fBranches = ImmutableList.builder();
            Builder<BlockFruitTreeLeaves> fLeaves = ImmutableList.builder();

            for (FruitTree tree : FruitTree.values())
            {
                fSaplings.add(register(r, "fruit_trees/sapling/" + tree.name().toLowerCase(), new BlockFruitTreeSapling(tree), CT_WOOD));
                fTrunks.add(register(r, "fruit_trees/trunk/" + tree.name().toLowerCase(), new BlockFruitTreeTrunk(tree)));
                fBranches.add(register(r, "fruit_trees/branch/" + tree.name().toLowerCase(), new BlockFruitTreeBranch(tree)));
                fLeaves.add(register(r, "fruit_trees/leaves/" + tree.name().toLowerCase(), new BlockFruitTreeLeaves(tree), CT_WOOD));
            }

            allFruitTreeSaplingBlocks = fSaplings.build();
            allFruitTreeTrunkBlocks = fTrunks.build();
            allFruitTreeBranchBlocks = fBranches.build();
            allFruitTreeLeavesBlocks = fLeaves.build();

            Builder<BlockBerryBush> fBerry = ImmutableList.builder();

            for (BerryBush bush : BerryBush.values())
            {
                fBerry.add(register(r, "berry_bush/" + bush.name().toLowerCase(), new BlockBerryBush(bush), CT_FOOD));
            }

            allBerryBushBlocks = fBerry.build();

            //Add ItemBlocks
            allFruitTreeSaplingBlocks.forEach(x -> inventoryItemBlocks.add(new ItemBlockTFC(x)));
            allFruitTreeLeavesBlocks.forEach(x -> inventoryItemBlocks.add(new ItemBlockTFC(x)));
            allBerryBushBlocks.forEach(x -> inventoryItemBlocks.add(new ItemBlockTFC(x)));
        }

        {

            Builder<BlockPlantTFC> b = ImmutableList.builder();
            for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
            {
                if (plant.getPlantType() != Plant.PlantType.SHORT_GRASS && plant.getPlantType() != Plant.PlantType.TALL_GRASS)
                    b.add(register(r, "plants/" + plant.getRegistryName().getPath(), plant.getPlantType().create(plant), CT_FLORA));
            }
            allPlantBlocks = b.build();
            for (BlockPlantTFC blockPlant : allPlantBlocks)
            {
                if (blockPlant instanceof BlockFloatingWaterTFC)
                {
                    inventoryItemBlocks.add(new ItemBlockFloatingWaterTFC((BlockFloatingWaterTFC) blockPlant));
                }
                else
                {
                    normalItemBlocks.add(new ItemBlockTFC(blockPlant));
                }
            }
        }

        {
            Builder<BlockPlantTFC> b = ImmutableList.builder();
            for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
            {
                if (plant.getPlantType() == Plant.PlantType.SHORT_GRASS || plant.getPlantType() == Plant.PlantType.TALL_GRASS)
                    b.add(register(r, "plants/" + plant.getRegistryName().getPath(), plant.getPlantType().create(plant), CT_FLORA));
            }
            allGrassBlocks = b.build();
            for (BlockPlantTFC blockPlant : allGrassBlocks)
            {
                normalItemBlocks.add(new ItemBlockTFC(blockPlant));
            }
        }

        // Registering JEI only blocks (for info)
        inventoryItemBlocks.add(new ItemBlock(register(r, "firepit", new BlockFirePit())));
        inventoryItemBlocks.add(new ItemBlock(register(r, "charcoal_forge", new BlockCharcoalForge())));
        inventoryItemBlocks.add(new ItemBlock(register(r, "pit_kiln", new BlockPitKiln())));
        inventoryItemBlocks.add(new ItemBlock(register(r, "placed_item", new BlockPlacedItem())));
        // technical blocks
        // These have no ItemBlock or Creative Tab
        register(r, "placed_item_flat", new BlockPlacedItemFlat());
        register(r, "placed_hide", new BlockPlacedHide());
        register(r, "charcoal_pile", new BlockCharcoalPile());
        register(r, "ingot_pile", new BlockIngotPile());
        register(r, "log_pile", new BlockLogPile());
        register(r, "molten", new BlockMolten());
        register(r, "bloom", new BlockBloom());
        register(r, "thatch_bed", new BlockThatchBed());

        // Note: if you add blocks you don't need to put them in this list of todos. Feel free to add them where they make sense :)

        // todo: smoke rack (placed with any string, so event based?) + smoke blocks or will we use particles?
        // todo: custom flower pot (TE based probably, unless we want to not care about the dirt in it)

        allNormalItemBlocks = normalItemBlocks.build();
        allInventoryItemBlocks = inventoryItemBlocks.build();

        // Register Tile Entities
        // Putting tile entity registration in the respective block can call it multiple times. Just put here to avoid duplicates

        register(TETickCounter.class, "tick_counter");
        register(TEPlacedItem.class, "placed_item");
        register(TEPlacedItemFlat.class, "placed_item_flat");
        register(TEPlacedHide.class, "placed_hide");
        register(TEPitKiln.class, "pit_kiln");
        register(TEChestTFC.class, "chest");
        register(TENestBox.class, "nest_box");
        register(TELogPile.class, "log_pile");
        register(TEIngotPile.class, "ingot_pile");
        register(TEFirePit.class, "fire_pit");
        register(TEToolRack.class, "tool_rack");
        register(TELoom.class, "loom");
        register(TELamp.class, "lamp");
        register(TEBellows.class, "bellows");
        register(TEBarrel.class, "barrel");
        register(TECharcoalForge.class, "charcoal_forge");
        register(TEAnvilTFC.class, "anvil");
        register(TECrucible.class, "crucible");
        register(TECropBase.class, "crop_base");
        register(TECropSpreading.class, "crop_spreading");
        register(TEBlastFurnace.class, "blast_furnace");
        register(TEBloomery.class, "bloomery");
        register(TEBloom.class, "bloom");
        register(TEMetalSheet.class, "metal_sheet");
        register(TEQuern.class, "quern");
        register(TELargeVessel.class, "large_vessel");
        register(TEPowderKeg.class, "powderkeg");
        register(TESluice.class, "sluice");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerVanillaOverrides(RegistryEvent.Register<Block> event)
    {
        // Vanilla Overrides. Used for small tweaks on vanilla items, rather than replacing them outright
        if (ConfigTFC.General.OVERRIDES.enableFrozenOverrides)
        {
            TerraFirmaCraft.getLog().info("The below warnings about unintended overrides are normal. The override is intended. ;)");
            event.getRegistry().registerAll(
                new BlockIceTFC(FluidsTFC.FRESH_WATER.get()).setRegistryName("minecraft", "ice").setTranslationKey("ice"),
                new BlockSnowTFC().setRegistryName("minecraft", "snow_layer").setTranslationKey("snow")
            );
        }

        if (ConfigTFC.General.OVERRIDES.enableTorchOverride)
        {
            event.getRegistry().register(new BlockTorchTFC().setRegistryName("minecraft", "torch").setTranslationKey("torch"));
        }
    }

    public static boolean isWater(IBlockState current)
    {
        return current.getMaterial() == Material.WATER;
    }

    public static boolean isFreshWater(IBlockState current)
    {
        return current == FluidsTFC.FRESH_WATER.get().getBlock().getDefaultState();
    }

    public static boolean isSaltWater(IBlockState current)
    {
        return current == FluidsTFC.SALT_WATER.get().getBlock().getDefaultState();
    }

    public static boolean isFreshWaterOrIce(IBlockState current)
    {
        return current.getBlock() == Blocks.ICE || isFreshWater(current);
    }

    public static boolean isRawStone(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).getType();
        return type == RAW;
    }

    public static boolean isClay(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).getType();
        return type == CLAY || type == CLAY_GRASS;
    }

    public static boolean isDirt(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).getType();
        return type == DIRT;
    }

    public static boolean isSand(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).getType();
        return type == SAND;
    }

    // todo: change to property of type? (soil & stone maybe?)

    public static boolean isSoil(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeat) return true;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).getType();
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == CLAY || type == CLAY_GRASS;
    }

    public static boolean isGrowableSoil(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeat) return false;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).getType();
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == CLAY || type == CLAY_GRASS;
    }

    public static boolean isSoilOrGravel(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeat) return true;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).getType();
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL;
    }

    public static boolean isGrass(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeatGrass) return true;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).getType();
        return type.isGrass;
    }

    public static boolean isDryGrass(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).getType();
        return type == DRY_GRASS;
    }

    public static boolean isGround(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).getType();
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL || type == RAW || type == SAND;
    }

    private static <T extends Block> T register(IForgeRegistry<Block> r, String name, T block, CreativeTabs ct)
    {
        block.setCreativeTab(ct);
        return register(r, name, block);
    }

    private static <T extends Block> T register(IForgeRegistry<Block> r, String name, T block)
    {
        block.setRegistryName(MOD_ID, name);
        block.setTranslationKey(MOD_ID + "." + name.replace('/', '.'));
        r.register(block);
        return block;
    }

    private static <T extends TileEntity> void register(Class<T> te, String name)
    {
        TileEntity.register(MOD_ID + ":" + name, te);
    }
}
