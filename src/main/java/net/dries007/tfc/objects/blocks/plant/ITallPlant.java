/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plant;

import net.minecraft.block.Block;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public interface ITallPlant
{
    VoxelShape PLANT_SHAPE = Block.makeCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    VoxelShape SHORTER_PLANT_SHAPE = Block.makeCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);

    default VoxelShape getTallShape(int age, IBlockReader world, BlockPos pos)
    {
        EnumBlockPart part = getPlantPart(world, pos);
        if (part == EnumBlockPart.LOWER || part == EnumBlockPart.MIDDLE)
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

    default EnumBlockPart getPlantPart(IBlockReader world, BlockPos pos)
    {
        if (world.getBlockState(pos.down()).getBlock() != this && world.getBlockState(pos.up()).getBlock() == this)
        {
            return EnumBlockPart.LOWER;
        }
        if (world.getBlockState(pos.down()).getBlock() == this && world.getBlockState(pos.up()).getBlock() == this)
        {
            return EnumBlockPart.MIDDLE;
        }
        if (world.getBlockState(pos.down()).getBlock() == this && world.getBlockState(pos.up()).getBlock() != this)
        {
            return EnumBlockPart.UPPER;
        }
        return EnumBlockPart.SINGLE;
    }

    enum EnumBlockPart implements IStringSerializable
    {
        UPPER,
        MIDDLE,
        LOWER,
        SINGLE;

        public String toString()
        {
            return this.getName();
        }

        public String getName()
        {
            return name().toLowerCase();
        }
    }
}
