package net.dries007.tfc.objects.blocks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.blocks.wood.*;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;
import static net.dries007.tfc.objects.Rock.Type.*;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class BlocksTFC
{
    private BlocksTFC() {}

    public static final BlockDebug DEBUG = null;

    public static final BlockFluidBase SALT_WATER = null;
    public static final BlockFluidBase FRESH_WATER = null;
    public static final BlockFluidBase HOT_WATER = null;
    public static final BlockFluidBase FINITE_SALT_WATER = null;
    public static final BlockFluidBase FINITE_FRESH_WATER = null;
    public static final BlockFluidBase FINITE_HOT_WATER = null;

    public static final BlockPeat PEAT = null;
    public static final BlockPeat PEAT_GRASS = null;

    // All these are for use in model registration. Do not use for block lookups.
    // Use the static get methods in the classes instead.
    private static ImmutableList<Block> allNormalItemBlocks;
    private static ImmutableList<Block> allInventoryItemBlocks;
    private static ImmutableList<BlockFluidBase> allFluidBlocks;
    private static ImmutableList<BlockRockVariant> allBlockRockVariants;
    private static ImmutableList<BlockOreTFC> allOreBlocks;
    private static ImmutableList<BlockWallTFC> allWallBlocks;
    private static ImmutableList<BlockLogTFC> allLogBlocks;
    private static ImmutableList<BlockLeavesTFC> allLeafBlocks;
    private static ImmutableList<BlockFenceGateTFC> allFenceGateBlocks;
    private static ImmutableList<BlockSaplingTFC> allSaplingBlocks;
    private static ImmutableList<BlockDoorTFC> allDoorBlocks;
    private static ImmutableList<BlockStairsTFC> allStairsBlocks;
    private static ImmutableList<BlockSlabTFC.Half> allSlabBlocks;

    public static ImmutableList<Block> getAllNormalItemBlocks()
    {
        return allNormalItemBlocks;
    }
    public static ImmutableList<Block> getAllInventoryItemBlocks()
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
    public static ImmutableList<BlockStairsTFC> getAllStairsBlocks()
    {
        return allStairsBlocks;
    }
    public static ImmutableList<BlockSlabTFC.Half> getAllSlabBlocks()
    {
        return allSlabBlocks;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> r = event.getRegistry();

        Builder<Block> normalItemBlocks = ImmutableList.builder();
        Builder<Block> inventoryItemBlocks = ImmutableList.builder();

        normalItemBlocks.add(register(r, "debug", new BlockDebug(), CT_MISC));

        normalItemBlocks.add(register(r, "peat", new BlockPeat(Material.GROUND), CT_ROCK_BLOCKS));
        normalItemBlocks.add(register(r, "peat_grass", new BlockPeatGrass(Material.GRASS), CT_ROCK_BLOCKS));

        {
            TerraFirmaCraft.getLog().info("The 3 warnings ('A mod has attempted to assign Block...') below this line are normal.");
            Builder<BlockFluidBase> b = ImmutableList.builder();
            for (Fluid f : FluidsTFC.getAllFluids())
                registerFluid(b, r, f, Material.WATER);
            allFluidBlocks = b.build();
        }

        {
            Builder<BlockRockVariant> b = ImmutableList.builder();
            for (Rock.Type type : Rock.Type.values())
                for (Rock rock : Rock.values())
                    b.add(register(r, (type.name() + "/" +  rock.name()).toLowerCase(), type.isGrass ? new BlockRockVariantConnected(type, rock) : new BlockRockVariant(type, rock), CT_ROCK_BLOCKS));
            allBlockRockVariants = b.build();
            normalItemBlocks.addAll(allBlockRockVariants);
        }

        {
            Builder<BlockOreTFC> b = ImmutableList.builder();
            for (Ore ore : Ore.values())
                for (Rock rock : Rock.values())
                    b.add(register(r, ("ore/"+ ore.name() + "/" +  rock.name()).toLowerCase(), new BlockOreTFC(ore, rock), CT_ORE_BLOCKS));
            allOreBlocks = b.build();
            normalItemBlocks.addAll(allOreBlocks);
        }

        {
            Builder<BlockWallTFC> b = ImmutableList.builder();
            for (Rock.Type type : new Rock.Type[]{COBBLE, BRICKS})
                for (Rock rock : Rock.values())
                    b.add(register(r, ("wall/" + type.name() + "/" + rock.name()).toLowerCase(), new BlockWallTFC(BlockRockVariant.get(rock, type)), CT_DECORATIONS));
            allWallBlocks = b.build();
            inventoryItemBlocks.addAll(allWallBlocks);
        }

        {
            Builder<BlockLogTFC> logs = ImmutableList.builder();
            Builder<BlockLeavesTFC> leaves = ImmutableList.builder();
            Builder<BlockFenceGateTFC> fenceGates = ImmutableList.builder();
            Builder<BlockSaplingTFC> saplings = ImmutableList.builder();
            Builder<BlockDoorTFC> doors = ImmutableList.builder();

            for (Wood wood : Wood.values())
            {
                logs.add(register(r, "wood/log/" + wood.name().toLowerCase(), new BlockLogTFC(wood), CT_WOOD));
                leaves.add(register(r, "wood/leaves/" + wood.name().toLowerCase(), new BlockLeavesTFC(wood), CT_WOOD));
                normalItemBlocks.add(register(r, "wood/planks/" + wood.name().toLowerCase(), new BlockPlanksTFC(wood), CT_WOOD));
                inventoryItemBlocks.add(register(r, "wood/fence/" + wood.name().toLowerCase(), new BlockFenceTFC(wood), CT_WOOD));
                fenceGates.add(register(r, "wood/fence_gate/" + wood.name().toLowerCase(), new BlockFenceGateTFC(wood), CT_WOOD));
                saplings.add(register(r, "wood/sapling/" + wood.name().toLowerCase(), new BlockSaplingTFC(wood), CT_WOOD));
                if (wood != Wood.PALM) //todo: make this enum constant
                    doors.add(register(r, "wood/door/" + wood.name().toLowerCase(), new BlockDoorTFC(wood), CT_WOOD));
            }
            allLogBlocks = logs.build();
            allLeafBlocks = leaves.build();
            allFenceGateBlocks = fenceGates.build();
            allSaplingBlocks = saplings.build();
            allDoorBlocks = doors.build();
            //logs are special
            normalItemBlocks.addAll(allLeafBlocks);
            inventoryItemBlocks.addAll(allFenceGateBlocks);
            inventoryItemBlocks.addAll(allSaplingBlocks);
            // doors are special
        }

        {
            Builder<BlockStairsTFC> stairs = new Builder<>();
            Builder<BlockSlabTFC.Half> slab = new Builder<>();
            // Stairs
            for (Rock.Type type : new Rock.Type[]{SMOOTH, COBBLE, BRICKS})
                for (Rock rock : Rock.values())
                    stairs.add(register(r, "stairs/" + (type.name() + "/" +  rock.name()).toLowerCase(), new BlockStairsTFC(rock, type), CT_DECORATIONS));
            for (Wood wood : Wood.values())
                stairs.add(register(r, "stairs/wood/" + wood.name().toLowerCase(), new BlockStairsTFC(wood), CT_DECORATIONS));

            // Full slabs are the same as full blocks, they are not saved to a list, they are kept track of by the halfslab version.
            for (Rock.Type type : new Rock.Type[]{SMOOTH, COBBLE, BRICKS})
                for (Rock rock : Rock.values())
                    register(r, "slab/full/" + (type.name() + "/" +  rock.name()).toLowerCase(), new BlockSlabTFC.Double(rock, type));
            for (Wood wood : Wood.values())
                register(r, "slab/full/wood/" + wood.name().toLowerCase(), new BlockSlabTFC.Double(wood));

            // Slabs
            for (Rock.Type type : new Rock.Type[]{SMOOTH, COBBLE, BRICKS})
                for (Rock rock : Rock.values())
                    slab.add(register(r, "slab/half/" + (type.name() + "/" +  rock.name()).toLowerCase(), new BlockSlabTFC.Half(rock, type), CT_DECORATIONS));
            for (Wood wood : Wood.values())
                slab.add(register(r, "slab/half/wood/" + wood.name().toLowerCase(), new BlockSlabTFC.Half(wood), CT_DECORATIONS));

            allStairsBlocks = stairs.build();
            allSlabBlocks = slab.build();
            normalItemBlocks.addAll(allStairsBlocks);
            // slabs are special.
        }

        allNormalItemBlocks = normalItemBlocks.build();
        allInventoryItemBlocks = inventoryItemBlocks.build();
    }

    private static void registerFluid(Builder<BlockFluidBase> b, IForgeRegistry<Block> r, Fluid fluid, Material material)
    {
        BlockFluidBase block = new BlockFluidClassicTFC(fluid, material);
        register(r, fluid.getName(), block);
        b.add(block);
        block = new BlockFluidFiniteTFC(fluid, material);
        register(r, "finite_" + fluid.getName(), block);
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

    public static boolean isWater(IBlockState current)
    {
        return current.getMaterial() == Material.WATER;
    }

    // todo: change to property of type? (soil & stone maybe?)

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
}
