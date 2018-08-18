/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.EnumMap;

import net.dries007.tfc.objects.MetalType;

public class ItemUnfiredMold extends ItemUnfiredPottery
{
    private static final EnumMap<MetalType, ItemUnfiredMold> MAP = new EnumMap<>(MetalType.class);

    public static ItemUnfiredMold get(MetalType category)
    {
        return MAP.get(category);
    }

    public final MetalType type;

    public ItemUnfiredMold(ItemFiredPottery firedVersion, MetalType type)
    {
        super(firedVersion);
        this.type = type;
        if (MAP.put(type, this) != null) throw new IllegalStateException("There can only be one.");
    }
}
