/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateRange;

public class SpreadingCaneBlock extends SpreadingBushBlock implements IBushBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape CANE_EAST = Block.box(0.0D, 3.0D, 0.0D, 8.0D, 12.0D, 16.0D);
    private static final VoxelShape CANE_WEST = Block.box(8.0D, 3.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    private static final VoxelShape CANE_SOUTH = Block.box(0.0D, 3.0D, 0.0D, 16.0D, 12.0D, 8.0D);
    private static final VoxelShape CANE_NORTH = Block.box(0.0D, 3.0D, 8.0D, 16.0D, 12.0D, 16.0D);

    public SpreadingCaneBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages, Supplier<? extends Block> companion, int maxHeight, Supplier<ClimateRange> climateRange)
    {
        super(properties, productItem, stages, companion, maxHeight, climateRange);
    }

    @Override
    protected ItemStack getTrimItemStack()
    {
        return new ItemStack(companion.get());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(FACING))
            {
                case NORTH -> CANE_NORTH;
                case WEST -> CANE_WEST;
                case SOUTH -> CANE_SOUTH;
                default -> CANE_EAST;
            };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    protected BlockState getDeadState(BlockState state)
    {
        return TFCBlocks.DEAD_CANE.get().defaultBlockState().setValue(STAGE, state.getValue(STAGE)).setValue(FACING, state.getValue(FACING));
    }

    @Override
    protected void propagate(Level level, BlockPos pos, Random random, BlockState state)
    {
        final int stage = state.getValue(STAGE);
        BlockState placeState = companion.get().defaultBlockState().setValue(STAGE, stage).setValue(LIFECYCLE, state.getValue(LIFECYCLE));
        if (stage == 2 && placeState.canSurvive(level, pos))
        {
            level.setBlockAndUpdate(pos, placeState);
        }
    }

    @Override
    protected boolean mayDie(Level level, BlockPos pos, BlockState state, int monthsSpentDying)
    {
        BlockState parent = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
        if (Helpers.isBlock(parent, TFCTags.Blocks.SPREADING_BUSH))
        {
            return false; // if the parent is alive we shouldn't die
        }
        return super.mayDie(level, pos, state, monthsSpentDying);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return true;
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return Helpers.isBlock(level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())), TFCTags.Blocks.ANY_SPREADING_BUSH);
    }
}
