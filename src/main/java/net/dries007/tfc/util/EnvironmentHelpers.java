/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.ThinSpikeBlock;
import net.dries007.tfc.common.fluids.TFCFluids;

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
    public static void onEnvironmentTick(ServerWorld world, Chunk chunkIn, Random random)
    {
        ChunkPos chunkPos = chunkIn.getPos();
        if (random.nextInt(16) == 0)
        {
            BlockPos pos = world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, world.getBlockRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15));
            if (world.isAreaLoaded(pos, 2))
            {
                float temperature = Climate.getTemperature(world, pos);
                if (world.isRaining() && temperature < Climate.SNOW_STACKING_TEMPERATURE)
                {
                    // Try and place snow both at the current location, and one below
                    // Snow needs to be placed at the current location if it's an air block (placing new snow) and one lower if it's a pile or existing snow block.
                    // If the current block is snow, check adjacent locations and pick a lower one if found
                    // This has the effect of smoothing out snow placement a bit, resulting in less awkward snow cover
                    BlockPos targetPos = findOptimalSnowLocation(world, pos, random);
                    BlockState targetState = world.getBlockState(targetPos);
                    if (!tryStackSnow(world, targetPos, targetState))
                    {
                        targetPos = findOptimalSnowLocation(world, pos.below(), random);
                        targetState = world.getBlockState(targetPos);
                        tryStackSnow(world, targetPos, targetState);
                    }
                }

                if (world.isRaining() && temperature < Climate.MAX_ICICLE_TEMPERATURE && temperature > Climate.MIN_ICICLE_TEMPERATURE)
                {
                    // Place icicles under overhangs
                    // This uses the original position as it is not concerned with smooth snow covering
                    final BlockPos iciclePos = findIcicleLocation(world, pos, random);
                    if (iciclePos != null)
                    {
                        BlockPos posAbove = iciclePos.above();
                        BlockState stateAbove = world.getBlockState(posAbove);
                        if (stateAbove.is(TFCBlocks.ICICLE.get()))
                        {
                            world.setBlock(posAbove, stateAbove.setValue(ThinSpikeBlock.TIP, false), 3 | 16);
                        }
                        world.setBlock(iciclePos, TFCBlocks.ICICLE.get().defaultBlockState().setValue(ThinSpikeBlock.TIP, true), 3);
                    }
                }

                if (temperature < Climate.SEA_ICE_FREEZE_TEMPERATURE)
                {
                    // Freeze salt water into sea ice
                    tryFreezeSeaIce(world, pos.below());
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static boolean tryStackSnow(IWorld world, BlockPos pos, BlockState state)
    {
        if ((state.is(Blocks.SNOW) || state.is(TFCBlocks.SNOW_PILE.get())) && state.getValue(SnowBlock.LAYERS) < 7)
        {
            // Vanilla snow block stacking
            BlockState newState = state.setValue(SnowBlock.LAYERS, state.getValue(SnowBlock.LAYERS) + 1);
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
     * Logic is borrowed from {@link net.minecraft.world.biome.Biome#shouldFreeze(IWorldReader, BlockPos)} but with the water fluid swapped out, and the temperature check changed (in the original code it's redirected by mixin)
     */
    private static void tryFreezeSeaIce(IWorld worldIn, BlockPos pos)
    {
        if (Climate.getTemperature(worldIn, pos) < Climate.SEA_ICE_FREEZE_TEMPERATURE)
        {
            if (pos.getY() >= 0 && pos.getY() < 256 && worldIn.getBrightness(LightType.BLOCK, pos) < 10)
            {
                BlockState state = worldIn.getBlockState(pos);
                FluidState fluid = worldIn.getFluidState(pos);
                if (fluid.getType() == TFCFluids.SALT_WATER.getSource() && state.getBlock() instanceof FlowingFluidBlock)
                {
                    if (!worldIn.isWaterAt(pos.west()) || !worldIn.isWaterAt(pos.east()) || !worldIn.isWaterAt(pos.north()) || !worldIn.isWaterAt(pos.south()))
                    {
                        worldIn.setBlock(pos, TFCBlocks.SEA_ICE.get().defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static BlockPos findOptimalSnowLocation(IWorld world, BlockPos pos, Random random)
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
                if (((adjState.is(Blocks.SNOW) || adjState.is(TFCBlocks.SNOW_PILE.get())) && adjState.getValue(SnowBlock.LAYERS) < state.getValue(SnowBlock.LAYERS)) || (adjState.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(world, adjPos)))
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
    @SuppressWarnings("deprecation")
    private static BlockPos findIcicleLocation(World world, BlockPos pos, Random random)
    {
        final Direction side = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos adjacentPos = pos.relative(side);
        final int adjacentHeight = world.getHeight(Heightmap.Type.MOTION_BLOCKING, adjacentPos.getX(), adjacentPos.getZ());
        BlockPos foundPos = null;
        int found = 0;
        for (int y = 0; y < adjacentHeight; y++)
        {
            final BlockState stateAt = world.getBlockState(adjacentPos);
            final BlockPos posAbove = adjacentPos.above();
            final BlockState stateAbove = world.getBlockState(posAbove);
            if (stateAt.isAir() && (stateAbove.getBlock().is(TFCBlocks.ICICLE.get()) || stateAbove.isFaceSturdy(world, posAbove, Direction.DOWN)))
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
