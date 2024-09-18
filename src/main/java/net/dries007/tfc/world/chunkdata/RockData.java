/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.region.Units;
import net.dries007.tfc.world.settings.RockSettings;

public class RockData
{
    private final @Nullable ChunkDataGenerator generator;
    private int @Nullable [] surfaceHeight;
    private @Nullable ChunkRockDataCache cache;

    public RockData(@Nullable ChunkDataGenerator generator)
    {
        this.generator = generator;
        this.surfaceHeight = null;
        this.cache = null;
    }

    /**
     * Initializes the rock data's {@link ChunkRockDataCache}. This means that future queries into {@link #getRock(BlockPos)} or {@link #getRock(int, int, int)} will populate and re-use the cache for noise.
     * <strong>N.B.</strong> Only use if this is used to query many positions in the chunk, otherwise the cost of populating the cache on the first few queries will be worse than with no cache.
     * @param pos The current chunk position.
     */
    public void useCache(ChunkPos pos)
    {
        this.cache = new ChunkRockDataCache(pos);
    }

    /**
     * Return the rock at the position {@code (x, z)}, at the surface. Note that this method <strong>may be used</strong>
     * without populating the chunk data's surface height cache, because it queries not for a specific position, but a specific height.
     */
    @SuppressWarnings("unused") // API
    public RockSettings getSurfaceRock(int x, int z)
    {
        assert generator != null;
        return generator.generateSurfaceRock(x, z);
    }

    public RockSettings getRock(BlockPos pos)
    {
        return getRock(pos.getX(), pos.getY(), pos.getZ());
    }

    public RockSettings getRock(int x, int y, int z)
    {
        assert generator != null && surfaceHeight != null;
        return generator.generateRock(x, y, z, surfaceHeight[Units.index(x, z)], cache);
    }

    public int[] getSurfaceHeight()
    {
        assert surfaceHeight != null;
        return surfaceHeight;
    }

    public void setSurfaceHeight(int[] surfaceHeight)
    {
        this.surfaceHeight = surfaceHeight;
    }
}
