/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import net.dries007.tfc.common.fluids.TFCFluids;

/**
 * {@link net.minecraft.world.level.block.CoralPlantBlock}
 */
public class LivingCoralPlantBlock extends TFCCoralPlantBlock
{
    private final Supplier<? extends Block> deadBlock;

    public LivingCoralPlantBlock(VoxelShape shape, Supplier<? extends Block> deadBlock, BlockBehaviour.Properties properties)
    {
        super(shape, properties);
        this.deadBlock = deadBlock;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        tryScheduleDieTick(state, worldIn, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand)
    {
        if (!scanForWater(state, worldIn, pos))
        {
            worldIn.setBlock(pos, deadBlock.get().defaultBlockState().setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)), 2);
        }

    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        else
        {
            this.tryScheduleDieTick(stateIn, worldIn, currentPos);
            if (stateIn.getValue(getFluidProperty()).getFluid().is(FluidTags.WATER))
            {
                worldIn.getLiquidTicks().scheduleTick(currentPos, TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getSource().getTickDelay(worldIn));
            }

            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }
}
