/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class TFCBubbleColumnBlock extends BubbleColumnBlock
{
    public static void updateColumnForFluid(LevelAccessor level, BlockPos pos, BlockState state, Fluid fluid)
    {
        updateColumnForFluid(level, pos, level.getBlockState(pos), state, fluid);
    }

    public static void updateColumnForFluid(LevelAccessor level, BlockPos pos, BlockState aboveState, BlockState belowState, Fluid fluid)
    {
        if (fluid.isSame(Fluids.EMPTY))
        {
            return;
        }
        if (canExistIn(aboveState, fluid))
        {
            BlockState blockstate = getColumnState(belowState, fluid);
            level.setBlock(pos, blockstate, 2);
            BlockPos.MutableBlockPos mutable = pos.mutable().move(Direction.UP);

            while (canExistIn(level.getBlockState(mutable), fluid))
            {
                if (!level.setBlock(mutable, blockstate, 2))
                {
                    return;
                }

                mutable.move(Direction.UP);
            }

        }
    }

    private static BlockState getColumnState(BlockState state, Fluid fluid)
    {
        BlockState toPlace = fluid.isSame(Fluids.WATER) ? TFCBlocks.FRESHWATER_BUBBLE_COLUMN.get().defaultBlockState() : TFCBlocks.SALTWATER_BUBBLE_COLUMN.get().defaultBlockState();
        if (state.getBlock() instanceof BubbleColumnBlock)
        {
            return state;
        }
        else if (Helpers.isBlock(state, TFCTags.Blocks.CREATES_DOWNWARD_BUBBLES))
        {
            return toPlace.setValue(DRAG_DOWN, true);
        }
        else if (Helpers.isBlock(state, TFCTags.Blocks.CREATES_UPWARD_BUBBLES))
        {
            return toPlace.setValue(DRAG_DOWN, false);
        }
        return fluid.defaultFluidState().createLegacyBlock();
    }

    private static boolean canExistIn(BlockState state, Fluid fluid)
    {
        return state.getBlock() instanceof BubbleColumnBlock || state.getFluidState().getType().isSame(fluid) && state.getFluidState().getAmount() >= 8 && state.getFluidState().isSource();
    }

    private static final double MAX_DOWN_SPEED = -0.1; // vanilla: -0.3
    private static final double MAX_UP_SPEED = 0.3; // vanilla: 0.7

    private final Supplier<? extends Fluid> fluid;

    public TFCBubbleColumnBlock(Properties properties, Supplier<? extends Fluid> fluid)
    {
        super(properties);
        this.fluid = fluid;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (Helpers.isEntity(entity, TFCTags.Entities.BUBBLE_COLUMN_IMMUNE))
        {
            return;
        }
        final BlockState aboveState = level.getBlockState(pos.above());
        if (aboveState.isAir())
        {
            modifyEntityMovement(entity, state.getValue(DRAG_DOWN), true);
            if (level instanceof ServerLevel serverLevel)
            {
                for (int i = 0; i < 2; ++i)
                {
                    serverLevel.sendParticles(ParticleTypes.SPLASH, (double) pos.getX() + level.random.nextDouble(), pos.getY() + 1, (double) pos.getZ() + level.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    serverLevel.sendParticles(ParticleTypes.BUBBLE, (double) pos.getX() + level.random.nextDouble(), pos.getY() + 1, (double) pos.getZ() + level.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        }
        else
        {
            modifyEntityMovement(entity, state.getValue(DRAG_DOWN), false);
        }

    }

    /**
     * Combo of {@link Entity#onInsideBubbleColumn(boolean)} and {@link Entity#onAboveBubbleCol(boolean)}
     */
    private void modifyEntityMovement(Entity entity, boolean dragDown, boolean above)
    {
        final Vec3 movement = entity.getDeltaMovement();
        double dy;
        if (dragDown)
        {
            dy = Math.max(above ? -0.9 : MAX_DOWN_SPEED, movement.y - 0.03);
        }
        else
        {
            dy = above ? Math.min(1.8D, movement.y + 0.1D) : Math.min(MAX_UP_SPEED, movement.y + 0.06D);
        }
        entity.setDeltaMovement(movement.x, dy, movement.z);
        if (!above)
        {
            entity.resetFallDistance();
        }
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return getFluid().defaultFluidState();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        level.scheduleTick(pos, getFluid(), getFluid().getTickDelay(level));
        if (!state.canSurvive(level, pos) || facing == Direction.DOWN || facing == Direction.UP && !(facingState.getBlock() instanceof BubbleColumnBlock) && canExistIn(facingState, getFluid()))
        {
            level.scheduleTick(pos, this, 5);
        }

        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random p_50974_)
    {
        updateColumnForFluid(level, pos, state, level.getBlockState(pos.below()), getFluid());
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state)
    {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        return new ItemStack(getFluid().getBucket());
    }

    public Fluid getFluid()
    {
        return fluid.get();
    }
}
