/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.util.Helpers;


public class RockSpikeBlock extends Block implements IFluidLoggable, IFallableBlock
{
    public static final EnumProperty<Part> PART = TFCBlockStateProperties.ROCK_SPIKE_PART;
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER_AND_LAVA;

    public static final VoxelShape BASE_SHAPE = box(2, 0, 2, 14, 16, 14);
    public static final VoxelShape MIDDLE_SHAPE = box(4, 0, 4, 12, 16, 12);
    public static final VoxelShape TIP_SHAPE = box(6, 0, 6, 10, 16, 10);

    public RockSpikeBlock(Properties properties)
    {
        super(properties);

        registerDefaultState(stateDefinition.any().setValue(PART, Part.BASE).setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        level.scheduleTick(pos, this, 1);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(PART))
            {
                case BASE -> BASE_SHAPE;
                case MIDDLE -> MIDDLE_SHAPE;
                default -> TIP_SHAPE;
            };
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        checkForPossibleCollapse(state, level, pos, true);
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
    public void onceFinishedFalling(Level level, BlockPos pos, FallingBlockEntity fallingBlock)
    {
        // Don't play the break sound, so don't call destroyBlock()
        level.setBlock(pos, level.getBlockState(pos).getFluidState().createLegacyBlock(), 3);
        Helpers.playSound(level, pos, TFCSounds.ROCK_SMASH.get());
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final BlockState state = super.getStateForPlacement(context);
        final FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        if (state != null && !fluidState.isEmpty())
        {
            return state.setValue(getFluidProperty(), getFluidProperty().keyForOrEmpty(fluidState.getType()));
        }
        return state;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        return false;
    }

    @Override
    public PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob)
    {
        return PathType.BLOCKED;
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PART, getFluidProperty());
    }

    /**
     * @param checkAbove If true, we ignore any possible support from above, as we know a previous invocation of this method just started to collapse the above block. However, due to the way falling blocks work, the spike will only be removed in the first tick of the falling block entity's tick() method.
     */
    private void checkForPossibleCollapse(BlockState state, ServerLevel level, BlockPos pos, boolean checkAbove)
    {
        // Check support from above or below
        final BlockPos belowPos = pos.below();
        final BlockState belowState = level.getBlockState(belowPos);
        if (belowState.getBlock() instanceof RockSpikeBlock && belowState.getValue(PART).isLargerThan(state.getValue(PART)))
        {
            // Larger spike below. Tick that to ensure it is supported
            level.scheduleTick(belowPos, this, 1);
            return;
        }
        else if (belowState.isFaceSturdy(level, belowPos, Direction.UP))
        {
            // Full block below, this is supported
            return;
        }

        if (checkAbove)
        {
            // No support below, try above
            final BlockPos abovePos = pos.above();
            final BlockState aboveState = level.getBlockState(abovePos);
            if (aboveState.getBlock() instanceof RockSpikeBlock && aboveState.getValue(PART).isLargerThan(state.getValue(PART)))
            {
                // Larger spike above. Tick to ensure that it is supported
                level.scheduleTick(abovePos, this, 1);
                return;
            }
            else if (aboveState.isFaceSturdy(level, abovePos, Direction.DOWN))
            {
                // Full block above, this is supported
                return;
            }
        }

        // No support, so either collapse, or break
        if (CollapseRecipe.collapseBlock(level, pos, state))
        {
            // Additionally, run a tick on the block below, on the exact same tick.
            // This ensures the whole spike collapses at the same time, rather than the upper parts destroying the bottom parts.
            if (belowState.getBlock() instanceof RockSpikeBlock)
            {
                checkForPossibleCollapse(belowState, level, belowPos, false);
            }
        }
        else
        {
            level.destroyBlock(pos, true);
        }
    }

    public enum Part implements StringRepresentable
    {
        BASE, MIDDLE, TIP;

        private final String serializedName;

        Part()
        {
            serializedName = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }

        public boolean isLargerThan(Part other)
        {
            return this.ordinal() <= other.ordinal();
        }
    }
}