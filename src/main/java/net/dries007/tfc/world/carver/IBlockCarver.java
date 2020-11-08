package net.dries007.tfc.world.carver;

import java.util.BitSet;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.WorldGenRegion;

import net.dries007.tfc.world.chunkdata.RockData;

/**
 * This is a wrapper around a series of checks and conditions required for creating caves
 * It manages checking and updating the carving mask, doing proper adjacency checks, updating newly exposed or unsupported blocks, and replacement with the correct block state.
 */
public interface IBlockCarver
{
    boolean carve(WorldGenRegion world, IChunk chunk, BlockPos pos, Random random, int seaLevel, BitSet airMask, BitSet liquidMask, RockData rockData);
}
