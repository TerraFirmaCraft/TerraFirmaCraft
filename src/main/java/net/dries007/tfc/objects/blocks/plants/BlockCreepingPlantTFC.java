/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
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

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.world.classic.CalenderTFC;

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
    private static final Map<Plant, EnumMap<Plant.PlantType, BlockCreepingPlantTFC>> TABLE = new HashMap<>();

    public static BlockCreepingPlantTFC get(Plant plant, Plant.PlantType type)
    {
        return BlockCreepingPlantTFC.TABLE.get(plant).get(type);
    }
    public final Plant plant;
    public final Plant.PlantType type;

    public BlockCreepingPlantTFC(Plant plant, Plant.PlantType type)
    {
        super(plant, type);
        if (!TABLE.containsKey(plant))
            TABLE.put(plant, new EnumMap<>(Plant.PlantType.class));
        TABLE.get(plant).put(type, this);

        this.plant = plant;
        this.type = type;
        this.setDefaultState(this.blockState.getBaseState().withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(DOWN, Boolean.valueOf(false)).withProperty(UP, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)));
    }

    public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos, facing);
        Block block = iblockstate.getBlock();
        return blockfaceshape == BlockFaceShape.SOLID || block instanceof BlockFence;
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
        return state.withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id())
            .withProperty(DOWN, canPlantConnectTo(worldIn, pos, EnumFacing.DOWN))
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
                return state.withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90:
                return state.withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH));
            case CLOCKWISE_90:
                return state.withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH));
            default:
                return state.withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id());
        }
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                return state.withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH));
            case FRONT_BACK:
                return state.withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()).withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST));
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
        return canConnectTo(world, pos.offset(facing), facing.getOpposite()) && !(world.getBlockState(pos.offset(facing)).getBlock() instanceof BlockFence);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {

        return worldIn.getBlockState(pos).getBlock() != this && canBlockStay(worldIn, pos, worldIn.getBlockState(pos));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            if (!canBlockStay(worldIn, pos, state))
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        return worldIn.getBlockState(pos.down()).getBlockFaceShape(worldIn, pos, EnumFacing.UP) == BlockFaceShape.SOLID ||
            worldIn.getBlockState(pos.up()).getBlockFaceShape(worldIn, pos, EnumFacing.DOWN) == BlockFaceShape.SOLID ||
            worldIn.getBlockState(pos.north()).getBlockFaceShape(worldIn, pos, EnumFacing.SOUTH) == BlockFaceShape.SOLID ||
            worldIn.getBlockState(pos.east()).getBlockFaceShape(worldIn, pos, EnumFacing.WEST) == BlockFaceShape.SOLID ||
            worldIn.getBlockState(pos.south()).getBlockFaceShape(worldIn, pos, EnumFacing.NORTH) == BlockFaceShape.SOLID ||
            worldIn.getBlockState(pos.west()).getBlockFaceShape(worldIn, pos, EnumFacing.EAST) == BlockFaceShape.SOLID ||
            worldIn.getBlockState(pos.down()).getBlock() instanceof BlockFence ||
            worldIn.getBlockState(pos.up()).getBlock() instanceof BlockFence ||
            worldIn.getBlockState(pos.north()).getBlock() instanceof BlockFence ||
            worldIn.getBlockState(pos.east()).getBlock() instanceof BlockFence ||
            worldIn.getBlockState(pos.south()).getBlock() instanceof BlockFence ||
            worldIn.getBlockState(pos.west()).getBlock() instanceof BlockFence;
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
