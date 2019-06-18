/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.forge;

public interface IForgeableMeasurable extends IForgeable
{
    /*
     * Gets the current metal amount stored in the object
     */
    int getMetalAmount();

    /*
     * Sets the metal amount stored in the object
     */
    void setMetalAmount(int metalAmount);
}
