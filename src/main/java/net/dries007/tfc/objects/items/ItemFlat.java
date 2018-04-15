package net.dries007.tfc.objects.items;

import net.dries007.tfc.objects.Rock;
import net.minecraft.item.Item;

import java.util.EnumMap;

public final class ItemFlat extends Item
{
    private static final EnumMap<Rock, ItemFlat> ROCK_MAP = new EnumMap<>(Rock.class);

    public ItemFlat()
    {
        setMaxStackSize(0);
        setNoRepair();
        setHasSubtypes(false);
    }

    public ItemFlat(Rock rock)
    {
        this();
        if (ROCK_MAP.put(rock, this) != null) throw new IllegalStateException("There can only be one.");
    }
}
