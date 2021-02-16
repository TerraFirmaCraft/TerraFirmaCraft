/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public abstract class TFCKelpTopBlock extends TopPlantBlock implements IFluidLoggable
{
    private final Supplier<? extends Block> bodyBlock;

    public static TFCKelpTopBlock create(AbstractBlock.Properties properties, Supplier<? extends Block> bodyBlock, Direction direction, VoxelShape shape, FluidProperty fluid)
    {
        return new TFCKelpTopBlock(properties, bodyBlock, direction, shape)
        {
            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    protected TFCKelpTopBlock(AbstractBlock.Properties properties, Supplier<? extends Block> bodyBlock, Direction direction, VoxelShape shape)
    {
        super(properties, bodyBlock, direction, shape);
        this.bodyBlock = bodyBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World world = context.getWorld();
        BlockState state = getDefaultState().with(AGE, world.getRandom().nextInt(25));
        FluidState fluidState = world.getFluidState(context.getPos());
        if (getFluidProperty().canContain(fluidState.getFluid()))
        {
            return state.with(getFluidProperty(), getFluidProperty().keyFor(fluidState.getFluid()));
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == growthDirection.getOpposite() && !stateIn.canBeReplacedByLeaves(worldIn, currentPos))
        {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
        }
        if (facing != growthDirection || !facingState.isIn(this) && !facingState.isIn(getBodyBlock()))
        {
            //Not sure if this is necessary
            Fluid fluid = stateIn.getFluidState().getFluid();
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, fluid, fluid.getTickRate(worldIn));
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        else// this is where it converts the top block to a body block when it gets placed on top of another top block
        {
            return this.getBodyBlock().getDefaultState().with(getFluidProperty(), stateIn.get(getFluidProperty()));
        }
    }

    @Override
    protected boolean canGrowInto(BlockState state)
    {
        Fluid fluid = state.getFluidState().getFluid();
        return getFluidProperty().canContain(fluid) && fluid != Fluids.EMPTY;
    }

    @Override
    protected boolean canAttachToBlock(Block blockIn)
    {
        return blockIn != Blocks.MAGMA_BLOCK;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
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

    @Override
    protected AbstractBodyPlantBlock getBodyBlock()
    {
        return (AbstractBodyPlantBlock) bodyBlock.get();
    }

    @Override
    public AbstractBlock.OffsetType getOffsetType()
    {
        return AbstractBlock.OffsetType.XZ;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape voxelshape = super.getShape(state, worldIn, pos, context);
        Vector3d vector3d = state.getOffset(worldIn, pos);
        return voxelshape.move(vector3d.x, vector3d.y, vector3d.z);
    }
}
