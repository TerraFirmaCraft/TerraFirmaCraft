/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerMold extends ContainerItemStack
{
    public ContainerMold(InventoryPlayer playerInv, ItemStack stack)
    {
        super(playerInv, stack);

        addPlayerInventorySlots(playerInv);
        addContainerSlots();
    }

    @Override
    protected void addContainerSlots()
    {
    }
}
