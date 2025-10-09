/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blockentities.AnemometerBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;

public class AnemometerBlock extends DeviceBlock
{
    public static BooleanProperty ATTACHED_WIND_DEVICES = TFCBlockStateProperties.ATTACHED_WIND_DEVICES;
    private static final VoxelShape SHAPE = box(6D, 0.0D, 6D, 10D, 8.0D, 10D);
    private static final VoxelShape SHAPE_ATTACHED = box(6D, 0.0D, 6D, 10D, 16.0D, 10D);

    public AnemometerBlock(ExtendedProperties properties)
    {
        super(properties, DeviceBlock.InventoryRemoveBehavior.NOOP);
        registerDefaultState(getStateDefinition().any().setValue(ATTACHED_WIND_DEVICES, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos blockpos = context.getClickedPos();
        return context.getLevel().getBlockState(blockpos.above()).is(TFCBlocks.VANE.get())
            ? this.defaultBlockState().setValue(ATTACHED_WIND_DEVICES, true)
            : this.defaultBlockState();
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos blockpos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN)
        {
            if (!this.canSurvive(state, level, blockpos))
            {
                return Blocks.AIR.defaultBlockState();
            }
        }
        else if (facing == Direction.UP)
        {
            return level.getBlockState(blockpos.above()).is(TFCBlocks.VANE.get())
                ? this.defaultBlockState().setValue(ATTACHED_WIND_DEVICES, true)
                : this.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, blockpos, facingPos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        super.onRemove(state,level,pos,newState,isMoving);
        level.updateNeighborsAt(pos.below(), this); // needs this for strong power to update afaik
    }

    @Override
    protected boolean isSignalSource(BlockState state)
    {
        return true;
    }

    @Override
    protected int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
    {
        if (side == Direction.UP)
        {
            return getSignal(blockState, blockAccess, pos, side);
        }
        return 0;
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
    {
        if (blockAccess.getBlockEntity(pos) instanceof AnemometerBlockEntity anemometer)
        {
            return anemometer.getRedstoneSignal();
        }
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(ATTACHED_WIND_DEVICES);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(ATTACHED_WIND_DEVICES) ? SHAPE_ATTACHED : SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos)
    {
        return canSupportCenter(levelReader, pos.below(), Direction.UP);
    }
}
