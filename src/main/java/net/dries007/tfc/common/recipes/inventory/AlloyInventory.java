/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.inventory;

import net.dries007.tfc.util.Alloy;

public class AlloyInventory implements EmptyInventory
{
    private final Alloy alloy;

    public AlloyInventory(Alloy alloy)
    {
        this.alloy = alloy;
    }

    public Alloy getAlloy()
    {
        return alloy;
    }
}
