/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.inventory;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

public class SlotSized extends SlotItemHandler
{
    private final Size size;
    private final Weight weight;

    public SlotSized(IItemHandler inv, int idx, int x, int y, Size size, Weight weight)
    {
        super(inv, idx, x, y);
        this.size = size;
        this.weight = weight;
    }

    public SlotSized(IItemHandler inv, int idx, int x, int y, Size size)
    {
        this(inv, idx, x, y, size, Weight.HEAVY);
    }

    public SlotSized(IItemHandler inv, int idx, int x, int y, Weight weight)
    {
        this(inv, idx, x, y, Size.HUGE, weight);
    }
}
