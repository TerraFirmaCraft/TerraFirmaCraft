/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.block.BlockState;

import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.RockData;

public class DeepSoilSurfaceState extends SoilSurfaceState
{
    public DeepSoilSurfaceState()
    {
        super(SoilBlockType.GRASS); // ignored
    }

    @Override
    public BlockState state(RockData rockData, int x, int y, int z, float temperature, float rainfall, boolean salty)
    {
        if (rainfall < RAINFALL_SAND)
        {
            // Sandy
            return sandstone(rockData, x, z);
        }
        else if (rainfall < RAINFALL_SAND_SANDY_MIX)
        {
            // Sandy - Sand Transition Zone
            float noise = patchNoise.noise(x, z);
            return noise > 0.2f * (rainfall - RAINFALL_SAND_SANDY_MEAN) / RAINFALL_SAND_SANDY_RANGE ? sandstone(rockData, x, z) : gravel(rockData, x, y, z);
        }
        else
        {
            // All others
            return gravel(rockData, x, y, z);
        }
    }

    private BlockState gravel(RockData rockData, int x, int y, int z)
    {
        return rockData.getRock(x, y, z).getBlock(Rock.BlockType.GRAVEL).defaultBlockState();
    }

    private BlockState sandstone(RockData rockData, int x, int z)
    {
        return rockData.getTopRock(x, z).getSandstone().defaultBlockState();
    }
}
