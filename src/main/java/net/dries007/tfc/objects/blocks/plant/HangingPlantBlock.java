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
import net.minecraft.world.IWorldReader;

public abstract class HangingPlantBlock extends PlantBlock
{
    private static final BooleanProperty HANGING = BlockStateProperties.HANGING;

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
}
