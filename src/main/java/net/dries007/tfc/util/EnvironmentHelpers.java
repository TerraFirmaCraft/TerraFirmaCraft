/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.ITag;
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
            BlockPos pos = world.getHeight(Heightmap.Type.MOTION_BLOCKING, world.getBlockRandomPos(chunkPos.getXStart(), 0, chunkPos.getZStart(), 15));
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
                        targetPos = findOptimalSnowLocation(world, pos.down(), random);
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
                        BlockPos posAbove = iciclePos.up();
                        BlockState stateAbove = world.getBlockState(posAbove);
                        if (stateAbove.isIn(TFCBlocks.ICICLE.get()))
                        {
                            world.setBlockState(posAbove, stateAbove.with(ThinSpikeBlock.TIP, false), 3 | 16);
                        }
                        world.setBlockState(iciclePos, TFCBlocks.ICICLE.get().getDefaultState().with(ThinSpikeBlock.TIP, true), 3);
                    }
                }

                if (temperature < Climate.SEA_ICE_FREEZE_TEMPERATURE)
                {
                    // Freeze salt water into sea ice
                    tryFreezeSeaIce(world, pos.down());
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static boolean tryStackSnow(IWorld world, BlockPos pos, BlockState state)
    {
        if ((state.isIn(Blocks.SNOW) || state.isIn(TFCBlocks.SNOW_PILE.get())) && state.get(SnowBlock.LAYERS) < 7)
        {
            // Vanilla snow block stacking
            BlockState newState = state.with(SnowBlock.LAYERS, state.get(SnowBlock.LAYERS) + 1);
            if (newState.isValidPosition(world, pos))
            {
                world.setBlockState(pos, newState, 3);
            }
            return true;
        }
        else if (TFCTags.Blocks.CAN_BE_SNOW_PILED.contains(state.getBlock()))
        {
            // Other snow block stacking
            SnowPileBlock.convertToPile(world, pos, state);
            return true;
        }
        else if (state.isAir() && Blocks.SNOW.getDefaultState().isValidPosition(world, pos))
        {
            // Vanilla snow placement (single layers)
            world.setBlockState(pos, Blocks.SNOW.getDefaultState(), 3);
        }
        return false;
    }

    /**
     * Logic is borrowed from {  net.minecraft.world.biome.Biome#shouldFreeze(IWorldReader, BlockPos)} but with the water fluid swapped out, and the temperature check changed (in the original code it's redirected by mixin)
     */
    private static void tryFreezeSeaIce(IWorld worldIn, BlockPos pos)
    {
        if (Climate.getTemperature(worldIn, pos) < Climate.SEA_ICE_FREEZE_TEMPERATURE)
        {
            if (pos.getY() >= 0 && pos.getY() < 256 && worldIn.getBrightness(pos) < 10)
            {
                BlockState state = worldIn.getBlockState(pos);
                FluidState fluid = worldIn.getFluidState(pos);
                if (fluid.getFluid() == TFCFluids.SALT_WATER.getSource() && state.getBlock() instanceof FlowingFluidBlock)
                {
                    if (!worldIn.hasWater(pos.west()) || !worldIn.hasWater(pos.east()) || !worldIn.hasWater(pos.north()) || !worldIn.hasWater(pos.south()))
                    {
                        worldIn.setBlockState(pos, TFCBlocks.SEA_ICE.get().getDefaultState(), 3);
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
        if (state.isIn(Blocks.SNOW) || state.isIn(TFCBlocks.SNOW_PILE.get()))
        {
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                BlockPos adjPos = pos.offset(direction);
                BlockState adjState = world.getBlockState(adjPos);
                if (((adjState.isIn(Blocks.SNOW) || adjState.isIn(TFCBlocks.SNOW_PILE.get())) && adjState.get(SnowBlock.LAYERS) < state.get(SnowBlock.LAYERS)) || (adjState.isAir() && Blocks.SNOW.getDefaultState().isValidPosition(world, adjPos)))
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
        final Direction side = Direction.Plane.HORIZONTAL.random(random);
        BlockPos adjacentPos = pos.offset(side);
        final int adjacentHeight = world.getHeight(Heightmap.Type.MOTION_BLOCKING, adjacentPos.getX(), adjacentPos.getZ());
        BlockPos foundPos = null;
        int found = 0;
        for (int y = 0; y < adjacentHeight; y++)
        {
            final BlockState stateAt = world.getBlockState(adjacentPos);
            final BlockPos posAbove = adjacentPos.up();
            final BlockState stateAbove = world.getBlockState(posAbove);
            if (stateAt.isAir() && (stateAbove.getBlock().isIn((ITag<Block>) TFCBlocks.ICICLE.get()) || stateAbove.isSolidSide(world, posAbove, Direction.DOWN)))
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
