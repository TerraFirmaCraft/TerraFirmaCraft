/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import org.jetbrains.annotations.Nullable;

import org.apache.commons.lang3.mutable.MutableBoolean;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.soil.IDirtBlock;
import net.dries007.tfc.util.Helpers;

public final class CarverHelpers
{
    public static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();
    public static final FluidState WATER = Fluids.WATER.defaultFluidState();
    public static final FluidState LAVA = Fluids.LAVA.defaultFluidState();

    public static <C extends CarverConfiguration> boolean carveBlock(CarvingContext context, C config, ChunkAccess chunk, BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos checkPos, Aquifer aquifer, MutableBoolean reachedSurface)
    {
        final BlockState stateAt = chunk.getBlockState(pos);
        if (canReplaceBlock(stateAt) || isDebugEnabled(config))
        {
            final BlockState carvingState = getCarveState(context, config, pos, aquifer);
            if (carvingState != null)
            {
                chunk.setBlockState(pos, carvingState, false);
                if (reachedSurface.isTrue())
                {
                    checkPos.setWithOffset(pos, Direction.DOWN);
                    if (chunk.getBlockState(checkPos).getBlock() instanceof IDirtBlock dirt)
                    {
                        chunk.setBlockState(checkPos, dirt.getGrass(), false);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean canReplaceBlock(BlockState state)
    {
        return Helpers.isBlock(state.getBlock(), TFCTags.Blocks.CAN_CARVE);
    }

    @Nullable
    public static <C extends CarverConfiguration> BlockState getCarveState(CarvingContext context, C config, BlockPos pos, Aquifer aquifer)
    {
        if (pos.getY() <= config.lavaLevel.resolveY(context))
        {
            return LAVA.createLegacyBlock();
        }
        else
        {
            final BlockState carveState = aquifer.computeSubstance(new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ()) , 0);
            if (carveState == null)
            {
                return isDebugEnabled(config) ? config.debugSettings.getBarrierState() : null;
            }
            return isDebugEnabled(config) ? getDebugState(config, carveState) : carveState;
        }
    }

    public static BlockState getDebugState(CarverConfiguration config, BlockState state)
    {
        if (Helpers.isBlock(state, Blocks.AIR))
        {
            return config.debugSettings.getAirState();
        }
        else if (Helpers.isBlock(state, Blocks.WATER))
        {
            final BlockState debugState = config.debugSettings.getWaterState();
            return debugState.hasProperty(BlockStateProperties.WATERLOGGED) ? debugState.setValue(BlockStateProperties.WATERLOGGED, true) : debugState;
        }
        else
        {
            return Helpers.isBlock(state, Blocks.LAVA) ? config.debugSettings.getLavaState() : state;
        }
    }

    public static boolean isDebugEnabled(CarverConfiguration config)
    {
        return config.debugSettings.isDebugMode();
    }
}
