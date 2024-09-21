/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Map;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.HorizontalPipeBlock;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.util.Helpers;

public class VerticalSupportBlock extends Block implements IForgeBlockExtension, IFluidLoggable, HorizontalPipeBlock
{
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER;

    private final Map<BlockState, VoxelShape> cachedShapes;
    private final ExtendedProperties properties;

    public VerticalSupportBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
        this.cachedShapes = makeShapes(box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D), getStateDefinition().getPossibleStates());

        registerDefaultState(getStateDefinition().any().setValue(NORTH, false).setValue(EAST, false).setValue(WEST, false).setValue(SOUTH, false).setValue(FLUID, FLUID.keyFor(Fluids.EMPTY)));
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
            state = state.setValue(PROPERTY_BY_DIRECTION.get(d), Helpers.isBlock(context.getLevel().getBlockState(mutablePos), TFCTags.Blocks.SUPPORT_BEAMS));
        }
        final FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        state = state.setValue(getFluidProperty(), getFluidProperty().keyForOrEmpty(fluid.getType()));
        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (level.isClientSide() || placer == null) return;
        if (stack.getCount() > 2 && !placer.isShiftKeyDown()) // need two because the item block hasn't shrunk the unsealedStack yet
        {
            final BlockPos above = pos.above(), above2 = above.above();
            final BlockState stateAbove = level.getBlockState(above), stateAbove2 = level.getBlockState(above2);
            final Fluid fluidAbove = stateAbove.getFluidState().getType(), fluidAbove2 = stateAbove2.getFluidState().getType();
            if (isEmptyOrValidFluid(stateAbove) && isEmptyOrValidFluid(stateAbove2))
            {
                if (level.getEntities(null, new AABB(above)).isEmpty())
                {
                    level.setBlock(above, defaultBlockState().setValue(getFluidProperty(), getFluidProperty().keyForOrEmpty(fluidAbove)), 2);
                    if (level.getEntities(null, new AABB(above2)).isEmpty())
                    {
                        level.setBlock(above2, defaultBlockState().setValue(getFluidProperty(), getFluidProperty().keyForOrEmpty(fluidAbove2)), 2);
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
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        if (facing.getAxis().isHorizontal())
        {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(facing), Helpers.isBlock(facingState, TFCTags.Blocks.SUPPORT_BEAMS));
        }
        else if (facing == Direction.DOWN)
        {
            if (Helpers.isBlock(facingState, TFCTags.Blocks.SUPPORT_BEAMS) || facingState.isFaceSturdy(level, facingPos, Direction.UP, SupportType.CENTER))
            {
                return state;
            }
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return Helpers.isBlock(belowState, TFCTags.Blocks.SUPPORT_BEAMS) || belowState.isFaceSturdy(level, belowPos, Direction.UP, SupportType.CENTER);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        VoxelShape shape = cachedShapes.get(state);
        if (shape != null) return shape;
        throw new IllegalArgumentException("Asked for Support VoxelShape that was not cached");
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(NORTH, EAST, SOUTH, WEST, FLUID));
    }

    protected Map<BlockState, VoxelShape> makeShapes(VoxelShape middleShape, ImmutableList<BlockState> possibleStates)
    {
        final ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (BlockState state : possibleStates)
        {
            VoxelShape shape = middleShape;
            for (Direction d : Direction.Plane.HORIZONTAL)
            {
                if (state.getValue(PROPERTY_BY_DIRECTION.get(d)))
                {
                    VoxelShape joinShape = switch (d)
                        {
                            case NORTH -> box(5.0D, 10.0D, 0.0D, 11.0D, 16.0D, 10.0D);
                            case SOUTH -> box(5.0D, 10.0D, 11.0D, 11.0D, 16.0D, 16.0D);
                            case EAST -> box(11.0D, 10.0D, 5.0D, 16.0D, 16.0D, 11.0D);
                            case WEST -> box(0.0D, 10.0D, 5.0D, 5.0D, 16.0D, 11.0D);
                            default -> Shapes.empty();
                        };
                    shape = Shapes.or(shape, joinShape);
                }
            }
            builder.put(state, shape);
        }
        return builder.build();
    }

    protected boolean isEmptyOrValidFluid(BlockState state)
    {
        return FluidHelpers.isAirOrEmptyFluid(state) && getFluidProperty().canContain(state.getFluidState().getType());
    }
}
