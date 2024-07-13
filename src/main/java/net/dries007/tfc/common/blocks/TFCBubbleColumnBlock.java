/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;

public class TFCBubbleColumnBlock extends BubbleColumnBlock
{
    /**
     * Like {@link BubbleColumnBlock#updateColumn(LevelAccessor, BlockPos, BlockState)} but will take into account the fluid type.
     */
    public static void updateColumnForFluid(LevelAccessor level, BlockPos pos)
    {
        final BlockPos abovePos = pos.above();
        final BlockState aboveState = level.getBlockState(abovePos);
        final BlockPos.MutableBlockPos cursor = abovePos.mutable();

        BlockState beforeState = aboveState;
        while (canExistIn(beforeState))
        {
            if (!level.setBlock(cursor, getColumnState(beforeState), Block.UPDATE_CLIENTS))
            {
                return;
            }

            cursor.move(Direction.UP);
            beforeState = level.getBlockState(cursor);
        }

    }

    public static boolean canExistIn(BlockState state)
    {
        return state.getBlock() instanceof TFCBubbleColumnBlock
            || (FluidHelpers.isAirOrEmptyFluid(state) && canExistIn(state.getFluidState().getType()));
    }

    public static boolean canExistIn(Fluid fluid)
    {
        return fluid == Fluids.WATER.getSource() || fluid == TFCFluids.SALT_WATER.getSource();
    }

    public static BlockState getColumnState(BlockState state)
    {
        return (state.getFluidState().getType() == Fluids.WATER.getSource() ? TFCBlocks.FRESHWATER_BUBBLE_COLUMN.get() : TFCBlocks.SALTWATER_BUBBLE_COLUMN.get()).defaultBlockState();
    }

    private final Supplier<? extends Fluid> fluid;

    public TFCBubbleColumnBlock(Properties properties, Supplier<? extends Fluid> fluid)
    {
        super(properties);
        this.fluid = fluid;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        // Override to add the entity immune check for all our aquatic animals, who want to ignore these
        if (!Helpers.isEntity(entity, TFCTags.Entities.BUBBLE_COLUMN_IMMUNE))
        {
            super.entityInside(state, level, pos, entity);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        updateColumnForFluid(level, pos);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return getFluid().defaultFluidState();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        // Our bubble columns have particles which rise, but current which falls down
        // This just causes us to animate as if we were vanilla 'up' bubble column
        super.animateTick(state.setValue(DRAG_DOWN, false), level, pos, random);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        // Modified from the vanilla one in order to use the fluid, not just water
        // Otherwise is identical
        final Fluid fluid = getFluid();

        level.scheduleTick(pos, fluid, fluid.getTickDelay(level));
        if (!state.canSurvive(level, pos) ||
            facing == Direction.DOWN ||
            (facing == Direction.UP && !(facingState.getBlock() instanceof TFCBubbleColumnBlock) && canExistIn(facingState)))
        {
            level.scheduleTick(pos, this, 5);
        }

        return state; // Don't call into the vanilla logic
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockState below = level.getBlockState(pos.below());
        return below.getBlock() instanceof TFCBubbleColumnBlock
            || below.getBlock() instanceof TFCMagmaBlock;
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state)
    {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        return new ItemStack(getFluid().getBucket());
    }

    public Fluid getFluid()
    {
        return fluid.get();
    }
}
