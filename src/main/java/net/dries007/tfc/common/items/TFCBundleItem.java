/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.component.size.IItemSize;
import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;

public class TFCBundleItem extends BundleItem
{
    public TFCBundleItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access)
    {
        final IItemSize data = ItemSizeManager.get(other);

        if (data.getWeight(other).isSmallerThan(Weight.MEDIUM) && data.getSize(other).isSmallerThan(Size.LARGE))
        {
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        }
        return false;
    }

}
