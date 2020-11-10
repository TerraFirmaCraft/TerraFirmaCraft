/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.block.Block;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public interface ITallPlant
{
    VoxelShape PLANT_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    VoxelShape SHORTER_PLANT_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);

    default VoxelShape getTallShape(int age, IBlockReader world, BlockPos pos)
    {
        Part part = getPlantPart(world, pos);
        if (part == Part.LOWER || part == Part.MIDDLE)
            return PLANT_SHAPE;
        switch (age)
        {
            case 0:
            case 1:
                return SHORTER_PLANT_SHAPE;
            default:
                return PLANT_SHAPE;
        }

    }

    default Part getPlantPart(IBlockReader world, BlockPos pos)
    {
        if (world.getBlockState(pos.below()).getBlock() != this && world.getBlockState(pos.above()).getBlock() == this)
        {
            return Part.LOWER;
        }
        if (world.getBlockState(pos.below()).getBlock() == this && world.getBlockState(pos.above()).getBlock() == this)
        {
            return Part.MIDDLE;
        }
        if (world.getBlockState(pos.below()).getBlock() == this && world.getBlockState(pos.above()).getBlock() != this)
        {
            return Part.UPPER;
        }
        return Part.SINGLE;
    }

    enum Part implements IStringSerializable
    {
        UPPER,
        MIDDLE,
        LOWER,
        SINGLE;

        @Override
        public String toString()
        {
            return this.getSerializedName();
        }

        @Override
        public String getSerializedName()
        {
            return name().toLowerCase();
        }
    }
}
