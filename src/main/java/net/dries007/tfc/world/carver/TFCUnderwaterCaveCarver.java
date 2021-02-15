/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableBoolean;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.UnderwaterCaveWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.chunkdata.RockData;

public class TFCUnderwaterCaveCarver extends UnderwaterCaveWorldCarver implements IContextCarver
{
    private final SaltWaterBlockCarver blockCarver;
    private boolean initialized;

    public TFCUnderwaterCaveCarver(Codec<ProbabilityConfig> dynamic)
    {
        super(dynamic);
        blockCarver = new SaltWaterBlockCarver();
        initialized = false;
    }

    /*@Override
    public boolean carveBlock(IChunk chunkIn, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig config)
    {
        if (!initialized)
        {
            throw new IllegalStateException("Not properly initialized! Cannot use TFCUnderwaterCaveCarver with a chunk generator that does not respect IContextCarver");
        }
        return super.carve(chunkIn, biomePos, rand, seaLevel, chunkXOffset, chunkZOffset, chunkX, chunkZ, carvingMask, config);
    }*/

    /*@Override
    protected int getCaveY(Random random)
    {
        return 16 + random.nextInt(90);
    }*/

    @Override
    public void setContext(long worldSeed, BitSet airCarvingMask, BitSet liquidCarvingMask, RockData rockData, @Nullable BitSet waterAdjacencyMask)
    {
        this.blockCarver.setContext(worldSeed, airCarvingMask, liquidCarvingMask, rockData, waterAdjacencyMask);
        this.initialized = true;
    }

    @Override
    protected boolean carveBlock(IChunk chunkIn, Function<BlockPos, Biome> lazyBiome, BitSet carvingMask, Random random, BlockPos.Mutable mutablePos1, BlockPos.Mutable mutablePos2, BlockPos.Mutable mutablePos3, int seaLevel, int chunkX, int chunkZ, int actualX, int actualZ, int localX, int y, int localZ, MutableBoolean reachedSurface)
    {
        mutablePos1.setPos(actualX, y, actualZ);
        return blockCarver.carve(chunkIn, mutablePos1, random, seaLevel);
    }
}