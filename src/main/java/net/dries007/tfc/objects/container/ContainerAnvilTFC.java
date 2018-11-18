/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;

import net.dries007.tfc.objects.te.TEAnvilTFC;

public class ContainerAnvilTFC extends ContainerTE<TEAnvilTFC>
{
    public ContainerAnvilTFC(InventoryPlayer playerInv, TEAnvilTFC te)
    {
        super(playerInv, te);
    }

    public void onReceivePacket(int buttonID)
    {

    }

    @Override
    protected void addContainerSlots()
    {
        // todo: add slots
    }

}
