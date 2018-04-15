package net.dries007.tfc.objects.items.pottery;

import net.dries007.tfc.objects.Metal;

import java.util.EnumMap;

public class ItemMold extends ItemFiredPottery
{
    private static final EnumMap<Metal.ItemType, ItemMold> MAP = new EnumMap<>(Metal.ItemType.class);

    public static ItemMold get(Metal.ItemType category)
    {
        return MAP.get(category);
    }

    public final Metal.ItemType type;

    public ItemMold(Metal.ItemType type)
    {
        this.type = type;
        if (MAP.put(type, this) != null) throw new IllegalStateException("There can only be one.");
    }
}
