/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class GrowingBranchingCactusBlock extends BranchingCactusBlock
{
    public static GrowingBranchingCactusBlock createGrowing(ExtendedProperties properties, Supplier<? extends Block> body, Supplier<? extends Block> flower)
    {
        return new GrowingBranchingCactusBlock(0.3125f, properties, body, flower);
    }

    public static final BooleanProperty GROWS_BRANCHES = TFCBlockStateProperties.GROWS_BRANCHES;

    private final Supplier<? extends Block> body;
    private final Supplier<? extends Block> flower;

    public GrowingBranchingCactusBlock(float size, ExtendedProperties properties, Supplier<? extends Block> body, Supplier<? extends Block> flower)
    {
        super(size, properties);
        registerDefaultState(getStateDefinition().any().setValue(GROWS_BRANCHES, true).setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
        this.flower = flower;
        this.body = body;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(GROWS_BRANCHES));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (rand.nextFloat() > TFCConfig.SERVER.plantGrowthChance.get())
        {
            growthTick(level, pos, state);
        }
    }

    public void growthTick(LevelAccessor level, BlockPos pos, BlockState state)
    {
        if (level.getBlockState(pos.above()).getBlock() instanceof BranchingCactusBlock)
        {
            level.setBlock(pos, Helpers.copyProperties(body.get().defaultBlockState(), state), 3);
            return;
        }
        if (state.getValue(GROWS_BRANCHES) || level.getRandom().nextFloat() < 0.1f)
        {
            if (level.getBlockState(pos.below(7)).getBlock() instanceof BranchingCactusBlock)
            {
                level.setBlock(pos, Helpers.copyProperties(body.get().defaultBlockState(), state), 3);
                tryFruit(level, pos.above());
                return;
            }
        }
        else
        {
            if (level.getBlockState(pos.below(3)).getBlock() instanceof BranchingCactusBlock || level.getRandom().nextFloat() < 0.5f)
            {
                level.setBlock(pos, Helpers.copyProperties(body.get().defaultBlockState(), state), 3);
                tryFruit(level, pos.above());
                return;
            }
        }

        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos().set(pos);
        final var random = level.getRandom();
        final var dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        final BlockState body = this.body.get().defaultBlockState();
        final BranchingCactusBlock bodyBlock = (BranchingCactusBlock) body.getBlock();
        boolean willBranch = random.nextFloat() < 0.7f && state.getValue(GROWS_BRANCHES);
        if (willBranch)
        {
            willBranch = ensureNeighborhoodForBranch(level, pos, cursor, dir, willBranch);
        }
        cursor.setWithOffset(pos, 0, 1, 0);
        level.setBlock(cursor, getStateForPlacement(level, cursor).setValue(GROWS_BRANCHES, state.getValue(GROWS_BRANCHES)), 3);
        cursor.move(0, -1, 0);
        BlockState below = level.getBlockState(cursor);
        if (below.getBlock() == this)
        {
            level.setBlock(cursor, Helpers.copyProperties(body, below), 3);
        }
        cursor.move(0, 1, 0);
        if (willBranch)
        {
            if (canGrowInto(level, cursor.move(dir), dir) && canGrowInto(level, cursor.move(dir), dir) && canGrowInto(level, cursor.move(0, 1, 0), Direction.UP))
            {
                cursor.setWithOffset(pos, dir);
                level.setBlock(cursor, bodyBlock.getStateForPlacement(level, cursor), 3);
                cursor.move(dir);
                level.setBlock(cursor, getStateForPlacement(level, cursor).setValue(GROWS_BRANCHES, false).setValue(UP, true), 3);
            }
        }
    }

    private static boolean ensureNeighborhoodForBranch(LevelAccessor level, BlockPos pos, BlockPos.MutableBlockPos cursor, Direction dir, boolean willBranch)
    {
        // if we are near the ground we won't branch
        cursor.move(0, -1, 0);
        if (!(level.getBlockState(cursor).getBlock() instanceof BranchingCactusBlock))
        {
            willBranch = false;
        }
        cursor.setWithOffset(pos, dir);
        for (int i = 0; i < 2; i++)
        {
            cursor.move(0, -1, 0);
            if (level.getBlockState(cursor).getBlock() instanceof BranchingCactusBlock)
            {
                willBranch = false;
            }
        }
        cursor.setWithOffset(pos, dir);
        for (int i = 0; i < 3; i++)
        {
            cursor.move(0, 1, 0);
            if (level.getBlockState(cursor).getBlock() instanceof BranchingCactusBlock)
            {
                willBranch = false;
            }
        }
        return willBranch;
    }

    public boolean growRecursively(LevelAccessor level, BlockPos pos, BlockState state, int height)
    {
        final BlockState body = this.body.get().defaultBlockState();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos().set(pos);
        final var random = level.getRandom();
        cursor.move(0, -1, 0);
        if (!testDown(state) || !canGrowInto(level, pos, Direction.UP) || !canGrowInto(level, pos.above(), Direction.UP))
        {
            return false;
        }
        final Set<Direction> usedDirections = new HashSet<>();
        for (int i = 0; i < height; i++)
        {
            BlockState placeState = body.setValue(DOWN, true).setValue(UP, true);
            cursor.setWithOffset(pos, 0, i, 0);
            if (i == height - 1)
            {
                cursor.move(0, 1, 0);
                tryFruit(level, cursor);
                cursor.move(0, -1, 0);
            }
            if (!canGrowInto(level, cursor, Direction.UP))
            {
                return true;
            }
            if (i == height - 1)
            {
                placeState = Helpers.copyProperties(body, placeState);
            }
            level.setBlock(cursor, placeState, 3);
            if (state.getValue(GROWS_BRANCHES) && i > 2 && random.nextFloat() < 0.6f && i < height - 1)
            {
                final var dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                if (!usedDirections.contains(dir))
                {
                    if (canGrowInto(level, cursor.move(dir), dir) && canGrowInto(level, cursor.move(dir), dir) && canGrowInto(level, cursor.move(0, 1, 0), Direction.UP))
                    {
                        usedDirections.add(dir);
                        cursor.setWithOffset(pos, dir).move(0, i, 0);
                        level.setBlock(cursor, body.setValue(PROPERTY_BY_DIRECTION.get(dir), true).setValue(PROPERTY_BY_DIRECTION.get(dir.getOpposite()), true), 3);
                        cursor.move(dir);
                        BlockState newState = state.setValue(GROWS_BRANCHES, false).setValue(PROPERTY_BY_DIRECTION.get(dir.getOpposite()), true).setValue(UP, true);
                        level.setBlock(cursor, newState, 3);
                        cursor.move(0, 1, 0);
                        growRecursively(level, cursor.immutable(), newState, 3);

                        // go back and fix the branch connection
                        cursor.setWithOffset(pos, 0, i, 0);
                        level.setBlock(cursor, placeState.setValue(PROPERTY_BY_DIRECTION.get(dir), true), 3);
                    }
                }
            }
        }
        return true;
    }

    public boolean canGrowInto(LevelAccessor level, BlockPos pos, Direction direction)
    {
        if (!level.getBlockState(pos).isAir())
        {
            return false;
        }
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (Direction dir : Helpers.DIRECTIONS)
        {
            cursor.setWithOffset(pos, dir);
            if (!level.isAreaLoaded(cursor, 1) || dir == direction.getOpposite())
            {
                continue; // skip if unloaded, or we are checking the direction we are coming from
            }
            if (testHorizontal(level.getBlockState(cursor)))
            {
                return false;
            }
        }
        return true;
    }

    private void tryFruit(LevelAccessor level, BlockPos pos)
    {
        if (level.getBlockState(pos).canBeReplaced())
        {
            PlantBlock plant = (PlantBlock) flower.get();
            level.setBlock(pos, plant.defaultBlockState(), 3);
        }
    }
}
