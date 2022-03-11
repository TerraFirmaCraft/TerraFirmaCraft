/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;

public class SpreadingBushBlock extends SeasonalPlantBlock implements IForgeBlockExtension, IBushBlock, HoeOverlayBlock
{
    protected final Supplier<? extends Block> companion;
    protected final int maxHeight;
    private final Supplier<ClimateRange> climateRange;

    public SpreadingBushBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages, Supplier<? extends Block> companion, int maxHeight, Supplier<ClimateRange> climateRange)
    {
        super(properties, productItem, stages);
        this.companion = companion;
        this.maxHeight = maxHeight;
        this.climateRange = climateRange;
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    @Override
    public void onUpdate(Level level, BlockPos pos, BlockState state)
    {
        level.getBlockEntity(pos, TFCBlockEntities.BERRY_BUSH.get()).ifPresent(bush -> {
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
                final int hydration = FarmlandBlock.getHydration(level, sourcePos);

                int stagesGrown = 0, monthsSpentDying = 0;
                do
                {
                    // This always runs at least once. It is called through random ticks, and calendar updates - although calendar updates will only call this if they've waited at least a day, or the average delta between random ticks.
                    // Otherwise it will just wait for the next random tick.

                    // Jump forward to nextTick.
                    // Advance both the stage (randomly, if the previous month was healthy), and lifecycle (if the at-the-time conditions were valid)
                    nextCalendarTick = Math.min(nextCalendarTick + Calendars.SERVER.getCalendarTicksInMonth(), currentCalendarTick);

                    if (currentLifecycle.active() && level.getRandom().nextInt(3) == 0)
                    {
                        stagesGrown++;
                    }

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

                if (monthsSpentDying > 0 && level.getRandom().nextInt(12) < monthsSpentDying && !level.getBlockState(pos.above()).is(TFCTags.Blocks.ANY_SPREADING_BUSH))
                {
                    // It may have died, as it spent too many consecutive months where it should've been healthy, in invalid conditions.
                    newState = getDeadState(state);
                }
                else
                {
                    // It's not dead! Now, perform the actual update over the time taken.
                    newState = state.setValue(STAGE, Math.min(2, state.getValue(STAGE) + stagesGrown))
                        .setValue(LIFECYCLE, currentLifecycle);

                    // Finally, possibly, cause a propagation event - this is based on the current time.
                    if (newState.getValue(LIFECYCLE).active() && level.getRandom().nextInt(3) == 0 && distanceToGround(level, pos, maxHeight) <= maxHeight)
                    {
                        propagate(level, pos, level.getRandom(), newState);
                    }
                }

                // And update the block
                if (state != newState)
                {
                    level.setBlock(pos, newState, 3);
                }
            }
            bush.afterUpdate();
        });
    }

    protected BlockState getDeadState(BlockState state)
    {
        return TFCBlocks.DEAD_BERRY_BUSH.get().defaultBlockState().setValue(STAGE, state.getValue(STAGE));
    }

    protected void propagate(Level level, BlockPos pos, Random random, BlockState state)
    {
        final int stage = state.getValue(STAGE);
        final BlockPos abovePos = pos.above();
        if (stage == 1 && level.isEmptyBlock(abovePos))
        {
            level.setBlockAndUpdate(abovePos, state.setValue(STAGE, 1));
        }
        else if (stage == 2)
        {
            Direction offset = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos offsetPos = pos.relative(offset);
            if (level.isEmptyBlock(offsetPos))
            {
                level.setBlockAndUpdate(offsetPos, companion.get().defaultBlockState().setValue(SpreadingCaneBlock.FACING, offset));
                level.getBlockEntity(offsetPos, TFCBlockEntities.BERRY_BUSH.get()).ifPresent(bush -> bush.reduceCounter(-1 * ICalendar.TICKS_IN_DAY * bush.getTicksSinceUpdate()));
            }
        }
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, List<Component> text, boolean isDebug)
    {
        final BlockPos sourcePos = pos.below();
        final ClimateRange range = climateRange.get();

        text.add(FarmlandBlock.getHydrationTooltip(level, sourcePos, range, false));
        text.add(FarmlandBlock.getTemperatureTooltip(level, sourcePos, range, false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return Helpers.isBlock(belowState, TFCTags.Blocks.BUSH_PLANTABLE_ON) || Helpers.isBlock(belowState, TFCTags.Blocks.ANY_SPREADING_BUSH) || this.mayPlaceOn(level.getBlockState(belowPos), level, belowPos);
    }
}
