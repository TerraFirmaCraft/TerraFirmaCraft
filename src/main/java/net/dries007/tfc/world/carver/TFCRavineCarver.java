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
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.carver.CanyonWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.mixin.world.gen.carver.CanyonWorldCarverAccessor;
import net.dries007.tfc.world.chunkdata.RockData;

public class TFCRavineCarver extends CanyonWorldCarver implements IContextCarver
{
    private final Set<Block> originalCarvableBlocks;
    private final AirBlockCarver blockCarver;

    private boolean initialized;

    public TFCRavineCarver(Codec<ProbabilityConfig> codec)
    {
        super(codec);
        originalCarvableBlocks = replaceableBlocks;
        blockCarver = new AirBlockCarver();
        initialized = false;

        // Need to run this every time the rock registry is reloaded
        RockManager.INSTANCE.addCallback(() -> replaceableBlocks = TFCCarvers.fixCarvableBlocksList(originalCarvableBlocks));
    }

    @Override
    public boolean carve(IChunk chunkIn, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig configIn)
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
    public void setContext(WorldGenRegion world, BitSet airCarvingMask, BitSet liquidCarvingMask, RockData rockData, BitSet waterAdjacencyMask)
    {
        this.blockCarver.setContext(world, airCarvingMask, liquidCarvingMask, rockData, waterAdjacencyMask);
        this.initialized = true;
    }

    @Override
    protected boolean carveBlock(IChunk chunkIn, Function<BlockPos, Biome> lazyBiome, BitSet carvingMask, Random random, BlockPos.Mutable mutablePos1, BlockPos.Mutable mutablePos2, BlockPos.Mutable mutablePos3, int seaLevel, int chunkX, int chunkZ, int actualX, int actualZ, int localX, int y, int localZ, MutableBoolean reachedSurface)
    {
        mutablePos1.set(actualX, y, actualZ);
        return blockCarver.carve(chunkIn, mutablePos1, random, seaLevel);
    }
}