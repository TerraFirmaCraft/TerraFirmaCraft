package net.dries007.tfc.common.recipes.inventory;

import net.dries007.tfc.util.Alloy;

public class AlloyRecipeWrapper implements IInventoryNoop
{
    private final Alloy alloy;

    public AlloyRecipeWrapper(Alloy alloy)
    {
        this.alloy = alloy;
    }

    public Alloy getAlloy()
    {
        return alloy;
    }
}
