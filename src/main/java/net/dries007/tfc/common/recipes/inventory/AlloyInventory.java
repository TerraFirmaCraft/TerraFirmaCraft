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
