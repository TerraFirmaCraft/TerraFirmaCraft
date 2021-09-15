/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Map;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;

public class VerticalSupportBlock extends Block implements IForgeBlockExtension
{
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream()
        .filter(facing -> facing.getKey().getAxis().isHorizontal()).collect(Util.toMap());

    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;

    private final Map<BlockState, VoxelShape> cachedShapes;
    private final ExtendedProperties properties;

    public VerticalSupportBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
        this.cachedShapes = makeShapes(box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D), getStateDefinition().getPossibleStates());

        registerDefaultState(getStateDefinition().any().setValue(NORTH, false).setValue(EAST, false).setValue(WEST, false).setValue(SOUTH, false));
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            mutablePos.setWithOffset(context.getClickedPos(), d);
            state = state.setValue(PROPERTY_BY_DIRECTION.get(d), context.getLevel().getBlockState(mutablePos).is(TFCTags.Blocks.SUPPORT_BEAM));
        }
        return state;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (worldIn.isClientSide() || placer == null) return;
        if (stack.getCount() > 2 && !placer.isShiftKeyDown()) // need two because the item block hasn't shrunk the stack yet
        {
            BlockPos above = pos.above();
            BlockPos above2 = above.above();
            if (worldIn.isEmptyBlock(above) && worldIn.isEmptyBlock(above2))
            {
                if (worldIn.getEntities(null, new AABB(above)).isEmpty())
                {
                    worldIn.setBlock(above, defaultBlockState(), 2);
                    if (worldIn.getEntities(null, new AABB(above2)).isEmpty())
                    {
                        worldIn.setBlock(above2, defaultBlockState(), 2);
                        stack.shrink(2);
                    }
                    else
                    {
                        stack.shrink(1);
                    }
                }
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getAxis().isHorizontal())
        {
            stateIn = stateIn.setValue(PROPERTY_BY_DIRECTION.get(facing), facingState.is(TFCTags.Blocks.SUPPORT_BEAM));
        }
        else if (facing == Direction.DOWN)
        {
            if (facingState.is(TFCTags.Blocks.SUPPORT_BEAM) || facingState.isFaceSturdy(world, facingPos, Direction.UP, SupportType.CENTER))
            {
                return stateIn;
            }
            return Blocks.AIR.defaultBlockState();
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = worldIn.getBlockState(belowPos);
        return belowState.is(TFCTags.Blocks.SUPPORT_BEAM) || belowState.isFaceSturdy(worldIn, belowPos, Direction.UP, SupportType.CENTER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape shape = cachedShapes.get(state);
        if (shape != null) return shape;
        throw new IllegalArgumentException("Asked for Support VoxelShape that was not cached");
    }

    protected Map<BlockState, VoxelShape> makeShapes(VoxelShape middleShape, ImmutableList<BlockState> possibleStates)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (BlockState state : possibleStates)
        {
            VoxelShape shape = middleShape;
            for (Direction d : Direction.Plane.HORIZONTAL)
            {
                if (state.getValue(PROPERTY_BY_DIRECTION.get(d)))
                {
                    VoxelShape joinShape = Shapes.empty();
                    switch (d)
                    {
                        case NORTH:
                            joinShape = box(5.0D, 10.0D, 0.0D, 11.0D, 16.0D, 10.0D);
                            break;
                        case SOUTH:
                            joinShape = box(5.0D, 10.0D, 11.0D, 11.0D, 16.0D, 16.0D);
                            break;
                        case EAST:
                            joinShape = box(11.0D, 10.0D, 5.0D, 16.0D, 16.0D, 11.0D);
                            break;
                        case WEST:
                            joinShape = box(0.0D, 10.0D, 5.0D, 5.0D, 16.0D, 11.0D);
                            break;
                    }
                    shape = Shapes.or(shape, joinShape);
                }
            }
            builder.put(state, shape);
        }
        return builder.build();
    }
}
