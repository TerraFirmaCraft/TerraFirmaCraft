/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.forge;

/**
 * Interface for the forgeable capability for items that store a metal amount, i.e. blooms
 */
public interface IForgeableMeasurable extends IForgeable
{
    int getMetalAmount();

    void setMetalAmount(int metalAmount);
}
