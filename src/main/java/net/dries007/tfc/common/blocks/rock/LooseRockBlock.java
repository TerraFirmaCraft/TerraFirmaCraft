/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public class LooseRockBlock extends GroundcoverBlock implements IFluidLoggable
{
    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_3;

    private static final VoxelShape ONE = makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
    private static final VoxelShape TWO = makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);
    private static final VoxelShape THREE = makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D);

    public LooseRockBlock(Properties properties)
    {
        super(properties, VoxelShapes.empty(), null);

        setDefaultState(getDefaultState().with(COUNT, 1));
    }

    @Override
    @Nonnull
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState stateAt = context.getWorld().getBlockState(context.getPos());
        if (stateAt.isIn(this))
        {
            return stateAt.with(COUNT, Math.min(3, stateAt.get(COUNT) + 1));
        }
        return super.getStateForPlacement(context);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(COUNT));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch (state.get(COUNT))
        {
            case 1:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
        }
        throw new IllegalStateException("Unknown value for property LooseRockBlock#ROCKS: " + state.get(COUNT));
    }
}
