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
import net.minecraft.world.gen.carver.CanyonWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.mixin.world.gen.carver.CanyonWorldCarverAccessor;
import net.dries007.tfc.world.chunkdata.RockData;

public class TFCRavineCarver extends CanyonWorldCarver implements IContextCarver
{
    private final AirBlockCarver blockCarver;
    private boolean initialized;

    public TFCRavineCarver(Codec<ProbabilityConfig> codec)
    {
        super(codec);
        blockCarver = new AirBlockCarver();
        initialized = false;
    }

    @Override
    public boolean carveRegion(IChunk chunkIn, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig configIn)
    {
        if (!initialized)
        {
            throw new IllegalStateException("Not properly initialized! Cannot use TFCRavineCarver with a chunk generator that does not respect IContextCarver");
        }
        double xOffset = chunkXOffset * 16 + rand.nextInt(16);
        double yOffset = rand.nextInt(rand.nextInt(seaLevel + 20) + 32) + 20; // Modified to use sea level, should reach surface more often
        double zOffset = chunkZOffset * 16 + rand.nextInt(16);
        float yaw = rand.nextFloat() * ((float) Math.PI * 2F);
        float pitch = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
        float width = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
        int branchAmount = 112 - rand.nextInt(28);
        ((CanyonWorldCarverAccessor) this).call$genCanyon(chunkIn, biomePos, rand.nextLong(), seaLevel, chunkX, chunkZ, xOffset, yOffset, zOffset, width, yaw, pitch, 0, branchAmount, 3.0D, carvingMask);
        return true;
    }

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