/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.BitSet;
import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableBoolean;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.BaseStoneSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.SingleBaseStoneSource;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.soil.IDirtBlock;

public final class CarverHelpers
{
    public static final BlockState AIR = Blocks.AIR.defaultBlockState();
    public static final FluidState WATER = Fluids.WATER.defaultFluidState();
    public static final FluidState LAVA = Fluids.LAVA.defaultFluidState();

    public static final BaseStoneSource FALLBACK = new SingleBaseStoneSource(Blocks.STONE.defaultBlockState());

    /**
     * Gets and correctly initializes the chunk carving mask for the current world height
     */
    public static BitSet getCarvingMask(ProtoChunk chunk, int height)
    {
        GenerationStep.Carving step = GenerationStep.Carving.AIR;
        BitSet carvingMask = chunk.getCarvingMask(step);
        if (carvingMask == null)
        {
            carvingMask = new BitSet(16 * 16 * height);
            chunk.setCarvingMask(step, carvingMask);
        }
        return carvingMask;
    }

    public static int maskIndex(int x, int y, int z, int minY)
    {
        return (x & 15) | ((z & 15) << 4) | ((y - minY) << 8);
    }

    public static <C extends CarverConfiguration> boolean carveBlock(CarvingContext context, C config, ChunkAccess chunk, BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos checkPos, Aquifer aquifer, MutableBoolean reachedSurface)
    {
        final BlockState stateAt = chunk.getBlockState(pos);
        if (canReplaceBlock(stateAt) || isDebugEnabled(config))
        {
            final BlockState carvingState = getCarveState(context, config, pos, aquifer, context instanceof ExtendedCarvingContext ex ? ex.getBaseStoneSource() : FALLBACK);
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
        return TFCTags.Blocks.CAN_CARVE.contains(state.getBlock());
    }

    @Nullable
    public static <C extends CarverConfiguration> BlockState getCarveState(CarvingContext context, C config, BlockPos pos, Aquifer aquifer, BaseStoneSource stoneSource)
    {
        if (pos.getY() <= config.lavaLevel.resolveY(context))
        {
            return LAVA.createLegacyBlock();
        }
        else if (!config.aquifersEnabled)
        {
            return isDebugEnabled(config) ? getDebugState(config, AIR) : AIR;
        }
        else
        {
            final BlockState carveState = aquifer.computeState(stoneSource, pos.getX(), pos.getY(), pos.getZ(), 0.0D);
            final boolean isSolid = !carveState.isAir() && carveState.getFluidState().isEmpty();
            if (isDebugEnabled(config))
            {
                return isSolid ? getDebugState(config, carveState) : config.debugSettings.getBarrierState();
            }
            return isSolid ? null : carveState;
        }
    }

    public static BlockState getDebugState(CarverConfiguration config, BlockState state)
    {
        if (state.is(Blocks.AIR))
        {
            return config.debugSettings.getAirState();
        }
        else if (state.is(Blocks.WATER))
        {
            final BlockState debugState = config.debugSettings.getWaterState();
            return debugState.hasProperty(BlockStateProperties.WATERLOGGED) ? debugState.setValue(BlockStateProperties.WATERLOGGED, true) : debugState;
        }
        else
        {
            return state.is(Blocks.LAVA) ? config.debugSettings.getLavaState() : state;
        }
    }

    public static boolean isDebugEnabled(CarverConfiguration config)
    {
        return config.debugSettings.isDebugMode();
    }
}
