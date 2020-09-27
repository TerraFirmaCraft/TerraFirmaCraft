/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.types.RockManager;

public class TFCCaveCarver extends CaveWorldCarver
{
    private final Set<Block> originalCarvableBlocks;
    private final CaveBlockReplacer blockCarver;

    public TFCCaveCarver(Codec<ProbabilityConfig> codec, int maxHeight)
    {
        super(codec, maxHeight);
        originalCarvableBlocks = replaceableBlocks;
        blockCarver = new CaveBlockReplacer();

        // Need to run this every time the rock registry is reloaded
        RockManager.INSTANCE.addCallback(() -> replaceableBlocks = TFCCarvers.fixCarvableBlocksList(originalCarvableBlocks));
    }

    @Override
    protected int getCaveY(Random random)
    {
        // Lower level caves are composed mostly of worley caves, higher level caves are vanilla.
        return 32 + random.nextInt(120);
    }

    /**
     * carveBlock or something, yet unnamed.
     */
    @Override
    protected boolean carveBlock(IChunk chunkIn, Function<BlockPos, Biome> lazyBiome, BitSet carvingMask, Random random, BlockPos.Mutable mutablePos1, BlockPos.Mutable mutablePos2, BlockPos.Mutable mutablePos3, int p_225556_8_, int p_225556_9_, int p_225556_10_, int actualX, int actualZ, int localX, int y, int localZ, MutableBoolean reachedSurface)
    {
        mutablePos1.set(actualX, y, actualZ);
        return blockCarver.carveBlock(chunkIn, mutablePos1, carvingMask);
    }
}