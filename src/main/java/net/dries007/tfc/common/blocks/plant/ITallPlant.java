/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface ITallPlant
{
    default Part getPlantPart(IBlockReader world, BlockPos pos)
    {
        if (world.getBlockState(pos.below()).getBlock() != this)
        {
            return Part.LOWER;
        }
        return Part.UPPER;
    }

    enum Part implements IStringSerializable
    {
        UPPER,
        LOWER;

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
