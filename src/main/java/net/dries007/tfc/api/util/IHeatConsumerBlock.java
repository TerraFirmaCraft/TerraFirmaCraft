/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Implement this on blocks that accept heat from outside sources
 * Various blocks (i.e. fire pits, forges) will try and heat blocks in specific locations if they implement this interface
 */
public interface IHeatConsumerBlock
{
    /**
     * Gets the current temperature of the block
     *
     * @param world       The world
     * @param pos         The position of the {@code IHeatConsumerBlock}
     * @param temperature a temperature. Will be in [0, max temp in config]
     */
    void acceptHeat(World world, BlockPos pos, float temperature);
}
