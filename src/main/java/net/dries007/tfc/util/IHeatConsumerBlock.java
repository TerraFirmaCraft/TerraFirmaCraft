/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Implement this on blocks that accept heat from outside sources
 * The block accepting heat should query the {@code IHeatConsumerBlock} for the current temperature, and then update itself accordingly
 * Example: {@link net.dries007.tfc.objects.te.TECrucible} will search for blocks that are one block down from itself and update its heat accordingly
 */
public interface IHeatConsumerBlock
{
    /**
     * Gets the current temperature of the block
     *
     * @param world       The world
     * @param pos         The position of the {@code IHeatConsumerBlock}
     * @param temperature a temperature in the range [0, {@link net.dries007.tfc.api.capability.heat.CapabilityItemHeat#MAX_TEMPERATURE}]
     */
    void acceptHeat(World world, BlockPos pos, float temperature);
}
