/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.IcePileBlock;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.ThinSpikeBlock;
import net.dries007.tfc.common.blocks.plant.KrummholzBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.util.tracker.WorldTracker;

import static net.dries007.tfc.util.climate.OverworldClimateModel.*;

/**
 * This is a helper class which handles environment effects
 * It would be called by <a href="https://github.com/MinecraftForge/MinecraftForge/pull/7235">MinecraftForge#7235</a>, until then we simply mixin the call to our handler
 * todo: change pretty much all of this
 */
public final class EnvironmentHelpers
{
    public static final int ICICLE_MELT_RANDOM_TICK_CHANCE = 4; // Icicles don't melt naturally well at all, since they form under overhangs
    public static final int SNOW_MELT_RANDOM_TICK_CHANCE = 75; // Snow and ice melt naturally, but snow naturally gets placed under overhangs due to smoothing
    public static final int ICE_MELT_RANDOM_TICK_CHANCE = 200; // Ice practically never should form under overhangs, so this can be very low chance
    public static final int ICICLE_MAX_LENGTH = 7;

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

    public static boolean isAdjacentToWater(LevelAccessor level, BlockPos pos)
    {
        return isAdjacentToMaybeWater(level, pos, true);
    }

    public static boolean isAdjacentToNotWater(LevelAccessor level, BlockPos pos)
    {
        return isAdjacentToMaybeWater(level, pos, false);
    }

    private static boolean isAdjacentToMaybeWater(LevelAccessor level, BlockPos pos, boolean expected)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            if (level.isWaterAt(mutablePos.setWithOffset(pos, direction)) == expected)
            {
                return true;
            }
        }
        return false;
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

    @Deprecated
    public static boolean isRainingOrSnowing(Level level, BlockPos pos)
    {
        return level.isRainingAt(pos); // todo: verify that this in fact works, and is redirected to include local rainfall effects properly
    }

    public static float adjustAvgTempForElev(int y, float averageTemp)
    {
        return adjustAvgTempForElev(y, averageTemp, SEA_LEVEL);
    }

    public static float adjustAvgTempForElev(int y, float averageTemp, float seaLevel)
    {
        if (y > seaLevel)
        {
            // -1.6 C / 10 blocks above sea level
            float elevationTemperature = Mth.clamp((y - seaLevel) * 0.16225f, 0, 17.822f);
            return averageTemp - elevationTemperature;
        }
        else
        {
            //Average temp doesn't vary below sea level
            return averageTemp;
        }
    }

    /**
     * Based on the temperature provided, returns an approximate estimate for how high snow should be layering.
     */
    public static float getExpectedSnowLayerHeight(float temperature)
    {
        return Mth.clampedMap(temperature, 2f, -26f, 0f, 7f);
    }

    private static void doSnow(Level level, BlockPos surfacePos, float temperature)
    {
        // Snow only accumulates during rain
        final RandomSource random = level.random;
        final int expectedLayers = (int) getExpectedSnowLayerHeight(temperature);
        if (temperature < OverworldClimateModel.SNOW_FREEZE_TEMPERATURE && isRainingOrSnowing(level, surfacePos) && level.getBrightness(LightLayer.BLOCK, surfacePos) <= 11)
        {
            if (random.nextInt(TFCConfig.SERVER.snowAccumulateChance.get()) == 0)
            {
                // Handle smoother snow placement: if there's an adjacent position with less snow, switch to that position instead
                // Additionally, handle up to two block tall plants if they can be piled
                // This means we need to check three levels deep
                if (!placeSnowOrSnowPile(level, surfacePos, random, expectedLayers))
                {
                    if (!placeSnowOrSnowPile(level, surfacePos.below(), random, expectedLayers))
                    {
                        placeSnowOrSnowPile(level, surfacePos.below(2), random, expectedLayers);
                    }
                }
            }
        }
        else
        {
            if (random.nextInt(TFCConfig.SERVER.snowMeltChance.get()) == 0)
            {
                removeSnowAt(level, surfacePos, temperature, expectedLayers);
                if (random.nextFloat() < 0.2f)
                {
                    removeSnowAt(level, surfacePos.relative(Direction.Plane.HORIZONTAL.getRandomDirection(random)), temperature, expectedLayers);
                }
            }
        }
    }

    private static void removeSnowAt(Level level, BlockPos surfacePos, float temperature, int expectedLayers)
    {
        // Snow melting - both snow and snow piles
        final BlockState state = level.getBlockState(surfacePos);
        if (isSnow(state))
        {
            // When melting snow, we melt layers at +2 from expected, while the temperature is still below zero
            // This slowly reduces massive excess amounts of snow, if they're present, but doesn't actually start melting snow a lot when we're still below freezing.
            SnowPileBlock.removePileOrSnow(level, surfacePos, state, temperature > 0f ? expectedLayers : expectedLayers + 2);
        }
        else if (state.getBlock() instanceof KrummholzBlock)
        {
            KrummholzBlock.updateFreezingInColumn(level, surfacePos, false);
        }
    }

    /**
     * @return {@code true} if a snow block or snow pile was placed.
     */
    private static boolean placeSnowOrSnowPile(Level level, BlockPos initialPos, RandomSource random, int expectedLayers)
    {
        if (expectedLayers < 1)
        {
            // Don't place snow if we're < 1 expected layers
            return false;
        }

        // First, try and find an optimal position, to smoothen out snow accumulation
        // This will only move to the side, if we're currently at a snow location
        final BlockPos pos = findOptimalSnowLocation(level, initialPos, level.getBlockState(initialPos), random);
        final BlockState state = level.getBlockState(pos);

        // If we didn't move to the side, then we still need to pass a can see sky check
        // If we did, we might've moved under an overhang from a previously valid snow location
        if (initialPos.equals(pos) && !level.canSeeSky(pos))
        {
            return false;
        }
        return placeSnowOrSnowPileAt(level, pos, state, random, expectedLayers);
    }

    private static boolean placeSnowOrSnowPileAt(LevelAccessor level, BlockPos pos, BlockState state, RandomSource random, int expectedLayers)
    {
        // Then, handle possibilities
        if (isSnow(state) && state.getValue(SnowLayerBlock.LAYERS) < 7)
        {
            // Snow and snow layers can accumulate snow
            // The chance that this works is reduced the higher the pile is
            final int currentLayers = state.getValue(SnowLayerBlock.LAYERS);
            final BlockState newState = state.setValue(SnowLayerBlock.LAYERS, currentLayers + 1);
            if (newState.canSurvive(level, pos) && random.nextInt(1 + 3 * currentLayers) == 0 && expectedLayers > currentLayers)
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
        else if (state.getBlock() instanceof KrummholzBlock)
        {
            KrummholzBlock.updateFreezingInColumn(level, pos, true);
        }
        else if (state.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(level, pos))
        {
            // Vanilla snow placement (single layers)
            level.setBlock(pos, Blocks.SNOW.defaultBlockState(), 3);
            return true;
        }
        else if (level instanceof Level fullLevel)
        {
            // Fills cauldrons with snow
            state.getBlock().handlePrecipitation(state, fullLevel, pos, Biome.Precipitation.SNOW);
        }
        return false;
    }

    /**
     * Smoothens out snow creation, so it doesn't create as uneven piles, by moving snowfall to adjacent positions where possible.
     */
    private static BlockPos findOptimalSnowLocation(LevelAccessor level, BlockPos pos, BlockState state, RandomSource random)
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
        final RandomSource random = level.getRandom();
        BlockState groundState = level.getBlockState(groundPos);
        if (temperature < OverworldClimateModel.ICE_FREEZE_TEMPERATURE)
        {
            if (random.nextInt(16) == 0)
            {
                // First, since we want to handle water with a single block above, if we find no water, but we find one below, we choose that instead
                // However, we have to also exclude ice here, since we don't intend to freeze two layers down
                if (isIce(groundState))
                {
                    return;
                }
                if (groundState.getFluidState().getType() != Fluids.WATER)
                {
                    groundPos = groundPos.below();
                    groundState = level.getBlockState(groundPos);
                }

                IcePileBlock.placeIcePileOrIce(level, groundPos, groundState, false);
            }
        }
        else if (temperature > OverworldClimateModel.ICE_MELT_TEMPERATURE)
        {
            // Handle ice melting
            if (groundState.getBlock() == Blocks.ICE || groundState.getBlock() == TFCBlocks.ICE_PILE.get())
            {
                // Apply a heuristic to try and make ice melting more smooth, in the same way ice freezing works
                if (random.nextInt(600) == 0 || (random.nextInt(12) == 0 && isAdjacentToWater(level, groundPos)))
                {
                    IcePileBlock.removeIcePileOrIce(level, groundPos, groundState);
                }
            }
        }
    }

    private static void doIcicles(Level level, BlockPos lcgPos, float temperature)
    {
        final RandomSource random = level.getRandom();
        if (random.nextInt(16) == 0 && isRainingOrSnowing(level, lcgPos) && level.getBrightness(LightLayer.BLOCK, lcgPos) <= 11 && temperature < OverworldClimateModel.ICICLE_MAX_FREEZE_TEMPERATURE && temperature > OverworldClimateModel.ICICLE_MIN_FREEZE_TEMPERATURE)
        {
            // Place icicles under overhangs
            final BlockPos iciclePos = findIcicleLocation(level, lcgPos, random);
            if (iciclePos != null)
            {
                BlockPos posAbove = iciclePos.above();
                BlockState stateAbove = level.getBlockState(posAbove);
                if (Helpers.isBlock(stateAbove, BlockTags.ICE))
                {
                    return;
                }
                if (Helpers.isBlock(stateAbove, TFCBlocks.ICICLE.get()))
                {
                    level.setBlock(posAbove, stateAbove.setValue(ThinSpikeBlock.TIP, false), 3 | 16);
                }
                level.setBlock(iciclePos, TFCBlocks.ICICLE.get().defaultBlockState().setValue(ThinSpikeBlock.TIP, true), 3);
            }
        }
    }

    @Nullable
    private static BlockPos findIcicleLocation(Level level, BlockPos pos, RandomSource random)
    {
        final Direction side = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos adjacentPos = pos.relative(side);
        final int adjacentHeight = level.getHeight(Heightmap.Types.MOTION_BLOCKING, adjacentPos.getX(), adjacentPos.getZ());
        BlockPos foundPos = null;
        int found = 0;
        for (int y = 0; y < adjacentHeight; y++)
        {
            final BlockState stateAt = level.getBlockState(adjacentPos);
            final BlockPos posAbove = adjacentPos.above();
            final BlockState stateAbove = level.getBlockState(posAbove);
            if (stateAt.isAir() && (stateAbove.getBlock() == TFCBlocks.ICICLE.get() || stateAbove.isFaceSturdy(level, posAbove, Direction.DOWN)))
            {
                found++;
                if (foundPos == null || random.nextInt(found) == 0)
                {
                    foundPos = adjacentPos;
                }
            }
            adjacentPos = posAbove;
        }
        if (foundPos != null)
        {
            final Random icicleLengthRandom = new Random(pos.atY(64).asLong());
            final BlockPos searchPos = foundPos.above(icicleLengthRandom.nextInt(ICICLE_MAX_LENGTH) + 1);
            if (level.isLoaded(searchPos) && Helpers.isBlock(level.getBlockState(searchPos), TFCBlocks.ICICLE.get()))
            {
                return null;
            }
            if (!level.getBlockState(foundPos.below()).isAir())
            {
                return null;
            }
        }
        return foundPos;
    }


}
