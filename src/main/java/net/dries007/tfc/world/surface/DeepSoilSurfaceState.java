/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.soil.SoilBlockType;

public class DeepSoilSurfaceState extends SoilSurfaceState
{
    public DeepSoilSurfaceState()
    {
        super(SoilBlockType.GRASS); // ignored
    }

    @Override
    public BlockState getState(SurfaceBuilderContext context)
    {
        final BlockPos pos = context.pos();
        final float rainfall = context.rainfall();

        if (rainfall < RAINFALL_SAND)
        {
            // Sandy
            return context.getRock().sandstone().defaultBlockState();
        }
        else if (rainfall < RAINFALL_SAND_SANDY_MIX)
        {
            // Sandy - Sand Transition Zone
            float noise = patchNoise.noise(pos.getX(), pos.getZ());
            return noise > 0.2f * (rainfall - RAINFALL_SAND_SANDY_MEAN) / RAINFALL_SAND_SANDY_RANGE ? context.getRock().sandstone().defaultBlockState() : context.getRock().gravel().defaultBlockState();
        }
        else
        {
            // All others
            return context.getRock().gravel().defaultBlockState();
        }
    }
}
