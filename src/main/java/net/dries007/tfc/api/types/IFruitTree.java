/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.util.IFruitTreeGenerator;
import net.dries007.tfc.util.calendar.Month;

public interface IFruitTree
{
    /**
     * @return the minimum time the tree will take to grow one trunk/fruit ripe (in hours)
     */
    float getGrowthTime();

    /**
     * Checks if this month is a flowering month season
     *
     * @param month the month to check
     * @return true if this month should use the flower texture
     */
    boolean isFlowerMonth(Month month);

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
     * @return true if the tree should spawn here
     */
    boolean isValidConditions(float temperature, float rainfall);

    /**
     * A stricter version of the above check. Allows the tree to grow trunks
     *
     * @param temperature the temperature, in -30 - +30
     * @param rainfall    the rainfall, in 0 - 500
     * @return true if the tree is allowed to grow.(false doesn't mean death)
     */
    boolean isValidForGrowth(float temperature, float rainfall);

    /**
     * Get the food item dropped by this fruit tree upon harvest(right clicking)
     *
     * @return the itemstack that should be dropped in world
     */
    ItemStack getFoodDrop();

    /**
     * Used to register in ore dict
     *
     * @return the name of this fruit tree
     */
    String getName();

    /**
     * Return the tree generator used to generate this fruit tree.
     */
    @Nonnull
    default IFruitTreeGenerator getGenerator()
    {
        return IFruitTreeGenerator.DEFAULT;
    }
}
