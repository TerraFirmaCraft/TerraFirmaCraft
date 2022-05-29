/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

public interface Tiered
{
    /**
     * @return the tier of this block. Roughly corresponds to the index in {@link net.dries007.tfc.util.Metal.Tier}, but is typed as an int to allow arbitrary tiers.
     */
    int getTier();
}
