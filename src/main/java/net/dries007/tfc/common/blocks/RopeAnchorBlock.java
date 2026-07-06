/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.rock.RockSpikeBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;

/**
 * Common behaviour for any block that ropes can be anchored to. Subclasses decide what the block becomes once its rope
 * has been recalled - see {@link #getStateAfterRemoval(BlockState)}. Every place that recognises an anchor (rope
 * attachment in {@link GroundedRopeBlock}, the knot in {@code RopeKnot}, and {@code RopeItem}) checks against this type,
 * so any subclass works as a valid anchor automatically.
 */
public abstract class RopeAnchorBlock extends AbstractRopeBlock implements IFluidLoggable
{
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER_AND_LAVA;

    public RopeAnchorBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(FLUID, FLUID.keyFor(Fluids.EMPTY)));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        if (!canSurvive(defaultBlockState(), context.getLevel(), context.getClickedPos()))
        {
            return null;
        }
        final FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        final BlockState state = defaultBlockState();
        if (getFluidProperty().canContain(fluidState.getType()))
        {
            return state.setValue(getFluidProperty(), getFluidProperty().keyFor(fluidState.getType()));
        }
        return state;
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
    protected boolean isRandomlyTicking(BlockState state)
    {
        return state.getFluidState().isRandomlyTicking();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        state.getFluidState().randomTick(level, pos, random);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FLUID));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos, Direction.UP, SupportType.CENTER);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        FluidHelpers.tickFluid(level, pos, state);
        if (direction == Direction.DOWN && !neighborState.isFaceSturdy(level, pos, Direction.UP, SupportType.CENTER))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        removeRope(level, pos, state, player);
        return InteractionResult.SUCCESS;
    }

    /**
     * Recalls any rope hanging from this anchor (returning it to the player) and reverts the anchor to its un-roped
     * form. Also used to cancel an in-progress throw, where there is no rope yet, so the recall is a no-op.
     */
    public void removeRope(Level level, BlockPos pos, BlockState state, Player player)
    {
        recallRope(level, pos, state, player, state.getValue(FACING));
        level.setBlockAndUpdate(pos, getStateAfterRemoval(state));
    }

    /**
     * @return the block state this anchor becomes once its rope has been recalled. A rock anchor turns back into its
     * spike; a freestanding (crafted) anchor stays in place with no rope attached.
     */
    protected abstract BlockState getStateAfterRemoval(BlockState state);

    public boolean isRopeAttached(LevelReader level, BlockPos pos, BlockState state)
    {
        final Direction facing = state.getValue(FACING);
        return isAttachedRope(level.getBlockState(pos.below().relative(facing)), facing)
            || isAttachedRope(level.getBlockState(pos.relative(facing)), facing);
    }

    private static boolean isAttachedRope(BlockState state, Direction anchorFacing)
    {
        return (state.getBlock() instanceof GroundedRopeBlock || state.getBlock() instanceof HangingRopeBlock)
            && state.getValue(FACING) == anchorFacing.getOpposite();
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        final BlockPos possibleRopePos = pos.below().relative(state.getValue(FACING));
        if (level.getBlockState(possibleRopePos).getBlock() instanceof AbstractRopeBlock)
        {
            level.destroyBlock(possibleRopePos, true);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return RockSpikeBlock.TIP_SHAPE;
    }
}
