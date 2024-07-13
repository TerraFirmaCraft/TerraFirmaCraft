/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
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
import net.dries007.tfc.util.climate.ClimateRanges;

public class BananaPlantBlock extends SeasonalPlantBlock implements IBushBlock, HoeOverlayBlock
{
    public static void kill(Level level, BlockPos pos)
    {
        // picking bananas kills the plant. this propagates death to the whole stalk.
        Block deadBlock = TFCBlocks.DEAD_BANANA_PLANT.get();
        if (!level.isClientSide)
        {
            BlockState deadState = deadBlock.defaultBlockState();
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos().set(pos.below());
            while (true)
            {
                BlockState foundState = level.getBlockState(mutable);
                if (!foundState.is(TFCBlocks.BANANA_PLANT.get())) break;
                level.setBlockAndUpdate(mutable, deadState.setValue(STAGE, foundState.getValue(STAGE)));
                mutable.move(Direction.DOWN);
            }
        }
    }

    public static final VoxelShape PLANT = box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    private static final VoxelShape TRUNK_0 = box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    private static final VoxelShape TRUNK_1 = box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);

    public BananaPlantBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages)
    {
        super(properties, ClimateRanges.BANANA_PLANT, productItem, stages);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final ItemInteractionResult result =super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (result.consumesAction())
        {
            kill(level, pos);
        }
        return result;
    }

    @Override
    public BlockState stateAfterPicking(BlockState state)
    {
        return TFCBlocks.DEAD_BANANA_PLANT.get().defaultBlockState().setValue(STAGE, 2);
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, List<Component> text, boolean isDebug)
    {
        final ClimateRange range = climateRange.get();

        text.add(FarmlandBlock.getHydrationTooltip(level, pos, range, false, FruitTreeLeavesBlock.getHydration(level, pos)));
        text.add(FarmlandBlock.getTemperatureTooltip(level, pos, range, false));
    }

    @Override
    public ItemStack getProductItem(RandomSource random)
    {
        return new ItemStack(productItem.get(), Mth.nextInt(random, 3, 6));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(STAGE))
            {
                case 0 -> TRUNK_0;
                case 1 -> TRUNK_1;
                default -> PLANT;
            };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(STAGE) == 2 ? Shapes.empty() : getShape(state, level, pos, context);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        // no op the superclass
    }

    @Override
    public void onUpdate(Level level, BlockPos pos, BlockState state)
    {
        // Bananas grow vertically as long as they can, until they reach stage 2
        // At that point the top block is able to fruit and flower
        // Once it is picked, the top block dies. And the plant is therefore dead.

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

                final ClimateRange range = climateRange.get();
                final int hydration = FruitTreeLeavesBlock.getHydration(level, pos);

                int stage = state.getValue(STAGE);

                BlockPos abovePos = pos.above();
                BlockState newState;
                do
                {
                    // This always runs at least once. It is called through random ticks, and calendar updates - although calendar updates will only call this if they've waited at least a day, or the average delta between random ticks.
                    // Otherwise it will just wait for the next random tick.

                    // Jump forward to nextTick.
                    // Advance both the stage (randomly, if the previous month was healthy), and lifecycle (if the at-the-time conditions were valid)
                    nextCalendarTick = Math.min(nextCalendarTick + Calendars.SERVER.getCalendarTicksInMonth(), currentCalendarTick);
                    if (currentLifecycle.active() && stage < 2)
                    {
                        BlockPos downPos = pos.below(3);
                        // increase the stage 1/3 of the time, or always if we realize we're starting to get tall
                        if (!Helpers.isBlock(level.getBlockState(abovePos), this) && (level.random.nextInt(4) == 0 || Helpers.isBlock(level.getBlockState(downPos), this)))
                        {
                            stage++;
                        }
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
                    // we don't allow the trunk blocks to fruit or flower
                    if (stage < 2 && currentLifecycle.active())
                    {
                        currentLifecycle = Lifecycle.HEALTHY;
                    }

                    newState = state.setValue(STAGE, stage).setValue(LIFECYCLE, currentLifecycle);

                    // bananas only grow for stages 0 and 1
                    if (stage < 2 && currentLifecycle.active())
                    {
                        if (level.isEmptyBlock(abovePos) && level.canSeeSky(abovePos))
                        {
                            level.setBlockAndUpdate(abovePos, newState);
                            final long newBushTicks = nextCalendarTick;
                            level.getBlockEntity(abovePos, TFCBlockEntities.BERRY_BUSH.get()).ifPresent(newBush -> newBush.setLastBushTick(newBushTicks));
                        }
                    }
                }
                while (nextCalendarTick < currentCalendarTick);

                if (state != newState)
                {
                    level.setBlockAndUpdate(pos, newState);
                }
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getValue(STAGE) == 2 && newState.isAir())
        {
            kill(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return Helpers.isBlock(belowState, TFCTags.Blocks.BUSH_PLANTABLE_ON) || Helpers.isBlock(belowState, this);
    }
}
