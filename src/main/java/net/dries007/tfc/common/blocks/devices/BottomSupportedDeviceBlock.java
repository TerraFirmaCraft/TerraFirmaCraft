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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public abstract class BottomSupportedDeviceBlock extends DeviceBlock
{
    public static boolean canSurvive(LevelReader level, BlockPos pos)
    {
        final BlockPos below = pos.below();
        final BlockState state = level.getBlockState(below);
        return state.isFaceSturdy(level, below, Direction.UP) || Helpers.isBlock(state, TFCTags.Blocks.SOLID_TOP_FACE);
    }

    private final VoxelShape shape;

    protected BottomSupportedDeviceBlock(ExtendedProperties properties, InventoryRemoveBehavior removeBehavior)
    {
        this(properties, removeBehavior, Shapes.block());
    }

    protected BottomSupportedDeviceBlock(ExtendedProperties properties, InventoryRemoveBehavior removeBehavior, VoxelShape shape)
    {
        super(properties, removeBehavior);
        this.shape = shape;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return shape;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return canSurvive(level, pos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return canSurvive(context.getLevel(), context.getClickedPos()) ? super.getStateForPlacement(context) : null;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return facing == Direction.DOWN && !canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }
}
