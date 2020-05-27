/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plant;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public abstract class HangingPlantBlock extends PlantBlock
{
    protected static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    protected static final VoxelShape NOT_HANGING_SHAPE = makeCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    public HangingPlantBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        for (Direction direction : new Direction[] {Direction.UP, Direction.DOWN})
        {
            if (worldIn.getBlockState(pos.offset(direction)).getMaterial() == Material.LEAVES)
            {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        if (context.getWorld().getBlockState(context.getPos().offset(Direction.UP)).getMaterial() == Material.LEAVES)
        {
            return this.getDefaultState().with(HANGING, true);
        }
        if (context.getWorld().getBlockState(context.getPos().offset(Direction.DOWN)).getMaterial() == Material.LEAVES)
        {
            return this.getDefaultState().with(HANGING, false);
        }
        return null;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(HANGING);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (state.get(HANGING))
        {
            return super.getShape(state, worldIn, pos, context);
        }
        else
        {
            return NOT_HANGING_SHAPE;
        }
    }
}
