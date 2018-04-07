package net.dries007.tfc.objects.blocks;

import com.google.common.collect.ImmutableList;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.objects.CreativeTab.CT_MISC;
import static net.dries007.tfc.objects.CreativeTab.CT_ROCK_SOIL;
import static net.dries007.tfc.objects.blocks.BlockTFCVariant.Type.*;

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

    public static final BlockTFCVariant RAW_GRANITE = null;
    public static final BlockTFCVariant RAW_DIORITE = null;
    public static final BlockTFCVariant RAW_GABBRO = null;
    public static final BlockTFCVariant RAW_SHALE = null;
    public static final BlockTFCVariant RAW_CLAYSTONE = null;
    public static final BlockTFCVariant RAW_ROCKSALT = null;
    public static final BlockTFCVariant RAW_LIMESTONE = null;
    public static final BlockTFCVariant RAW_CONGLOMERATE = null;
    public static final BlockTFCVariant RAW_DOLOMITE = null;
    public static final BlockTFCVariant RAW_CHERT = null;
    public static final BlockTFCVariant RAW_CHALK = null;
    public static final BlockTFCVariant RAW_RHYOLITE = null;
    public static final BlockTFCVariant RAW_BASALT = null;
    public static final BlockTFCVariant RAW_ANDESITE = null;
    public static final BlockTFCVariant RAW_DACITE = null;
    public static final BlockTFCVariant RAW_QUARTZITE = null;
    public static final BlockTFCVariant RAW_SLATE = null;
    public static final BlockTFCVariant RAW_PHYLLITE = null;
    public static final BlockTFCVariant RAW_SCHIST = null;
    public static final BlockTFCVariant RAW_GNEISS = null;
    public static final BlockTFCVariant RAW_MARBLE = null;
    public static final BlockTFCVariant SMOOTH_GRANITE = null;
    public static final BlockTFCVariant SMOOTH_DIORITE = null;
    public static final BlockTFCVariant SMOOTH_GABBRO = null;
    public static final BlockTFCVariant SMOOTH_SHALE = null;
    public static final BlockTFCVariant SMOOTH_CLAYSTONE = null;
    public static final BlockTFCVariant SMOOTH_ROCKSALT = null;
    public static final BlockTFCVariant SMOOTH_LIMESTONE = null;
    public static final BlockTFCVariant SMOOTH_CONGLOMERATE = null;
    public static final BlockTFCVariant SMOOTH_DOLOMITE = null;
    public static final BlockTFCVariant SMOOTH_CHERT = null;
    public static final BlockTFCVariant SMOOTH_CHALK = null;
    public static final BlockTFCVariant SMOOTH_RHYOLITE = null;
    public static final BlockTFCVariant SMOOTH_BASALT = null;
    public static final BlockTFCVariant SMOOTH_ANDESITE = null;
    public static final BlockTFCVariant SMOOTH_DACITE = null;
    public static final BlockTFCVariant SMOOTH_QUARTZITE = null;
    public static final BlockTFCVariant SMOOTH_SLATE = null;
    public static final BlockTFCVariant SMOOTH_PHYLLITE = null;
    public static final BlockTFCVariant SMOOTH_SCHIST = null;
    public static final BlockTFCVariant SMOOTH_GNEISS = null;
    public static final BlockTFCVariant SMOOTH_MARBLE = null;
    public static final BlockTFCVariant COBBLE_GRANITE = null;
    public static final BlockTFCVariant COBBLE_DIORITE = null;
    public static final BlockTFCVariant COBBLE_GABBRO = null;
    public static final BlockTFCVariant COBBLE_SHALE = null;
    public static final BlockTFCVariant COBBLE_CLAYSTONE = null;
    public static final BlockTFCVariant COBBLE_ROCKSALT = null;
    public static final BlockTFCVariant COBBLE_LIMESTONE = null;
    public static final BlockTFCVariant COBBLE_CONGLOMERATE = null;
    public static final BlockTFCVariant COBBLE_DOLOMITE = null;
    public static final BlockTFCVariant COBBLE_CHERT = null;
    public static final BlockTFCVariant COBBLE_CHALK = null;
    public static final BlockTFCVariant COBBLE_RHYOLITE = null;
    public static final BlockTFCVariant COBBLE_BASALT = null;
    public static final BlockTFCVariant COBBLE_ANDESITE = null;
    public static final BlockTFCVariant COBBLE_DACITE = null;
    public static final BlockTFCVariant COBBLE_QUARTZITE = null;
    public static final BlockTFCVariant COBBLE_SLATE = null;
    public static final BlockTFCVariant COBBLE_PHYLLITE = null;
    public static final BlockTFCVariant COBBLE_SCHIST = null;
    public static final BlockTFCVariant COBBLE_GNEISS = null;
    public static final BlockTFCVariant COBBLE_MARBLE = null;
    public static final BlockTFCVariant BRICK_GRANITE = null;
    public static final BlockTFCVariant BRICK_DIORITE = null;
    public static final BlockTFCVariant BRICK_GABBRO = null;
    public static final BlockTFCVariant BRICK_SHALE = null;
    public static final BlockTFCVariant BRICK_CLAYSTONE = null;
    public static final BlockTFCVariant BRICK_ROCKSALT = null;
    public static final BlockTFCVariant BRICK_LIMESTONE = null;
    public static final BlockTFCVariant BRICK_CONGLOMERATE = null;
    public static final BlockTFCVariant BRICK_DOLOMITE = null;
    public static final BlockTFCVariant BRICK_CHERT = null;
    public static final BlockTFCVariant BRICK_CHALK = null;
    public static final BlockTFCVariant BRICK_RHYOLITE = null;
    public static final BlockTFCVariant BRICK_BASALT = null;
    public static final BlockTFCVariant BRICK_ANDESITE = null;
    public static final BlockTFCVariant BRICK_DACITE = null;
    public static final BlockTFCVariant BRICK_QUARTZITE = null;
    public static final BlockTFCVariant BRICK_SLATE = null;
    public static final BlockTFCVariant BRICK_PHYLLITE = null;
    public static final BlockTFCVariant BRICK_SCHIST = null;
    public static final BlockTFCVariant BRICK_GNEISS = null;
    public static final BlockTFCVariant BRICK_MARBLE = null;
    public static final BlockTFCVariant SAND_GRANITE = null;
    public static final BlockTFCVariant SAND_DIORITE = null;
    public static final BlockTFCVariant SAND_GABBRO = null;
    public static final BlockTFCVariant SAND_SHALE = null;
    public static final BlockTFCVariant SAND_CLAYSTONE = null;
    public static final BlockTFCVariant SAND_ROCKSALT = null;
    public static final BlockTFCVariant SAND_LIMESTONE = null;
    public static final BlockTFCVariant SAND_CONGLOMERATE = null;
    public static final BlockTFCVariant SAND_DOLOMITE = null;
    public static final BlockTFCVariant SAND_CHERT = null;
    public static final BlockTFCVariant SAND_CHALK = null;
    public static final BlockTFCVariant SAND_RHYOLITE = null;
    public static final BlockTFCVariant SAND_BASALT = null;
    public static final BlockTFCVariant SAND_ANDESITE = null;
    public static final BlockTFCVariant SAND_DACITE = null;
    public static final BlockTFCVariant SAND_QUARTZITE = null;
    public static final BlockTFCVariant SAND_SLATE = null;
    public static final BlockTFCVariant SAND_PHYLLITE = null;
    public static final BlockTFCVariant SAND_SCHIST = null;
    public static final BlockTFCVariant SAND_GNEISS = null;
    public static final BlockTFCVariant SAND_MARBLE = null;
    public static final BlockTFCVariant GRAVEL_GRANITE = null;
    public static final BlockTFCVariant GRAVEL_DIORITE = null;
    public static final BlockTFCVariant GRAVEL_GABBRO = null;
    public static final BlockTFCVariant GRAVEL_SHALE = null;
    public static final BlockTFCVariant GRAVEL_CLAYSTONE = null;
    public static final BlockTFCVariant GRAVEL_ROCKSALT = null;
    public static final BlockTFCVariant GRAVEL_LIMESTONE = null;
    public static final BlockTFCVariant GRAVEL_CONGLOMERATE = null;
    public static final BlockTFCVariant GRAVEL_DOLOMITE = null;
    public static final BlockTFCVariant GRAVEL_CHERT = null;
    public static final BlockTFCVariant GRAVEL_CHALK = null;
    public static final BlockTFCVariant GRAVEL_RHYOLITE = null;
    public static final BlockTFCVariant GRAVEL_BASALT = null;
    public static final BlockTFCVariant GRAVEL_ANDESITE = null;
    public static final BlockTFCVariant GRAVEL_DACITE = null;
    public static final BlockTFCVariant GRAVEL_QUARTZITE = null;
    public static final BlockTFCVariant GRAVEL_SLATE = null;
    public static final BlockTFCVariant GRAVEL_PHYLLITE = null;
    public static final BlockTFCVariant GRAVEL_SCHIST = null;
    public static final BlockTFCVariant GRAVEL_GNEISS = null;
    public static final BlockTFCVariant GRAVEL_MARBLE = null;
    public static final BlockTFCVariant DIRT_GRANITE = null;
    public static final BlockTFCVariant DIRT_DIORITE = null;
    public static final BlockTFCVariant DIRT_GABBRO = null;
    public static final BlockTFCVariant DIRT_SHALE = null;
    public static final BlockTFCVariant DIRT_CLAYSTONE = null;
    public static final BlockTFCVariant DIRT_ROCKSALT = null;
    public static final BlockTFCVariant DIRT_LIMESTONE = null;
    public static final BlockTFCVariant DIRT_CONGLOMERATE = null;
    public static final BlockTFCVariant DIRT_DOLOMITE = null;
    public static final BlockTFCVariant DIRT_CHERT = null;
    public static final BlockTFCVariant DIRT_CHALK = null;
    public static final BlockTFCVariant DIRT_RHYOLITE = null;
    public static final BlockTFCVariant DIRT_BASALT = null;
    public static final BlockTFCVariant DIRT_ANDESITE = null;
    public static final BlockTFCVariant DIRT_DACITE = null;
    public static final BlockTFCVariant DIRT_QUARTZITE = null;
    public static final BlockTFCVariant DIRT_SLATE = null;
    public static final BlockTFCVariant DIRT_PHYLLITE = null;
    public static final BlockTFCVariant DIRT_SCHIST = null;
    public static final BlockTFCVariant DIRT_GNEISS = null;
    public static final BlockTFCVariant DIRT_MARBLE = null;
    public static final BlockTFCVariant GRASS_GRANITE = null;
    public static final BlockTFCVariant GRASS_DIORITE = null;
    public static final BlockTFCVariant GRASS_GABBRO = null;
    public static final BlockTFCVariant GRASS_SHALE = null;
    public static final BlockTFCVariant GRASS_CLAYSTONE = null;
    public static final BlockTFCVariant GRASS_ROCKSALT = null;
    public static final BlockTFCVariant GRASS_LIMESTONE = null;
    public static final BlockTFCVariant GRASS_CONGLOMERATE = null;
    public static final BlockTFCVariant GRASS_DOLOMITE = null;
    public static final BlockTFCVariant GRASS_CHERT = null;
    public static final BlockTFCVariant GRASS_CHALK = null;
    public static final BlockTFCVariant GRASS_RHYOLITE = null;
    public static final BlockTFCVariant GRASS_BASALT = null;
    public static final BlockTFCVariant GRASS_ANDESITE = null;
    public static final BlockTFCVariant GRASS_DACITE = null;
    public static final BlockTFCVariant GRASS_QUARTZITE = null;
    public static final BlockTFCVariant GRASS_SLATE = null;
    public static final BlockTFCVariant GRASS_PHYLLITE = null;
    public static final BlockTFCVariant GRASS_SCHIST = null;
    public static final BlockTFCVariant GRASS_GNEISS = null;
    public static final BlockTFCVariant GRASS_MARBLE = null;
    public static final BlockTFCVariant DRY_GRASS_GRANITE = null;
    public static final BlockTFCVariant DRY_GRASS_DIORITE = null;
    public static final BlockTFCVariant DRY_GRASS_GABBRO = null;
    public static final BlockTFCVariant DRY_GRASS_SHALE = null;
    public static final BlockTFCVariant DRY_GRASS_CLAYSTONE = null;
    public static final BlockTFCVariant DRY_GRASS_ROCKSALT = null;
    public static final BlockTFCVariant DRY_GRASS_LIMESTONE = null;
    public static final BlockTFCVariant DRY_GRASS_CONGLOMERATE = null;
    public static final BlockTFCVariant DRY_GRASS_DOLOMITE = null;
    public static final BlockTFCVariant DRY_GRASS_CHERT = null;
    public static final BlockTFCVariant DRY_GRASS_CHALK = null;
    public static final BlockTFCVariant DRY_GRASS_RHYOLITE = null;
    public static final BlockTFCVariant DRY_GRASS_BASALT = null;
    public static final BlockTFCVariant DRY_GRASS_ANDESITE = null;
    public static final BlockTFCVariant DRY_GRASS_DACITE = null;
    public static final BlockTFCVariant DRY_GRASS_QUARTZITE = null;
    public static final BlockTFCVariant DRY_GRASS_SLATE = null;
    public static final BlockTFCVariant DRY_GRASS_PHYLLITE = null;
    public static final BlockTFCVariant DRY_GRASS_SCHIST = null;
    public static final BlockTFCVariant DRY_GRASS_GNEISS = null;
    public static final BlockTFCVariant DRY_GRASS_MARBLE = null;

    private static ImmutableList<BlockTFCVariant> allBlockTFCVariants;
    private static ImmutableList<BlockFluidBase> allFluidBlocks;

    public static ImmutableList<BlockTFCVariant> getAllBlockTFCVariants()
    {
        return allBlockTFCVariants;
    }
    public static ImmutableList<BlockFluidBase> getAllFluidBlocks()
    {
        return allFluidBlocks;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> r = event.getRegistry();

        register(r, "debug", new BlockDebug(), CT_MISC);

        {
            TerraFirmaCraft.getLog().info("The 3 warnings ('A mod has attempted to assign Block...') below this line are normal.");
            ImmutableList.Builder<BlockFluidBase> b = ImmutableList.builder();
            for (Fluid f : FluidsTFC.getAllFluids())
                registerFluid(b, r, f, Material.WATER);
            allFluidBlocks = b.build();
        }

        {
            ImmutableList.Builder<BlockTFCVariant> b = ImmutableList.builder();
            for (BlockTFCVariant.Type type : BlockTFCVariant.Type.values())
                for (BlockTFCVariant.Rock rock : BlockTFCVariant.Rock.values())
                    registerSoil(b, r, type, rock);
            allBlockTFCVariants = b.build();
        }
    }

    private static void registerFluid(ImmutableList.Builder<BlockFluidBase> b, IForgeRegistry<Block> r, Fluid fluid, Material material)
    {
        BlockFluidBase block = new BlockFluidClassic(fluid, material);
        register(r, fluid.getName(), block);
        b.add(block);
        block = new BlockFluidFinite(fluid, material);
        register(r, "finite_" + fluid.getName(), block);
        b.add(block);
    }

    private static void registerSoil(ImmutableList.Builder<BlockTFCVariant> b, IForgeRegistry<Block> r, BlockTFCVariant.Type type, BlockTFCVariant.Rock rock)
    {
        BlockTFCVariant block = new BlockTFCVariant(type, rock);
        block.setResistance(0).setHardness(0); //todo: remove
        b.add(block);
        register(r, (type.name() + "_" +  rock.name()).toLowerCase(), block, CT_ROCK_SOIL);
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
        if (!(current.getBlock() instanceof BlockTFCVariant)) return false;
        BlockTFCVariant.Type type = ((BlockTFCVariant) current.getBlock()).type;
        return type == RAW;
    }

    public static boolean isDirt(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockTFCVariant)) return false;
        BlockTFCVariant.Type type = ((BlockTFCVariant) current.getBlock()).type;
        return type == DIRT;
    }

    public static boolean isSand(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockTFCVariant)) return false;
        BlockTFCVariant.Type type = ((BlockTFCVariant) current.getBlock()).type;
        return type == SAND;
    }

    public static boolean isSoil(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockTFCVariant)) return false;
        BlockTFCVariant.Type type = ((BlockTFCVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT;
    }

    public static boolean isSoilOrGravel(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockTFCVariant)) return false;
        BlockTFCVariant.Type type = ((BlockTFCVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL;
    }

    public static boolean isGrass(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockTFCVariant)) return false;
        BlockTFCVariant.Type type = ((BlockTFCVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS;
    }

    public static boolean isGround(IBlockState current)
    {
        if (!(current.getBlock() instanceof BlockTFCVariant)) return false;
        BlockTFCVariant.Type type = ((BlockTFCVariant) current.getBlock()).type;
        return type == GRASS || type == DRY_GRASS || type == DIRT || type == GRAVEL || type == RAW || type == SAND;
    }
}
