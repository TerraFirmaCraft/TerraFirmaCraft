package net.dries007.tfc.world;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;

/**
 * A collection of debug world generation things
 */
@SuppressWarnings("unused")
public final class Debug
{
    interface SlopeFunction
    {
        double sampleSlope(double[] slopeMap, int x, int z);
    }

    public static void slopeVisualization(IChunk chunk, int[] surfaceHeightMap, double[] slopeMap, int chunkX, int chunkZ, SlopeFunction slopeFunction)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final Block[] meter = new Block[] {
            Blocks.WHITE_STAINED_GLASS,
            Blocks.LIGHT_GRAY_STAINED_GLASS,
            Blocks.LIGHT_BLUE_STAINED_GLASS,
            Blocks.BLUE_STAINED_GLASS,
            Blocks.CYAN_STAINED_GLASS,
            Blocks.GREEN_STAINED_GLASS,
            Blocks.LIME_STAINED_GLASS,
            Blocks.YELLOW_STAINED_GLASS,
            Blocks.ORANGE_STAINED_GLASS,
            Blocks.RED_STAINED_GLASS,
            Blocks.MAGENTA_STAINED_GLASS,
            Blocks.PINK_STAINED_GLASS
        };

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                int y = surfaceHeightMap[x + 16 * z];
                mutablePos.set(chunkX + x, y, chunkZ + z);
                double slope = slopeFunction.sampleSlope(slopeMap, x, z);
                int slopeIndex = MathHelper.clamp((int) slope, 0, meter.length - 1);
                chunk.setBlockState(mutablePos, meter[slopeIndex].defaultBlockState(), false);
            }
        }
    }
}
