package net.dries007.tfc.objects.items.metal;

import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.util.IPlacableItem;

public class ItemAnvil extends ItemMetal implements IPlacableItem
{
    public ItemAnvil(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }
}
