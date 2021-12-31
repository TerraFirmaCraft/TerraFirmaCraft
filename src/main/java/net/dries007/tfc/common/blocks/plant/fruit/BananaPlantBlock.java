/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.ClimateRanges;

public class BananaPlantBlock extends SeasonalPlantBlock implements IBushBlock
{
    public static final VoxelShape PLANT = box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    private static final VoxelShape TRUNK_0 = box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    private static final VoxelShape TRUNK_1 = box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);

    private final Supplier<ClimateRange> climateRange = ClimateRanges.BANANA_PLANT; //todo refactors coming soon

    public BananaPlantBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages)
    {
        super(properties, productItem, stages);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        super.use(state, level, pos, player, hand, hit);

        // picking bananas kills the plant. this propagates death to the whole stalk.
        Block deadBlock = TFCBlocks.DEAD_BANANA_PLANT.get();
        if (!level.isClientSide && level.getBlockState(pos).is(deadBlock))
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
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState stateAfterPicking(BlockState state)
    {
        return TFCBlocks.DEAD_BANANA_PLANT.get().defaultBlockState().setValue(STAGE, 2);
    }

    @Override
    protected ItemStack getProductItem(Random random)
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

                int stage = state.getValue(STAGE);
                boolean grewUpwards = false;
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
                        if (level.random.nextInt(3) == 0 || level.getBlockState(downPos).is(TFCTags.Blocks.FRUIT_TREE_BRANCH))
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

                    // bananas only grow for stages 1 and 2
                    if (!grewUpwards && stage < 2 && currentLifecycle.active())
                    {
                        grewUpwards = true;
                        BlockPos abovePos = pos.above(); // if we have space to grow, let's grow
                        if (level.isEmptyBlock(abovePos) && level.canSeeSky(abovePos))
                        {
                            level.setBlockAndUpdate(abovePos, newState);
                            final long newBushTicks = nextCalendarTick;
                            level.getBlockEntity(abovePos, TFCBlockEntities.BERRY_BUSH.get()).ifPresent(newBush -> newBush.setLastUpdateTick(newBushTicks));
                        }
                    }
                }
                while (nextCalendarTick < currentCalendarTick);

                if (state != newState)
                {
                    level.setBlockAndUpdate(pos, newState);
                }
            }
            bush.afterUpdate();
        });
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return belowState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON) || belowState.is(TFCTags.Blocks.FRUIT_TREE_BRANCH);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return new ItemStack(TFCBlocks.BANANA_SAPLING.get());
    }
}
