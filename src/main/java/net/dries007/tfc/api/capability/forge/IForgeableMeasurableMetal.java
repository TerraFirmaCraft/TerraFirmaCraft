/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.forge;

import net.dries007.tfc.api.types.Metal;

/**
 * Interface for the forgeable heatable capability for items that store a metal amount, i.e. blooms
 */
public interface IForgeableMeasurableMetal extends IForgeableHeatable
{
    /**
     * Gets the metal content in this item
     *
     * @return the metal amount contained in this item
     */
    int getMetalAmount();

    /**
     * Sets the metal content in this item
     *
     * @param metalAmount the amount of metal to set this item to
     */
    void setMetalAmount(int metalAmount);

    /**
     * Gets the metal this item stores
     *
     * @return the metal obj
     */
    Metal getMetal();

    /**
     * Sets the metal this item stores
     *
     * @param metal the metal obj
     */
    void setMetal(Metal metal);
}
