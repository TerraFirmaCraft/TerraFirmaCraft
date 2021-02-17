/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public abstract class TFCKelpBlock extends BodyPlantBlock implements IFluidLoggable
{
    private final Supplier<? extends Block> headBlock;

    public static TFCKelpBlock create(AbstractBlock.Properties properties, Supplier<? extends Block> headBlock, Direction direction, VoxelShape shape, FluidProperty fluid)
    {
        return new TFCKelpBlock(properties, headBlock, shape, direction)
        {
            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    protected TFCKelpBlock(AbstractBlock.Properties properties, Supplier<? extends Block> headBlock, VoxelShape shape, Direction direction)
    {
        super(properties, headBlock, shape, direction);
        this.headBlock = headBlock;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == this.growthDirection.getOpposite() && !stateIn.canBeReplacedByLeaves(worldIn, currentPos))
        {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
        }
        //This is where vanilla assumes (wrongly) that the abstract block has correct waterlogged handling
        AbstractTopPlantBlock abstracttopplantblock = this.getTopPlantBlock();
        if (facing == this.growthDirection)
        {
            Block block = facingState.getBlock();
            if (block != this && block != abstracttopplantblock)
            {
                return abstracttopplantblock.getStateForPlacement(worldIn).with(getFluidProperty(), stateIn.get(getFluidProperty()));
            }
        }
        if (this.scheduleFluidTicks)
        {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(getFluidProperty());
    }

    @Override
    public boolean canPlaceLiquid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        return false;
    }

    @Override
    public boolean placeLiquid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        return false;
    }

    protected AbstractTopPlantBlock getTopPlantBlock()
    {
        return (AbstractTopPlantBlock) headBlock.get();
    }

    @Override
    public AbstractBlock.OffsetType getOffsetType()
    {
        return AbstractBlock.OffsetType.XZ;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape voxelshape = super.getShape(state, worldIn, pos, context);
        Vector3d vector3d = state.getOffset(worldIn, pos);
        return voxelshape.move(vector3d.x, vector3d.y, vector3d.z);
    }
}
