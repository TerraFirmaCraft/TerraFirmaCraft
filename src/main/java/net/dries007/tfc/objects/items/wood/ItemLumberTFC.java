package net.dries007.tfc.objects.items.wood;

import net.dries007.tfc.objects.Wood;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.EnumMap;

public class ItemLumberTFC extends Item
{
    private static final EnumMap<Wood, ItemLumberTFC> MAP = new EnumMap<>(Wood.class);

    public static ItemLumberTFC get(Wood wood)
    {
        return MAP.get(wood);
    }

    public static ItemStack get(Wood wood, int amount)
    {
        return new ItemStack(MAP.get(wood), amount);
    }

    public final Wood wood;

    public ItemLumberTFC(Wood wood)
    {
        this.wood = wood;
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        setMaxDamage(0);
    }
}