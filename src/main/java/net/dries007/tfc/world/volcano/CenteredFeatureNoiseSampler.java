/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.volcano;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.noise.Cellular2D;

/**
 * Interface for cellular noise features that are dependent on the biome at the center of a 2D cell
 */
public interface CenteredFeatureNoiseSampler
{
    default double setColumnAndSampleHeight(double heightIn, int x, int z, BiomeSourceExtension biomeSource)
    {
        return heightIn;
    }

    BiomeExtension getCenterBiome(int x, int z, BiomeSourceExtension biomeSource);

    /**
     * @return {@code true} if {@code biome} can generate these noise features at all.
     */
    boolean isValidBiome(BiomeExtension biome);

    /**
     * @return A {@code frequency} parameter for this noise feature within the given biome. Will only be called if {@link #isValidBiome}
     * returns {@code true}. This value is provided to the following methods.
     */
    default float getFrequency(BiomeExtension biome)
    {
        return biome.getCenteredFeatureFrequency();
    }

    /**
     * @return A value representing how close we are to a given center point in {@code [0, 1]}, where higher values are closer to the center.
     */
    default float calculateEasing(BlockPos pos, BiomeExtension biome)
    {
        return calculateEasing(pos.getX(), pos.getZ(), getFrequency(biome));
    }

    /**
     * @return A value representing how close we are to a given center point in {@code [0, 1]}, where higher values are closer to the center.
     */
    float calculateEasing(int x, int z, float frequency);

    /**
     * @return The nearest center point to the given position.
     */
    @Nullable
    default BlockPos calculateCenter(BlockPos pos, BiomeExtension biome)
    {
        return calculateCenter(pos.getX(), pos.getY(), pos.getZ(), getFrequency(biome));
    }

    /**
     * @return The nearest center point to the given position.
     */
    @Nullable
    BlockPos calculateCenter(int x, int y, int z, float frequency);

    /**
     * Sample the nearest cellular noise feature cell to a given position.
     */
    default boolean checkCellRarity(Cellular2D.Cell cell, float frequency)
    {
        if (frequency == 0) return false;
        return Math.abs(cell.noise()) <= frequency;
    }

    /**
     * @return The cell at a given location.
     */
    Cellular2D getCellularNoise();

    /**
     * @return The cell at a given location.
     */
    @Nullable
    VolcanoVariant getVolcanoVariant(Cellular2D.Cell cell);
}
