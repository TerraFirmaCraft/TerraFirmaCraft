/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;

public class StationaryBerryBushBlock extends SeasonalPlantBlock implements HoeOverlayBlock, IBushBlock
{
    private static final VoxelShape HALF_PLANT = box(2, 0, 2, 14, 8, 14);

    /**
     * Any bush that spends four consecutive months dormant when it shouldn't be, should die.
     * Since most bushes have a 7 month non-dormant cycle, this means that it just needs to be in valid conditions for about 1 month a year in order to not die.
     * It won't produce (it needs more months to properly advance the cycle from dormant -> healthy -> flowering -> fruiting, requiring 4 months at least), but it won't outright die.
     */
    private static final int MONTHS_SPENT_DORMANT_TO_DIE = 4;

    public StationaryBerryBushBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] lifecycle, Supplier<ClimateRange> climateRange)
    {
        super(properties, climateRange, productItem, lifecycle);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(LIFECYCLE, getLifecycleForCurrentMonth().active() ? Lifecycle.HEALTHY : Lifecycle.DORMANT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return HALF_PLANT;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        IBushBlock.randomTick(this, state, level, pos, random);
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, Consumer<Component> text, boolean isDebug)
    {
        final BlockPos sourcePos = pos.below();
        final ClimateRange range = climateRange.get();

        text.accept(FarmlandBlock.getHydrationTooltip(level, sourcePos, range, false));
        text.accept(FarmlandBlock.getTemperatureTooltip(level, sourcePos, range, false));
    }

    @Override
    public void onUpdate(Level level, BlockPos pos, BlockState state)
    {
        if (level.getBlockEntity(pos) instanceof BerryBushBlockEntity bush)
        {
            Lifecycle currentLifecycle = state.getValue(LIFECYCLE);
            Lifecycle expectedLifecycle = getLifecycleForCurrentMonth();
            // if we are not working with a plant that is or should be dormant
            if (!checkAndSetDormant(level, pos, state, currentLifecycle, expectedLifecycle))
            {
                // Otherwise, we do a month-by-month evaluation of how the bush should have grown.
                // We only do this up to a year. Why? Because eventually, it will have become dormant, and any 'progress' during that year would've been lost anyway because it would unconditionally become dormant.
                long deltaTicks = Math.min(bush.getTicksSinceBushUpdate(), Calendars.SERVER.getCalendarTicksInYear());
                long currentCalendarTick = Calendars.SERVER.getCalendarTicks();
                long nextCalendarTick = currentCalendarTick - deltaTicks;

                final BlockPos sourcePos = pos.below();
                final ClimateRange range = climateRange.get();
                final int hydration = getHydration(level, sourcePos, state);

                int monthsSpentDying = 0;
                do
                {
                    // This always runs at least once. It is called through random ticks, and calendar updates - although calendar updates will only call this if they've waited at least a day, or the average delta between random ticks.
                    // Otherwise it will just wait for the next random tick.

                    // Jump forward to nextTick.
                    // Advance both the stage (randomly, if the previous month was healthy), and lifecycle (if the at-the-time conditions were valid)
                    nextCalendarTick = Math.min(nextCalendarTick + Calendars.SERVER.getCalendarTicksInMonth(), currentCalendarTick);


                    float temperatureAtNextTick = Climate.getTemperature(level, pos, nextCalendarTick, Calendars.SERVER.getCalendarDaysInMonth());
                    Lifecycle lifecycleAtNextTick = getLifecycleForMonth(ICalendar.getMonthOfYear(nextCalendarTick, Calendars.SERVER.getCalendarDaysInMonth()));
                    if (range.checkBoth(hydration, temperatureAtNextTick, false))
                    {
                        currentLifecycle = currentLifecycle.advanceTowards(lifecycleAtNextTick);
                    }
                    else
                    {
                        currentLifecycle = Lifecycle.DORMANT;
                    }

                    if (lifecycleAtNextTick != Lifecycle.DORMANT && currentLifecycle == Lifecycle.DORMANT)
                    {
                        monthsSpentDying++; // consecutive months spent where the conditions were invalid, but they shouldn't've been
                    }
                    else
                    {
                        monthsSpentDying = 0;
                    }

                } while (nextCalendarTick < currentCalendarTick);

                BlockState newState;

                if (mayDie(level, pos, state, monthsSpentDying))
                {
                    newState = getDeadState(state);
                }
                else
                {
                    // It's not dead! Now, perform the actual update over the time taken.
                    newState = growAndPropagate(level, pos, level.getRandom(), state.setValue(LIFECYCLE, currentLifecycle));
                }

                // And update the block
                if (state != newState)
                {
                    level.setBlock(pos, newState, 3);
                }
            }
        }
    }

    protected int getHydration(LevelAccessor level, BlockPos pos, BlockState state)
    {
        return FarmlandBlock.getHydration(level, pos);
    }

    /**
     * Can this bush die, given that it spent {@code monthsSpentDying} consecutive months in a dormant state, when it should've been in a non-dormant state.
     */
    protected boolean mayDie(Level level, BlockPos pos, BlockState state, int monthsSpentDying)
    {
        return monthsSpentDying >= MONTHS_SPENT_DORMANT_TO_DIE;
    }

    protected BlockState getNewState(Level level, BlockPos pos)
    {
        return defaultBlockState().setValue(STAGE, 0).setValue(LIFECYCLE, Lifecycle.HEALTHY);
    }

    protected boolean canPlaceNewBushAt(Level level, BlockPos pos, BlockState placementState)
    {
        return level.isEmptyBlock(pos) && placementState.canSurvive(level, pos);
    }

    protected BlockState getDeadState(BlockState state)
    {
        return TFCBlocks.DEAD_BERRY_BUSH.get().defaultBlockState().setValue(STAGE, state.getValue(STAGE));
    }

    /**
     * Performs growth and (optional) propagation of the bush.
     * Propagation should be naturally limited to not cause runaway generation.
     * @return The new state of the bush at {@code pos}. This will be set by the caller.
     */
    protected BlockState growAndPropagate(Level level, BlockPos pos, RandomSource random, BlockState state)
    {
        if (state.getValue(LIFECYCLE).active())
        {
            return state; // Only grow when active
        }

        // Increment stage by one
        final BlockState newState = state.setValue(STAGE, Math.min(2, state.getValue(STAGE) + 1));

        // Conditions:
        // 1. Must be in max growth stage, and an active lifecycle
        // 2. Must not have more than 3 other bushes within the expansion radius
        if (newState.getValue(STAGE) != 2)
        {
            return newState;
        }

        int count = 0;
        for (BlockPos target : BlockPos.betweenClosed(pos.offset(-2, -1, -2), pos.offset(2, 1, 2)))
        {
            if (level.getBlockState(target).getBlock() == this)
            {
                count++;
                if (count > 3)
                {
                    return newState;
                }
            }
        }

        // Then, try and pick a random position within the expansion radius, and place a bush there.
        final BlockPos.MutableBlockPos cursor = pos.mutable();
        for (int tries = 0; tries < 6; tries++)
        {
            cursor.setWithOffset(pos, Helpers.triangle(random, 3), Helpers.triangle(random, 2), Helpers.triangle(random, 3));
            final BlockState placementState = getNewState(level, cursor);
            if (canPlaceNewBushAt(level, pos, placementState))
            {
                level.setBlockAndUpdate(cursor, placementState);
                return newState;
            }
        }
        return newState;
    }
}
