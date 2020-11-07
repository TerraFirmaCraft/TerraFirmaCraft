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
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.types.RockManager;

public class TFCCaveCarver extends CaveWorldCarver implements IContextCarver
{
    private final Set<Block> originalCarvableBlocks;
    private final CaveBlockReplacer blockCarver;

    private GenerationStage.Carving stage;
    private BitSet liquidCarvingMask;

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

    @Override
    public void setContext(long worldSeed, GenerationStage.Carving stage, BitSet liquidCarvingMask)
    {
        this.stage = stage;
        this.liquidCarvingMask = liquidCarvingMask;
    }

    @Override
    public boolean carve(IChunk chunkIn, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig config)
    {
        if (stage == null)
        {
            throw new IllegalStateException("Not properly initialized! Cannot use TFCCaveCarver with a chunk generator that does not respect IContextCarver");
        }
        return super.carve(chunkIn, biomePos, rand, seaLevel, chunkXOffset, chunkZOffset, chunkX, chunkZ, carvingMask, config);
    }

    @Override
    protected boolean carveBlock(IChunk chunkIn, Function<BlockPos, Biome> lazyBiome, BitSet carvingMask, Random random, BlockPos.Mutable mutablePos1, BlockPos.Mutable mutablePos2, BlockPos.Mutable mutablePos3, int p_225556_8_, int p_225556_9_, int p_225556_10_, int actualX, int actualZ, int localX, int y, int localZ, MutableBoolean reachedSurface)
    {
        mutablePos1.set(actualX, y, actualZ);
        return blockCarver.carveBlock(chunkIn, mutablePos1, stage, carvingMask, liquidCarvingMask);
    }
}