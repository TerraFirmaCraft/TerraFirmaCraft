/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

public class ItemCraftingTool extends ItemMisc
{
    public ItemCraftingTool(int durability, Size size, Weight weight, Object... oreNameParts)
    {
        super(size, weight, oreNameParts);
        setMaxDamage(durability);
        setMaxStackSize(1);
        setNoRepair();
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        return false;
    }
}
