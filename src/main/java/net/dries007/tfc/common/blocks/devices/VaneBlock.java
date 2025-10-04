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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blockentities.VaneBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;

public class VaneBlock extends ExtendedBlock implements EntityBlockExtension, IForgeBlockExtension
{
    public static BooleanProperty ATTACHED_WIND_DEVICES = TFCBlockStateProperties.ATTACHED_WIND_DEVICES;
    private static final VoxelShape SHAPE = box(6D, 0.0D, 6D, 10D, 12.0D, 10D);
    private static final VoxelShape SHAPE_ATTACHED = box(6D, 0.0D, 6D, 10D, 6.0D, 10D);

    public VaneBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(ATTACHED_WIND_DEVICES, false));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos blockpos = context.getClickedPos();
        return context.getLevel().getBlockState(blockpos.below()).is(TFCBlocks.ANEMOMETER.get())
            ? this.defaultBlockState().setValue(ATTACHED_WIND_DEVICES, true)
            : this.defaultBlockState();
    }

    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos blockpos, BlockPos facingPos)
    {
        return level.getBlockState(blockpos.below()).is(TFCBlocks.ANEMOMETER.get())
            ? this.defaultBlockState().setValue(ATTACHED_WIND_DEVICES, true)
            : this.defaultBlockState();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(ATTACHED_WIND_DEVICES) ? SHAPE_ATTACHED : SHAPE;
    }

    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.below(), this);
        level.removeBlockEntity(pos); // wasn't getting removed otherwise?
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(ATTACHED_WIND_DEVICES);
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    protected boolean isSignalSource(BlockState state)
    {
        return true;
    }

    protected int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
    {
        return getSignal(blockState, blockAccess, pos, side);
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
    {
        if (blockAccess.getBlockEntity(pos) instanceof VaneBlockEntity vane)
        {
            return vane.getRedstoneSignal();
        }
        return 0;
    }

}
