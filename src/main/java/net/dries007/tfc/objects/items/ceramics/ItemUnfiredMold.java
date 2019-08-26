/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.EnumMap;

import net.dries007.tfc.api.types.Metal;

public class ItemUnfiredMold extends ItemPottery
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
        if (MAP.put(type, this) != null)
        {
            throw new IllegalStateException("There can only be one.");
        }
    }
}
