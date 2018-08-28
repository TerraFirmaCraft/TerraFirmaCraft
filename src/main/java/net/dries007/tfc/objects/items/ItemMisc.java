/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.items;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

public class ItemMisc extends ItemTFC implements IItemSize
{
    private final Size size;
    private final Weight weight;

    public ItemMisc(Size size, Weight weight)
    {
        super();
        this.size = size;
        this.weight = weight;
    }

    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return size;
    }

    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return weight;
    }
}
