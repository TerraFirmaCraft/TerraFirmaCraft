/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.blocks.plants;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;

public class BlockCreepingPlantTFC extends BlockPlantTFC
{
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool[] ALL_FACES = new PropertyBool[] {DOWN, UP, NORTH, SOUTH, WEST, EAST};
    protected static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    protected static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.0D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);


    public BlockCreepingPlantTFC()
    {
        super();
        this.setDefaultState(this.blockState.getBaseState().withProperty(DOWN, Boolean.valueOf(false)).withProperty(UP, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)));
    }

    public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos, facing);
        Block block = iblockstate.getBlock();
        return blockfaceshape == BlockFaceShape.SOLID;
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {DOWN, UP, NORTH, EAST, WEST, SOUTH, GROWTHSTAGE});
    }

    @Override
    public Block.EnumOffsetType getOffsetType()
    {
        return EnumOffsetType.NONE;
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        state = state.getActualState(source, pos);
        int i = 0;
        AxisAlignedBB axisalignedbb = FULL_BLOCK_AABB;

        if (((Boolean) state.getValue(DOWN)).booleanValue())
        {
            axisalignedbb = DOWN_AABB;
            ++i;
        }

        if (((Boolean) state.getValue(UP)).booleanValue())
        {
            axisalignedbb = UP_AABB;
            ++i;
        }

        if (((Boolean) state.getValue(NORTH)).booleanValue())
        {
            axisalignedbb = NORTH_AABB;
            ++i;
        }

        if (((Boolean) state.getValue(EAST)).booleanValue())
        {
            axisalignedbb = EAST_AABB;
            ++i;
        }

        if (((Boolean) state.getValue(SOUTH)).booleanValue())
        {
            axisalignedbb = SOUTH_AABB;
            ++i;
        }

        if (((Boolean) state.getValue(WEST)).booleanValue())
        {
            axisalignedbb = WEST_AABB;
            ++i;
        }

        return i == 1 ? axisalignedbb : FULL_BLOCK_AABB;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(DOWN, canPlantConnectTo(worldIn, pos, EnumFacing.DOWN))
            .withProperty(UP, canPlantConnectTo(worldIn, pos, EnumFacing.UP))
            .withProperty(NORTH, canPlantConnectTo(worldIn, pos, EnumFacing.NORTH))
            .withProperty(EAST, canPlantConnectTo(worldIn, pos, EnumFacing.EAST))
            .withProperty(SOUTH, canPlantConnectTo(worldIn, pos, EnumFacing.SOUTH))
            .withProperty(WEST, canPlantConnectTo(worldIn, pos, EnumFacing.WEST));
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        switch (rot)
        {
            case CLOCKWISE_180:
                return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90:
                return state.withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH));
            case CLOCKWISE_90:
                return state.withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH));
            default:
                return state;
        }
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH));
            case FRONT_BACK:
                return state.withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST));
            default:
                return super.withMirror(state, mirrorIn);
        }
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        return canConnectTo(world, pos.offset(facing), facing.getOpposite());
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).isBlockNormalCube() ||
            worldIn.getBlockState(pos.up()).isBlockNormalCube() ||
            worldIn.getBlockState(pos.north()).isBlockNormalCube() ||
            worldIn.getBlockState(pos.east()).isBlockNormalCube() ||
            worldIn.getBlockState(pos.south()).isBlockNormalCube() ||
            worldIn.getBlockState(pos.west()).isBlockNormalCube() ||
            worldIn.getBlockState(pos.down()).getBlock() instanceof BlockLeavesTFC ||
            worldIn.getBlockState(pos.up()).getBlock() instanceof BlockLeavesTFC ||
            worldIn.getBlockState(pos.north()).getBlock() instanceof BlockLeavesTFC ||
            worldIn.getBlockState(pos.east()).getBlock() instanceof BlockLeavesTFC ||
            worldIn.getBlockState(pos.south()).getBlock() instanceof BlockLeavesTFC ||
            worldIn.getBlockState(pos.west()).getBlock() instanceof BlockLeavesTFC;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            if (!worldIn.getBlockState(pos.down()).isBlockNormalCube() &&
                !worldIn.getBlockState(pos.up()).isBlockNormalCube() &&
                !worldIn.getBlockState(pos.north()).isBlockNormalCube() &&
                !worldIn.getBlockState(pos.east()).isBlockNormalCube() &&
                !worldIn.getBlockState(pos.south()).isBlockNormalCube() &&
                !worldIn.getBlockState(pos.west()).isBlockNormalCube() &&
                !(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockLeavesTFC) &&
                !(worldIn.getBlockState(pos.up()).getBlock() instanceof BlockLeavesTFC) &&
                !(worldIn.getBlockState(pos.north()).getBlock() instanceof BlockLeavesTFC) &&
                !(worldIn.getBlockState(pos.east()).getBlock() instanceof BlockLeavesTFC) &&
                !(worldIn.getBlockState(pos.south()).getBlock() instanceof BlockLeavesTFC) &&
                !(worldIn.getBlockState(pos.west()).getBlock() instanceof BlockLeavesTFC))
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        return true;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    private boolean canPlantConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        BlockPos other = pos.offset(facing);
        Block block = world.getBlockState(other).getBlock();
        return block.canBeConnectedTo(world, other, facing.getOpposite()) || canConnectTo(world, other, facing.getOpposite());
    }
}
