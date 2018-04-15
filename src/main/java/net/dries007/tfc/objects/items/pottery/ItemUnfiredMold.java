package net.dries007.tfc.objects.items.pottery;

import net.dries007.tfc.objects.Metal;

import java.util.EnumMap;

public class ItemUnfiredMold extends ItemUnfiredPottery
{
    private static final EnumMap<Metal.ItemType, ItemUnfiredMold> MAP = new EnumMap<>(Metal.ItemType.class);

    public static ItemUnfiredMold get(Metal.ItemType category)
    {
        return MAP.get(category);
    }

    public final Metal.ItemType type;

    public ItemUnfiredMold(Metal.ItemType type)
    {
        this.type = type;
        if (MAP.put(type, this) != null) throw new IllegalStateException("There can only be one.");
    }
}
