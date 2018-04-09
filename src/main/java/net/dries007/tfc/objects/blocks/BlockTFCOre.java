package net.dries007.tfc.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
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
    public static final PropertyEnum<Grade> GRADE = PropertyEnum.create("grade", Grade.class);

    public final Ore ore;
    public final BlockRockVariant.Rock rock;

    private BlockTFCOre[] rocks;

    public BlockTFCOre(Ore ore, BlockRockVariant.Rock rock)
    {
        super(BlockRockVariant.Type.RAW.material);
        this.ore = ore;
        this.rock = rock;
        if (rock == BlockRockVariant.Rock.GRANITE)
            ore.ref = this;
        this.setDefaultState(blockState.getBaseState().withProperty(GRADE, Grade.NORMAL));
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(GRADE, Grade.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(GRADE).getMeta();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, GRADE);
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

    public static IBlockState get(BlockRockVariant.Rock rock, Ore ore, Grade grade)
    {
        IBlockState state = ore.ref.getForRock(rock).getDefaultState();
        if (!ore.graded) return state;
        return state.withProperty(GRADE, grade);
    }

    public BlockTFCOre getForRock(BlockRockVariant.Rock r)
    {
        if (rock == r) return this;
        if (rocks == null)
        {
            BlockRockVariant.Rock[] types = BlockRockVariant.Rock.values();
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

    public enum Grade implements IStringSerializable
    {
        NORMAL, POOR, RICH;

        public static Grade byMetadata(int meta)
        {
            return values()[meta % 3];
        }

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }

        public int getMeta()
        {
            return this.ordinal();
        }
    }

    public enum Ore
    {
        NATIVE_COPPER(true),
        NATIVE_GOLD(true),
        NATIVE_PLATINUM(true),
        HEMATITE(true),
        NATIVE_SILVER(true),
        CASSITERITE(true),
        GALENA(true),
        BISMUTHINITE(true),
        GARNIERITE(true),
        MALACHITE(true),
        MAGNETITE(true),
        LIMONITE(true),
        SPHALERITE(true),
        TETRAHEDRITE(true),
        BITUMINOUS_COAL(false),
        LIGNITE(false),
        KAOLINITE(false),
        GYPSUM(false),
        SATINSPAR(false),
        SELENITE(false),
        GRAPHITE(false),
        KIMBERLITE(false),
        PETRIFIED_WOOD(false),
        SULFUR(false),
        JET(false),
        MICROCLINE(false),
        PITCHBLENDE(false),
        CINNABAR(false),
        CRYOLITE(false),
        SALTPETER(false),
        SERPENTINE(false),
        SYLVITE(false),
        BORAX(false),
        OLIVINE(false),
        LAPIS_LAZULI(false);

        public final boolean graded;
        private BlockTFCOre ref;

        Ore(boolean graded)
        {
            this.graded = graded;
        }
    }
}
