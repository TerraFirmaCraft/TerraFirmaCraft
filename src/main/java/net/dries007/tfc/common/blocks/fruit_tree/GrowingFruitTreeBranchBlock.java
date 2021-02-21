package net.dries007.tfc.common.blocks.fruit_tree;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.util.Helpers;

/**
 * If I had my way, everything in this mod would be chorus fruit.
 * @author EERussianguy
 */
public class GrowingFruitTreeBranchBlock extends FruitTreeBranchBlock
{
    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_3;
    public static final IntegerProperty SAPLINGS = TFCBlockStateProperties.SAPLINGS;

    private final FruitTree fruitTree;
    private final Supplier<? extends Block> body;
    private final Supplier<? extends Block> leaves;

    public GrowingFruitTreeBranchBlock(ForgeBlockProperties properties, FruitTree fruitTree, Supplier<? extends Block> body, Supplier<? extends Block> leaves)
    {
        super(properties);
        this.fruitTree = fruitTree;
        this.body = body;
        this.leaves = leaves;
        registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false).setValue(STAGE, 0));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        TickCounterTileEntity te = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);
        if (te == null) return;

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
                if (belowBlock.is(TFCTags.Blocks.BUSH_PLANTABLE_ON))
                {
                    willGrowUpward = true;
                }
                else if (belowBlock == body)
                {
                    BlockPos.Mutable mutablePos = new BlockPos.Mutable();
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
                    placeBody(world, pos);
                    placeGrownFlower(world, abovePos, stage, state.getValue(SAPLINGS));
                }
                else if (stage < 2)
                {
                    int branches = Math.max(0, state.getValue(SAPLINGS) - stage);
                    BlockPos.Mutable mutablePos = new BlockPos.Mutable();
                    List<Direction> directions = Direction.Plane.HORIZONTAL.stream().collect(Collectors.toList());
                    while (branches > 0)
                    {
                        Direction test = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        if (directions.contains(test))
                        {
                            mutablePos.setWithOffset(pos, test);
                            if (canGrowIntoLocations(world, mutablePos, mutablePos.below()) && allNeighborsEmpty(world, mutablePos, test.getOpposite()))
                            {
                                placeGrownFlower(world, mutablePos, stage + 1, state.getValue(SAPLINGS));
                            }
                            directions.remove(test);
                            branches--;
                        }
                    }
                    placeBody(world, pos);
                }
                else
                {
                    placeBody(world, pos);
                }
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return state.getValue(STAGE) < 3;
    }

    private static boolean canGrowIntoLocations(IWorldReader world, BlockPos... pos)
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
    private static boolean canGrowInto(IWorldReader world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.is(TFCTags.Blocks.FRUIT_TREE_LEAVES);
    }

    private void placeGrownFlower(World worldIn, BlockPos pos, int stage, int saplings)
    {
        worldIn.setBlock(pos, getStateForPlacement(worldIn, pos).setValue(STAGE, stage).setValue(SAPLINGS, saplings), 2);
        addLeaves(worldIn, pos);
    }

    private void placeBody(IWorld worldIn, BlockPos pos)
    {
        FruitTreeBranchBlock plant = (FruitTreeBranchBlock) this.body.get();
        worldIn.setBlock(pos, plant.getStateForPlacement(worldIn, pos), 2);
        addLeaves(worldIn, pos);
    }

    @SuppressWarnings("deprecation")
    private void addLeaves(IWorld world, BlockPos pos)
    {
        BlockState downState = world.getBlockState(pos.below(2));
        if (!(downState.isAir() || downState.is(TFCTags.Blocks.FRUIT_TREE_LEAVES) || downState.is(TFCTags.Blocks.FRUIT_TREE_BRANCH))) return;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction d : Direction.values())
        {
            if (d != Direction.DOWN)
            {
                mutable.setWithOffset(pos, d);
                if (canGrowInto(world, mutable))
                {
                    world.setBlock(mutable, this.leaves.get().defaultBlockState(), 3);
                }
            }
        }
    }

    private static boolean allNeighborsEmpty(IWorldReader worldIn, BlockPos pos, @Nullable Direction excludingSide)
    {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
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

    public void generatePlant(IWorld worldIn, BlockPos pos, Random rand, int maxHorizontalDistance)
    {
        placeBody(worldIn, pos);
        growTreeRecursive(worldIn, pos, rand, pos, maxHorizontalDistance, 0);
    }

    public void growTreeRecursive(IWorld worldIn, BlockPos branchPos, Random rand, BlockPos originalBranchPos, int maxHorizontalDistance, int iterations)
    {
        int i = rand.nextInt(5) + 1;
        if (iterations == 0)
        {
            ++i;
        }
        for (int j = 0; j < i; ++j)
        {
            BlockPos blockpos = branchPos.above(j + 1);
            if (!allNeighborsEmpty(worldIn, blockpos, null))
            {
                return;
            }
            placeBody(worldIn, blockpos);
            placeBody(worldIn, blockpos.below());
        }

        boolean willContinue = false;
        if (iterations < 4)
        {
            int branchAttempts = rand.nextInt(4);
            if (iterations == 0)
            {
                ++branchAttempts;
            }

            for (int k = 0; k < branchAttempts; ++k)
            {
                Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
                BlockPos aboveRelativePos = branchPos.above(i).relative(direction);
                if (Math.abs(aboveRelativePos.getX() - originalBranchPos.getX()) < maxHorizontalDistance
                    && Math.abs(aboveRelativePos.getZ() - originalBranchPos.getZ()) < maxHorizontalDistance
                    && canGrowIntoLocations(worldIn, aboveRelativePos, aboveRelativePos.below())
                    && allNeighborsEmpty(worldIn, aboveRelativePos, direction.getOpposite()))
                {
                    willContinue = true;
                    placeBody(worldIn, aboveRelativePos);
                    placeBody(worldIn, aboveRelativePos.relative(direction.getOpposite()));
                    growTreeRecursive(worldIn, aboveRelativePos, rand, originalBranchPos, maxHorizontalDistance, iterations + 1);
                }
            }
        }
        if (!willContinue)
        {
            worldIn.setBlock(branchPos.above(i), defaultBlockState().setValue(STAGE, 2), 2);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        TickCounterTileEntity te = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);
        if (te != null)
        {
            te.setRemoved(); // we have to ensure that the tile is destroyed when the block is or stuff gets really badas
        }
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TickCounterTileEntity te = Helpers.getTileEntity(worldIn, pos, TickCounterTileEntity.class);
        if (te != null)
        {
            te.resetCounter();
        }
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(STAGE, SAPLINGS);
    }
}
