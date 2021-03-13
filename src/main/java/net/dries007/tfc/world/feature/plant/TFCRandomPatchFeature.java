/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Various extensions to {@link net.minecraft.world.gen.feature.RandomPatchFeature}
 */
public class TFCRandomPatchFeature extends Feature<TFCRandomPatchConfig>
{
    public TFCRandomPatchFeature(Codec<TFCRandomPatchConfig> codec)
    {
        super(codec);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, TFCRandomPatchConfig config)
    {
        BlockPos posAt;
        if (config.project)
        {
            posAt = world.getHeightmapPos(config.projectToOceanFloor ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG, pos);
        }
        else
        {
            posAt = pos;
        }

        int placed = 0;
        int tries = config.tries;
        if (config.useDensity)
        {
            // Scale between 50% - 150% of tries based on the adjusted forest density
            ChunkData data = ChunkData.get(world, posAt);
            tries *= (data.getAdjustedForestDensity() + 0.5f);
        }

        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final BlockState baseState = config.stateProvider.getState(random, posAt);
        for (int i = 0; i < tries; ++i)
        {
            mutablePos.setWithOffset(posAt, random.nextInt(config.xSpread + 1) - random.nextInt(config.xSpread + 1), random.nextInt(config.ySpread + 1) - random.nextInt(config.ySpread + 1), random.nextInt(config.zSpread + 1) - random.nextInt(config.zSpread + 1));

            // Water plants need to be flooded with the target fluid, if possible, in order to pass the canSurvive() check
            final BlockState stateAt = world.getBlockState(mutablePos);
            final BlockState placementState = config.canReplaceWater || config.canReplaceSurfaceWater ? FluidHelpers.fillWithFluid(baseState, stateAt.getFluidState().getType()) : baseState;

            // First check: is the state placeable at the current location
            if (placementState != null && placementState.canSurvive(world, mutablePos))
            {
                // Second check: is the below state passable with the white and black lists
                final BlockState stateBelow = world.getBlockState(mutablePos.below());
                if ((config.whitelist.isEmpty() || config.whitelist.contains(stateBelow.getBlock())) && !config.blacklist.contains(stateBelow))
                {
                    // Third check: is the position clear and valid
                    if ((config.canReplaceAir && stateAt.isAir()) ||
                        (config.canReplaceWater && stateAt.getFluidState().is(FluidTags.WATER) && FluidHelpers.isEmptyFluid(stateAt)) ||
                        (config.canReplaceSurfaceWater && stateAt.getFluidState().is(FluidTags.WATER) && FluidHelpers.isEmptyFluid(stateAt) && world.isEmptyBlock(mutablePos.above())))
                    {
                        config.blockPlacer.place(world, mutablePos, placementState, random);
                        placed++;
                    }
                }
            }
        }
        return placed > 0;
    }
}
