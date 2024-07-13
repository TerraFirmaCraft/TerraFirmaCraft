/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
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
    protected BlockState growAndPropagate(Level level, BlockPos pos, RandomSource random, BlockState state)
    {
        if (!state.getValue(LIFECYCLE).active())
        {
            return state; // Only grow when active
        }

        final int prevStage = state.getValue(STAGE);
        if (prevStage < 2)
        {
            return state.setValue(STAGE, prevStage + 1); // Increment stage if possible
        }

        // Otherwise, try and convert to a bush bock
        // Bush blocks start at stage = 1 when they're grown from another bush block, as stage = 0 is just for newly planted
        final BlockState placeState = companion.get().defaultBlockState().setValue(STAGE, 1).setValue(LIFECYCLE, state.getValue(LIFECYCLE));
        if (placeState.canSurvive(level, pos))
        {
            level.setBlockAndUpdate(pos, placeState);
        }

        return state;
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

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        return new ItemStack(companion.get());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
