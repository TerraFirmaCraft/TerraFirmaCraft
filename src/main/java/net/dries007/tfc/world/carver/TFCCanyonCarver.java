/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;

import com.mojang.serialization.Codec;

public class TFCCanyonCarver extends CanyonWorldCarver
{
    public TFCCanyonCarver(Codec<CanyonCarverConfiguration> codec)
    {
        super(codec);
    }

    @Override
    protected boolean carveBlock(CarvingContext context, CanyonCarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Biome> biomeAccessor, BitSet carvingMask, Random random, BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos checkPos, Aquifer aquifer, MutableBoolean reachedSurface)
    {
        return CarverHelpers.carveBlock(context, config, chunk, pos, checkPos, aquifer, reachedSurface);
    }

    @Override
    protected boolean canReplaceBlock(BlockState state)
    {
        return CarverHelpers.canReplaceBlock(state);
    }
}
