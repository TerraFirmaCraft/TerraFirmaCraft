/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableListMultimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.blocks.metal.BlockAnvilTFC;
import net.dries007.tfc.objects.blocks.metal.BlockIngotPile;
import net.dries007.tfc.objects.blocks.metal.BlockSheet;
import net.dries007.tfc.objects.blocks.stone.BlockButtonStoneTFC;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.blocks.stone.BlockWallTFC;
import net.dries007.tfc.objects.blocks.wood.*;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.items.ItemBlockTFC;
import net.dries007.tfc.objects.items.ItemBlockTorchTFC;
import net.dries007.tfc.objects.te.*;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.api.types.Rock.Type.*;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class BlocksTFC
{
    @GameRegistry.ObjectHolder("fluid/salt_water")
    public static final BlockFluidBase FLUID_SALT_WATER = null;
    @GameRegistry.ObjectHolder("fluid/fresh_water")
    public static final BlockFluidBase FLUID_FRESH_WATER = null;
    @GameRegistry.ObjectHolder("fluid/hot_water")
    public static final BlockFluidBase FLUID_HOT_WATER = null;
    @GameRegistry.ObjectHolder("fluid/finite_salt_water")
    public static final BlockFluidBase FLUID_FINITE_SALT_WATER = null;
    @GameRegistry.ObjectHolder("fluid/finite_fresh_water")
    public static final BlockFluidBase FLUID_FINITE_FRESH_WATER = null;
    @GameRegistry.ObjectHolder("fluid/finite_hot_water")
    public static final BlockFluidBase FLUID_FINITE_HOT_WATER = null;
    @GameRegistry.ObjectHolder("fluid/rum")
    public static final BlockFluidBase FLUID_RUM = null;
    @GameRegistry.ObjectHolder("fluid/beer")
    public static final BlockFluidBase FLUID_BEER = null;
    @GameRegistry.ObjectHolder("fluid/whiskey")
    public static final BlockFluidBase FLUID_WHISKEY = null;
    @GameRegistry.ObjectHolder("fluid/rye_whiskey")
    public static final BlockFluidBase FLUID_RYE_WHISKEY = null;
    @GameRegistry.ObjectHolder("fluid/corn_whiskey")
    public static final BlockFluidBase FLUID_CORN_WHISKEY = null;
    @GameRegistry.ObjectHolder("fluid/sake")
    public static final BlockFluidBase FLUID_SAKE = null;
    @GameRegistry.ObjectHolder("fluid/vodka")
    public static final BlockFluidBase FLUID_VODKA = null;
    @GameRegistry.ObjectHolder("fluid/cider")
    public static final BlockFluidBase FLUID_CIDER = null;
    @GameRegistry.ObjectHolder("fluid/vinegar")
    public static final BlockFluidBase FLUID_VINEGAR = null;
    @GameRegistry.ObjectHolder("fluid/brine")
    public static final BlockFluidBase FLUID_BRINE = null;
    @GameRegistry.ObjectHolder("fluid/milk")
    public static final BlockFluidBase FLUID_MILK = null;
    @GameRegistry.ObjectHolder("fluid/olive_oil")
    public static final BlockFluidBase FLUID_OLIVE_OIL = null;
    @GameRegistry.ObjectHolder("fluid/tannin")
    public static final BlockFluidBase FLUID_TANNIN = null;
    @GameRegistry.ObjectHolder("fluid/limewater")
    public static final BlockFluidBase FLUID_LIMEWATER = null;
    @GameRegistry.ObjectHolder("fluid/milk_curdled")
    public static final BlockFluidBase FLUID_MILK_CURDLED = null;
    @GameRegistry.ObjectHolder("fluid/milk_vinegar")
    public static final BlockFluidBase FLUID_MILK_VINEGAR = null;

    public static final BlockDebug DEBUG = null;
    public static final BlockPeat PEAT = null;
    public static final BlockPeat PEAT_GRASS = null;
    public static final BlockFirePit FIREPIT = null;
    public static final BlockThatch THATCH = null;
    public static final BlockPitKiln PIT_KILN = null;
    public static final BlockWorldItem WORLD_ITEM = null;
    public static final BlockCharcoalPile CHARCOAL_PILE = null;
    public static final BlockLogPile LOG_PILE = null;
    public static final BlockIngotPile INGOT_PILE = null;

    // All these are for use in model registration. Do not use for block lookups.
    // Use the static get methods in the classes instead.
    private static ImmutableListMultimap<Block, Class<? extends ItemBlock>> allNormalItemBlocks;
    private static ImmutableListMultimap<Block, Class<? extends ItemBlock>> allInventoryItemBlocks;
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
    private static ImmutableList<BlockStairsTFC> allStairsBlocks;
    private static ImmutableList<BlockSlabTFC.Half> allSlabBlocks;
    private static ImmutableList<BlockChestTFC> allChestBlocks;
    private static ImmutableList<BlockAnvilTFC> allAnvils;
    private static ImmutableList<BlockSheet> allSheets;

    public static ImmutableListMultimap<Block, Class<? extends ItemBlock>> getAllNormalItemBlocks()
    {
        return allNormalItemBlocks;
    }

    public static ImmutableListMultimap<Block, Class<? extends ItemBlock>> getAllInventoryItemBlocks()
    {
        return allInventoryItemBlocks;
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

    public static ImmutableList<BlockSheet> getAllSheets()
    {
        return allSheets;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> r = event.getRegistry();

        ImmutableListMultimap.Builder<Block, Class<? extends ItemBlock>> normalItemBlocks = ImmutableListMultimap.builder();
        ImmutableListMultimap.Builder<Block, Class<? extends ItemBlock>> inventoryItemBlocks = ImmutableListMultimap.builder();

        normalItemBlocks.put(register(r, "debug", new BlockDebug(), CT_MISC), ItemBlock.class);

        normalItemBlocks.put(register(r, "peat", new BlockPeat(Material.GROUND), CT_ROCK_BLOCKS), ItemBlock.class);
        normalItemBlocks.put(register(r, "peat_grass", new BlockPeatGrass(Material.GRASS), CT_ROCK_BLOCKS), ItemBlock.class);

        normalItemBlocks.put(register(r, "thatch", new BlockThatch(Material.PLANTS), CT_DECORATIONS), ItemBlock.class);

        register(r, "firepit", new BlockFirePit()); // No item or creative tab.

        {
            TerraFirmaCraft.getLog().info("The fluid warnings ('A mod has attempted to assign Block...') below this line are normal.");
            Builder<BlockFluidBase> b = ImmutableList.builder();
            for (Fluid fluid : FluidsTFC.getAllInfiniteFluids())
                registerFluid(b, r, fluid, Material.WATER);
            for (Fluid fluid : FluidsTFC.getAllAlcoholsFluids())
                b.add(register(r, "fluid/" + fluid.getName(), new BlockFluidFiniteTFC(fluid, FluidsTFC.MATERIAL_ALCOHOL)));
            for (Fluid fluid : FluidsTFC.getAllOtherFiniteFluids())
                b.add(register(r, "fluid/" + fluid.getName(), new BlockFluidFiniteTFC(fluid, Material.WATER)));
            allFluidBlocks = b.build();
        }

        {
            Builder<BlockRockVariant> b = ImmutableList.builder();
            for (Rock.Type type : Rock.Type.values())
                for (Rock rock : Rock.values())
                    b.add(register(r, (type.name() + "/" + rock.name()).toLowerCase(), BlockRockVariant.create(rock, type), CT_ROCK_BLOCKS));
            allBlockRockVariants = b.build();
            allBlockRockVariants.forEach(x -> normalItemBlocks.put(x, ItemBlockTFC.class));
        }

        {
            Builder<BlockOreTFC> b = ImmutableList.builder();
            for (Ore ore : Ore.values())
                for (Rock rock : Rock.values())
                    b.add(register(r, ("ore/" + ore.name() + "/" + rock.name()).toLowerCase(), new BlockOreTFC(ore, rock), CT_ROCK_BLOCKS));
            allOreBlocks = b.build();
            allOreBlocks.forEach(x -> normalItemBlocks.put(x, ItemBlockTFC.class));
        }

        {
            Builder<BlockLogTFC> logs = ImmutableList.builder();
            Builder<BlockLeavesTFC> leaves = ImmutableList.builder();
            Builder<BlockFenceGateTFC> fenceGates = ImmutableList.builder();
            Builder<BlockSaplingTFC> saplings = ImmutableList.builder();
            Builder<BlockDoorTFC> doors = ImmutableList.builder();
            Builder<BlockTrapDoorWoodTFC> trapdoorwood = ImmutableList.builder();
            Builder<BlockChestTFC> chests = ImmutableList.builder();

            for (Tree wood : Tree.values())
            {
                logs.add(register(r, "wood/log/" + wood.name(), new BlockLogTFC(wood), CT_WOOD));
                leaves.add(register(r, "wood/leaves/" + wood.name(), new BlockLeavesTFC(wood), CT_WOOD));
                normalItemBlocks.put(register(r, "wood/planks/" + wood.name(), new BlockPlanksTFC(wood), CT_WOOD), ItemBlockTFC.class);
                normalItemBlocks.put(register(r, "wood/bookshelf/" + wood.name(), new BlockBookshelfTFC(wood), CT_DECORATIONS), ItemBlockTFC.class);
                normalItemBlocks.put(register(r, "wood/workbench/" + wood.name(), new BlockWorkbenchTFC(wood), CT_DECORATIONS), ItemBlockTFC.class);
                inventoryItemBlocks.put(register(r, "wood/fence/" + wood.name(), new BlockFenceTFC(wood), CT_DECORATIONS), ItemBlockTFC.class);
                fenceGates.add(register(r, "wood/fence_gate/" + wood.name(), new BlockFenceGateTFC(wood), CT_DECORATIONS));
                saplings.add(register(r, "wood/sapling/" + wood.name(), new BlockSaplingTFC(wood), CT_WOOD));
                doors.add(register(r, "wood/door/" + wood.name(), new BlockDoorTFC(wood), CT_DECORATIONS));
                trapdoorwood.add(register(r, "wood/trapdoor/" + wood.name(), new BlockTrapDoorWoodTFC(wood), CT_DECORATIONS));
                chests.add(register(r, "wood/chest/" + wood.name(), new BlockChestTFC(BlockChest.Type.BASIC, wood), CT_DECORATIONS));
                chests.add(register(r, "wood/chest_trap/" + wood.name(), new BlockChestTFC(BlockChest.Type.TRAP, wood), CT_DECORATIONS));
                inventoryItemBlocks.put(register(r, "wood/button/" + wood.name(), new BlockButtonWoodTFC(wood), CT_DECORATIONS), ItemBlockTFC.class);
            }
            allLogBlocks = logs.build();
            allLeafBlocks = leaves.build();
            allFenceGateBlocks = fenceGates.build();
            allSaplingBlocks = saplings.build();
            allDoorBlocks = doors.build();
            allTrapDoorWoodBlocks = trapdoorwood.build();
            allChestBlocks = chests.build();

            //logs are special
            allLeafBlocks.forEach(x -> normalItemBlocks.put(x, ItemBlockTFC.class));
            allFenceGateBlocks.forEach(x -> inventoryItemBlocks.put(x, ItemBlockTFC.class));
            allSaplingBlocks.forEach(x -> inventoryItemBlocks.put(x, ItemBlockTFC.class));

            // doors are special
            allTrapDoorWoodBlocks.forEach(x -> inventoryItemBlocks.put(x, ItemBlockTFC.class));
            allChestBlocks.forEach(x -> normalItemBlocks.put(x, ItemBlockTFC.class));
        }

        {
            Builder<BlockWallTFC> b = ImmutableList.builder();
            Builder<BlockStairsTFC> stairs = new Builder<>();
            Builder<BlockSlabTFC.Half> slab = new Builder<>();

            // Walls
            for (Rock.Type type : new Rock.Type[] {COBBLE, BRICKS})
                for (Rock rock : Rock.values())
                    b.add(register(r, ("wall/" + type.name() + "/" + rock.name()).toLowerCase(), new BlockWallTFC(BlockRockVariant.get(rock, type)), CT_DECORATIONS));
            // Stairs
            for (Rock.Type type : new Rock.Type[] {SMOOTH, COBBLE, BRICKS})
                for (Rock rock : Rock.values())
                    stairs.add(register(r, "stairs/" + (type.name() + "/" + rock.name()).toLowerCase(), new BlockStairsTFC(rock, type), CT_DECORATIONS));
            for (Tree wood : Tree.values())
                stairs.add(register(r, "stairs/wood/" + wood.name(), new BlockStairsTFC(wood), CT_DECORATIONS));

            // Full slabs are the same as full blocks, they are not saved to a list, they are kept track of by the halfslab version.
            for (Rock.Type type : new Rock.Type[] {SMOOTH, COBBLE, BRICKS})
                for (Rock rock : Rock.values())
                    register(r, "slab/full/" + (type.name() + "/" + rock.name()).toLowerCase(), new BlockSlabTFC.Double(rock, type));
            for (Tree wood : Tree.values())
                register(r, "slab/full/wood/" + wood.name(), new BlockSlabTFC.Double(wood));

            // Slabs
            for (Rock.Type type : new Rock.Type[] {SMOOTH, COBBLE, BRICKS})
                for (Rock rock : Rock.values())
                    slab.add(register(r, "slab/half/" + (type.name() + "/" + rock.name()).toLowerCase(), new BlockSlabTFC.Half(rock, type), CT_DECORATIONS));
            for (Tree wood : Tree.values())
                slab.add(register(r, "slab/half/wood/" + wood.name(), new BlockSlabTFC.Half(wood), CT_DECORATIONS));

            for (Rock rock : Rock.values())
                inventoryItemBlocks.put(register(r, "stone/button/" + rock.name().toLowerCase(), new BlockButtonStoneTFC(rock), CT_DECORATIONS), ItemBlockTFC.class);

            allWallBlocks = b.build();
            allStairsBlocks = stairs.build();
            allSlabBlocks = slab.build();
            allWallBlocks.forEach(x -> inventoryItemBlocks.put(x, ItemBlockTFC.class));
            allStairsBlocks.forEach(x -> normalItemBlocks.put(x, ItemBlockTFC.class));
            // slabs are special. (ItemSlabTFC)
        }

        {
            Builder<BlockAnvilTFC> anvils = ImmutableList.builder();
            Builder<BlockSheet> sheets = ImmutableList.builder();

            for (Metal metal : Metal.values())
            {
                // Anvils
                if (metal.hasType(Metal.ItemType.ANVIL))
                    anvils.add(register(r, "anvil/" + metal.name().toLowerCase(), new BlockAnvilTFC(metal), CT_METAL));

                // Sheets
                if (metal.hasType(Metal.ItemType.SHEET))
                    sheets.add(register(r, "sheet/" + metal.name().toLowerCase(), new BlockSheet(metal), CT_METAL));
            }

            allAnvils = anvils.build();
            allSheets = sheets.build();
        }

        inventoryItemBlocks.put(register(r, "torch", new BlockTorchTFC(), CT_MISC), ItemBlockTorchTFC.class);

        // technical blocks
        register(r, "pit_kiln", new BlockPitKiln());

        // todo: cactus ?
        // todo: reeds/sugarcane ?
        // todo: pumpkin/melon ?
        // todo: waterplants
        // todo: varied lilypads?
        // todo: plants
        // todo: flowers
        // todo: moss? (It's unused in tfc1710, but it's like a retextured vine that spawns on trees, might be nice to have)
        // todo: fruit tree stuff (leaves, saplings, logs)

        // todo: supports (h & v)
        // todo: farmland
        // todo: barrels
        // todo: tool racks
        // todo: wood trap doors

        // todo: metal lamps (on/off with states)
        // todo: sluice
        // todo: quern
        // todo: loom
        // todo: bellows
        // todo: forge
        // todo: anvils (items exist already)
        // todo: bloomery
        // todo: bloom/molten blocks
        // todo: crusible
        // todo: large vessels
        // todo: nestbox
        // todo: leather rack
        // todo: grill
        // todo: metal trap doors
        // todo: smoke rack (placed with any string, so event based?) + smoke blocks or will we use particles?
        // todo: custom flower pot (TE based probably, unless we want to not care about the dirt in it)

        // todo: custom hopper or just a separate press block? I prefer the separate block, this will simplify things a lot.

        // todo: placable items: pottery, metal sheets, (anvils are special because TE), tools?
        register(r, "world_item", new BlockWorldItem());
        // todo: pitkiln (maybe not a seperate block but rather a variation on the TE from any placeable item)
        register(r, "charcoal_pile", new BlockCharcoalPile());
        // todo: ingot pile
        register(r, "ingot_pile", new BlockIngotPile());
        register(r, "log_pile", new BlockLogPile());

        allNormalItemBlocks = normalItemBlocks.build();
        allInventoryItemBlocks = inventoryItemBlocks.build();

        // Register Tile Entities
        // Putting tile entity registration in the respective block calls it multiple times. Just put here to avoid duplicates
        TileEntity.register(TESaplingTFC.ID.toString(), TESaplingTFC.class);
        TileEntity.register(TEChestTFC.ID.toString(), TEChestTFC.class);
        TileEntity.register(TEWorldItem.ID.toString(), TEWorldItem.class);
        TileEntity.register(TETorchTFC.ID.toString(), TETorchTFC.class);
        TileEntity.register(TEPitKiln.ID.toString(), TEPitKiln.class);
        TileEntity.register(TELogPile.ID.toString(), TELogPile.class);
        TileEntity.register(TEIngotPile.ID.toString(), TEIngotPile.class);

    }

    public static boolean isWater(IBlockState current)
    {
        return current.getMaterial() == Material.WATER;
    }

    public static boolean isRawStone(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == RAW;
    }

    public static boolean isClay(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == CLAY || type == CLAY_GRASS;
    }

    public static boolean isDirt(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == DIRT;
    }

    public static boolean isSand(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == SAND;
    }

    // todo: change to property of type? (soil & stone maybe?)

    public static boolean isSoil(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeat) return true;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == CLAY || type == CLAY_GRASS;
    }

    public static boolean isSoilOrGravel(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeat) return true;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL;
    }

    public static boolean isGrass(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeatGrass) return true;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type.isGrass;
    }

    public static boolean isGround(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Rock.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL || type == RAW || type == SAND;
    }

    private static void registerFluid(Builder<BlockFluidBase> b, IForgeRegistry<Block> r, Fluid fluid, Material material)
    {
        BlockFluidBase block = new BlockFluidClassicTFC(fluid, material);
        register(r, "fluid/" + fluid.getName(), block);
        b.add(block);
        block = new BlockFluidFiniteTFC(fluid, material);
        register(r, "fluid/finite_" + fluid.getName(), block);
        b.add(block);
    }

    private static <T extends Block> T register(IForgeRegistry<Block> r, String name, T block, CreativeTabs ct)
    {
        block.setCreativeTab(ct);
        return register(r, name, block);
    }

    private static <T extends Block> T register(IForgeRegistry<Block> r, String name, T block)
    {
        block.setRegistryName(MOD_ID, name);
        block.setUnlocalizedName(MOD_ID + "." + name.replace('/', '.'));
        r.register(block);
        return block;
    }
}
