package net.dries007.tfc.api.util;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.items.ItemQuiver;

public interface IQuiverAmmo
{
    default void replenishJavelin(InventoryPlayer playerInv)
    {
        ItemStack quiver = findQuiver(playerInv);
        if (quiver != null)
        {
            IItemHandler quiverCapability = quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (quiverCapability instanceof ItemQuiver.QuiverCapability)
            {
                ItemStack newJav = ((ItemQuiver.QuiverCapability) quiverCapability).findJavelin();
                if (newJav != null)
                {
                    playerInv.setInventorySlotContents(playerInv.currentItem, newJav);
                }
            }
        }
    }

    default ItemStack findQuiver(InventoryPlayer playerInv)
    {
        if (playerInv.offHandInventory.get(0).getItem() instanceof ItemQuiver)
        {
            return playerInv.offHandInventory.get(0);
        }
        else
        {
            for (int i = 0; InventoryPlayer.isHotbar(i); i++)
            {
                ItemStack cur = playerInv.mainInventory.get(i);
                if (cur.getItem() instanceof ItemQuiver)
                {
                    return cur;
                }
            }
        }
        return null;
    }

}
