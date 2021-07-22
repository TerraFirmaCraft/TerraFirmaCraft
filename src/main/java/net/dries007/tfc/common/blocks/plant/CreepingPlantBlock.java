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

    protected static final VoxelShape UP_SHAPE = box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape DOWN_SHAPE = box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
    protected static final VoxelShape EAST_SHAPE = box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);

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

    protected final Map<BlockState, VoxelShape> shapeCache;

    protected CreepingPlantBlock(Properties properties)
    {
        super(properties);

        shapeCache = getStateDefinition().getPossibleStates().stream().collect(Collectors.toMap(state -> state, state -> SHAPES_BY_PROPERTY.entrySet().stream().filter(entry -> state.getValue(entry.getKey())).map(Map.Entry::getValue).reduce(VoxelShapes::or).orElseGet(VoxelShapes::empty)));

        registerDefaultState(defaultBlockState().setValue(UP, false).setValue(DOWN, false).setValue(EAST, false).setValue(WEST, false).setValue(NORTH, false).setValue(SOUTH, false));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction direction, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        stateIn = stateIn.setValue(SixWayBlock.PROPERTY_BY_DIRECTION.get(direction), facingState.is(TFCTags.Blocks.CREEPING_PLANTABLE_ON));
        return isEmpty(stateIn) ? Blocks.AIR.defaultBlockState() : stateIn;
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (Direction direction : UPDATE_SHAPE_ORDER)
        {
            if (worldIn.getBlockState(mutablePos.setWithOffset(pos, direction)).is(TFCTags.Blocks.CREEPING_PLANTABLE_ON))
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
        if (!canSurvive(state, worldIn, pos))
        {
            worldIn.destroyBlock(pos, false);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return shapeCache.get(state);
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return updateStateFromSides(context.getLevel(), context.getClickedPos(), updateStateWithCurrentMonth(defaultBlockState()));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST));
    }

    private BlockState updateStateFromSides(IWorld world, BlockPos pos, BlockState state)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        boolean hasEarth = false;
        for (Direction direction : UPDATE_SHAPE_ORDER)
        {
            mutablePos.setWithOffset(pos, direction);
            boolean ground = world.getBlockState(mutablePos).is(TFCTags.Blocks.CREEPING_PLANTABLE_ON);

            state = state.setValue(SixWayBlock.PROPERTY_BY_DIRECTION.get(direction), ground);
            hasEarth |= ground;
        }
        return hasEarth ? state : Blocks.AIR.defaultBlockState();
    }

    private boolean isEmpty(BlockState state)
    {
        for (BooleanProperty property : SHAPES_BY_PROPERTY.keySet())
        {
            if (state.getValue(property))
            {
                return false;
            }
        }
        return true;
    }
}
