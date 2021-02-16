/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.fluids.TFCFluids;

/**
 * {  net.minecraft.block.CoralPlantBlock}
 */
public class TFCCoralPlantBlock extends TFCAbstractCoralPlantBlock
{
    private final Supplier<? extends Block> deadBlock;
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    public TFCCoralPlantBlock(Supplier<? extends Block> deadBlock, AbstractBlock.Properties properties)
    {
        super(properties);
        this.deadBlock = deadBlock;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        tryScheduleDieTick(state, worldIn, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!scanForWater(state, worldIn, pos))
        {
            worldIn.setBlockState(pos, deadBlock.get().getDefaultState().with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)), 2);
        }

    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN && !stateIn.canBeReplacedByLeaves(worldIn, currentPos))
        {
            return Blocks.AIR.getDefaultState();
        }
        else
        {
            this.tryScheduleDieTick(stateIn, worldIn, currentPos);
            if (stateIn.get(getFluidProperty()).getFluid().isIn(FluidTags.WATER))
            {
                worldIn.getPendingFluidTicks().scheduleTick(currentPos, TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getSource().getTickRate(worldIn));
            }

            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }
}
