package net.dries007.tfc.objects.blocks;

import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.Type;
import net.dries007.tfc.objects.items.ItemOreTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.util.InsertOnlyEnumTable;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockOreTFC extends Block
{
    private static final InsertOnlyEnumTable<Ore, Rock, BlockOreTFC> TABLE = new InsertOnlyEnumTable<>(Ore.class, Rock.class);

    public static BlockOreTFC get(Ore ore, Rock rock)
    {
        return TABLE.get(ore, rock);
    }

    public static IBlockState get(Ore ore, Rock rock, Grade grade)
    {
        IBlockState state = TABLE.get(ore, rock).getDefaultState();
        if (!ore.graded) return state;
        return state.withProperty(GRADE, grade);
    }

    public static final PropertyEnum<Grade> GRADE = PropertyEnum.create("grade", Grade.class);

    public final Ore ore;
    public final Rock rock;

    public BlockOreTFC(Ore ore, Rock rock)
    {
        super(Type.RAW.material);
        TABLE.put(ore, rock, this);
        this.ore = ore;
        this.rock = rock;
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
    public int damageDropped(IBlockState state){return getMetaFromState(state);}

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ItemsTFC.getAllOreItems().get(this.ore.ordinal());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    public enum Grade implements IStringSerializable
    {
        NORMAL, POOR, RICH;

        public static Grade byMetadata(int meta)
        {
            return Grade.values()[meta];
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
}
