/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

import net.minecraft.block.BlockState;

import net.dries007.tfc.world.chunkdata.RockData;

/**
 * Interface for block replacements.
 * These are ran during surface generation. Before then, the entire world is generated using vanilla blocks, some as markers to be replaced later
 */
@FunctionalInterface
public interface IBlockReplacer
{
    BlockState getReplacement(RockData rockData, int x, int y, int z, float rainfall, float temperature, boolean salty);

    /**
     * Set the seed for this replacer
     */
    default void setSeed(long seed) {}
}