package net.dries007.tfc.common.blocks.wood;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class TFCBookshelfBlock extends Block
{
    protected static final VoxelShape INNER_COLUMN = makeCuboidShape(1d, 0, 1d, 15d, 16d, 15d);
    protected static final VoxelShape FRAME_A = VoxelShapes.combineAndSimplify(makeCuboidShape(0, 0, 0, 1d, 16d, 16d), makeCuboidShape(0, 1d, 1d, 1d, 15d, 15d), IBooleanFunction.ONLY_FIRST);
    protected static final VoxelShape FRAME_B = VoxelShapes.combineAndSimplify(makeCuboidShape(15d, 0, 0, 16d, 16, 16), makeCuboidShape(15d, 1d, 1d, 16d, 15d, 15d), IBooleanFunction.ONLY_FIRST);
    protected static final VoxelShape FRAME_C = VoxelShapes.combineAndSimplify(makeCuboidShape(1d, 0, 0, 15d, 16d, 1d), makeCuboidShape(1d, 1d, 0, 15d, 15d, 1d), IBooleanFunction.ONLY_FIRST);
    protected static final VoxelShape FRAME_D = VoxelShapes.combineAndSimplify(makeCuboidShape(1d, 0, 15d, 15d, 16d, 16d), makeCuboidShape(1d, 1d, 15d, 15d, 15d, 16d), IBooleanFunction.ONLY_FIRST);

    public TFCBookshelfBlock(Properties properties) { super(properties); }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.or(INNER_COLUMN, FRAME_A, FRAME_B, FRAME_C, FRAME_D);
    }
}
