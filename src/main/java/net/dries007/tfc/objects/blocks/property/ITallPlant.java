/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.property;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface ITallPlant
{
    AxisAlignedBB PLANT_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
    AxisAlignedBB SHORTER_PLANT_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.5D, 0.875D);

    default AxisAlignedBB getTallBoundingBax(int age, IBlockState state, IBlockAccess source, BlockPos pos)
    {
        if (getPlantPart(source, pos) == EnumBlockPart.LOWER || getPlantPart(source, pos) == EnumBlockPart.MIDDLE)
            return PLANT_AABB.offset(state.getOffset(source, pos));
        switch (age)
        {
            case 0:
            case 1:
                return SHORTER_PLANT_AABB.offset(state.getOffset(source, pos));
            default:
                return PLANT_AABB.offset(state.getOffset(source, pos));
        }

    }

    default EnumBlockPart getPlantPart(IBlockAccess world, BlockPos pos)
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
