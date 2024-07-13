/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
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
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, List<Component> text, boolean isDebug)
    {
        final BlockPos sourcePos = pos.below();
        final ClimateRange range = climateRange.get();

        if (state.getValue(getFluidProperty()).getFluid() == Fluids.EMPTY)
        {
            text.add(Component.translatable("tfc.tooltip.berry_bush.not_underwater"));
        }
        text.add(FarmlandBlock.getTemperatureTooltip(level, sourcePos, range, false));
    }

    @Override
    protected int getHydration(LevelAccessor level, BlockPos pos, BlockState state)
    {
        return state.getValue(FLUID).getFluid() != Fluids.EMPTY ? 100 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(getFluidProperty()));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        return state.canSurvive(level, currentPos) ? super.updateShape(state, facing, facingState, level, currentPos, facingPos) : state.getFluidState().createLegacyBlock();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();
        state = FluidHelpers.fillWithFluid(state, context.getLevel().getFluidState(context.getClickedPos()).getType());
        return state;
    }

    @Override
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
        return super.mayPlaceOn(state, level, pos) || Helpers.isBlock(level.getBlockState(pos.below()), TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON);
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
