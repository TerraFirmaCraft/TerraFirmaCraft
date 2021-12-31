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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class StationaryBerryBushBlock extends SeasonalPlantBlock implements HoeOverlayBlock, IBushBlock
{
    private static final VoxelShape HALF_PLANT = box(2, 0, 2, 14, 8, 14);

    private final Supplier<ClimateRange> climateRange; // todo: move this field to SeasonalPlantBlock

    public StationaryBerryBushBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] lifecycle, Supplier<ClimateRange> climateRange)
    {
        super(properties, productItem, lifecycle);

        this.climateRange = climateRange;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(LIFECYCLE, getLifecycleForCurrentMonth().active() ? Lifecycle.HEALTHY : Lifecycle.DORMANT);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        // Flowering bushes can be cut to create trimmings. This is how one moves or creates new bushes.
        // The larger the bush is (higher stage), the better chance you have of
        // 1. damaging it less (i.e. reducing the stage, or killing it), and
        // 2. making a clipping.
        if (state.getValue(LIFECYCLE) == Lifecycle.FLOWERING)
        {
            final ItemStack held = player.getItemInHand(hand);
            if (TFCTags.Items.BUSH_CUTTING_TOOLS.contains(held.getItem()))
            {
                level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 0.5f, 1.0f);
                if (!level.isClientSide())
                {
                    level.getBlockEntity(pos, TFCBlockEntities.BERRY_BUSH.get()).ifPresent(bush -> {
                        final int finalStage = state.getValue(STAGE) - 1 - level.getRandom().nextInt(2);
                        if (finalStage >= 0)
                        {
                            // We didn't kill the bush, but we have cut the flowers off
                            level.setBlock(pos, state.setValue(STAGE, finalStage).setValue(LIFECYCLE, Lifecycle.HEALTHY), 3);
                        }
                        else
                        {
                            // Oops
                            level.destroyBlock(pos, false, player);
                        }

                        held.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));

                        // But, if we were successful, we have obtained a clipping (2 / 3 chance)
                        if (level.getRandom().nextInt(3) != 0)
                        {
                            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(this));
                        }
                    });
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return HALF_PLANT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        // Target average delay between random ticks = one day.
        // Ticks are done at a rate of randomTickSpeed ticks / chunk section / world tick.
        final int rarity = Math.max(1, (int) (ICalendar.TICKS_IN_DAY * level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING) * (1 / 4096f)));
        if (random.nextInt(rarity) == 0)
        {
            onUpdate(level, pos, state);
        }
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, List<Component> text)
    {
        final BlockPos sourcePos = pos.below();
        final ClimateRange range = climateRange.get();

        text.add(FarmlandBlock.getHydrationTooltip(level, sourcePos, range, false));
        text.add(FarmlandBlock.getTemperatureTooltip(level, sourcePos, range, false));
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

                if (monthsSpentDying > 0 && level.getRandom().nextInt(6) < monthsSpentDying)
                {
                    // It may have died, as it spent too many consecutive months where it should've been healthy, in invalid conditions.
                    newState = TFCBlocks.DEAD_BERRY_BUSH.get().defaultBlockState();
                }
                else
                {
                    // It's not dead! Now, perform the actual update over the time taken.
                    newState = state.setValue(STAGE, Math.min(2, state.getValue(STAGE) + stagesGrown))
                        .setValue(LIFECYCLE, currentLifecycle);

                    // Finally, possibly, cause a propagation event - this is based on the current time.
                    if (newState.getValue(STAGE) == 2 && newState.getValue(LIFECYCLE).active() && level.getRandom().nextInt(3) == 0)
                    {
                        propagate(level, pos, level.getRandom());
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

    protected BlockState getNewState(Level level, BlockPos pos)
    {
        return defaultBlockState().setValue(STAGE, 0).setValue(LIFECYCLE, Lifecycle.HEALTHY);
    }

    protected boolean canPlaceNewBushAt(Level level, BlockPos pos, BlockState placementState)
    {
        return level.isEmptyBlock(pos) && placementState.canSurvive(level, pos);
    }

    private void propagate(Level level, BlockPos pos, Random random)
    {
        // Conditions:
        // 1. Must be in max growth stage, and an active lifecycle
        // 2. Must not have more than 3 other bushes within the expansion radius
        int count = 0;
        for (BlockPos target : BlockPos.betweenClosed(pos.offset(-2, -1, -2), pos.offset(2, 1, 2)))
        {
            if (level.getBlockState(target).getBlock() == this)
            {
                count++;
                if (count > 3)
                {
                    return;
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
                return;
            }
        }
    }
}
