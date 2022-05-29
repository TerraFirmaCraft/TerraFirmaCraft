/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.DensityFunction;

public record MutableDensityFunctionContext(BlockPos.MutableBlockPos cursor) implements DensityFunction.FunctionContext
{
    @Override
    public int blockX()
    {
        return cursor.getX();
    }

    @Override
    public int blockY()
    {
        return cursor.getY();
    }

    @Override
    public int blockZ()
    {
        return cursor.getZ();
    }
}
