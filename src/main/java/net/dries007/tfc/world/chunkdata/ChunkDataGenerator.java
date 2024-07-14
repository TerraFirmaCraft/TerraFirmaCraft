/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.settings.RockSettings;

/**
 * This is the object responsible for generating TFC chunk data, in parallel with normal chunk generation.
 * <p>
 * In order to apply this to a custom chunk generator: the chunk generator MUST implement {@link ChunkGeneratorExtension} and return a {@link ChunkDataProvider}, which contains an instance of this generator.
 */
public interface ChunkDataGenerator
{
    /**
     * Generate the provided chunk data
     */
    void generate(ChunkData data);

    /**
     * Generate the rock at the given {@code (x, y, z)} position. Coordinates must be <strong>block coordinates</strong>, not chunk-local. {@code surfaceY} should be the view provided in {@link RockData}
     * @param cache An optional cache. If present, this will be used to reduce repeated computation when invoking {@code generateRock()} repeatedly on points within the same chunk.
     */
    RockSettings generateRock(int x, int y, int z, int surfaceY, @Nullable ChunkRockDataCache cache);

    default void displayDebugInfo(List<String> tooltip, BlockPos pos, int surfaceY) {}
}
