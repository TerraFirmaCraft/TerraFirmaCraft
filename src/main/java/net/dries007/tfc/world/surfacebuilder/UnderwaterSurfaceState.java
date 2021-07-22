/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.block.BlockState;

import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class UnderwaterSurfaceState implements ISurfaceState
{
    private static final long VARIANT_NOISE_SEED = 9128639581632L;

    private final INoise2D variantNoise = new OpenSimplex2D(VARIANT_NOISE_SEED).octaves(2).spread(0.015f);
    private final boolean deep;

    public UnderwaterSurfaceState(boolean deep)
    {
        this.deep = deep;
    }

    @Override
    public BlockState state(RockData rockData, int x, int y, int z, float temperature, float rainfall, boolean salty)
    {
        final float variantValue = variantNoise.noise(x, z);
        if (variantValue > 0)
        {
            if (deep)
            {
                return rockData.getTopRock(x, z).getSandstone().defaultBlockState(); // Sandstone
            }
            return rockData.getTopRock(x, z).getSand().defaultBlockState(); // Sand
        }
        return rockData.getTopRock(x, z).getBlock(Rock.BlockType.GRAVEL).defaultBlockState(); // Gravel
    }
}
