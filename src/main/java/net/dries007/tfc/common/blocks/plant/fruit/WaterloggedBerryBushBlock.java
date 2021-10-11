/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.ClimateRange;

public class WaterloggedBerryBushBlock extends StationaryBerryBushBlock implements IFluidLoggable
{
    public static final FluidProperty FLUID = TFCBlockStateProperties.FRESH_WATER;

    public WaterloggedBerryBushBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] lifecycle, Supplier<ClimateRange> climateRange)
    {
        super(properties, productItem, lifecycle, climateRange);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(getFluidProperty()));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!canSurvive(stateIn, worldIn, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        else
        {
            final Fluid containedFluid = stateIn.getValue(getFluidProperty()).getFluid();
            if (containedFluid != Fluids.EMPTY)
            {
                worldIn.getLiquidTicks().scheduleTick(currentPos, containedFluid, containedFluid.getTickDelay(worldIn));
            }
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(getFluidProperty(), getFluidProperty().keyForOrEmpty(context.getLevel().getFluidState(context.getClickedPos()).getType()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return super.mayPlaceOn(state, level, pos) || level.getBlockState(pos.below()).is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON);
    }

    @Override
    protected BlockState getNewState(Level level, BlockPos pos)
    {
        return super.getNewState(level, pos).setValue(getFluidProperty(), getFluidProperty().keyForOrEmpty(level.getFluidState(pos).getType()));
    }

    @Override
    protected boolean canPlaceNewBushAt(Level level, BlockPos pos, BlockState placementState)
    {
        return placementState.canSurvive(level, pos) && (FluidHelpers.isAirOrEmptyFluid(level.getBlockState(pos)) && getFluidProperty().canContain(level.getFluidState(pos).getType()));
    }
}
