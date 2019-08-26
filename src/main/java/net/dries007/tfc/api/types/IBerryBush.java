/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.util.calendar.Month;

public interface IBerryBush
{
    /**
     * @return the minimum time the bush will take to grow one bush/fruit ripe (in hours)
     */
    float getGrowthTime();

    /**
     * Checks if this month is a harvesting season
     *
     * @param month the month to check
     * @return true if this month should bear fruits
     */
    boolean isHarvestMonth(Month month);

    /**
     * Checks if the conditions are valid for world gen spawning / living
     *
     * @param temperature the temperature, in -30 - +30
     * @param rainfall    the rainfall, in 0 - 500
     * @return true if the bush should spawn here
     */
    boolean isValidConditions(float temperature, float rainfall);

    /**
     * A stricter version of the above check. Allows the bush to grow fruits/adjacent bushes
     *
     * @param temperature the temperature, in -30 - +30
     * @param rainfall    the rainfall, in 0 - 500
     * @return true if the bush is allowed to grow.(false doesn't mean death)
     */
    boolean isValidForGrowth(float temperature, float rainfall);

    /**
     * Get the food item dropped by this bush upon harvest(right clicking)
     *
     * @return the itemstack that should be dropped in world
     */
    ItemStack getFoodDrop();

    /**
     * Get the bush size, used by bush block to determine it's height
     *
     * @return Size enum
     */
    Size getSize();

    /**
     * Determines if this bush has spikes (eg: Damage the player/entity on collision)
     *
     * @return true if this damages entities
     */
    boolean isSpiky();

    enum Size
    {
        SMALL, MEDIUM, LARGE
    }
}
