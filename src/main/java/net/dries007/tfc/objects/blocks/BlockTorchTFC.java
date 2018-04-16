package net.dries007.tfc.objects.blocks;

import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * todo: add TE with timer
 */
public class BlockTorchTFC extends BlockTorch /*implements ITileEntityProvider*/
{
    public static final PropertyBool LIT = PropertyBool.create("lit");

    public BlockTorchTFC()
    {
        super();
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(LIT, true));
        OreDictionaryHelper.register(this, "torch");
    }
/*
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TETorchTFC();
    }
*/
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? 15 : 0; //super.getLightValue(state, world, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (!stateIn.getValue(LIT)) return;
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(FACING, EnumFacing.values()[1 + (meta % 6)]).withProperty(LIT, (meta & 0b1000) == 0b1000);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(FACING).ordinal() - 1) & (state.getValue(LIT) ? 0b1000 : 0);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, LIT);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        worldIn.setBlockState(pos, state.cycleProperty(LIT), 2);
        return true;
    }
}
