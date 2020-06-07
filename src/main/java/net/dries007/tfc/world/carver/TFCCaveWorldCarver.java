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

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import com.mojang.datafixers.Dynamic;
import net.dries007.tfc.objects.types.RockManager;

public class TFCCaveWorldCarver extends CaveWorldCarver
{
    private final Set<Block> originalCarvableBlocks;
    private final CaveBlockReplacer blockCarver;

    public TFCCaveWorldCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> dynamic, int maxHeight)
    {
        super(dynamic, maxHeight);
        originalCarvableBlocks = carvableBlocks;
        blockCarver = new CaveBlockReplacer();

        // Need to run this every time the rock registry is reloaded
        RockManager.INSTANCE.addCallback(() -> carvableBlocks = TFCWorldCarvers.fixCarvableBlocksList(originalCarvableBlocks));
    }

    @Override
    protected int generateCaveStartY(Random random)
    {
        // Lower level caves are composed mostly of worley caves, higher level caves are vanilla.
        return 32 + random.nextInt(120);
    }

    /**
     * carveBlock or something, yet unnamed.
     */
    @Override
    protected boolean func_225556_a_(IChunk chunkIn, Function<BlockPos, Biome> lazyBiome, BitSet carvingMask, Random random, BlockPos.Mutable mutablePos1, BlockPos.Mutable mutablePos2, BlockPos.Mutable mutablePos3, int p_225556_8_, int p_225556_9_, int p_225556_10_, int actualX, int actualZ, int localX, int y, int localZ, AtomicBoolean reachedSurface)
    {
        mutablePos1.setPos(actualX, y, actualZ);
        return blockCarver.carveBlock(chunkIn, mutablePos1, carvingMask);
    }
}
