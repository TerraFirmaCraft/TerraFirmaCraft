package net.dries007.tfc.objects.blocks.soil;

import java.util.Random;
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
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.objects.TFCTags;

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
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        worldIn.setBlockState(pos, updateStateFromNeighbors(worldIn, pos, state));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        worldIn.setBlockState(pos, updateStateFromNeighbors(worldIn, pos, state));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        worldIn.setBlockState(pos, updateStateFromNeighbors(worldIn, pos, state), 2);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (newState.getBlock() != state.getBlock())
        {
            updateSurroundingGrassConnections(worldIn, pos);
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    @Nonnull
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return updateStateFromNeighbors(context.getWorld(), context.getPos(), getDefaultState());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    private void updateSurroundingGrassConnections(IWorld world, BlockPos pos)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos targetPos = pos.up().offset(direction);
            BlockState targetState = world.getBlockState(targetPos);
            if (targetState.getBlock() instanceof TFCGrassBlock)
            {
                world.setBlockState(targetPos, updateStateFromDirection(world, targetPos, targetState, direction.getOpposite()), 2);
            }
        }
    }

    private BlockState updateStateFromNeighbors(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            state = updateStateFromDirection(worldIn, pos, state, direction);
        }
        return state;
    }

    /**
     * Updates the provided state based on the specific direction passed in
     *
     * @param pos       the position of the grass block to update
     * @param direction the direction in which to update
     */
    private BlockState updateStateFromDirection(IBlockReader worldIn, BlockPos pos, BlockState stateIn, Direction direction)
    {
        BooleanProperty property = getPropertyForFace(direction);
        if (property != null)
        {
            return stateIn.with(property, TFCTags.GRASS.contains(worldIn.getBlockState(pos.offset(direction).down()).getBlock()));
        }
        return stateIn;
    }
}
