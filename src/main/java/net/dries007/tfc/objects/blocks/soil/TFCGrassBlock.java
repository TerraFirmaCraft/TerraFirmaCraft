package net.dries007.tfc.objects.blocks.soil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.dries007.tfc.util.tags.TFCBlockTags;

@ParametersAreNonnullByDefault
public class TFCGrassBlock extends Block
{
    // Used for connected textures only.
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    @Nullable
    private static BooleanProperty getPropertyForFace(Direction direction)
    {
        switch (direction)
        {
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case WEST:
                return WEST;
            case SOUTH:
                return SOUTH;
            default:
                return null;
        }
    }

    public TFCGrassBlock(Properties properties)
    {
        super(properties);

        setDefaultState(stateContainer.getBaseState().with(SOUTH, false).with(EAST, false).with(NORTH, false).with(WEST, false));
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return updateStateWithNeighbor(worldIn, facingPos.down(), stateIn, facing);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        BlockPos posDown = pos.down();
        for (Direction face : Direction.Plane.HORIZONTAL)
        {
            state = updateStateWithNeighbor(worldIn, posDown.offset(face), state, face);
        }
        worldIn.setBlockState(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        // Update surrounding grass blocks with the new grass location
        updateSurroundingGrassBlocks(worldIn, pos);
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (newState.getBlock() != state.getBlock())
        {
            updateSurroundingGrassBlocks(worldIn, pos);
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    @Nonnull
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState();
        BlockPos posDown = context.getPos().down();
        for (Direction face : Direction.Plane.HORIZONTAL)
        {
            state = updateStateWithNeighbor(context.getWorld(), posDown.offset(face), state, face);
        }
        return state;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    private void updateSurroundingGrassBlocks(IWorld world, BlockPos pos)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos targetPos = pos.up().offset(direction);
            BlockState targetState = world.getBlockState(targetPos);
            if (targetState.getBlock() instanceof TFCGrassBlock)
            {
                world.setBlockState(targetPos, updateStateWithNeighbor(world, pos, targetState, direction.getOpposite()), 3);
            }
        }
    }

    private BlockState updateStateWithNeighbor(IBlockReader worldIn, BlockPos checkPos, BlockState stateIn, Direction face)
    {
        BooleanProperty property = getPropertyForFace(face);
        if (property != null)
        {
            return stateIn.with(property, TFCBlockTags.GRASS.contains(worldIn.getBlockState(checkPos).getBlock()));
        }
        return stateIn;
    }
}
