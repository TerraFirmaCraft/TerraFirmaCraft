/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.EnumMap;

import net.dries007.tfc.objects.MetalType;

public class ItemMold extends ItemFiredPottery
{
    private static final EnumMap<MetalType, ItemMold> MAP = new EnumMap<>(MetalType.class);

    public static ItemMold get(MetalType category)
    {
        return MAP.get(category);
    }

    public final MetalType type;

    public ItemMold(MetalType type)
    {
        this.type = type;
        if (MAP.put(type, this) != null) throw new IllegalStateException("There can only be one.");
    }
}
