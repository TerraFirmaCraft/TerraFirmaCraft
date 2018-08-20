/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.Random;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.te.TETorchTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

/**
 * todo: add TE with timer
 */
public class BlockTorchTFC extends BlockTorch implements ITileEntityProvider
{
    public static final PropertyBool LIT = PropertyBool.create("lit");

    public BlockTorchTFC()
    {
        super();
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(LIT, true));
        OreDictionaryHelper.register(this, "torch");
        setLightLevel(0.9375F);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TETorchTFC();
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
        IBlockState state = getDefaultState().withProperty(LIT, (meta & 0b1000) == 0b1000);
        switch (meta & 0b0111)
        {
            default:
            case 0b0000:
                return state.withProperty(FACING, EnumFacing.UP);
            case 0b0001:
                return state.withProperty(FACING, EnumFacing.EAST);
            case 0b0010:
                return state.withProperty(FACING, EnumFacing.WEST);
            case 0b0011:
                return state.withProperty(FACING, EnumFacing.SOUTH);
            case 0b0100:
                return state.withProperty(FACING, EnumFacing.NORTH);
        }
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int meta = state.getValue(LIT) ? 0b1000 : 0;
        switch (state.getValue(FACING))
        {
            default:
            case UP:
                return meta | 0b0000;
            case EAST:
                return meta | 0b0001;
            case WEST:
                return meta | 0b0010;
            case SOUTH:
                return meta | 0b0011;
            case NORTH:
                return meta | 0b0100;
        }
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, LIT);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote) return true;

        TETorchTFC te = Helpers.getTE(worldIn, pos, TETorchTFC.class);
        if (te == null) return true;
        te.toggle();
        return true;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? super.getLightValue(state, world, pos) : 0;
    }
}
