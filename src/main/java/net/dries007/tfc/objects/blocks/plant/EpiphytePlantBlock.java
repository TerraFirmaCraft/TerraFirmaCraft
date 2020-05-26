/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plant;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LogBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public abstract class EpiphytePlantBlock extends PlantBlock
{
    protected static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected static final VoxelShape PLANT_UP_SHAPE = makeCuboidShape(4.0, 0.0, 4.0, 12.0, 12.0, 12.0);
    protected static final VoxelShape PLANT_DOWN_SHAPE = makeCuboidShape(4.0, 4.0, 4.0, 12.0, 16.0, 12.0);
    protected static final VoxelShape PLANT_NORTH_SHAPE = makeCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape PLANT_SOUTH_SHAPE = makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
    protected static final VoxelShape PLANT_WEST_SHAPE = makeCuboidShape(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape PLANT_EAST_SHAPE = makeCuboidShape(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);

    protected static final VoxelShape[] SHAPES = Util.make(new VoxelShape[6], shape -> {
        shape[Direction.UP.getIndex()] = PLANT_UP_SHAPE;
        shape[Direction.DOWN.getIndex()] = PLANT_DOWN_SHAPE;
        shape[Direction.NORTH.getIndex()] = PLANT_NORTH_SHAPE;
        shape[Direction.SOUTH.getIndex()] = PLANT_SOUTH_SHAPE;
        shape[Direction.WEST.getIndex()] = PLANT_WEST_SHAPE;
        shape[Direction.EAST.getIndex()] = PLANT_EAST_SHAPE;
    });


    public EpiphytePlantBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getFace());
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        Direction direction = state.get(FACING);
        return hasEnoughSolidSide(world, pos.offset(direction.getOpposite()), direction)
            && world.getBlockState(pos.offset(direction.getOpposite())).getBlock() instanceof LogBlock;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPES[state.get(FACING).getIndex()];
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(FACING);
    }
}
