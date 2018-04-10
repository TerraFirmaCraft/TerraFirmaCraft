package net.dries007.tfc.objects.blocks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.Type;
import net.dries007.tfc.objects.Wood;
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
import static net.dries007.tfc.objects.Type.*;

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

    private static ImmutableList<Block> allNormalItemBlocks;
    private static ImmutableList<Block> allInventoryItemBlocks;
    private static ImmutableList<BlockFluidBase> allFluidBlocks;
    private static ImmutableList<BlockRockVariant> allBlockRockVariants;
    private static ImmutableList<BlockLogTFC> allLogBlocks;
    private static ImmutableList<BlockLeavesTFC> allLeafBlocks;
    private static ImmutableList<BlockPlanksTFC> allPlankBlocks;
    private static ImmutableList<BlockOreTFC> allOreBlocks;
    private static ImmutableList<BlockFenceTFC> allFenceBlocks;
    private static ImmutableList<BlockFenceGateTFC> allFenceGateBlocks;
    private static ImmutableList<BlockSaplingTFC> allSaplingBlocks;
    private static ImmutableList<BlockWallTFC> allWallBlocks;

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
    public static ImmutableList<BlockLeavesTFC> getAllLeafBlocks()
    {
        return allLeafBlocks;
    }
    public static ImmutableList<BlockOreTFC> getAllOreBlocks()
    {
        return allOreBlocks;
    }
    public static ImmutableList<BlockLogTFC> getAllLogBlocks()
    {
        return allLogBlocks;
    }
    public static ImmutableList<BlockPlanksTFC> getAllPlankBlocks()
    {
        return allPlankBlocks;
    }
    public static ImmutableList<BlockFenceTFC> getAllFenceBlocks()
    {
        return allFenceBlocks;
    }
    public static ImmutableList<BlockFenceGateTFC> getAllFenceGateBlocks()
    {
        return allFenceGateBlocks;
    }
    public static ImmutableList<BlockSaplingTFC> getAllSaplingBlocksBlocks()
    {
        return allSaplingBlocks;
    }
    public static ImmutableList<BlockWallTFC> getAllWallBlocks()
    {
        return allWallBlocks;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> r = event.getRegistry();

        Builder<Block> normalItemBlocks = ImmutableList.builder();
        Builder<Block> inventoryItemBlocks = ImmutableList.builder();

        normalItemBlocks.add(register(r, "debug", new BlockDebug(), CT_MISC));

        normalItemBlocks.add(register(r, "peat", new BlockPeat(Material.GROUND), CT_ROCK_SOIL));
        normalItemBlocks.add(register(r, "peat_grass", new BlockPeatGrass(Material.GRASS), CT_ROCK_SOIL));

        {
            TerraFirmaCraft.getLog().info("The 3 warnings ('A mod has attempted to assign Block...') below this line are normal.");
            Builder<BlockFluidBase> b = ImmutableList.builder();
            for (Fluid f : FluidsTFC.getAllFluids())
                registerFluid(b, r, f, Material.WATER);
            allFluidBlocks = b.build();
        }

        {
            Builder<BlockRockVariant> b = ImmutableList.builder();
            for (Type type : Type.values())
                for (Rock rock : Rock.values())
                    b.add(register(r, (type.name() + "_" +  rock.name()).toLowerCase(), type.isGrass ? new BlockRockVariantConnected(type, rock) : new BlockRockVariant(type, rock), CT_ROCK_SOIL));
            allBlockRockVariants = b.build();
            normalItemBlocks.addAll(allBlockRockVariants);
        }

        {
            Builder<BlockOreTFC> b = ImmutableList.builder();
            for (Ore ore : Ore.values())
                for (Rock rock : Rock.values())
                    b.add(register(r, (ore.name() + "_" +  rock.name()).toLowerCase(), new BlockOreTFC(ore, rock), CT_ORE_BLOCKS));
            allOreBlocks = b.build();
            normalItemBlocks.addAll(allOreBlocks);
        }

        {
            Builder<BlockWallTFC> b = ImmutableList.builder();
            for (Type type : new Type[]{Type.COBBLE, Type.BRICK})
                for (Rock rock : Rock.values())
                    b.add(register(r, ("wall_" + type.name() + "_" + rock.name()).toLowerCase(), new BlockWallTFC(BlockRockVariant.get(rock, type)), CT_DECORATIONS));
            allWallBlocks = b.build();
            inventoryItemBlocks.addAll(allWallBlocks);
        }

        {
            Builder<BlockLogTFC> b1 = ImmutableList.builder();
            Builder<BlockLeavesTFC> b2 = ImmutableList.builder();
            Builder<BlockPlanksTFC> b3 = ImmutableList.builder();
            Builder<BlockFenceTFC> b4 = ImmutableList.builder();
            Builder<BlockFenceGateTFC> b5 = ImmutableList.builder();
            Builder<BlockSaplingTFC> b6 = ImmutableList.builder();

            for (Wood wood : Wood.values())
            {
                b1.add(register(r, "log_" + wood.name().toLowerCase(), new BlockLogTFC(wood), CT_WOOD));
                b2.add(register(r, "leaves_" + wood.name().toLowerCase(), new BlockLeavesTFC(wood), CT_WOOD));
                b3.add(register(r, "planks_" + wood.name().toLowerCase(), new BlockPlanksTFC(wood), CT_WOOD));
                b4.add(register(r, "fence_" + wood.name().toLowerCase(), new BlockFenceTFC(wood), CT_WOOD));
                b5.add(register(r, "fence_gate_" + wood.name().toLowerCase(), new BlockFenceGateTFC(wood), CT_WOOD));
                b6.add(register(r, "sapling_" + wood.name().toLowerCase(), new BlockSaplingTFC(wood), CT_WOOD));
            }
            allLogBlocks = b1.build();
            allLeafBlocks = b2.build();
            allPlankBlocks = b3.build();
            allFenceBlocks = b4.build();
            allFenceGateBlocks = b5.build();
            allSaplingBlocks = b6.build();
            normalItemBlocks.addAll(allLogBlocks);
            normalItemBlocks.addAll(allLeafBlocks);
            normalItemBlocks.addAll(allPlankBlocks);
            normalItemBlocks.addAll(allSaplingBlocks);
            inventoryItemBlocks.addAll(allFenceBlocks);
            inventoryItemBlocks.addAll(allFenceGateBlocks);
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
        block.setResistance(0).setHardness(0); // todo: remove
        return register(r, name, block);
    }

    private static <T extends Block> T register(IForgeRegistry<Block> r, String name, T block)
    {
        block.setRegistryName(MOD_ID, name);
        block.setUnlocalizedName(MOD_ID + "." + name.replace('_', '.'));
        r.register(block);
        return block;
    }

    public static boolean isWater(IBlockState current)
    {
        return current.getMaterial() == Material.WATER;
    }

    // todo: change to property of type? (soil & stone maybe?)
    // todo: peat grass, clay grass

    public static boolean isRawStone(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == RAW;
    }

    public static boolean isClay(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == CLAY || type == CLAY_GRASS;
    }

    public static boolean isDirt(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == DIRT;
    }

    public static boolean isSand(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == SAND;
    }

    public static boolean isSoil(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeat) return true;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == CLAY || type == CLAY_GRASS;
    }

    public static boolean isSoilOrGravel(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeat) return true;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL;
    }

    public static boolean isGrass(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeatGrass) return true;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Type type = ((BlockRockVariant) current.getBlock()).type;
        return type.isGrass;
    }

    public static boolean isGround(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL || type == RAW || type == SAND;
    }
}
