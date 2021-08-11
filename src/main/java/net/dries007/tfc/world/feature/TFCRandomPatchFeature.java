/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Various extensions to {@link net.minecraft.world.level.levelgen.feature.RandomPatchFeature}
 */
public class TFCRandomPatchFeature extends Feature<TFCRandomPatchConfig>
{
    public TFCRandomPatchFeature(Codec<TFCRandomPatchConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<TFCRandomPatchConfig> context)
    {
        final WorldGenLevel world = context.level();
        final BlockPos pos = context.origin();
        final Random random = context.random();
        final TFCRandomPatchConfig config = context.config();

        BlockPos posAt;
        if (config.project && !config.projectEachLocation)
        {
            posAt = world.getHeightmapPos(config.projectToOceanFloor ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE_WG, pos);
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

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final BlockState baseState = config.stateProvider.getState(random, posAt);
        for (int i = 0; i < tries; ++i)
        {
            final int x = posAt.getX() + random.nextInt(config.xSpread + 1) - random.nextInt(config.xSpread + 1);
            final int z = posAt.getZ() + random.nextInt(config.zSpread + 1) - random.nextInt(config.zSpread + 1);
            final int y = random.nextInt(config.ySpread + 1) - random.nextInt(config.ySpread + 1);
            if (config.projectEachLocation)
            {
                mutablePos.set(x, y + world.getHeight(config.projectToOceanFloor ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE_WG, x, z), z);
            }
            else
            {
                mutablePos.set(x, y + posAt.getY(), z);
            }

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
                        (config.canReplaceWater && stateAt.getFluidState().is(FluidTags.WATER) && FluidHelpers.isAirOrEmptyFluid(stateAt)) ||
                        (config.canReplaceSurfaceWater && stateAt.getFluidState().is(FluidTags.WATER) && FluidHelpers.isAirOrEmptyFluid(stateAt) && world.isEmptyBlock(mutablePos.above())))
                    {
                        // Fourth check: for extra conditions such as only allowing placement underground
                        if (config.onlyUnderground)
                        {
                            final int surfaceHeight = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ());
                            if (stateAt.getBlock() != Blocks.CAVE_AIR || pos.getY() >= surfaceHeight - 1)
                            {
                                continue;
                            }
                        }
                        config.blockPlacer.place(world, mutablePos, placementState, random);
                        placed++;
                    }
                }
            }
        }
        return placed > 0;
    }
}
