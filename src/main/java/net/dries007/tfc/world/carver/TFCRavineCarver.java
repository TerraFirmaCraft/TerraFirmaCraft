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
import net.minecraft.world.gen.carver.CanyonWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import com.mojang.datafixers.Dynamic;
import net.dries007.tfc.objects.types.RockManager;

public class TFCRavineCarver extends CanyonWorldCarver
{
    private final Set<Block> originalCarvableBlocks;
    private final CaveBlockReplacer blockCarver;

    public TFCRavineCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> dynamic)
    {
        super(dynamic);
        originalCarvableBlocks = carvableBlocks;
        blockCarver = new CaveBlockReplacer();

        // Need to run this every time the rock registry is reloaded
        RockManager.INSTANCE.addCallback(() -> carvableBlocks = TFCCarvers.fixCarvableBlocksList(originalCarvableBlocks));
    }

    @Override
    public boolean carveRegion(IChunk chunkIn, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig configIn)
    {
        double xOffset = chunkXOffset * 16 + rand.nextInt(16);
        double yOffset = rand.nextInt(rand.nextInt(seaLevel + 20) + 32) + 20; // Modified to use sea level, should reach surface more often
        double zOffset = chunkZOffset * 16 + rand.nextInt(16);
        float yaw = rand.nextFloat() * ((float) Math.PI * 2F);
        float pitch = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
        float width = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
        int branchAmount = 112 - rand.nextInt(28);
        func_227204_a_(chunkIn, biomePos, rand.nextLong(), seaLevel, chunkX, chunkZ, xOffset, yOffset, zOffset, width, yaw, pitch, 0, branchAmount, 3.0D, carvingMask); /* carveRegion */
        return true;
    }

    @Override
    protected boolean carveBlock(IChunk chunkIn, Function<BlockPos, Biome> lazyBiome, BitSet carvingMask, Random random, BlockPos.Mutable mutablePos1, BlockPos.Mutable mutablePos2, BlockPos.Mutable mutablePos3, int p_225556_8_, int p_225556_9_, int p_225556_10_, int actualX, int actualZ, int localX, int y, int localZ, AtomicBoolean reachedSurface)
    {
        mutablePos1.setPos(actualX, y, actualZ);
        return blockCarver.carveBlock(chunkIn, mutablePos1, carvingMask);
    }
}
