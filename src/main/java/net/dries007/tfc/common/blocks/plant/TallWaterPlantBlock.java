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

        setDefaultState(getDefaultState().any().with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)).with(TFCBlockStateProperties.TALL_PLANT_PART, Part.LOWER));
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.down());
        if (state.get(PART) == Part.LOWER)
        {
            return belowState.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON);
        }
        else
        {
            if (state.getBlock() != this)
            {
                return belowState.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            }
            return belowState.getBlock() == this && belowState.get(PART) == Part.LOWER;
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos pos = context.getPos();
        FluidState fluidState = context.getWorld().getFluidState(pos);
        BlockState state = updateStateWithCurrentMonth(getDefaultState());

        if (getFluidProperty().canContain(fluidState.getFluid()))
        {
            state = state.with(getFluidProperty(), getFluidProperty().keyFor(fluidState.getFluid()));
        }

        return pos.getY() < 255 && context.getWorld().getBlockState(pos.up()).canBeReplaced(context) ? state : null;
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
        BlockState lowerState = getStateWithFluid(getDefaultState(), world.getFluidState(pos).getType());
        if (lowerState.get(getFluidProperty()).getFluid() == Fluids.EMPTY)
            return;
        world.setBlockState(pos, lowerState.with(TFCBlockStateProperties.TALL_PLANT_PART, Part.LOWER).with(TFCBlockStateProperties.AGE_3, age), flags);
        world.setBlockState(pos.up(), getStateWithFluid(getDefaultState().with(TFCBlockStateProperties.TALL_PLANT_PART, Part.UPPER).with(TFCBlockStateProperties.AGE_3, age), world.getFluidState(pos.up()).getType()), flags);
    }
}
