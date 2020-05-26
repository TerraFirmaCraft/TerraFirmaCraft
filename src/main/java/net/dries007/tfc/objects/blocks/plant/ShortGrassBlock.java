/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plant;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public abstract class ShortGrassBlock extends PlantBlock
{
    protected static final VoxelShape GRASS_SHAPE = makeCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape SHORTER_GRASS_SHAPE = makeCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);
    protected static final VoxelShape SHORT_GRASS_SHAPE = makeCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    protected static final VoxelShape SHORTEST_GRASS_SHAPE = makeCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

    public ShortGrassBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch (state.get(AGE))
        {
            case 0:
                return SHORTEST_GRASS_SHAPE;
            case 1:
                return SHORTER_GRASS_SHAPE;
            case 2:
                return SHORT_GRASS_SHAPE;
            default:
                return GRASS_SHAPE;
        }
    }

    @Override
    public OffsetType getOffsetType()
    {
        return OffsetType.XZ;
    }
}
