/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.chunkdata.ChunkData.Status;
import net.dries007.tfc.world.noise.Noise2D;

public interface BiomeNoiseSampler
{
    /**
     * Outputs of {@link BiomeNoiseSampler} have positive values indicating air, above a certain threshold
     */
    double SOLID = 0;
    double AIR_THRESHOLD = 0.4;

    static BiomeNoiseSampler fromHeightNoise(Noise2D heightNoise)
    {
        return new BiomeNoiseSampler()
        {
            private float height;

            @Override
            public void setColumn(int x, int z)
            {
                height = (float) heightNoise.noise(x, z);
            }

            @Override
            public double height()
            {
                return height;
            }

            @Override
            public double noise(int y)
            {
                return SOLID;
            }
        };
    }

    /**
     * This is called once, before any sampling is done on this chunk. It should not modify the chunk, but may use it to
     * query useful information i.e. chunk data (which is populated up until {@link Status#PARTIAL}) at this time.
     * <p>
     * Note that the chunk <strong>may be</strong> {@code null}. This occurs when this is used for sampling height through
     * {@link net.minecraft.world.level.chunk.ChunkGenerator#getBaseHeight(int, int, Heightmap.Types, LevelHeightAccessor, RandomState)},
     * where the chunk does not exist yet.
     *
     * @param generator The chunk generator that is about to generate this chunk
     * @param chunk The chunk about to be populated.
     */
    @SuppressWarnings("unused") // Provided as API to allow climate-based terrain noise
    default void prepare(ChunkGeneratorExtension generator, @Nullable ChunkAccess chunk) {}

    /**
     * This is called once before populating each column in the chunk. It is intended to be used to locally cache any noise values that only vary
     * on a {@code (x, z)} basis. After this, {@link #height()} and {@link #noise(int)} will be called, possibly multiple times for each column.
     *
     * @param x The exact block X coordinate
     * @param z The exact block Z coordinate
     */
    void setColumn(int x, int z);

    /**
     * @return The height of the terrain at this area. {@link #noise(int)} will be called for {@code y} values, generally lower than {@code height}.
     */
    double height();

    /**
     * Returns density noise value for this coordinate, given by {@link #setColumn(int, int)} and {@code y}.Values of {@link #SOLID} indicate
     * solid terrain, whereas higher values (greater than {@link #AIR_THRESHOLD}) indicate air (or water).
     *
     * @param y The exact block Y coordinate
     * @return A density noise value for this coordinate.
     */
    double noise(int y);
}
