/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceState;

public class UnderwaterSurfaceState implements SurfaceState
{
    private static final long VARIANT_NOISE_SEED = 9128639581632L;

    private final Noise2D variantNoise = new OpenSimplex2D(VARIANT_NOISE_SEED).octaves(2).spread(0.015f);
    private final boolean deep;

    public UnderwaterSurfaceState(boolean deep)
    {
        this.deep = deep;
    }

    @Override
    public BlockState getState(SurfaceBuilderContext context)
    {
        final BlockPos pos = context.pos();
        final float variantValue = variantNoise.noise(pos.getX(), pos.getZ());
        if (variantValue > 0)
        {
            if (deep)
            {
                return context.getRock().sandstone().defaultBlockState(); // Sandstone
            }
            return context.getRock().sand().defaultBlockState(); // Sand
        }
        return context.getRock().gravel().defaultBlockState(); // Gravel
    }
}
