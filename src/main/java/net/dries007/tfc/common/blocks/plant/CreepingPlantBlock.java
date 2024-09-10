/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class CreepingPlantBlock extends PlantBlock implements DirectionPropertyBlock
{
    protected static final VoxelShape UP_SHAPE = box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape DOWN_SHAPE = box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
    protected static final VoxelShape EAST_SHAPE = box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);

    protected static final Map<BooleanProperty, VoxelShape> SHAPES = ImmutableMap.<BooleanProperty, VoxelShape>builder()
        .put(UP, UP_SHAPE)
        .put(DOWN, DOWN_SHAPE)
        .put(NORTH, NORTH_SHAPE)
        .put(SOUTH, SOUTH_SHAPE)
        .put(EAST, EAST_SHAPE)
        .put(WEST, WEST_SHAPE)
        .build();

    public static CreepingPlantBlock create(RegistryPlant plant, ExtendedProperties properties)
    {
        return new CreepingPlantBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }
        };
    }

    public static CreepingPlantBlock createStone(RegistryPlant plant, ExtendedProperties properties)
    {
        return new CreepingPlantBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }

            @Override
            public boolean canCreepOn(LevelReader level, BlockPos pos, BlockState state, Direction direction)
            {
                return Helpers.isBlock(state, TFCTags.Blocks.CREEPING_STONE_PLANTABLE_ON) && super.canCreepOn(level, pos, state, direction);
            }
        };
    }

    protected final Map<BlockState, VoxelShape> shapeCache;

    protected CreepingPlantBlock(ExtendedProperties properties)
    {
        super(properties);

        registerDefaultState(DirectionPropertyBlock.setAllDirections(getStateDefinition().any(), false));
        shapeCache = DirectionPropertyBlock.makeShapeCache(getStateDefinition(), SHAPES::get);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        super.randomTick(state, level, pos, random);
        if (PlantRegrowth.canSpread(level, random))
        {
            Direction direction = Direction.getRandom(random);
            if (direction == Direction.DOWN)
                direction = Direction.UP;
            final BlockPos newPos = random.nextFloat() < 0.2f ? pos.relative(direction).above() : pos.relative(direction);
            if (level.getBlockState(newPos).isAir())
            {
                final BlockState newState = updateStateFromSides(level, newPos, state);
                if (!newState.isAir())
                {
                    level.setBlockAndUpdate(newPos, newState);
                }
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), canCreepOn(level, facingPos, facingState, direction));
        return isEmptyContents(state) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : UPDATE_SHAPE_ORDER)
        {
            mutablePos.setWithOffset(pos, direction);
            if (canCreepOn(level, mutablePos, level.getBlockState(mutablePos), direction))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
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
        return updateStateFromSides(context.getLevel(), context.getClickedPos(), defaultBlockState());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST));
    }

    public static BlockState updateStateFromSides(LevelAccessor level, BlockPos pos, BlockState state)
    {
        final CreepingPlantBlock block = (CreepingPlantBlock) state.getBlock();
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        boolean hasEarth = false;
        for (Direction direction : UPDATE_SHAPE_ORDER)
        {
            mutablePos.setWithOffset(pos, direction);
            boolean ground = block.canCreepOn(level, pos, level.getBlockState(mutablePos), direction);

            state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), ground);
            hasEarth |= ground;
        }
        return hasEarth ? state : Blocks.AIR.defaultBlockState();
    }

    private static boolean isEmptyContents(BlockState state)
    {
        for (BooleanProperty property : SHAPES.keySet())
        {
            if (state.getValue(property))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return DirectionPropertyBlock.rotate(state, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return DirectionPropertyBlock.mirror(state, mirror);
    }

    public boolean canCreepOn(LevelReader level, BlockPos pos, BlockState state, Direction direction)
    {
        return state.isFaceSturdy(level, pos, direction.getOpposite());
    }
}
