package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;

import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

public class SluiceBlock extends ExtendedBlock implements EntityBlockExtension
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty UPPER = TFCBlockStateProperties.UPPER;

    public SluiceBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(UPPER, true).setValue(FACING, Direction.NORTH));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!level.isClientSide)
        {
            final BlockPos fluidPos = pos.relative(state.getValue(FACING).getOpposite()).below();
            final FluidState fluid = level.getFluidState(fluidPos);
            if (fluid.is(FluidTags.WATER))
            {
                level.setBlockAndUpdate(fluidPos, Blocks.AIR.defaultBlockState());
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        if (direction == Direction.UP && facingState.getBlock() instanceof LiquidBlock && facingState.getValue(LiquidBlock.LEVEL) < 15)
        {
            level.setBlock(facingPos, Blocks.AIR.defaultBlockState(), 3);
        }

        Direction facing = state.getValue(FACING);
        if (!state.getValue(UPPER) && direction == facing && !facingState.is(this))
        {
            return Blocks.AIR.defaultBlockState();
        }
        else if (direction == facing.getOpposite() && !facingState.is(this))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion)
    {
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(FACING))
        {
            BlockPos fluidPos = pos.relative(state.getValue(FACING).getOpposite()).below();
            if (level.getFluidState(fluidPos).is(FluidTags.WATER))
            {
                level.setBlockAndUpdate(fluidPos, Blocks.AIR.defaultBlockState());
            }
        }
        super.wasExploded(level, pos, explosion);
    }
}
