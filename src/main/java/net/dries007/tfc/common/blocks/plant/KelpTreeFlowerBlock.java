/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.config.TFCConfig;

/**
 * Almost all methods in here are adapted from
 * {@link net.minecraft.block.ChorusFlowerBlock}
 */
public abstract class KelpTreeFlowerBlock extends Block implements IFluidLoggable
{
    public static final IntegerProperty AGE = BlockStateProperties.AGE_5;
    private static final VoxelShape SHAPE = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    public static KelpTreeFlowerBlock create(AbstractBlock.Properties builder, Supplier<? extends Block> plant, FluidProperty fluid)
    {
        return new KelpTreeFlowerBlock(builder, plant)
        {
            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    public static boolean isEmptyWaterBlock(IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.isWaterAt(pos) && !(worldIn.getBlockState(pos).getBlock() instanceof IFluidLoggable);
    }

    private static boolean allNeighborsEmpty(IWorldReader worldIn, BlockPos pos, @Nullable Direction excludingSide)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            if (direction != excludingSide && !isEmptyWaterBlock(worldIn, pos.relative(direction)))
            {
                return false;
            }
        }
        return true;
    }
    private final Supplier<? extends Block> bodyBlock;

    protected KelpTreeFlowerBlock(AbstractBlock.Properties builder, Supplier<? extends Block> bodyBlock)
    {
        super(builder);
        this.bodyBlock = bodyBlock;
        registerDefaultState(stateDefinition.any().setValue(AGE, 0).setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return state.getValue(AGE) < 5;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(getFluidProperty(), AGE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        final Fluid containedFluid = stateIn.getValue(getFluidProperty()).getFluid();
        if (containedFluid != Fluids.EMPTY)
        {
            worldIn.getLiquidTicks().scheduleTick(currentPos, containedFluid, containedFluid.getTickDelay(worldIn));
        }
        if (facing != Direction.UP && !stateIn.canSurvive(worldIn, currentPos))
        {
            worldIn.getBlockTicks().scheduleTick(currentPos, this, 1);
            return Blocks.AIR.defaultBlockState();
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        KelpTreeBlock body = (KelpTreeBlock) getBodyBlock().get();

        BlockState blockstate = worldIn.getBlockState(pos.below());
        if (blockstate.getBlock() != body && !blockstate.is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
        {
            if (!isEmptyWaterBlock(worldIn, pos.below()))
            {
                return false;
            }
            else
            {
                boolean isValid = false;
                for (Direction direction : Direction.Plane.HORIZONTAL)
                {
                    BlockState relativeState = worldIn.getBlockState(pos.relative(direction));
                    if (relativeState.is(body))
                    {
                        if (isValid)
                        {
                            return false;
                        }

                        isValid = true;
                    }
                    else if (!isEmptyWaterBlock(worldIn, pos.relative(direction)))
                    {
                        return false;
                    }
                }

                return isValid;
            }
        }
        else
        {
            return true;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        KelpTreeBlock body = (KelpTreeBlock) getBodyBlock().get();
        Fluid fluid = state.getValue(getFluidProperty()).getFluid();

        BlockPos abovePos = pos.above();
        if (isEmptyWaterBlock(worldIn, abovePos) && abovePos.getY() < 256 && TFCConfig.SERVER.plantGrowthChance.get() > random.nextDouble())
        {
            int i = state.getValue(AGE);
            if (i < 5 && ForgeHooks.onCropsGrowPre(worldIn, abovePos, state, true))
            {
                boolean shouldPlaceNewBody = false;
                boolean foundGroundFurtherDown = false;
                BlockState belowState = worldIn.getBlockState(pos.below());
                Block belowBlock = belowState.getBlock();
                if (belowBlock.is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
                {
                    shouldPlaceNewBody = true;
                }
                else if (belowBlock == body)
                {
                    int j = 1;

                    for (int k = 0; k < 4; ++k)
                    {
                        Block belowBlockOffset = worldIn.getBlockState(pos.below(j + 1)).getBlock();
                        if (belowBlockOffset != body)
                        {
                            if (belowBlockOffset.is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
                            {
                                foundGroundFurtherDown = true;
                            }
                            break;
                        }

                        ++j;
                    }

                    if (j < 2 || j <= random.nextInt(foundGroundFurtherDown ? 5 : 4))
                    {
                        shouldPlaceNewBody = true;
                    }
                }
                else if (isEmptyWaterBlock(worldIn, pos.below()))
                {
                    shouldPlaceNewBody = true;
                }

                if (shouldPlaceNewBody && allNeighborsEmpty(worldIn, abovePos, null) && isEmptyWaterBlock(worldIn, pos.above(2)))
                {
                    setBodyBlockWithFluid(worldIn, pos, fluid);
                    this.placeGrownFlower(worldIn, abovePos, i);
                }
                else if (i < 4)
                {
                    int l = random.nextInt(4);
                    if (foundGroundFurtherDown)
                    {
                        ++l;
                    }
                    boolean foundValidGrowthSpace = false;
                    for (int i1 = 0; i1 < l; ++i1)
                    {
                        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        BlockPos relativePos = pos.relative(direction);
                        if (isEmptyWaterBlock(worldIn, relativePos) && isEmptyWaterBlock(worldIn, relativePos.below()) && allNeighborsEmpty(worldIn, relativePos, direction.getOpposite()))
                        {
                            this.placeGrownFlower(worldIn, relativePos, i + 1);
                            foundValidGrowthSpace = true;
                        }
                    }
                    if (foundValidGrowthSpace)
                    {
                        setBodyBlockWithFluid(worldIn, pos, fluid);
                    }
                    else
                    {
                        this.placeDeadFlower(worldIn, pos);
                    }
                }
                else
                {
                    this.placeDeadFlower(worldIn, pos);
                }
                ForgeHooks.onCropsGrowPost(worldIn, pos, state);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!state.canSurvive(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    public void generatePlant(IWorld worldIn, BlockPos pos, Random rand, int maxHorizontalDistance, Fluid fluid)
    {
        if (!getFluidProperty().canContain(fluid))
            return;
        setBodyBlockWithFluid(worldIn, pos, fluid);
        growTreeRecursive(worldIn, pos, rand, pos, maxHorizontalDistance, 0, fluid);
    }

    public void growTreeRecursive(IWorld worldIn, BlockPos branchPos, Random rand, BlockPos originalBranchPos, int maxHorizontalDistance, int iterations, Fluid fluid)
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
            setBodyBlockWithFluid(worldIn, blockpos, fluid);
            setBodyBlockWithFluid(worldIn, blockpos.below(), fluid);
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
                if (Math.abs(aboveRelativePos.getX() - originalBranchPos.getX()) < maxHorizontalDistance && Math.abs(aboveRelativePos.getZ() - originalBranchPos.getZ()) < maxHorizontalDistance && isEmptyWaterBlock(worldIn, aboveRelativePos) && isEmptyWaterBlock(worldIn, aboveRelativePos.below()) && allNeighborsEmpty(worldIn, aboveRelativePos, direction.getOpposite()))
                {
                    willContinue = true;
                    setBodyBlockWithFluid(worldIn, aboveRelativePos, fluid);
                    setBodyBlockWithFluid(worldIn, aboveRelativePos.relative(direction.getOpposite()), fluid);
                    growTreeRecursive(worldIn, aboveRelativePos, rand, originalBranchPos, maxHorizontalDistance, iterations + 1, fluid);
                }
            }
        }
        if (!willContinue)
        {
            worldIn.setBlock(branchPos.above(i), defaultBlockState().setValue(AGE, rand.nextInt(10) == 1 ? 3 : 5).setValue(getFluidProperty(), getFluidProperty().keyFor(fluid)), 2);
        }
    }

    private void placeGrownFlower(World worldIn, BlockPos pos, int age)
    {
        Fluid fluid = worldIn.getFluidState(pos).getType().getFluid();
        worldIn.setBlock(pos, defaultBlockState().setValue(getFluidProperty(), getFluidProperty().keyFor(fluid)).setValue(AGE, age), 2);
        worldIn.levelEvent(1033, pos, 0);
    }

    private void placeDeadFlower(World worldIn, BlockPos pos)
    {
        Fluid fluid = worldIn.getFluidState(pos).getType().getFluid();
        worldIn.setBlock(pos, defaultBlockState().setValue(getFluidProperty(), getFluidProperty().keyFor(fluid)).setValue(AGE, 5), 2);
        worldIn.levelEvent(1034, pos, 0);
    }

    private void setBodyBlockWithFluid(IWorld worldIn, BlockPos pos, Fluid fluid)
    {
        BlockState state = getBodyStateWithFluid(worldIn, pos, fluid);
        worldIn.setBlock(pos, state, 2);
    }

    private BlockState getBodyStateWithFluid(IWorld worldIn, BlockPos pos, Fluid fluid)
    {
        KelpTreeBlock plant = (KelpTreeBlock) getBodyBlock().get();
        return plant.getStateForPlacement(worldIn, pos).setValue(getFluidProperty(), getFluidProperty().keyFor(fluid));
    }

    private Supplier<? extends Block> getBodyBlock()
    {
        return bodyBlock;
    }
}
