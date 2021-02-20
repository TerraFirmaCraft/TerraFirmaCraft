/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SixWayBlock;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;

public abstract class CreepingPlantBlock extends PlantBlock
{
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    protected static final VoxelShape UP_SHAPE = makeCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape DOWN_SHAPE = makeCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
    protected static final VoxelShape EAST_SHAPE = makeCuboidShape(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = makeCuboidShape(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = makeCuboidShape(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);

    protected static final Map<BooleanProperty, VoxelShape> SHAPES_BY_PROPERTY = ImmutableMap.<BooleanProperty, VoxelShape>builder().put(UP, UP_SHAPE).put(DOWN, DOWN_SHAPE).put(NORTH, NORTH_SHAPE).put(SOUTH, SOUTH_SHAPE).put(EAST, EAST_SHAPE).put(WEST, WEST_SHAPE).build();

    public static CreepingPlantBlock create(IPlant plant, Properties properties)
    {
        return new CreepingPlantBlock(properties)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected final Map<BlockState, VoxelShape> shapeCache=null;

    protected CreepingPlantBlock(Properties properties)
    {
        super(properties);
        // these lines of code may not working on 1.16
        /*shapeCache = getDefaultState().get(Collectors.toMap(state -> state,
            state -> SHAPES_BY_PROPERTY.entrySet().stream()
                .findAny().stream()
                .filter(entry -> state.get(entry.getKey()))
                .map(Map.Entry::getValue)
                .reduce(VoxelShapes::or)
                .orElseGet(VoxelShapes::empty)));*/

        setDefaultState(getDefaultState().with(UP, false).with(DOWN, false).with(EAST, false).with(WEST, false).with(NORTH, false).with(SOUTH, false));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction direction, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        stateIn = stateIn.with(SixWayBlock.FACING_TO_PROPERTY_MAP.get(direction), facingState.isIn(TFCTags.Blocks.CREEPING_PLANTABLE_ON));
        return isEmpty(stateIn) ? Blocks.AIR.getDefaultState() : stateIn;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (Direction direction : UPDATE_ORDER)
        {
            if (worldIn.getBlockState(mutablePos.setAndMove(pos, direction)).isIn(TFCTags.Blocks.CREEPING_PLANTABLE_ON))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (!isValidPosition(state, worldIn, pos))
        {
            worldIn.destroyBlock(pos, false);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return shapeCache.get(state);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST));
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return updateStateFromSides(context.getWorld(), context.getPos(), updateStateWithCurrentMonth(getDefaultState()));
    }

    private BlockState updateStateFromSides(IWorld world, BlockPos pos, BlockState state)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        boolean hasEarth = false;
        for (Direction direction : UPDATE_ORDER)
        {
            mutablePos.setAndMove(pos, direction);
            boolean ground = world.getBlockState(mutablePos).isIn(TFCTags.Blocks.CREEPING_PLANTABLE_ON);

            state = state.with(SixWayBlock.FACING_TO_PROPERTY_MAP.get(direction), ground);
            hasEarth |= ground;
        }
        return hasEarth ? state : Blocks.AIR.getDefaultState();
    }

    private boolean isEmpty(BlockState state)
    {
        for (BooleanProperty property : SHAPES_BY_PROPERTY.keySet())
        {
            if (state.get(property))
            {
                return false;
            }
        }
        return true;
    }
}
