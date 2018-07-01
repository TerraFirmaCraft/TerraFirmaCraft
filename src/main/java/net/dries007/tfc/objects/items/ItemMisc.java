/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.items;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Size;
import net.dries007.tfc.objects.Weight;
import net.dries007.tfc.util.IItemSize;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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
    public Size getSize(ItemStack stack)
    {
        return size;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return weight;
    }
}
