package net.dries007.tfc.objects.items.ceramics;

import java.util.EnumMap;

import net.dries007.tfc.objects.Metal;

public class ItemUnfiredMold extends ItemUnfiredPottery
{
    private static final EnumMap<Metal.ItemType, ItemUnfiredMold> MAP = new EnumMap<>(Metal.ItemType.class);

    public static ItemUnfiredMold get(Metal.ItemType category)
    {
        return MAP.get(category);
    }

    public final Metal.ItemType type;

    public ItemUnfiredMold(ItemFiredPottery firedVersion, Metal.ItemType type)
    {
        super(firedVersion);
        this.type = type;
        if (MAP.put(type, this) != null) throw new IllegalStateException("There can only be one.");
    }
}
