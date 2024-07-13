/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

public class SluiceBlock extends DeviceBlock implements EntityBlockExtension
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty UPPER = TFCBlockStateProperties.UPPER;

    public static BlockPos getFluidOutputPos(BlockState state, BlockPos pos)
    {
        return pos.relative(state.getValue(FACING), 2).below();
    }

    private static VoxelShape createTopShape(Direction direction)
    {
        return Shapes.or(
            Helpers.rotateShape(direction, 0, 0, 0, 16, 10, 1),
            Helpers.rotateShape(direction, 0, 0, 1, 16, 9, 4),
            Helpers.rotateShape(direction, 0, 0, 4, 16, 12, 5),
            Helpers.rotateShape(direction, 0, 0, 5, 16, 11, 8),
            Helpers.rotateShape(direction, 0, 0, 8, 16, 14, 9),
            Helpers.rotateShape(direction, 0, 0, 9, 16, 13, 12),
            Helpers.rotateShape(direction, 0, 0, 12, 16, 16, 13),
            Helpers.rotateShape(direction, 0, 0, 13, 16, 15, 16)
        );
    }

    private static VoxelShape createBottomShape(Direction direction)
    {
        return Shapes.or(
            Helpers.rotateShape(direction, 0, 0, 0, 16, 2, 1),
            Helpers.rotateShape(direction, 0, 0, 1, 16, 1, 4),
            Helpers.rotateShape(direction, 0, 0, 4, 16, 4, 5),
            Helpers.rotateShape(direction, 0, 0, 5, 16, 3, 8),
            Helpers.rotateShape(direction, 0, 0, 8, 16, 6, 9),
            Helpers.rotateShape(direction, 0, 0, 9, 16, 5, 12),
            Helpers.rotateShape(direction, 0, 0, 12, 16, 8, 13),
            Helpers.rotateShape(direction, 0, 0, 13, 16, 7, 16)
        );
    }

    private static final VoxelShape[] TOP_SHAPES = Helpers.computeHorizontalShapes(SluiceBlock::createTopShape);
    private static final VoxelShape[] BOTTOM_SHAPES = Helpers.computeHorizontalShapes(SluiceBlock::createBottomShape);

    public SluiceBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(UPPER, true).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(UPPER, FACING));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
    {
        level.setBlockAndUpdate(pos.relative(state.getValue(FACING)), state.setValue(UPPER, false));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final BlockPos pos = context.getClickedPos();
        final Direction direction = context.getHorizontalDirection();
        final Level level = context.getLevel();
        if (level.getBlockState(pos).canBeReplaced() && level.getBlockState(pos.relative(direction)).canBeReplaced())
        {
            return defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(UPPER, true);
        }
        return null; // do not place the block
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        final int idx = state.getValue(FACING).get2DDataValue();
        return state.getValue(UPPER) ? TOP_SHAPES[idx] : BOTTOM_SHAPES[idx];
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        // 1.13 water physics causes items to fly everywhere, we need them to get 'caught' on the sluice
        if (state.getValue(UPPER) && entity instanceof ItemEntity item)
        {
            item.setDeltaMovement(0D, 0D, 0D);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!level.isClientSide)
        {
            final BlockPos fluidPos = getFluidOutputPos(state, pos);
            final BlockState originalState = level.getBlockState(fluidPos);
            final FluidState fluid = originalState.getFluidState();
            if (Helpers.isFluid(fluid, TFCTags.Fluids.USABLE_IN_SLUICE) || FluidHelpers.isMeltableIce(originalState))
            {
                final BlockState resultState = FluidHelpers.emptyFluidFrom(originalState);

                level.setBlockAndUpdate(fluidPos, resultState);
                if (!resultState.isAir())
                {
                    level.scheduleTick(fluidPos, resultState.getBlock(), 1);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        Direction facing = state.getValue(FACING);
        if (!state.getValue(UPPER) && direction == facing.getOpposite() && !Helpers.isBlock(facingState, this))
        {
            return Blocks.AIR.defaultBlockState();
        }
        else if (state.getValue(UPPER) && direction == facing && !Helpers.isBlock(facingState, this))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion)
    {
        final BlockState state = level.getBlockState(pos);
        if (state.hasProperty(FACING))
        {
            final BlockPos fluidPos = getFluidOutputPos(state, pos);
            final BlockState fluidState = level.getBlockState(fluidPos);
            if (Helpers.isFluid(fluidState.getFluidState(), TFCTags.Fluids.USABLE_IN_SLUICE))
            {
                level.setBlockAndUpdate(fluidPos, FluidHelpers.emptyFluidFrom(fluidState));
            }
        }
        super.wasExploded(level, pos, explosion);
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
