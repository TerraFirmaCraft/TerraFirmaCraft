/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.EnumMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Util;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.soil.SandBlockType;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.objects.blocks.soil.TFCSandBlock;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.objects.TFCItemGroup.ROCK_BLOCKS;
import static net.dries007.tfc.objects.items.TFCItems.ITEMS;


public final class TFCBlocks
{
    /*
    @ObjectHolder("ceramics/fired/large_vessel")
    public static final BlockLargeVessel FIRED_LARGE_VESSEL = getNull();

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
    */

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MOD_ID);

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<Block>>> ROCKS = Util.make(new EnumMap<>(Rock.Default.class), map -> {
        for (Rock.Default rock : Rock.Default.values())
        {
            Map<Rock.BlockType, RegistryObject<Block>> inner = new EnumMap<>(Rock.BlockType.class);
            for (Rock.BlockType type : Rock.BlockType.values())
            {
                String name = ("rock/" + type.name() + "/" + rock.name()).toLowerCase();
                RegistryObject<Block> block = BLOCKS.register(name, () -> type.create(rock));
                ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(ROCK_BLOCKS)));
                inner.put(type, block);
            }
            map.put(rock, inner);
        }
    });

    public static final Map<SandBlockType, RegistryObject<Block>> SAND = Util.make(new EnumMap<>(SandBlockType.class), map -> {
        for (SandBlockType type : SandBlockType.values())
        {
            String name = ("sand/" + type.name()).toLowerCase();
            RegistryObject<Block> block = BLOCKS.register(name, () -> new TFCSandBlock(type.getDustColor(), Block.Properties.create(Material.SAND, MaterialColor.ADOBE).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
            ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(ROCK_BLOCKS)));
            map.put(type, block);
        }
    });

    public static final Map<SoilBlockType, Map<SoilBlockType.Variant, RegistryObject<Block>>> SOIL = Util.make(new EnumMap<>(SoilBlockType.class), map -> {
        for (SoilBlockType type : SoilBlockType.values())
        {
            Map<SoilBlockType.Variant, RegistryObject<Block>> inner = new EnumMap<>(SoilBlockType.Variant.class);
            for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
            {
                String name = (type.name() + "/" + variant.name()).toLowerCase();
                RegistryObject<Block> block = BLOCKS.register(name, type::create);
                ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(ROCK_BLOCKS)));
                inner.put(variant, block);
            }
            map.put(type, inner);
        }
    });

    private static final Logger LOGGER = LogManager.getLogger();

    /*
    // All these are for use in model registration. Do not use for block lookups.
    // Use the static get methods in the classes instead.
    private static ImmutableList<ItemBlock> allNormalItemBlocks;
    private static ImmutableList<ItemBlock> allInventoryItemBlocks;
    private static ImmutableList<ItemBlockBarrel> allBarrelItemBlocks;

    private static ImmutableList<BlockFluidBase> allFluidBlocks;
    private static ImmutableList<RockVariantBlock> allBlockRockVariants;
    private static ImmutableList<BlockOreTFC> allOreBlocks;
    private static ImmutableList<BlockWallTFC> allWallBlocks;
    private static ImmutableList<BlockLogTFC> allLogBlocks;
    private static ImmutableList<BlockLeavesTFC> allLeafBlocks;
    private static ImmutableList<BlockFenceGateTFC> allFenceGateBlocks;
    private static ImmutableList<BlockSaplingTFC> allSaplingBlocks;
    private static ImmutableList<BlockDoorTFC> allDoorBlocks;
    private static ImmutableList<BlockTrapDoorWoodTFC> allTrapDoorWoodBlocks;
    private static ImmutableList<BlockStairsTFC> allStairsBlocks;
    private static ImmutableList<BlockSlabTFC.Half> allSlabBlocks;
    private static ImmutableList<BlockChestTFC> allChestBlocks;
    private static ImmutableList<BlockAnvilTFC> allAnvils;
    private static ImmutableList<BlockMetalSheet> allSheets;
    private static ImmutableList<BlockToolRack> allToolRackBlocks;
    private static ImmutableList<BlockCropTFC> allCropBlocks;
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

    public static ImmutableList<RockVariantBlock> getAllBlockRockVariants()
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

    public static ImmutableList<BlockToolRack> getAllToolRackBlocks()
    {
        return allToolRackBlocks;
    }

    public static ImmutableList<BlockCropTFC> getAllCropBlocks()
    {
        return allCropBlocks;
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
    */

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        LOGGER.info("Registering Blocks");

        // This is called here because it needs to wait until Metal registry has fired
        //FluidsTFC.registerFluids();

        IForgeRegistry<Block> r = event.getRegistry();

        //Builder<BlockItem> normalItemBlocks = ImmutableList.builder();
        //Builder<BlockItem> inventoryItemBlocks = ImmutableList.builder();

        //normalItemBlocks.add(new ItemBlockTFC(register(r, "quern", new BlockQuern(), CT_MISC)));
        //normalItemBlocks.add(new ItemBlockTFC(register(r, "crucible", new BlockCrucible(), CT_MISC)));
        //normalItemBlocks.add(new ItemBlockTFC(register(r, "blast_furnace", new BlockBlastFurnace(), CT_MISC)));
        //inventoryItemBlocks.add(new ItemBlockTFC(register(r, "bellows", new BlockBellows(), CT_MISC)));
        //inventoryItemBlocks.add(new ItemBlockTFC(register(r, "bloomery", new BlockBloomery(), CT_MISC)));
        //inventoryItemBlocks.add(new ItemBlockTFC(register(r, "nest_box", new BlockNestBox(), CT_MISC)));
        //inventoryItemBlocks.add(new ItemBlockSluice(register(r, "sluice", new BlockSluice(), CT_MISC)));
//
        //normalItemBlocks.add(new ItemBlockTFC(register(r, "sea_ice", new BlockIceTFC(FluidsTFC.SALT_WATER.get()), CT_MISC)));
//
        //normalItemBlocks.add(new ItemBlockLargeVessel(register(r, "ceramics/fired/large_vessel", new BlockLargeVessel(), CT_POTTERY)));

        /*
        {
            Builder<BlockFluidBase> b = ImmutableList.builder();
            // We always want to register our water variants, as they absolutely need special subclasses
            b.add(
                register(r, "fluid/hot_water", new BlockFluidHotWater()),
                register(r, "fluid/fresh_water", new BlockFluidWater(FluidsTFC.FRESH_WATER.get(), Material.WATER, false)),
                register(r, "fluid/salt_water", new BlockFluidWater(FluidsTFC.SALT_WATER.get(), Material.WATER, true))
            );
            for (FluidWrapper wrapper : FluidsTFC.getAllAlcoholsFluids())
            {
                if (wrapper.isDefault())
                {
                    b.add(register(r, "fluid/" + wrapper.get().getName(), new BlockFluidTFC(wrapper.get(), Material.WATER)));
                }
            }
            for (FluidWrapper wrapper : FluidsTFC.getAllOtherFiniteFluids())
            {
                if (wrapper.isDefault())
                {
                    b.add(register(r, "fluid/" + wrapper.get().getName(), new BlockFluidTFC(wrapper.get(), Material.WATER)));
                }
            }
            for (FluidWrapper wrapper : FluidsTFC.getAllMetalFluids())
            {
                if (wrapper.isDefault())
                {
                    b.add(register(r, "fluid/" + wrapper.get().getName(), new BlockFluidTFC(wrapper.get(), Material.LAVA)));
                }
            }
            allFluidBlocks = b.build();
        }
        */


        {
            //for (Rock.BlockType type : Rock.BlockType.values())
            //{
            //    for (Rock.Default rock : Rock.Default.values())
            //    {
            //        register(r, ROCKS.add(type.create(rock)), ("rock/" + type.name() + "/" + rock.name()).toLowerCase());
            //    }
            //}
            //allBlockRockVariants.forEach(x ->
            //{
            //    if (x.getType() == Rock.Type.SAND)
            //        normalItemBlocks.add(new ItemBlockHeat(x, 1, 600));
            //    else
            //        normalItemBlocks.add(new ItemBlockTFC(x));
            //});

            // Dirt

        }


        /*
        {
            Builder<BlockOreTFC> b = ImmutableList.builder();
            for (Ore ore : TFCRegistries.ORES.getValuesCollection())
                for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                    b.add(register(r, ("ore/" + ore.getRegistryName().getPath() + "/" + rock.getRegistryName().getPath()).toLowerCase(), new BlockOreTFC(ore, rock), CT_ROCK_BLOCKS));
            allOreBlocks = b.build();
            allOreBlocks.forEach(x -> normalItemBlocks.add(new ItemBlockTFC(x)));
        }
        */

        /*
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
                chests.add(register(r, "wood/chest/" + wood.getRegistryName().getPath(), new BlockChestTFC(BlockChest.Type.BASIC, wood), CT_DECORATIONS));
                chests.add(register(r, "wood/chest_trap/" + wood.getRegistryName().getPath(), new BlockChestTFC(BlockChest.Type.TRAP, wood), CT_DECORATIONS));

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
        */

        /*
        {
            Builder<BlockWallTFC> b = ImmutableList.builder();
            Builder<BlockStairsTFC> stairs = new Builder<>();
            Builder<BlockSlabTFC.Half> slab = new Builder<>();

            // Walls
            for (Rock.Type type : new Rock.Type[] {SMOOTH, COBBLE, BRICKS})
                for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                    b.add(register(r, ("wall/" + type.name() + "/" + rock.getRegistryName().getPath()).toLowerCase(), new BlockWallTFC(RockVariantBlock.get(rock, type)), CT_DECORATIONS));
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
                inventoryItemBlocks.add(new ItemBlockTFC(register(r, "rock/button/" + rock.getRegistryName().getPath().toLowerCase(), new BlockButtonStoneTFC(rock), CT_DECORATIONS)));

            // Anvils are special because they don't have an ItemBlock + they only exist for certian types
            for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                if (rock.getRockCategory().hasAnvil())
                    register(r, "anvil/" + rock.getRegistryName().getPath(), new BlockStoneAnvil(rock));

            allWallBlocks = b.build();
            allStairsBlocks = stairs.build();
            allSlabBlocks = slab.build();
            allWallBlocks.forEach(x -> inventoryItemBlocks.add(new ItemBlockTFC(x)));
            allStairsBlocks.forEach(x -> normalItemBlocks.add(new ItemBlockTFC(x)));
            // slabs are special. (ItemSlabTFC)
        }
        */

        /*
        {
            Builder<BlockAnvilTFC> anvils = ImmutableList.builder();
            Builder<BlockMetalSheet> sheets = ImmutableList.builder();

            for (Metal metal : TFCRegistries.METALS.getValuesCollection())
            {
                if (Metal.ItemType.ANVIL.hasType(metal))
                    anvils.add(register(r, "anvil/" + metal.getRegistryName().getPath(), new BlockAnvilTFC(metal), CT_METAL));
                if (Metal.ItemType.SHEET.hasType(metal))
                    sheets.add(register(r, "sheet/" + metal.getRegistryName().getPath(), new BlockMetalSheet(metal), CT_METAL));
            }

            allAnvils = anvils.build();
            allSheets = sheets.build();
        }

        {
            Builder<BlockCropTFC> b = ImmutableList.builder();

            for (Crop crop : Crop.values())
            {
                b.add(register(r, "crop/" + crop.name().toLowerCase(), crop.create()));
            }

            allCropBlocks = b.build();
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

        // technical blocks
        // These have no ItemBlock or Creative Tab
        register(r, "firepit", new BlockFirePit());
        register(r, "charcoal_forge", new BlockCharcoalForge());
        register(r, "placed_item_flat", new BlockPlacedItemFlat());
        register(r, "placed_item", new BlockPlacedItem());
        register(r, "placed_hide", new BlockPlacedHide());
        register(r, "charcoal_pile", new BlockCharcoalPile());
        register(r, "ingot_pile", new BlockIngotPile());
        register(r, "log_pile", new BlockLogPile());
        register(r, "pit_kiln", new BlockPitKiln());
        register(r, "molten", new BlockMolten());
        register(r, "bloom", new BlockBloom());
        register(r, "thatch_bed", new BlockThatchBed());

        // Note: if you add blocks you don't need to put them in this list of todos. Feel free to add them where they make sense :)

        // todo: metal lamps (on/off with states)
        // todo: sluice
        // todo: grill
        // todo: metal trap doors
        // todo: smoke rack (placed with any string, so event based?) + smoke blocks or will we use particles?
        // todo: custom flower pot (TE based probably, unless we want to not care about the dirt in it)
        // todo: custom hopper or just a separate press block? I prefer the separate block, this will simplify things a lot.

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
        register(TEBellows.class, "bellows");
        register(TEBarrel.class, "barrel");
        register(TECharcoalForge.class, "charcoal_forge");
        register(TEAnvilTFC.class, "anvil");
        register(TECrucible.class, "crucible");
        register(TECropSpreading.class, "crop_spreading");
        register(TEBlastFurnace.class, "blast_furnace");
        register(TEBloomery.class, "bloomery");
        register(TEBloom.class, "bloom");
        register(TEMetalSheet.class, "metal_sheet");
        register(TEQuern.class, "quern");
        register(TELargeVessel.class, "large_vessel");
        register(TESluice.class, "sluice");
        */
    }

    //@SubscribeEvent(priority = EventPriority.LOWEST)
    //public static void registerVanillaOverrides(RegistryEvent.Register<Block> event)
    //{
    //    // Vanilla Overrides. Used for small tweaks on vanilla items, rather than replacing them outright
    //    TerraFirmaCraft.getLog().info("The below warnings about unintended overrides are normal. The override is intended. ;)");
    //    //event.getRegistry().registerAll(
    //    //    new BlockIceTFC(FluidsTFC.FRESH_WATER.get()).setRegistryName("minecraft", "ice").setTranslationKey("ice"),
    //    //    new BlockSnowTFC().setRegistryName("minecraft", "snow_layer").setTranslationKey("snow"),
    //    //    new BlockTorchTFC().setRegistryName("minecraft", "torch").setTranslationKey("torch")
    //    //);
    //}

    //public static boolean isWater(BlockState current)
    //{
    //    return current.getMaterial() == Material.WATER;
    //}

    //public static boolean isFreshWater(BlockState current)
    //{
    //    return current == FluidsTFC.FRESH_WATER.get().getBlock().getDefaultState();
    //}

    //public static boolean isSaltWater(BlockState current)
    //{
    //    return current == FluidsTFC.SALT_WATER.get().getBlock().getDefaultState();
    //}

    //public static boolean isFreshWaterOrIce(BlockState current)
    //{
    //    return current.getBlock() == Blocks.ICE || isFreshWater(current);
    //}

    //public static boolean isRawStone(BlockState current)
    //{
    //    if (!(current.getBlock() instanceof RockVariantBlock)) return false;
    //    Rock.Type type = ((RockVariantBlock) current.getBlock()).getType();
    //    return type == RAW;
    //}
//
    //public static boolean isClay(IBlockState current)
    //{
    //    if (!(current.getBlock() instanceof RockVariantBlock)) return false;
    //    Rock.Type type = ((RockVariantBlock) current.getBlock()).getType();
    //    return type == CLAY || type == CLAY_GRASS;
    //}
//
    //public static boolean isDirt(IBlockState current)
    //{
    //    if (!(current.getBlock() instanceof RockVariantBlock)) return false;
    //    Rock.Type type = ((RockVariantBlock) current.getBlock()).getType();
    //    return type == DIRT;
    //}
//
    //public static boolean isSand(IBlockState current)
    //{
    //    if (!(current.getBlock() instanceof RockVariantBlock)) return false;
    //    Rock.Type type = ((RockVariantBlock) current.getBlock()).getType();
    //    return type == SAND;
    //}
//
    //// todo: change to property of type? (soil & rock maybe?)
//
    //public static boolean isSoil(IBlockState current)
    //{
    //    if (current.getBlock() instanceof BlockPeat) return true;
    //    if (!(current.getBlock() instanceof RockVariantBlock)) return false;
    //    Rock.Type type = ((RockVariantBlock) current.getBlock()).getType();
    //    return type == GRASS || type == DRY_GRASS || type == DIRT || type == CLAY || type == CLAY_GRASS;
    //}
//
    //public static boolean isSoilOrGravel(IBlockState current)
    //{
    //    if (current.getBlock() instanceof BlockPeat) return true;
    //    if (!(current.getBlock() instanceof RockVariantBlock)) return false;
    //    Rock.Type type = ((RockVariantBlock) current.getBlock()).getType();
    //    return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL;
    //}
//
    //public static boolean isGrass(IBlockState current)
    //{
    //    if (current.getBlock() instanceof BlockPeatGrass) return true;
    //    if (!(current.getBlock() instanceof RockVariantBlock)) return false;
    //    Rock.Type type = ((RockVariantBlock) current.getBlock()).getType();
    //    return type.isGrass;
    //}
//
    //public static boolean isDryGrass(IBlockState current)
    //{
    //    if (!(current.getBlock() instanceof RockVariantBlock)) return false;
    //    Rock.Type type = ((RockVariantBlock) current.getBlock()).getType();
    //    return type == DRY_GRASS;
    //}
//
    //public static boolean isGround(IBlockState current)
    //{
    //    if (!(current.getBlock() instanceof RockVariantBlock)) return false;
    //    Rock.Type type = ((RockVariantBlock) current.getBlock()).getType();
    //    return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL || type == RAW || type == SAND;
    //}

    //private static <B extends Block> B register(IForgeRegistry<Block> registry, B block, String name)
    //{
    //    block.setRegistryName(Helpers.identifier(name));
    //    registry.register(block);
    //    return block;
    //}
}
