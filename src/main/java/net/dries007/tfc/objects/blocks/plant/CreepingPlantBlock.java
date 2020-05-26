/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plant;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class CreepingPlantBlock extends PlantBlock
{
    protected static final BooleanProperty UP = BlockStateProperties.UP;
    protected static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    protected static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    protected static final BooleanProperty EAST = BlockStateProperties.EAST;
    protected static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    protected static final BooleanProperty WEST = BlockStateProperties.WEST;

    protected static final VoxelShape UP_SHAPE = makeCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape DOWN_SHAPE = makeCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
    protected static final VoxelShape EAST_SHAPE = makeCuboidShape(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = makeCuboidShape(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = makeCuboidShape(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);

    protected static final BooleanProperty[] ALL_FACES = new BooleanProperty[] {UP, DOWN, NORTH, EAST, SOUTH, WEST};
    protected static final VoxelShape[] ALL_SHAPES = new VoxelShape[] {UP_SHAPE, DOWN_SHAPE, NORTH_SHAPE, EAST_SHAPE, SOUTH_SHAPE, WEST_SHAPE};

    public CreepingPlantBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            if (hasEnoughSolidSide(worldIn, pos.offset(direction), direction.getOpposite()) || worldIn.getBlockState(pos.offset(direction)).getMaterial() == Material.LEAVES)
            {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (isValidPosition(state, worldIn, pos))
        {
            worldIn.setBlockState(pos, getActualState(state, worldIn, pos));
        }
        else
        {
            worldIn.destroyBlock(pos, false);
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos pos = context.getPos();
        World world = context.getWorld();
        return getActualState(getDefaultState(), world, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape shape = VoxelShapes.empty();
        for (int i = 0; i < ALL_FACES.length; i++)
        {
            BooleanProperty face = ALL_FACES[i];
            if (state.get(face))
            {
                shape = VoxelShapes.or(shape, ALL_SHAPES[i]);
            }
        }
        return shape;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(ALL_FACES);
    }

    protected BlockState getActualState(BlockState state, IWorldReader world, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            if (hasEnoughSolidSide(world, pos.offset(direction), direction.getOpposite()) || world.getBlockState(pos.offset(direction)).getMaterial() == Material.LEAVES)
            {
                state = state.with(SixWayBlock.FACING_TO_PROPERTY_MAP.get(direction), true);
            }
            else
            {
                state = state.with(SixWayBlock.FACING_TO_PROPERTY_MAP.get(direction), false);
            }
        }
        return state;
    }
}
