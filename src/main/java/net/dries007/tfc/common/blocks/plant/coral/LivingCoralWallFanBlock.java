/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.fluids.TFCFluids;

/**
 * {@link CoralWallFanBlock}
 */
public class LivingCoralWallFanBlock extends CoralWallFanBlock
{
    private final Supplier<? extends Block> deadBlock;

    public LivingCoralWallFanBlock(Supplier<? extends Block> deadBlock, BlockBehaviour.Properties builder)
    {
        super(builder);
        this.deadBlock = deadBlock;
    }

    @Override
    protected void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        tryScheduleDieTick(state, worldIn, pos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (!scanForWater(state, level, pos))
        {
            level.setBlock(pos, deadBlock.get().defaultBlockState().setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)).setValue(FACING, state.getValue(FACING)), 2);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        else
        {
            if (state.getValue(getFluidProperty()).getFluid() == TFCFluids.SALT_WATER.getSource())
            {
                level.scheduleTick(currentPos, TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getSource().getTickDelay(level));
            }

            this.tryScheduleDieTick(state, level, currentPos);
            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }
}
