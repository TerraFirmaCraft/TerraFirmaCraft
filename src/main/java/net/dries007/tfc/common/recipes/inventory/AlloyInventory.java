/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.inventory;

import net.dries007.tfc.util.Alloy;

public record AlloyInventory(Alloy alloy) implements EmptyInventory
{
    /** @deprecated Use {@link #alloy()} instead */
    @Deprecated
    public Alloy getAlloy()
    {
        return alloy;
    }
}
