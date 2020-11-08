/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.carver.UnderwaterCanyonWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.types.RockManager;

public class TFCUnderwaterRavineCarver extends UnderwaterCanyonWorldCarver implements IContextCarver
{
    private final Set<Block> originalCarvableBlocks;
    private final IBlockCarver blockCarver;

    private WorldGenRegion world;
    private BitSet airCarvingMask;
    private BitSet liquidCarvingMask;

    public TFCUnderwaterRavineCarver(Codec<ProbabilityConfig> codec)
    {
        super(codec);
        originalCarvableBlocks = replaceableBlocks;
        blockCarver = new LiquidBlockCarver();

        // Need to run this every time the rock registry is reloaded
        RockManager.INSTANCE.addCallback(() -> replaceableBlocks = TFCCarvers.fixCarvableBlocksList(originalCarvableBlocks));
    }

    @Override
    public boolean carve(IChunk chunkIn, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig config)
    {
        if (world == null)
        {
            throw new IllegalStateException("Not properly initialized! Cannot use TFCUnderwaterRavineCarver with a chunk generator that does not respect IContextCarver");
        }
        return super.carve(chunkIn, biomePos, rand, seaLevel, chunkXOffset, chunkZOffset, chunkX, chunkZ, carvingMask, config);
    }

    @Override
    public void setContext(WorldGenRegion world, BitSet airCarvingMask, BitSet liquidCarvingMask)
    {
        this.world = world;
        this.airCarvingMask = airCarvingMask;
        this.liquidCarvingMask = liquidCarvingMask;
    }

    @Override
    protected boolean carveBlock(IChunk chunkIn, Function<BlockPos, Biome> lazyBiome, BitSet carvingMask, Random random, BlockPos.Mutable mutablePos1, BlockPos.Mutable mutablePos2, BlockPos.Mutable mutablePos3, int seaLevel, int chunkX, int chunkZ, int actualX, int actualZ, int localX, int y, int localZ, MutableBoolean reachedSurface)
    {
        mutablePos1.set(actualX, y, actualZ);
        return blockCarver.carve(world, chunkIn, mutablePos1, random, seaLevel, airCarvingMask, liquidCarvingMask);
    }
}