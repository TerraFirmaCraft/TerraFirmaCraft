/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import java.util.EnumMap;

import net.minecraft.item.Item;

import net.dries007.tfc.objects.Rock;

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
