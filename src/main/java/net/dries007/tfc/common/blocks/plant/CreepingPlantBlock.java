/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Map;
import java.util.stream.Collectors;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

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

    public static CreepingPlantBlock create(IPlant plant, ExtendedProperties properties)
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

    protected CreepingPlantBlock(ExtendedProperties properties)
    {
        super(properties);

        shapeCache = getStateDefinition().getPossibleStates().stream().collect(Collectors.toMap(state -> state, state -> SHAPES_BY_PROPERTY.entrySet().stream().filter(entry -> state.getValue(entry.getKey())).map(Map.Entry::getValue).reduce(Shapes::or).orElseGet(Shapes::empty)));

        registerDefaultState(defaultBlockState().setValue(UP, false).setValue(DOWN, false).setValue(EAST, false).setValue(WEST, false).setValue(NORTH, false).setValue(SOUTH, false));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), Helpers.isBlock(facingState, TFCTags.Blocks.CREEPING_PLANTABLE_ON));
        return isEmpty(state) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : UPDATE_SHAPE_ORDER)
        {
            if (Helpers.isBlock(level.getBlockState(mutablePos.setWithOffset(pos, direction)), TFCTags.Blocks.CREEPING_PLANTABLE_ON))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (!canSurvive(state, level, pos))
        {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return shapeCache.get(state);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return updateStateFromSides(context.getLevel(), context.getClickedPos(), updateStateWithCurrentMonth(defaultBlockState()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST));
    }

    private BlockState updateStateFromSides(LevelAccessor level, BlockPos pos, BlockState state)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        boolean hasEarth = false;
        for (Direction direction : UPDATE_SHAPE_ORDER)
        {
            mutablePos.setWithOffset(pos, direction);
            boolean ground = Helpers.isBlock(level.getBlockState(mutablePos), TFCTags.Blocks.CREEPING_PLANTABLE_ON);

            state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), ground);
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
