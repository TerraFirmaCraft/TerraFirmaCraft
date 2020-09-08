/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TFCLeavesBlock extends LeavesBlock
{
    public TFCLeavesBlock(Properties properties)
    {
        super(properties);

        // Both these properties are unused by us, but kept here for compatibility with vanilla
        setDefaultState(stateContainer.getBaseState().with(DISTANCE, 0).with(PERSISTENT, false));
    }

    @Override
    public boolean ticksRandomly(BlockState state)
    {
        return false;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        // NO-OP
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        // Check for leaf decay
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }
}
