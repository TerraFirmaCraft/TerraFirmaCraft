package net.dries007.tfc.objects.items;

import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.util.InsertOnlyEnumTable;
import net.minecraft.item.Item;

public class ItemMetal extends Item
{
    private static final InsertOnlyEnumTable<Metal, Metal.ItemType, ItemMetal> TABLE = new InsertOnlyEnumTable<>(Metal.class, Metal.ItemType.class);

    public static ItemMetal get(Metal metal, Metal.ItemType type)
    {
        return TABLE.get(metal, type);
    }

    public final Metal metal;
    public final Metal.ItemType type;

    public ItemMetal(Metal metal, Metal.ItemType type)
    {
        this.metal = metal;
        this.type = type;
        TABLE.put(metal, type, this);
    }
}
