/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public abstract class WaterPlantBlock extends PlantBlock implements IFluidLoggable
{
    public static WaterPlantBlock create(IPlant plant, FluidProperty fluid, Properties properties)
    {
        return new WaterPlantBlock(properties)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }

            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    protected WaterPlantBlock(Properties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any().with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos pos = context.getPos();
        FluidState fluidState = context.getWorld().getFluidState(pos);
        BlockState state = updateStateWithCurrentMonth(defaultBlockState());
        if (getFluidProperty().canContain(fluidState.getType()))
        {
            state = state.with(getFluidProperty(), getFluidProperty().keyFor(fluidState.getType()));
        }
        return state;
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.down());
        return belowState.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON) && state.get(getFluidProperty()) != getFluidProperty().keyFor(Fluids.EMPTY);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(getFluidProperty());
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }
}
