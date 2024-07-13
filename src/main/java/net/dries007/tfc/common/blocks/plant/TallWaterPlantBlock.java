/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class TallWaterPlantBlock extends TFCTallGrassBlock implements IFluidLoggable
{
    public static final EnumProperty<ITallPlant.Part> PART = TFCBlockStateProperties.TALL_PLANT_PART;

    public static TallWaterPlantBlock create(RegistryPlant plant, FluidProperty fluid, Properties properties)
    {
        return new TallWaterPlantBlock(ExtendedProperties.of(properties))
        {
            @Override
            public RegistryPlant getPlant()
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

    protected TallWaterPlantBlock(ExtendedProperties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any().setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)).setValue(PART, Part.LOWER));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockState belowState = level.getBlockState(pos.below());
        if (state.getValue(PART) == Part.LOWER)
        {
            if (Helpers.isBlock(state, TFCTags.Blocks.HALOPHYTE))
            {
                return Helpers.isBlock(belowState, TFCTags.Blocks.HALOPHYTE_PLANTABLE_ON);
            }
            return Helpers.isBlock(belowState, TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON);
        }
        else
        {
            if (state.getBlock() != this)
            {
                return Helpers.isBlock(belowState, TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            }
            return belowState.getBlock() == this && belowState.getValue(PART) == Part.LOWER;
        }
    }

    //Used on player placement, not worldgen
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(pos);
        BlockState state = updateStateWithCurrentMonth(defaultBlockState());

        if (getFluidProperty().canContain(fluidState.getType()))
        {
            state = state.setValue(getFluidProperty(), getFluidProperty().keyFor(fluidState.getType()));
        }

        return pos.getY() < context.getLevel().getMaxBuildHeight() - 1 && context.getLevel().getBlockState(pos.above()).canBeReplaced(context) ? state : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(getFluidProperty()));
    }

    //Used in worldgen, not on player placement
    @Override
    public void placeTwoHalves(LevelAccessor level, BlockPos pos, int flags, RandomSource random)
    {
        final BlockPos posAbove = pos.above();
        final int age = random.nextInt(4);
        final Fluid fluidBottom = level.getFluidState(pos).getType();
        final Fluid fluidTop = level.getFluidState(posAbove).getType();
        if (!fluidBottom.isSame(Fluids.EMPTY))
        {
            final BlockState state = FluidHelpers.fillWithFluid(defaultBlockState().setValue(AGE, age).setValue(PART, Part.LOWER), fluidBottom);
            final BlockState stateUp = FluidHelpers.fillWithFluid(defaultBlockState().setValue(AGE, age).setValue(PART, Part.UPPER), fluidTop);
            if (state != null && stateUp != null)
            {
                level.setBlock(pos, state, flags);
                level.setBlock(posAbove, stateUp, flags);
            }
        }
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }
}
