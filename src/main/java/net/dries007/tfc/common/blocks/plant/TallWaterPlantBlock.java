/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public abstract class TallWaterPlantBlock extends TFCTallGrassBlock implements IFluidLoggable
{
    public static TallWaterPlantBlock create(IPlant plant, FluidProperty fluid, Properties properties)
    {
        return new TallWaterPlantBlock(properties)
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

    protected TallWaterPlantBlock(Properties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any().setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)).setValue(TFCBlockStateProperties.TALL_PLANT_PART, Part.LOWER));
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.below());
        if (state.getValue(PART) == Part.LOWER)
        {
            return belowState.is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON);
        }
        else
        {
            if (state.getBlock() != this)
            {
                return belowState.is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            }
            return belowState.getBlock() == this && belowState.getValue(PART) == Part.LOWER;
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(pos);
        BlockState state = updateStateWithCurrentMonth(defaultBlockState());

        if (getFluidProperty().canContain(fluidState.getType()))
        {
            state = state.setValue(getFluidProperty(), getFluidProperty().keyFor(fluidState.getType()));
        }

        return pos.getY() < 255 && context.getLevel().getBlockState(pos.above()).canBeReplaced(context) ? state : null;
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
    public void placeTwoHalves(IWorld world, BlockPos pos, int flags, Random random)
    {
        int age = random.nextInt(4);
        BlockState lowerState = getStateWithFluid(defaultBlockState(), world.getFluidState(pos).getType());
        if (lowerState.getValue(getFluidProperty()).getFluid() == Fluids.EMPTY)
            return;
        world.setBlock(pos, lowerState.setValue(TFCBlockStateProperties.TALL_PLANT_PART, Part.LOWER).setValue(TFCBlockStateProperties.AGE_3, age), flags);
        world.setBlock(pos.above(), getStateWithFluid(defaultBlockState().setValue(TFCBlockStateProperties.TALL_PLANT_PART, Part.UPPER).setValue(TFCBlockStateProperties.AGE_3, age), world.getFluidState(pos.above()).getType()), flags);
    }
}
