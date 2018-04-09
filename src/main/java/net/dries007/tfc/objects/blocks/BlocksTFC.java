package net.dries007.tfc.objects.blocks;

import com.google.common.collect.ImmutableList;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.CreativeTabsTFC;
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
import static net.dries007.tfc.objects.blocks.BlockRockVariant.Type.*;

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

    private static ImmutableList<BlockFluidBase> allFluidBlocks;
    private static ImmutableList<BlockRockVariant> allBlockRockVariants;
    private static ImmutableList<BlockLogTFC> allLogBlocks;
    private static ImmutableList<BlockLeavesTFC> allLeafBlocks;
    private static ImmutableList<BlockPlanksTFC> allPlankBlocks;
    private static ImmutableList<BlockTFCOre> allOreBlocks;

    public static ImmutableList<BlockFluidBase> getAllFluidBlocks()
    {
        return allFluidBlocks;
    }
    public static ImmutableList<BlockRockVariant> getAllBlockRockVariants()
    {
        return allBlockRockVariants;
    }
    public static ImmutableList<BlockLogTFC> getAllLogBlocks() { return allLogBlocks; }
    public static ImmutableList<BlockLeavesTFC> getAllLeafBlocks() { return allLeafBlocks; }
    public static ImmutableList<BlockPlanksTFC> getAllPlankBlocks() { return allPlankBlocks; }
    public static ImmutableList<BlockTFCOre> getAllOreBlocks()
    {
        return allOreBlocks;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> r = event.getRegistry();

        register(r, "debug", new BlockDebug(), CreativeTabsTFC.CT_MISC);

        register(r, "peat", new BlockPeat(Material.GROUND), CreativeTabsTFC.CT_MISC);
        register(r, "peat_grass", new BlockPeatGrass(Material.GRASS), CreativeTabsTFC.CT_MISC);

        {
            TerraFirmaCraft.getLog().info("The 3 warnings ('A mod has attempted to assign Block...') below this line are normal.");
            ImmutableList.Builder<BlockFluidBase> b = ImmutableList.builder();
            for (Fluid f : FluidsTFC.getAllFluids())
                registerFluid(b, r, f, Material.WATER);
            allFluidBlocks = b.build();
        }

        {
            ImmutableList.Builder<BlockRockVariant> b = ImmutableList.builder();
            for (BlockRockVariant.Type type : BlockRockVariant.Type.values())
                for (BlockRockVariant.Rock rock : BlockRockVariant.Rock.values())
                    registerSoil(b, r, type, rock);
            allBlockRockVariants = b.build();
        }

        {
            ImmutableList.Builder<BlockLogTFC> b1 = ImmutableList.builder();
            ImmutableList.Builder<BlockLeavesTFC> b2 = ImmutableList.builder();
            ImmutableList.Builder<BlockPlanksTFC> b3 = ImmutableList.builder();
            for (BlockLogTFC.Wood wood : BlockLogTFC.Wood.values())
                registerWood(b1, b2, b3, r, wood);
            allLogBlocks = b1.build();
            allLeafBlocks = b2.build();
            allPlankBlocks = b3.build();
        }

        {
            ImmutableList.Builder<BlockTFCOre> b = ImmutableList.builder();
            for (BlockTFCOre.Ore ore : BlockTFCOre.Ore.values())
                for (BlockRockVariant.Rock rock : BlockRockVariant.Rock.values())
                    registerOre(b, r, ore, rock);
            allOreBlocks = b.build();
        }
    }

    private static void registerFluid(ImmutableList.Builder<BlockFluidBase> b, IForgeRegistry<Block> r, Fluid fluid, Material material)
    {
        BlockFluidBase block = new BlockFluidClassicTFC(fluid, material);
        register(r, fluid.getName(), block);
        b.add(block);
        block = new BlockFluidFiniteTFC(fluid, material);
        register(r, "finite_" + fluid.getName(), block);
        b.add(block);
    }

    private static void registerSoil(ImmutableList.Builder<BlockRockVariant> b, IForgeRegistry<Block> r, BlockRockVariant.Type type, BlockRockVariant.Rock rock)
    {
        b.add(register(r, (type.name() + "_" +  rock.name()).toLowerCase(), type.isGrass ? new BlockRockVariantConnected(type, rock) : new BlockRockVariant(type, rock), CreativeTabsTFC.CT_ROCK_SOIL));
    }

    private static void registerWood(ImmutableList.Builder<BlockLogTFC> b1, ImmutableList.Builder<BlockLeavesTFC> b2, ImmutableList.Builder<BlockPlanksTFC> b3, IForgeRegistry<Block> r, BlockLogTFC.Wood wood)
    {
        b1.add(register(r, "log_" + wood.name().toLowerCase(), new BlockLogTFC(wood), CreativeTabsTFC.CT_WOOD));
        b2.add(register(r, "leaves_" + wood.name().toLowerCase(), new BlockLeavesTFC(wood), CreativeTabsTFC.CT_WOOD));
        b3.add(register(r, "planks_" + wood.name().toLowerCase(), new BlockPlanksTFC(wood), CreativeTabsTFC.CT_WOOD));
    }

    private static void registerOre(ImmutableList.Builder<BlockTFCOre> b, IForgeRegistry<Block> r, BlockTFCOre.Ore ore, BlockRockVariant.Rock rock)
    {
        b.add(register(r, (ore.name() + "_" +  rock.name()).toLowerCase(), new BlockTFCOre(ore, rock), CreativeTabsTFC.CT_ORES));
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
        BlockRockVariant.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == RAW;
    }

    public static boolean isClay(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        BlockRockVariant.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == CLAY || type == CLAY_GRASS;
    }

    public static boolean isDirt(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        BlockRockVariant.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == DIRT;
    }

    public static boolean isSand(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        BlockRockVariant.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == SAND;
    }

    public static boolean isSoil(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeat) return true;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        BlockRockVariant.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == CLAY || type == CLAY_GRASS;
    }

    public static boolean isSoilOrGravel(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeat) return true;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        BlockRockVariant.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL;
    }

    public static boolean isGrass(IBlockState current)
    {
        if (current.getBlock() instanceof BlockPeatGrass) return true;
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        BlockRockVariant.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type.isGrass;
    }

    public static boolean isGround(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        BlockRockVariant.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL || type == RAW || type == SAND;
    }
}
