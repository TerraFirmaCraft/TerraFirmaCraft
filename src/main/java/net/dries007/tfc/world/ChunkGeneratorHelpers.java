package net.dries007.tfc.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.Heightmap;

import net.dries007.tfc.mixin.world.gen.HeightmapAccessor;

public final class ChunkGeneratorHelpers
{
    /**
     * Updates the world generation height maps for a chunk.
     * Required as early operations on the chunk do not update the height maps for each placement as they go through the sections directly.
     *
     * @param chunk The chunk
     */
    public static void updateChunkHeightMaps(ChunkPrimer chunk)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final int maxY = chunk.getHighestSectionPosition() + 16;

        final HeightmapAccessor oceanFloorHeightMap = (HeightmapAccessor) chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);
        final HeightmapAccessor worldSurfaceHeightMap = (HeightmapAccessor) chunk.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG);

        for (int localX = 0; localX < 16; ++localX)
        {
            for (int localZ = 0; localZ < 16; ++localZ)
            {
                boolean reachedTopSurface = false;
                for (int y = maxY - 1; y >= 0; --y)
                {
                    mutablePos.set(localX, y, localZ);
                    BlockState state = chunk.getBlockState(mutablePos);
                    if (state.getBlock() != Blocks.AIR)
                    {
                        if (!reachedTopSurface)
                        {
                            // Non-air block found, update world surface height map
                            worldSurfaceHeightMap.call$setHeight(localX, localZ, y + 1);
                            reachedTopSurface = true;
                        }

                        if (Heightmap.Type.OCEAN_FLOOR_WG.isOpaque().test(state))
                        {
                            // Update ocean floor height map, then go to next x/z position
                            oceanFloorHeightMap.call$setHeight(localX, localZ, y + 1);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Shortcut for early world generation to directly set the chunk section
     *
     * @param chunk The chunk
     * @param x     The x position in [0, 15]
     * @param y     The y position in [0, 256]
     * @param z     The z position in [0, 15]
     * @param state The state to set. Must not emit light or have any other special requirements
     * @return The previous state
     */
    public static BlockState setEarlyBlockState(ChunkPrimer chunk, int x, int y, int z, BlockState state)
    {
        return chunk.getOrCreateSection(y >> 4).setBlockState(x, y & 15, z, state, false);
    }
}
