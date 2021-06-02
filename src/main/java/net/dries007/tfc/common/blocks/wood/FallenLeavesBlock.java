/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import net.dries007.tfc.common.blocks.GroundcoverBlock;

public class FallenLeavesBlock extends GroundcoverBlock
{
    private static final VoxelShape VERY_FLAT = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public FallenLeavesBlock(Properties properties)
    {
        super(properties, VERY_FLAT, null);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return true; // Not for the purposes of leaf decay, but for the purposes of seasonal updates
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VERY_FLAT;
    }
}
