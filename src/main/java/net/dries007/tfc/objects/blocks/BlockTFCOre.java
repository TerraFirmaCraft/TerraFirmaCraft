package net.dries007.tfc.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static net.dries007.tfc.Constants.MOD_ID;

public class BlockTFCOre extends Block
{
    public final Ore ore;
    public final BlockTFCVariant.Rock rock;

    private BlockTFCOre[] rocks;

    public BlockTFCOre(Ore ore, BlockTFCVariant.Rock rock)
    {
        super(BlockTFCVariant.Type.RAW.material);
        this.ore = ore;
        if (rock == BlockTFCVariant.Rock.GRANITE)
            ore.ref = this;
        this.rock = rock;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR; //todo
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        //todo
//        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    public static IBlockState get(BlockTFCVariant.Rock rock, Ore ore, Grade grade)
    {
        //todo grade
        return ore.ref.getForRock(rock).getDefaultState();
    }

    public BlockTFCOre getForRock(BlockTFCVariant.Rock r)
    {
        if (rock == r) return this;
        if (rocks == null)
        {
            BlockTFCVariant.Rock[] types = BlockTFCVariant.Rock.values();
            rocks = new BlockTFCOre[types.length];
            for (int i = 0; i < types.length; i++)
            {
                //noinspection ConstantConditions
                String name = getRegistryName().getResourcePath().replace(rock.name().toLowerCase(), types[i].name().toLowerCase());
                rocks[i] = (BlockTFCOre) ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID, name));
            }
        }
        return rocks[r.ordinal()];
    }

    public enum Grade
    {
        POOR, NORMAL, RICH
    }

    public enum Ore
    {
        NATIVE_COPPER,
        NATIVE_GOLD,
        NATIVE_PLATINUM,
        HEMATITE,
        NATIVE_SILVER,
        CASSITERITE,
        GALENA,
        BISMUTHINITE,
        GARNIERITE,
        MALACHITE,
        MAGNETITE,
        LIMONITE,
        SPHALERITE,
        TETRAHEDRITE,
        BITUMINOUS_COAL,
        LIGNITE,
        KAOLINITE, 
        GYPSUM,
        SATINSPAR,
        SELENITE,
        GRAPHITE,
        KIMBERLITE,
        PETRIFIED_WOOD,
        SULFUR,
        JET,
        MICROCLINE,
        PITCHBLENDE,
        CINNABAR,
        CRYOLITE,
        SALTPETER,
        SERPENTINE,
        SYLVITE,
        BORAX,
        OLIVINE,
        LAPIS_LAZULI;

        private BlockTFCOre ref;
    }
}
