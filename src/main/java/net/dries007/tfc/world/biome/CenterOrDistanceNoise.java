/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Template for a particular type of noise that generates biome-dependent points (centers), and surrounds them with an area.
 */
public interface CenterOrDistanceNoise
{
    /**
     * @return {@code true} if {@code biome} can generate these noise features at all.
     */
    boolean isValidBiome(BiomeExtension biome);

    /**
     * @return A {@code rarity} parameter for this noise feature within the given biome. Will only be called if {@link #isValidBiome}
     * returns {@code true}. This value is provided to the following methods.
     */
    int getRarity(BiomeExtension biome);

    /**
     * @return A value representing how close we are to a given center point in {@code [0, 1]}, where higher values are closer to the center.
     */
    default float calculateEasing(BlockPos pos, BiomeExtension biome)
    {
        return calculateEasing(pos.getX(), pos.getZ(), getRarity(biome));
    }

    /**
     * @return A value representing how close we are to a given center point in {@code [0, 1]}, where higher values are closer to the center.
     */
    float calculateEasing(int x, int z, int rarity);

    /**
     * @return The nearest center point to the given position.
     */
    @Nullable
    default BlockPos calculateCenter(BlockPos pos, BiomeExtension biome)
    {
        return calculateCenter(pos.getX(), pos.getY(), pos.getZ(), getRarity(biome));
    }

    /**
     * @return The nearest center point to the given position.
     */
    @Nullable
    BlockPos calculateCenter(int x, int y, int z, int rarity);
}
