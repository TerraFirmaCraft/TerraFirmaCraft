/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;

/**
 * If I had my way, everything in this mod would be chorus fruit.
 *
 * @author EERussianguy
 */
public class GrowingFruitTreeBranchBlock extends FruitTreeBranchBlock implements EntityBlockExtension
{
    public static final IntegerProperty SAPLINGS = TFCBlockStateProperties.SAPLINGS;
    public static final BooleanProperty NATURAL = TFCBlockStateProperties.NATURAL; // prevents climate check
    private static final Direction[] NOT_DOWN = new Direction[] {Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.NORTH, Direction.UP};

    private static boolean canGrowInto(LevelReader level, BlockPos pos)
    {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || Helpers.isBlock(state, TFCTags.Blocks.FRUIT_TREE_LEAVES);
    }

    private static boolean allNeighborsEmpty(LevelReader level, BlockPos pos, @Nullable Direction excludingSide)
    {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            mutablePos.set(pos).move(direction);
            if (direction != excludingSide && !canGrowInto(level, mutablePos))
            {
                return false;
            }
        }
        return true;
    }

    private final Supplier<? extends Block> body;
    private final Supplier<? extends Block> leaves;
    private final Supplier<ClimateRange> climateRange;

    public GrowingFruitTreeBranchBlock(ExtendedProperties properties, Supplier<? extends Block> body, Supplier<? extends Block> leaves, Supplier<ClimateRange> climateRange)
    {
        super(properties, climateRange);

        this.body = body;
        this.leaves = leaves;
        this.climateRange = climateRange;

        registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, true).setValue(STAGE, 0).setValue(NATURAL, false));
    }

    @Override
    public void addExtraInfo(List<Component> text)
    {
        text.add(Component.translatable("tfc.tooltip.fruit_tree.growing"));
    }

    public void grow(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, int cyclesLeft)
    {
        FruitTreeBranchBlock body = (FruitTreeBranchBlock) this.body.get();
        BlockPos abovePos = pos.above();
        final boolean natural = state.getValue(NATURAL);
        if (canGrowInto(level, abovePos) && abovePos.getY() < level.getMaxBuildHeight() - 1)
        {
            int stage = state.getValue(STAGE);
            if (stage < 3)
            {
                boolean willGrowUpward = false;
                BlockState belowState = level.getBlockState(pos.below());
                Block belowBlock = belowState.getBlock();
                if (Helpers.isBlock(belowBlock, TFCTags.Blocks.BUSH_PLANTABLE_ON))
                {
                    willGrowUpward = true;
                }
                else if (belowBlock == body)
                {
                    BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                    int j = 1;
                    for (int k = 0; k < 4; ++k)
                    {
                        mutablePos.setWithOffset(pos, 0, -1 * (j + 1), 0);
                        if (level.getBlockState(mutablePos).getBlock() != body)
                        {
                            break;
                        }
                        ++j;
                    }
                    if (j < 2)
                    {
                        willGrowUpward = true;
                    }
                }
                else if (canGrowInto(level, pos.below()))
                {
                    willGrowUpward = true;
                }

                if (willGrowUpward && allNeighborsEmpty(level, abovePos, null) && canGrowInto(level, pos.above(2)))
                {
                    placeBody(level, pos, stage);
                    placeGrownFlower(level, abovePos, stage, state.getValue(SAPLINGS), cyclesLeft - 1, natural);
                }
                else if (stage < 2)
                {
                    int branches = Math.max(0, state.getValue(SAPLINGS) - stage);
                    BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                    List<Direction> directions = Direction.Plane.HORIZONTAL.stream().collect(Collectors.toList());
                    while (branches > 0)
                    {
                        Direction test = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        if (directions.contains(test))
                        {
                            if (couldBranchInDirection(level, pos, mutablePos, test))
                            {
                                boolean doubleBranch = false;
                                if (random.nextBoolean())
                                {
                                    mutablePos.move(test, 1);
                                    if (couldBranchInDirection(level, pos, mutablePos, test))
                                    {
                                        mutablePos.move(test, -1);
                                        placeBody(level, mutablePos, stage);
                                        mutablePos.move(test, 1);
                                        placeGrownFlower(level, mutablePos, stage + 1, state.getValue(SAPLINGS), cyclesLeft - 1, natural);
                                        doubleBranch = true;
                                    }
                                }
                                if (!doubleBranch)
                                {
                                    placeGrownFlower(level, mutablePos, stage + 1, state.getValue(SAPLINGS), cyclesLeft - 1, natural);
                                }
                            }
                            directions.remove(test);
                            branches--;
                        }
                    }
                    placeBody(level, pos, stage);
                }
                else
                {
                    placeBody(level, pos, stage);
                }
            }
        }
    }

    private static boolean couldBranchInDirection(ServerLevel level, BlockPos pos, BlockPos.MutableBlockPos mutablePos, Direction test)
    {
        mutablePos.setWithOffset(pos, test);
        if (canGrowInto(level, mutablePos))
        {
            mutablePos.move(0, -1, 0);
            if (canGrowInto(level, mutablePos))
            {
                mutablePos.move(0, 1, 0);
                return allNeighborsEmpty(level, mutablePos, test.getOpposite());
            }
        }
        return false;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(SAPLINGS, NATURAL));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return state.getValue(STAGE) < 3;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        final int hydration = FruitTreeLeavesBlock.getHydration(level, pos);
        final float temp = Climate.getAverageTemperature(level, pos);
        if (!climateRange.get().checkBoth(hydration, temp, false) && !state.getValue(NATURAL))
        {
            TickCounterBlockEntity.reset(level, pos);
        }
        super.randomTick(state, level, pos, random);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        super.tick(state, level, pos, rand);
        if (level.getBlockEntity(pos) instanceof TickCounterBlockEntity counter)
        {
            long days = counter.getTicksSinceUpdate() / ICalendar.TICKS_IN_DAY;
            int cycles = (int) (days / 5);
            if (cycles >= 1)
            {
                grow(state, level, pos, rand, cycles);
                counter.resetCounter();
            }
        }
    }

    private void placeGrownFlower(ServerLevel level, BlockPos pos, int stage, int saplings, int cycles, boolean natural)
    {
        level.setBlock(pos, getStateForPlacement(level, pos).setValue(STAGE, stage).setValue(SAPLINGS, saplings).setValue(NATURAL, natural), 3);
        if (level.getBlockEntity(pos) instanceof TickCounterBlockEntity counter)
        {
            counter.resetCounter();
            counter.reduceCounter(-1L * ICalendar.TICKS_IN_DAY * cycles * 5);
        }
        addLeaves(level, pos);
        level.getBlockState(pos).randomTick(level, pos, level.random);
    }

    private void placeBody(LevelAccessor level, BlockPos pos, int stage)
    {
        FruitTreeBranchBlock plant = (FruitTreeBranchBlock) this.body.get();
        level.setBlock(pos, plant.getStateForPlacement(level, pos).setValue(STAGE, stage), 3);
        addLeaves(level, pos);
    }

    private void addLeaves(LevelAccessor level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final BlockState leaves = this.leaves.get().defaultBlockState();
        mutablePos.setWithOffset(pos, 0, -2, 0);
        final BlockState downState = level.getBlockState(mutablePos);
        if (!(downState.isAir() || Helpers.isBlock(downState, TFCTags.Blocks.FRUIT_TREE_LEAVES) || Helpers.isBlock(downState, TFCTags.Blocks.FRUIT_TREE_BRANCH)))
        {
            return;
        }
        for (Direction d : NOT_DOWN)
        {
            mutablePos.setWithOffset(pos, d);
            if (level.isEmptyBlock(mutablePos))
            {
                level.setBlock(mutablePos, leaves, 2);
            }
        }
    }
}
