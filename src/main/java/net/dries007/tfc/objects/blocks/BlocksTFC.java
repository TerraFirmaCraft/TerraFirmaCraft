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

    private static ImmutableList<BlockFluidBase> allFluidBlocks;
    private static ImmutableList<BlockRockVariant> allBlockRockVariants;
    private static ImmutableList<BlockTFCOre> allOreBlocks;

    public static ImmutableList<BlockFluidBase> getAllFluidBlocks()
    {
        return allFluidBlocks;
    }
    public static ImmutableList<BlockRockVariant> getAllBlockRockVariants()
    {
        return allBlockRockVariants;
    }
    public static ImmutableList<BlockTFCOre> getAllOreBlocks()
    {
        return allOreBlocks;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> r = event.getRegistry();

        register(r, "debug", new BlockDebug(), CreativeTabsTFC.CT_MISC);

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
        BlockRockVariant block = type.isColorIndexed ? new BlockRockVariantConnected(type, rock) : new BlockRockVariant(type, rock);
        block.setResistance(0).setHardness(0); //todo: remove
        b.add(block);
        register(r, (type.name() + "_" +  rock.name()).toLowerCase(), block, CreativeTabsTFC.CT_ROCK_SOIL);
    }

    private static void registerOre(ImmutableList.Builder<BlockTFCOre> b, IForgeRegistry<Block> r, BlockTFCOre.Ore ore, BlockRockVariant.Rock rock)
    {
        BlockTFCOre block = new BlockTFCOre(ore, rock);
        block.setResistance(0).setHardness(0); //todo: remove
        b.add(block);
        register(r, (ore.name() + "_" +  rock.name()).toLowerCase(), block, CreativeTabsTFC.CT_ORES);
    }

    private static void register(IForgeRegistry<Block> r, String name, Block block, CreativeTabs ct)
    {
        block.setCreativeTab(ct);
        register(r, name, block);
    }

    private static void register(IForgeRegistry<Block> r, String name, Block block)
    {
        block.setRegistryName(MOD_ID, name);
        block.setUnlocalizedName(MOD_ID + "." + name.replace('_', '.'));

        r.register(block);
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
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        BlockRockVariant.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT;
    }

    public static boolean isSoilOrGravel(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        BlockRockVariant.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL;
    }

    public static boolean isGrass(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        BlockRockVariant.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS;
    }

    public static boolean isGround(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockRockVariant)) return false;
        BlockRockVariant.Type type = ((BlockRockVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL || type == RAW || type == SAND;
    }
}
