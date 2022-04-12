/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Random;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.IcePileBlock;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.ThinSpikeBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.OverworldClimateModel;

/**
 * This is a helper class which handles environment effects
 * It would be called by https://github.com/MinecraftForge/MinecraftForge/pull/7235, until then we simply mixin the call to our handler
 */
public final class EnvironmentHelpers
{
    /**
     * Ticks a chunk for environment specific effects.
     * Handles:
     * - Placing snow while snowing, respecting snow pile-able blocks, and stacking snow.
     * - Freezing ice if cold enough, respecting freezable plants
     * - Placing icicles while snowing under overhangs
     * - Melting ice and snow due to temperature.
     */
    public static void tickChunk(ServerLevel level, LevelChunk chunk, ProfilerFiller profiler)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final BlockPos lcgPos = level.getBlockRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15);
        final BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, lcgPos);
        final BlockPos groundPos = surfacePos.below();
        final float temperature = Climate.getTemperature(level, surfacePos);

        profiler.push("tfcSnow");
        doSnow(level, surfacePos, temperature);
        profiler.popPush("tfcIce");
        doIce(level, groundPos, temperature);
        profiler.popPush("tfcIcicles");
        doIcicles(level, surfacePos, temperature);
        profiler.pop();
    }

    public static boolean isSnow(BlockState state)
    {
        return Helpers.isBlock(state, Blocks.SNOW) || Helpers.isBlock(state, TFCBlocks.SNOW_PILE.get());
    }

    public static boolean isIce(BlockState state)
    {
        return Helpers.isBlock(state, Blocks.ICE) || Helpers.isBlock(state, TFCBlocks.ICE_PILE.get()) || Helpers.isBlock(state, TFCBlocks.SEA_ICE.get());
    }

    public static boolean isWater(BlockState state)
    {
        return Helpers.isBlock(state, Blocks.WATER) || Helpers.isBlock(state, TFCBlocks.SALT_WATER.get());
    }

    public static boolean isWaterAtEdge(LevelAccessor level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            if (!level.isWaterAt(mutablePos.setWithOffset(pos, direction)))
            {
                return true;
            }
        }
        return false;
    }

    private static void doSnow(Level level, BlockPos surfacePos, float temperature)
    {
        final Random random = level.getRandom();
        if (random.nextInt(16) == 0)
        {
            // Snow only accumulates during rain
            if (temperature < OverworldClimateModel.SNOW_FREEZE_TEMPERATURE && level.isRaining())
            {
                // Handle smoother snow placement: if there's an adjacent position with less snow, switch to that position instead
                // Additionally, handle up to two block tall plants if they can be piled
                // This means we need to check three levels deep
                if (!placeSnowOrSnowPile(level, surfacePos, random))
                {
                    if (!placeSnowOrSnowPile(level, surfacePos.below(), random))
                    {
                        placeSnowOrSnowPile(level, surfacePos.below(2), random);
                    }
                }
            }
            else if (temperature > OverworldClimateModel.SNOW_MELT_TEMPERATURE)
            {
                // Snow melting - both snow and snow piles
                final BlockState state = level.getBlockState(surfacePos);
                if (isSnow(state))
                {
                    SnowPileBlock.removePileOrSnow(level, surfacePos, state);
                }
            }
        }
    }

    /**
     * @return {@code true} if a snow block or snow pile was able to be placed.
     */
    private static boolean placeSnowOrSnowPile(LevelAccessor level, BlockPos initialPos, Random random)
    {
        // First, try and find an optimal position, to smoothen out snow accumulation
        final BlockPos pos = findOptimalSnowLocation(level, initialPos, level.getBlockState(initialPos), random);
        final BlockState state = level.getBlockState(pos);

        // Then, handle possibilities
        if (isSnow(state) && state.getValue(SnowLayerBlock.LAYERS) < 7)
        {
            // Snow and snow layers can accumulate snow
            final BlockState newState = state.setValue(SnowLayerBlock.LAYERS, state.getValue(SnowLayerBlock.LAYERS) + 1);
            if (newState.canSurvive(level, pos))
            {
                level.setBlock(pos, newState, 3);
            }
            return true;
        }
        else if (SnowPileBlock.canPlaceSnowPile(level, pos, state))
        {
            SnowPileBlock.placeSnowPile(level, pos, state, false);
            return true;
        }
        else if (state.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(level, pos))
        {
            // Vanilla snow placement (single layers)
            level.setBlock(pos, Blocks.SNOW.defaultBlockState(), 3);
            return true;
        }
        return false;
    }

    /**
     * Smoothens out snow creation so it doesn't create as uneven piles, by moving snowfall to adjacent positions where possible.
     */
    private static BlockPos findOptimalSnowLocation(LevelAccessor level, BlockPos pos, BlockState state, Random random)
    {
        BlockPos targetPos = null;
        int found = 0;
        if (isSnow(state))
        {
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                final BlockPos adjPos = pos.relative(direction);
                final BlockState adjState = level.getBlockState(adjPos);
                if ((isSnow(adjState) && adjState.getValue(SnowLayerBlock.LAYERS) < state.getValue(SnowLayerBlock.LAYERS)) // Adjacent snow that's lower than this one
                    || ((adjState.isAir() || Helpers.isBlock(adjState.getBlock(), TFCTags.Blocks.CAN_BE_SNOW_PILED)) && Blocks.SNOW.defaultBlockState().canSurvive(level, adjPos))) // Or, empty space that could support snow
                {
                    found++;
                    if (targetPos == null || random.nextInt(found) == 0)
                    {
                        targetPos = adjPos;
                    }
                }
            }
            if (targetPos != null)
            {
                return targetPos;
            }
        }
        return pos;
    }

    private static void doIce(Level level, BlockPos groundPos, float temperature)
    {
        final Random random = level.getRandom();
        if (random.nextInt(16) == 0)
        {
            BlockState groundState = level.getBlockState(groundPos);
            if (temperature < OverworldClimateModel.ICE_FREEZE_TEMPERATURE)
            {
                FluidState groundFluid = groundState.getFluidState();

                // First, since we want to handle water with a single block above, if we find no water, but we find one below, we choose that instead
                if (groundFluid.getType() != Fluids.WATER)
                {
                    groundPos = groundPos.below();
                    groundState = level.getBlockState(groundPos);
                }

                IcePileBlock.placeIcePileOrIce(level, groundPos, groundState, false);
            }
            else if (temperature > OverworldClimateModel.ICE_MELT_TEMPERATURE)
            {
                // Handle ice melting
                if (groundState.getBlock() == Blocks.ICE || groundState.getBlock() == TFCBlocks.ICE_PILE.get())
                {
                    IcePileBlock.removeIcePileOrIce(level, groundPos, groundState);
                }
            }
        }
    }

    private static void doIcicles(Level level, BlockPos lcgPos, float temperature)
    {
        final Random random = level.getRandom();
        if (random.nextInt(16) == 0 && level.isRaining() && temperature < OverworldClimateModel.ICICLE_MAX_FREEZE_TEMPERATURE && temperature > OverworldClimateModel.ICICLE_MIN_FREEZE_TEMPERATURE)
        {
            // Place icicles under overhangs
            final BlockPos iciclePos = findIcicleLocation(level, lcgPos, random);
            if (iciclePos != null)
            {
                BlockPos posAbove = iciclePos.above();
                BlockState stateAbove = level.getBlockState(posAbove);
                if (Helpers.isBlock(stateAbove, TFCBlocks.ICICLE.get()))
                {
                    level.setBlock(posAbove, stateAbove.setValue(ThinSpikeBlock.TIP, false), 3 | 16);
                }
                level.setBlock(iciclePos, TFCBlocks.ICICLE.get().defaultBlockState().setValue(ThinSpikeBlock.TIP, true), 3);
            }
        }
    }

    @Nullable
    private static BlockPos findIcicleLocation(Level world, BlockPos pos, Random random)
    {
        final Direction side = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos adjacentPos = pos.relative(side);
        final int adjacentHeight = world.getHeight(Heightmap.Types.MOTION_BLOCKING, adjacentPos.getX(), adjacentPos.getZ());
        BlockPos foundPos = null;
        int found = 0;
        for (int y = 0; y < adjacentHeight; y++)
        {
            final BlockState stateAt = world.getBlockState(adjacentPos);
            final BlockPos posAbove = adjacentPos.above();
            final BlockState stateAbove = world.getBlockState(posAbove);
            if (stateAt.isAir() && (stateAbove.getBlock() == TFCBlocks.ICICLE.get() || stateAbove.isFaceSturdy(world, posAbove, Direction.DOWN)))
            {
                found++;
                if (foundPos == null || random.nextInt(found) == 0)
                {
                    foundPos = adjacentPos;
                }
            }
            adjacentPos = posAbove;
        }
        return foundPos;
    }

    public static boolean isWorldgenReplaceable(WorldGenLevel level, BlockPos pos)
    {
        return isWorldgenReplaceable(level.getBlockState(pos));
    }

    public static boolean isWorldgenReplaceable(BlockState state)
    {
        return FluidHelpers.isAirOrEmptyFluid(state) || Helpers.isBlock(state, TFCTags.Blocks.SINGLE_BLOCK_REPLACEABLE);
    }

    public static boolean canPlaceBushOn(WorldGenLevel level, BlockPos pos)
    {
        return isWorldgenReplaceable(level, pos) && Helpers.isBlock(level.getBlockState(pos.below()), TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    public static boolean isOnSturdyFace(WorldGenLevel level, BlockPos pos)
    {
        pos = pos.below();
        return level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP);
    }
}
