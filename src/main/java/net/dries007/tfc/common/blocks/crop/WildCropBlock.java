/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.ISpecialPile;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.TFCBushBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Month;

public class WildCropBlock extends TFCBushBlock implements ISpecialPile
{
    public static boolean isMature(LevelAccessor level)
    {
        final int month = Calendars.get(level).getCalendarMonthOfYear().ordinal();
        return month >= Month.JUNE.ordinal() && month <= Month.OCTOBER.ordinal();
    }

    public static final BooleanProperty MATURE = TFCBlockStateProperties.MATURE;

    public WildCropBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(MATURE, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(MATURE, isMature(context.getLevel()));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (state.getValue(MATURE) != isMature(level))
        {
            level.setBlockAndUpdate(pos, state.cycle(MATURE));
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return CropBlock.FULL_SHAPE;
    }

    @Override
    public BlockState getHiddenState(BlockState internalState, boolean byPlayer)
    {
        return internalState.setValue(MATURE, false);
    }

    @Override
    @Nullable
    public BlockState getHiddenStateAbove(@Nullable BlockState aboveState, boolean byPlayer)
    {
        return aboveState != null && aboveState.hasProperty(MATURE) ? aboveState.setValue(MATURE, false) : aboveState;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return Helpers.isBlock(level.getBlockState(pos), TFCTags.Blocks.GRASS_PLANTABLE_ON);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(MATURE));
    }
}
