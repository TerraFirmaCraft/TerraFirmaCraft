/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.ThinSpikeBlock;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.OverworldClimateModel;

/**
 * This is a helper class which handles environment effects
 * It would be called by https://github.com/MinecraftForge/MinecraftForge/pull/7235, until then we simply mixin the call to our handler
 */
public final class EnvironmentHelpers
{
    /**
     * When snowing, perform two additional changes:
     * - Snow or snow piles should stack up to 7 high
     * - Convert possible blocks to snow piles
     * - Freeze sea water into sea ice
     */
    public static void onEnvironmentTick(ServerLevel level, LevelChunk chunkIn)
    {
        Random random = level.getRandom();
        ChunkPos chunkPos = chunkIn.getPos();
        if (random.nextInt(16) == 0)
        {
            BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, level.getBlockRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15));
            if (level.isAreaLoaded(pos, 2))
            {
                float temperature = Climate.getTemperature(level, pos);
                if (level.isRaining() && temperature < OverworldClimateModel.SNOW_STACKING_TEMPERATURE)
                {
                    // Try and place snow both at the current location, and one below
                    // Snow needs to be placed at the current location if it's an air block (placing new snow) and one lower if it's a pile or existing snow block.
                    // If the current block is snow, check adjacent locations and pick a lower one if found
                    // This has the effect of smoothing out snow placement a bit, resulting in less awkward snow cover
                    BlockPos targetPos = findOptimalSnowLocation(level, pos, random);
                    BlockState targetState = level.getBlockState(targetPos);
                    if (!tryStackSnow(level, targetPos, targetState))
                    {
                        targetPos = findOptimalSnowLocation(level, pos.below(), random);
                        targetState = level.getBlockState(targetPos);
                        tryStackSnow(level, targetPos, targetState);
                    }
                }

                if (level.isRaining() && temperature < OverworldClimateModel.MAX_ICICLE_TEMPERATURE && temperature > OverworldClimateModel.MIN_ICICLE_TEMPERATURE)
                {
                    // Place icicles under overhangs
                    // This uses the original position as it is not concerned with smooth snow covering
                    final BlockPos iciclePos = findIcicleLocation(level, pos, random);
                    if (iciclePos != null)
                    {
                        BlockPos posAbove = iciclePos.above();
                        BlockState stateAbove = level.getBlockState(posAbove);
                        if (stateAbove.is(TFCBlocks.ICICLE.get()))
                        {
                            level.setBlock(posAbove, stateAbove.setValue(ThinSpikeBlock.TIP, false), 3 | 16);
                        }
                        level.setBlock(iciclePos, TFCBlocks.ICICLE.get().defaultBlockState().setValue(ThinSpikeBlock.TIP, true), 3);
                    }
                }

                if (temperature < OverworldClimateModel.SEA_ICE_FREEZE_TEMPERATURE)
                {
                    // Freeze salt water into sea ice
                    tryFreezeSeaIce(level, pos.below());
                }
            }
        }
    }

    private static boolean tryStackSnow(LevelAccessor world, BlockPos pos, BlockState state)
    {
        if ((state.is(Blocks.SNOW) || state.is(TFCBlocks.SNOW_PILE.get())) && state.getValue(SnowLayerBlock.LAYERS) < 7)
        {
            // Vanilla snow block stacking
            BlockState newState = state.setValue(SnowLayerBlock.LAYERS, state.getValue(SnowLayerBlock.LAYERS) + 1);
            if (newState.canSurvive(world, pos))
            {
                world.setBlock(pos, newState, 3);
            }
            return true;
        }
        else if (TFCTags.Blocks.CAN_BE_SNOW_PILED.contains(state.getBlock()))
        {
            // Other snow block stacking
            SnowPileBlock.convertToPile(world, pos, state);
            return true;
        }
        else if (state.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(world, pos))
        {
            // Vanilla snow placement (single layers)
            world.setBlock(pos, Blocks.SNOW.defaultBlockState(), 3);
        }
        return false;
    }

    /**
     * Logic is borrowed from {@link net.minecraft.world.level.biome.Biome#shouldFreeze(LevelReader, BlockPos)} but with the water fluid swapped out, and the temperature check changed (in the original code it's redirected by mixin)
     */
    private static void tryFreezeSeaIce(Level level, BlockPos pos)
    {
        if (Climate.getTemperature(level, pos) < OverworldClimateModel.SEA_ICE_FREEZE_TEMPERATURE)
        {
            if (pos.getY() >= 0 && pos.getY() < 256 && level.getBrightness(LightLayer.BLOCK, pos) < 10)
            {
                BlockState state = level.getBlockState(pos);
                FluidState fluid = level.getFluidState(pos);
                if (fluid.getType() == TFCFluids.SALT_WATER.getSource() && state.getBlock() instanceof LiquidBlock)
                {
                    if (!level.isWaterAt(pos.west()) || !level.isWaterAt(pos.east()) || !level.isWaterAt(pos.north()) || !level.isWaterAt(pos.south()))
                    {
                        level.setBlock(pos, TFCBlocks.SEA_ICE.get().defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static BlockPos findOptimalSnowLocation(LevelAccessor world, BlockPos pos, Random random)
    {
        BlockState state = world.getBlockState(pos);
        BlockPos targetPos = null;
        int found = 0;
        if (state.is(Blocks.SNOW) || state.is(TFCBlocks.SNOW_PILE.get()))
        {
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                BlockPos adjPos = pos.relative(direction);
                BlockState adjState = world.getBlockState(adjPos);
                if (((adjState.is(Blocks.SNOW) || adjState.is(TFCBlocks.SNOW_PILE.get())) && adjState.getValue(SnowLayerBlock.LAYERS) < state.getValue(SnowLayerBlock.LAYERS)) || (adjState.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(world, adjPos)))
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
}
