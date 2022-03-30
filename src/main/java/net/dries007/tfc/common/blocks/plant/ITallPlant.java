/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public interface ITallPlant
{
    default Part getPlantPart(BlockGetter level, BlockPos pos)
    {
        if (level.getBlockState(pos.below()).getBlock() != this)
        {
            return Part.LOWER;
        }
        return Part.UPPER;
    }

    enum Part implements StringRepresentable
    {
        UPPER,
        LOWER;

        private final String serializedName;

        Part()
        {
            serializedName = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }
}
