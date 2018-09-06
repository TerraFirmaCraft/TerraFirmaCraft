/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.blocks.wood;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.te.TEToolRack;
import net.dries007.tfc.util.Helpers;

import static net.minecraft.block.BlockHorizontal.FACING;
import static net.minecraft.block.material.Material.WOOD;

public class BlockToolRack extends BlockContainer
{//Borrowed from signs, shall be resized
    protected static final AxisAlignedBB RACK_EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
    protected static final AxisAlignedBB RACK_WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB RACK_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    protected static final AxisAlignedBB RACK_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);

    public Tree wood;

    public BlockToolRack(Tree wood)
    {
        super(WOOD);
        this.wood = wood;
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TEToolRack();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch (state.getValue(FACING))
        {
            case NORTH:
            default:
                return RACK_NORTH_AABB;
            case SOUTH:
                return RACK_SOUTH_AABB;
            case WEST:
                return RACK_WEST_AABB;
            case EAST:
                return RACK_EAST_AABB;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        switch (state.getValue(FACING))
        {
            case SOUTH:
            {
                if (!worldIn.isSideSolid(pos.add(0, 0, -1), EnumFacing.SOUTH))
                    pop(state, worldIn, pos);
                break;
            }
            case NORTH:
            {
                if (!worldIn.isSideSolid(pos.add(0, 0, 1), EnumFacing.NORTH))
                    pop(state, worldIn, pos);
                break;
            }
            case EAST:
            {
                if (!worldIn.isSideSolid(pos.add(-1, 0, 0), EnumFacing.EAST))
                    pop(state, worldIn, pos);
                break;
            }
            case WEST:
                if (!worldIn.isSideSolid(pos.add(1, 0, 0), EnumFacing.WEST))
                    pop(state, worldIn, pos);
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TEToolRack te = Helpers.getTE(worldIn, pos, TEToolRack.class);
        if (te == null) return true;
        //TODO: this one rotates, I cannot get away w/ this
        te.onRightClick(playerIn, playerIn.getHeldItem(hand), hitX < 0.5, hitZ < 0.5);
        return true;
    }

    /*@Nullable
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return getBoundingBox(blockState,worldIn,pos);
    }*/

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (facing.getAxis() == EnumFacing.Axis.Y)
            facing = placer.getHorizontalFacing().getOpposite();
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    public void pop(IBlockState state, World worldIn, BlockPos pos)
    {
        dropBlockAsItem(worldIn, pos, state, 0);
        TEToolRack te = Helpers.getTE(worldIn, pos, TEToolRack.class);
        if (te != null)
            te.onBreakBlock();
        worldIn.setBlockToAir(pos);
    }
}
