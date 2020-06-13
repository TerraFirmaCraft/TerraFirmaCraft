/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plant;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import net.dries007.tfc.objects.blocks.IBlockItemSupplier;
import net.dries007.tfc.objects.items.blockitems.FloatingWaterPlantItem;

public abstract class FloatingWaterPlantBlock extends PlantBlock implements IBlockItemSupplier
{
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

    public FloatingWaterPlantBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).getMaterial() == Material.WATER;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public BlockItem apply(Item.Properties properties)
    {
        return new FloatingWaterPlantItem(this, properties);
    }
}
