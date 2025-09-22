/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blockentities.TickCountingBranchBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.tracker.WorldTracker;

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
    public void addExtraInfo(Consumer<Component> text)
    {
        text.accept(Component.translatable("tfc.tooltip.fruit_tree.growing"));
    }

    public void grow(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, int cyclesLeft)
    {
        FruitTreeBranchBlock body = (FruitTreeBranchBlock) this.body.get();
        BlockPos abovePos = pos.above();
        final boolean natural = state.getValue(NATURAL);
        if (canGrowInto(level, abovePos) && abovePos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockEntity(pos) instanceof TickCountingBranchBlockEntity activeBranch)
        {
            final BlockPos stemPos = activeBranch.getStemPos();
            int stage = state.getValue(STAGE);

            // Stage tracks horizontal Manhattan distance from trunk
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

                // Grow upward if possible
                if (willGrowUpward && allNeighborsEmpty(level, abovePos, null) && canGrowInto(level, pos.above(2)))
                {
                    placeBody(level, pos, stemPos, stage);
                    placeGrownFlower(level, abovePos, stemPos, stage, state.getValue(SAPLINGS), cyclesLeft - 1, natural);
                }
                // Try and branch if near enough to the trunk
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
                                        placeBody(level, mutablePos, stemPos, stage);
                                        mutablePos.move(test, 1);
                                        placeGrownFlower(level, mutablePos, stemPos, stage + 1, state.getValue(SAPLINGS), cyclesLeft - 1, natural);
                                        doubleBranch = true;
                                    }
                                }
                                if (!doubleBranch)
                                {
                                    placeGrownFlower(level, mutablePos, stemPos, stage + 1, state.getValue(SAPLINGS), cyclesLeft - 1, natural);
                                }
                            }
                            directions.remove(test);
                            branches--;
                        }
                    }
                    placeBody(level, pos, stemPos, stage);
                }
            }
            else
            {
                placeBody(level, pos, stemPos, stage);
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
        final int hydration = getFruitBranchHydration(level, pos);

        final float temp = Climate.getAverageTemperature(level, pos);
        if (!climateRange.get().checkBoth(hydration, temp, false) && !state.getValue(NATURAL))
        {
            TickCountingBranchBlockEntity.reset(level, pos);
        }
        else
        {
            this.tick(state, level, pos, random);
        }
        super.randomTick(state, level, pos, random);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        super.tick(state, level, pos, rand);
        if (level.getBlockEntity(pos) instanceof TickCountingBranchBlockEntity counter)
        {
            long days = counter.getTicksSinceUpdate() / ICalendar.CALENDAR_TICKS_IN_DAY;
            int cycles = (int) (days / 5);
            if (cycles >= 1)
            {
                grow(state, level, pos, rand, cycles);
                counter.resetCounter();
            }
        }
    }

    /**
     * Places the actively growing branch block
     */
    private void placeGrownFlower(ServerLevel level, BlockPos childPos, BlockPos stemPos, int stage, int saplings, int cycles, boolean natural)
    {
        final BlockState newState = getStateForPlacement(level, childPos).setValue(STAGE, stage).setValue(SAPLINGS, saplings).setValue(NATURAL, natural);
        level.setBlock(childPos, newState, Block.UPDATE_ALL);
        if (level.getBlockEntity(childPos) instanceof TickCountingBranchBlockEntity counter)
        {
            counter.resetCounter();
            counter.increaseCounter((long) ICalendar.CALENDAR_TICKS_IN_DAY * cycles * 5);

            counter.setStemPos(stemPos);
            addLeaves(level, childPos, stemPos);
        }
        else
        {
            TerraFirmaCraft.LOGGER.error("Failed to update block entity at: " + childPos);
        }
        level.getBlockState(childPos).randomTick(level, childPos, level.random);
    }

    /**
     * Places a static branch block that will not grow
     */
    private void placeBody(LevelAccessor level, BlockPos bodyPos, BlockPos stemPos, int stage)
    {
        FruitTreeBranchBlock plant = (FruitTreeBranchBlock) this.body.get();
        level.setBlock(bodyPos, plant.getStateForPlacement(level, bodyPos).setValue(STAGE, stage), Block.UPDATE_ALL);
        addLeaves(level, bodyPos, stemPos);
    }

    private void addLeaves(LevelAccessor level, BlockPos centerPos, BlockPos stemPos)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final BlockState leaves = this.leaves.get().defaultBlockState();
        mutablePos.setWithOffset(centerPos, 0, -2, 0);
        final BlockState downState = level.getBlockState(mutablePos);
        if (!(downState.isAir() || Helpers.isBlock(downState, TFCTags.Blocks.FRUIT_TREE_LEAVES) || Helpers.isBlock(downState, TFCTags.Blocks.FRUIT_TREE_BRANCH)))
        {
            return;
        }
        for (Direction d : NOT_DOWN)
        {
            mutablePos.setWithOffset(centerPos, d);
            if (level.isEmptyBlock(mutablePos))
            {
                level.setBlock(mutablePos, leaves, Block.UPDATE_ALL);
                if (level.getBlockEntity(mutablePos) instanceof BerryBushBlockEntity leaf)
                {
                    leaf.setStemPos(stemPos);
                }
            }
        }
    }

    /**
     * Evaluates hydration at the base of the tree
     * @param leafPos Must be the position of a valid {@link TickCountingBranchBlockEntity}
     */
    protected static int getFruitBranchHydration(Level level, BlockPos leafPos)
    {
        final BlockPos sourcePos;
        if (level.getBlockEntity(leafPos) instanceof TickCountingBranchBlockEntity branch)
        {
            sourcePos = branch.getStemPos().below();
        }
        else
        {
            TerraFirmaCraft.LOGGER.error("Fruit tree leaf block entity not present");
            sourcePos = leafPos;
        }
        return getFruitBranchHydrationFromRootPos(level, sourcePos);
    }

    /**
     * Evaluates hydration at the base of the tree
     * @param rootPos can be any block location you want to know the hydration level at
     */
    protected static int getFruitBranchHydrationFromRootPos(Level level, BlockPos rootPos)
    {
        final float averageRainfall = WorldTracker.get(level).getClimateModel().getAverageRainfall(level, rootPos);
        return FarmlandBlock.getHydrationFromRainHydration(level, rootPos, FarmlandBlock.getRainHydration(averageRainfall));
    }
}
