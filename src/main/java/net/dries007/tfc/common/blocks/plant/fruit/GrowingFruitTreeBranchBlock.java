/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 * If I had my way, everything in this mod would be chorus fruit.
 *
 * @author EERussianguy
 */
public class GrowingFruitTreeBranchBlock extends FruitTreeBranchBlock implements EntityBlockExtension
{
    public static final IntegerProperty SAPLINGS = TFCBlockStateProperties.SAPLINGS;
    private static final Direction[] NOT_DOWN = new Direction[] {Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.NORTH, Direction.UP};

    private static boolean canGrowIntoLocations(LevelReader world, BlockPos... pos)
    {
        for (BlockPos p : pos)
        {
            if (!canGrowInto(world, p))
            {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    private static boolean canGrowInto(LevelReader world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.is(TFCTags.Blocks.FRUIT_TREE_LEAVES);
    }

    private static boolean allNeighborsEmpty(LevelReader worldIn, BlockPos pos, @Nullable Direction excludingSide)
    {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            mutablePos.set(pos).move(direction);
            if (direction != excludingSide && !canGrowInto(worldIn, mutablePos))
            {
                return false;
            }
        }
        return true;
    }

    private final Supplier<? extends Block> body;
    private final Supplier<? extends Block> leaves;

    public GrowingFruitTreeBranchBlock(ForgeBlockProperties properties, Supplier<? extends Block> body, Supplier<? extends Block> leaves)
    {
        super(properties);

        this.body = body;
        this.leaves = leaves;

        registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, true).setValue(STAGE, 0));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random)
    {
        TickCounterBlockEntity te = Helpers.getBlockEntity(world, pos, TickCounterBlockEntity.class);
        if (te == null || world.isClientSide()) return;

        // todo: better climate checks
        /*
        ChunkData chunkData = ChunkData.get(world, pos);
        if (!fruitTree.getBase().isValidConditions(chunkData.getAverageTemp(pos), chunkData.getRainfall(pos)))
        {
            te.resetCounter();
        }
*/
        super.randomTick(state, world, pos, random);
    }

    public void grow(BlockState state, ServerLevel world, BlockPos pos, Random random, int cyclesLeft)
    {
        FruitTreeBranchBlock body = (FruitTreeBranchBlock) this.body.get();
        BlockPos abovePos = pos.above();
        if (canGrowInto(world, abovePos) && abovePos.getY() < world.getMaxBuildHeight() - 1)
        {
            int stage = state.getValue(STAGE);
            if (stage < 3)
            {
                boolean willGrowUpward = false;
                BlockState belowState = world.getBlockState(pos.below());
                Block belowBlock = belowState.getBlock();
                if (TFCTags.Blocks.BUSH_PLANTABLE_ON.contains(belowBlock))
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
                        if (world.getBlockState(mutablePos).getBlock() != body)
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
                else if (canGrowInto(world, pos.below()))
                {
                    willGrowUpward = true;
                }

                if (willGrowUpward && allNeighborsEmpty(world, abovePos, null) && canGrowInto(world, pos.above(2)))
                {
                    placeBody(world, pos, stage);
                    placeGrownFlower(world, abovePos, stage, state.getValue(SAPLINGS), cyclesLeft - 1);
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
                            mutablePos.setWithOffset(pos, test);
                            if (canGrowIntoLocations(world, mutablePos, mutablePos.below()) && allNeighborsEmpty(world, mutablePos, test.getOpposite()))
                            {
                                placeGrownFlower(world, mutablePos, stage + 1, state.getValue(SAPLINGS), cyclesLeft - 1);
                            }
                            directions.remove(test);
                            branches--;
                        }
                    }
                    placeBody(world, pos, stage);
                }
                else
                {
                    placeBody(world, pos, stage);
                }
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return state.getValue(STAGE) < 3;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(SAPLINGS);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random rand)
    {
        super.tick(state, world, pos, rand);
        TickCounterBlockEntity te = Helpers.getBlockEntity(world, pos, TickCounterBlockEntity.class);
        if (te == null || world.isEmptyBlock(pos) || world.isClientSide()) return;

        long days = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_DAY;
        int cycles = (int) (days / 5);
        if (cycles >= 1)
        {
            grow(state, world, pos, rand, cycles);
            te.resetCounter();
        }
    }

    private void placeGrownFlower(ServerLevel worldIn, BlockPos pos, int stage, int saplings, int cycles)
    {
        worldIn.setBlock(pos, getStateForPlacement(worldIn, pos).setValue(STAGE, stage).setValue(SAPLINGS, saplings), 3);
        TickCounterBlockEntity te = Helpers.getBlockEntity(worldIn, pos, TickCounterBlockEntity.class);
        if (te != null)
        {
            te.resetCounter();
            te.reduceCounter(-1L * ICalendar.TICKS_IN_DAY * cycles * 5);
        }
        addLeaves(worldIn, pos);
        worldIn.getBlockState(pos).randomTick(worldIn, pos, worldIn.random);
    }

    private void placeBody(LevelAccessor worldIn, BlockPos pos, int stage)
    {
        FruitTreeBranchBlock plant = (FruitTreeBranchBlock) this.body.get();
        worldIn.setBlock(pos, plant.getStateForPlacement(worldIn, pos).setValue(STAGE, stage), 3);
        addLeaves(worldIn, pos);
    }

    @SuppressWarnings("deprecation")
    private void addLeaves(LevelAccessor world, BlockPos pos)
    {
        final BlockState leaves = this.leaves.get().defaultBlockState();
        BlockState downState = world.getBlockState(pos.below(2));
        if (!(downState.isAir() || downState.is(TFCTags.Blocks.FRUIT_TREE_LEAVES) || downState.is(TFCTags.Blocks.FRUIT_TREE_BRANCH)))
            return;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction d : NOT_DOWN)
        {
            mutablePos.setWithOffset(pos, d);
            if (world.isEmptyBlock(mutablePos))
            {
                world.setBlock(mutablePos, leaves, 2);
            }
        }
    }
}
